package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.*
import com.globo.jarvis.home.HomeOfferCategoriesQuery
import com.globo.jarvis.home.HomeOfferTitleQuery
import com.globo.jarvis.home.HomeOfferVideoQuery
import com.globo.jarvis.home.HomeQuery
import com.globo.jarvis.model.*
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device
) {

    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    @JvmOverloads
    fun categories(
        offerId: String?,
        page: Int = 1,
        perPage: Int = 12,
        callback: Callback.Home
    ) {
        compositeDisposable.add(
            categories(offerId, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        callback.onOfferCategoriesSuccess(it)
                    },
                    { throwable ->
                        callback.onFailure(throwable)
                    }
                ))
    }

    fun structure(
        pageId: String, highlightLogoScale: String,
        callback: Callback.Home
    ) {
        compositeDisposable.add(
            structure(pageId, highlightLogoScale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        callback.onStructureSuccess(it)
                    },
                    { throwable ->
                        callback.onFailure(throwable)
                    }
                ))
    }


    //Rx
    @JvmOverloads
    fun categories(
        offerId: String?,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<Triple<Int?, Boolean, List<Category>>> = apolloClient
        .query(builderOfferCategoriesQuery(offerId, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val paginatedItems =
                it.data()?.genericOffer()?.fragments()?.categoriesOffer()?.paginatedItems()
            val nextPage = paginatedItems?.nextPage()
            val hasNextPage = paginatedItems?.hasNextPage() ?: false

            val categoriesFiltered: List<Category> =
                transformResourceToCategoryVO(paginatedItems?.resources())

            return@map Triple(
                nextPage,
                hasNextPage,
                categoriesFiltered
            )
        }

    fun structure(pageId: String, highlightLogoScale: String)
            : Observable<Triple<String, HomeQuery.PremiumHighlight?, List<HomeQuery.OfferItem>>> =
        apolloClient
            .query(builderHomeQuery(pageId, highlightLogoScale))
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                val page =
                    it.data()?.page() ?: throw Exception(
                        it.errors().firstOrNull()?.message()
                            ?: "Não foi possível carregar a estrutura da home!"
                    )

                return@map Triple(
                    page.identifier(),
                    page.premiumHighlight(),
                    page.offerItems() ?: listOf()
                )
            }


    fun detailsOfferTitle(
        homePageOfferFragment: HomePageOfferFragment?,
        page: Int,
        perPage: Int,
        posterScale: String
    ): Observable<Offer> =
        apolloClient
            .query(
                builderOfferTitleQuery(
                    homePageOfferFragment?.offerId(),
                    posterScale,
                    page,
                    perPage,
                    device
                )
            )
            .rx()
            .subscribeOn(Schedulers.io())
            .filter {
                val recommendedTitleOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeRecommendedTitleOffers()

                val genericTitleOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeGenericTitleOffers()

                val predicateGenericOffer =
                    (genericTitleOffers?.contentType() == OfferContentType.VIDEO
                            || genericTitleOffers?.contentType() == OfferContentType.TITLE)
                            && genericTitleOffers.items()?.resources()?.isNotEmpty() == true

                val predicateRecommendedOffers =
                    (recommendedTitleOffers?.contentType() == OfferContentType.VIDEO
                            || recommendedTitleOffers?.contentType() == OfferContentType.TITLE)
                            && recommendedTitleOffers.items()?.resources()?.isNotEmpty() == true

                return@filter predicateGenericOffer || predicateRecommendedOffers
            }
            .map {
                //Tipo de componente
                val componentType = ComponentType.normalize(homePageOfferFragment?.componentType())

                //Destino e slug da página, utilizado no chápeu do trilho
                val destination = Destination.normalize(
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByPage()?.identifier(),
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByUrl()?.url()
                )

                val navigation = Navigation.extractSlug(
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByPage()?.identifier(),
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByUrl()?.url()
                )


                val recommendedTitleOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeRecommendedTitleOffers()

                val genericTitleOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeGenericTitleOffers()

                if (genericTitleOffers != null) {
                    return@map Offer(
                        id = genericTitleOffers.id(),
                        title = homePageOfferFragment?.title(),
                        componentType = componentType,
                        contentType = ContentType.normalizeOffer(genericTitleOffers.contentType()),
                        navigation = Navigation(navigation, destination),
                        titleList = transformHomeGenericTitleOffersToTitle(
                            genericTitleOffers.items()?.resources()
                        )
                    )
                } else {
                    return@map Offer(
                        id = recommendedTitleOffers?.id(),
                        title = recommendedTitleOffers?.items()?.customTitle()
                            ?: homePageOfferFragment?.title(),
                        componentType = componentType,
                        contentType = ContentType.normalizeOffer(
                            recommendedTitleOffers
                                ?.contentType()
                        ),
                        navigation = Navigation(navigation, destination),
                        abExperiment = AbExperiment(
                            recommendedTitleOffers?.items()?.abExperiment()
                                ?.alternative(),
                            recommendedTitleOffers?.items()?.abExperiment()?.experiment(),
                            recommendedTitleOffers?.items()?.abExperiment()?.trackId()
                        ),
                        titleList = transformHomeRecommendedTitleOffersToTitle(
                            recommendedTitleOffers?.items()?.resources()
                        )
                    )
                }
            }

    fun detailsOfferVideo(
        homePageOfferFragment: HomePageOfferFragment?,
        page: Int,
        perPage: Int,
        logoScale: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String
    ): Observable<Offer> =
        apolloClient
            .query(
                builderOfferVideoQuery(
                    homePageOfferFragment?.offerId(),
                    page,
                    perPage,
                    logoScale,
                    broadcastChannelTrimmedLogoScales,
                    broadcastImageOnAirScales
                )
            )
            .rx()
            .subscribeOn(Schedulers.io())
            .filter {
                val genericVideoOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeGenericVideoOffers()

                val recommendedVideoOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeRecommendedVideoOffers()

                val predicateGenericOffer =
                    (genericVideoOffers?.contentType() == OfferContentType.VIDEO
                            || genericVideoOffers?.contentType() == OfferContentType.TITLE)
                            && genericVideoOffers.items()?.resources()?.isNotEmpty() == true

                val predicateRecomendedOffers =
                    (recommendedVideoOffers?.contentType() == OfferContentType.VIDEO
                            || recommendedVideoOffers?.contentType() == OfferContentType.TITLE)
                            && recommendedVideoOffers.items()?.resources()?.isNotEmpty() == true

                return@filter predicateGenericOffer || predicateRecomendedOffers
            }
            .map {
                //Tipo de componente
                val componentType = ComponentType.normalize(homePageOfferFragment?.componentType())

                //Destino e slug da página, utilizado no chápeu do trilho
                val destination = Destination.normalize(
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByPage()?.identifier(),
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByUrl()?.url()
                )

                val navigation = Navigation.extractSlug(
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByPage()?.identifier(),
                    homePageOfferFragment?.navigation()?.fragments()?.homeNavigationByUrl()?.url()
                )

                val genericVideoOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeGenericVideoOffers()

                val recommendedVideoOffers = it.data()?.genericOffer()
                    ?.fragments()?.homeRecommendedVideoOffers()


                if (genericVideoOffers != null) {
                    //Oferta trilho de video genérica
                    return@map Offer(
                        id = genericVideoOffers.id(),
                        title = homePageOfferFragment?.title(),
                        componentType = componentType,
                        contentType = ContentType.normalizeOffer(genericVideoOffers.contentType()),
                        navigation = Navigation(navigation, destination),
                        thumbList = transformHomeGenericVideoOffersToThumb(
                            genericVideoOffers.items()?.resources()
                        )
                    )
                } else {
                    //Oferta trilho de video recomendada
                    return@map Offer(
                        id = recommendedVideoOffers?.id(),
                        title = recommendedVideoOffers?.items()?.customTitle()
                            ?: homePageOfferFragment?.title(),
                        componentType = componentType,
                        contentType = ContentType.normalizeOffer(recommendedVideoOffers?.contentType()),
                        navigation = Navigation(navigation, destination),
                        abExperiment = AbExperiment(
                            recommendedVideoOffers?.items()?.abExperiment()?.alternative(),
                            recommendedVideoOffers?.items()?.abExperiment()?.experiment(),
                            recommendedVideoOffers?.items()?.abExperiment()?.trackId()
                        ),
                        thumbList = transformHomeRecommendedVideoOffersToThumb(
                            recommendedVideoOffers?.items()?.resources()
                        )
                    )
                }
            }
            .onExceptionResumeNext(Observable.empty())
            .onErrorResumeNext(Observable.empty())


    //structure
    internal fun builderHomeQuery(pageId: String, highlightLogoScale: String) =
        builderImageHomeQuery(HomeQuery.builder(), highlightLogoScale)
            .id(pageId)
            .filter(PageMetadataFilter.builder().type(PageType.HOME).build())
            .build()

    internal fun builderImageHomeQuery(homeQuery: HomeQuery.Builder, highlightLogoScale: String) =
        when (device) {
            Device.TV -> {
                homeQuery.highlightLogoTVScales(HighlightLogoTVScales.safeValueOf(highlightLogoScale))
                homeQuery.highlightImageTvScales(
                    HighlightImageTVScales.safeValueOf(
                        highlightLogoScale
                    )
                )
            }

            Device.TABLET -> {
                homeQuery.highlightLogoTabletScales(
                    HighlightLogoTabletScales.safeValueOf(
                        highlightLogoScale
                    )
                )
                homeQuery.highlightImageTabletScales(
                    HighlightImageTabletScales.safeValueOf(
                        highlightLogoScale
                    )
                )
            }

            else -> {
                homeQuery.highlightLogoMobileScales(
                    HighlightLogoMobileScales.safeValueOf(
                        highlightLogoScale
                    )
                )
                homeQuery.highlightImageMobileScales(
                    HighlightImageMobileScales.safeValueOf(
                        highlightLogoScale
                    )
                )
            }
        }


    //Offer Categories Query
    internal fun builderOfferCategoriesQuery(offerId: String?, page: Int, perPage: Int) =
        HomeOfferCategoriesQuery
            .builder()
            .offerId(offerId ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun transformResourceToCategoryVO(resourceList: List<CategoriesOffer.Resource>?) =
        resourceList
            ?.map { resource ->
                val categories = resource.fragments().homeCategories()
                val navigationByPage = categories?.navigation()?.fragments()?.homePage()
                val navigationBySlug = categories?.navigation()?.fragments()?.homeSlug()

                return@map Category(
                    Navigation.extractSlug(
                        navigationByPage?.identifier(),
                        navigationBySlug?.slug()
                    ),
                    categories?.name(),
                    categories?.background(),
                    Destination.normalize(navigationByPage?.identifier(), navigationBySlug?.slug())
                )
            }
            ?.filter { it.destination != Destination.UNKNOWN && it.id != null }
            ?: listOf()

    //Offer Title Query
    internal fun builderOfferTitleQuery(
        id: String?,
        posterScale: String,
        page: Int,
        perPage: Int,
        device: Device
    ) =
        builderImageOfferTitle(HomeOfferTitleQuery.builder(), device, posterScale)
            .id(id.orEmpty())
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderImageOfferTitle(
        offerTitleQuery: HomeOfferTitleQuery.Builder,
        device: Device,
        posterScale: String
    ) =
        when (device) {
            Device.TV -> offerTitleQuery.tvPosterScales(TVPosterScales.safeValueOf(posterScale))

            Device.TABLET -> offerTitleQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    posterScale
                )
            )

            else -> offerTitleQuery.mobilePosterScales(MobilePosterScales.safeValueOf(posterScale))
        }


    internal fun transformHomeGenericTitleOffersToTitle(resourceList: List<HomeGenericTitleOffers.Resource>?) =
        resourceList?.map { resource ->
            val titleOffer = resource.fragments().homeTitleOffer()

            val poster = when (device) {
                Device.TV -> titleOffer?.poster()?.tv()
                Device.TABLET -> titleOffer?.poster()?.tablet()
                else -> titleOffer?.poster()?.mobile()
            }

            return@map Title(
                originProgramId = titleOffer?.originProgramId(),
                titleId = titleOffer?.titleId(),
                headline = titleOffer?.headline(),
                type = Type.normalize(titleOffer?.type()),
                poster = poster,
                abExperiment = AbExperiment(pathUrl = titleOffer?.url())
            )
        } ?: listOf()

    internal fun transformHomeRecommendedTitleOffersToTitle(resourceList: List<HomeRecommendedTitleOffers.Resource>?) =
        resourceList?.map { resource ->
            val titleOffer = resource.fragments().homeTitleOffer()

            val poster = when (device) {
                Device.TV -> titleOffer?.poster()?.tv()
                Device.TABLET -> titleOffer?.poster()?.tablet()
                else -> titleOffer?.poster()?.mobile()
            }

            return@map Title(
                originProgramId = titleOffer?.originProgramId(),
                titleId = titleOffer?.titleId(),
                headline = titleOffer?.headline(),
                type = Type.normalize(titleOffer?.type()),
                poster = poster,
                abExperiment = AbExperiment(pathUrl = titleOffer?.url())
            )
        } ?: listOf()


    //Offer Video Query
    internal fun builderOfferVideoQuery(
        id: String?,
        page: Int,
        perPage: Int,
        logoScale: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String
    ) =
        builderImageOfferVideoQuery(HomeOfferVideoQuery.builder(), logoScale)
            .id(id.orEmpty())
            .page(page)
            .perPage(perPage)
            .broadcastImageOnAirScales(
                BroadcastImageOnAirScales.safeValueOf(
                    broadcastImageOnAirScales
                )
            )
            .broadcastChannelTrimmedLogoScales(
                BroadcastChannelTrimmedLogoScales.safeValueOf(
                    broadcastChannelTrimmedLogoScales
                )
            )
            .build()


    internal fun builderImageOfferVideoQuery(
        offerVideoQuery: HomeOfferVideoQuery.Builder,
        logoScale: String
    ) =
        when (device) {
            Device.TV -> offerVideoQuery.logoTVScales(TVLogoScales.safeValueOf(logoScale))

            Device.TABLET -> offerVideoQuery.tabletLogoScales(TabletLogoScales.safeValueOf(logoScale))

            else -> offerVideoQuery.mobileLogoScales(MobileLogoScales.safeValueOf(logoScale))
        }

    internal fun transformHomeGenericVideoOffersToThumb(resourceList: List<HomeGenericVideoOffers.Resource>?) =
        resourceList?.map { resource ->
            val videoOffer = resource.fragments().homeVideoOffer()
            val broadcast = videoOffer?.broadcast()
            val isValidBroadcast = broadcast?.mediaId()?.isNotEmpty() ?: false

            val normalizedKind =
                if (isValidBroadcast) Kind.EVENT else Kind.normalize(videoOffer?.kind())

            return@map Thumb(
                id = videoOffer?.id(),
                headline = videoOffer?.headline(),
                duration = videoOffer?.duration() ?: 0,
                formattedDuration = videoOffer?.formattedDuration(),
                thumb = when (normalizedKind) {
                    Kind.LIVE, Kind.EVENT -> broadcast?.imageOnAir()
                        ?: videoOffer?.liveThumbnail()
                    else -> videoOffer?.thumb()
                },
                title = Title(
                    originProgramId = videoOffer?.title()?.originProgramId(),
                    headline = videoOffer?.title()?.headline()
                ),
                availableFor = AvailableFor.normalize(videoOffer?.availableFor()),
                kind = normalizedKind,
                broadcast = Broadcast(
                    media = Media(
                        idWithDVR = broadcast?.mediaId(),
                        idWithoutDVR = broadcast?.withoutDVRMediaId()
                    ),
                    channel = Channel(trimmedLogo = broadcast?.channel()?.trimmedLogo())
                )
            )

        } ?: listOf()

    internal fun transformHomeRecommendedVideoOffersToThumb(resourceList: List<HomeRecommendedVideoOffers.Resource>?) =
        resourceList?.map { resource ->
            val videoOffer = resource.fragments().homeVideoOffer()
            val broadcast = videoOffer?.broadcast()
            val isValidBroadcast = !broadcast?.mediaId().isNullOrEmpty()

            val normalizedKind =
                if (isValidBroadcast) Kind.EVENT else Kind.normalize(videoOffer?.kind())

            return@map Thumb(
                id = videoOffer?.id(),
                headline = videoOffer?.headline(),
                duration = videoOffer?.duration() ?: 0,
                formattedDuration = videoOffer?.formattedDuration(),
                thumb = when (normalizedKind) {
                    Kind.LIVE, Kind.EVENT -> broadcast?.imageOnAir()
                        ?: videoOffer?.liveThumbnail()
                    else -> videoOffer?.thumb()
                },
                title = Title(
                    originProgramId = videoOffer?.title()?.originProgramId(),
                    headline = videoOffer?.title()?.headline()
                ),
                availableFor = AvailableFor.normalize(videoOffer?.availableFor()),
                kind = normalizedKind,
                broadcast = Broadcast(
                    media = Media(
                        idWithDVR = broadcast?.mediaId(),
                        idWithoutDVR = broadcast?.withoutDVRMediaId()
                    ),
                    channel = Channel(trimmedLogo = broadcast?.channel()?.trimmedLogo())
                )
            )

        } ?: listOf()

}
