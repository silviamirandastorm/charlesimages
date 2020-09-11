package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.categories.CategoriesQuery
import com.globo.jarvis.categoriesdetails.CategoryDetailByIdQuery
import com.globo.jarvis.categoriesdetails.CategoryDetailBySlugQuery
import com.globo.jarvis.categoriesdetails.CategoryDetailsOfferTitleQuery
import com.globo.jarvis.categoriesdetails.CategoryDetailsStructureQuery
import com.globo.jarvis.fragment.CategoriesDetailsPageOfferFragment
import com.globo.jarvis.fragment.CategoryDetailsGenericTitleOffers
import com.globo.jarvis.fragment.CategoryDetailsRecommendedTitleOffers
import com.globo.jarvis.model.*
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CategoriesRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device
) {
    companion object {
        const val SPLIT = ", "
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun detailsGrid(
        slug: String? = null,
        id: String? = null,
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            detailsGrid(slug, id, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onCategoryDetailsGridSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun detailsPage(
        id: String?,
        scale: String,
        page: Int,
        perPage: Int,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            detailsPage(id, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onCategoryDetailsPageSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun detailsOfferTitle(
        id: String?,
        offerTitle: String? = null,
        pageComponentType: PageComponentType? = null,
        pageOfferFragmentNavigation: CategoriesDetailsPageOfferFragment.Navigation? = null,
        scale: String,
        page: Int,
        perPage: Int,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            detailsOfferTitle(
                id,
                offerTitle,
                pageComponentType,
                pageOfferFragmentNavigation,
                scale,
                page,
                perPage
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onDetailsOfferTitleSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun paginationGrid(
        id: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            paginationGrid(id, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onPaginationGridSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun structure(
        id: String?,
        scale: String,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            structure(id, scale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onStructureSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun detailsOffer(
        offerItemList: List<CategoryDetailsStructureQuery.OfferItem>,
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            detailsOffer(offerItemList, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onOfferDetailsSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun detailsCategoryBySlug(
        slug: String? = null,
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            detailsCategoryBySlug(slug, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onDetailsCategoryBySlugSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun detailsCategoryById(
        id: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            detailsCategoryById(id, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onDetailsCategoryByIdSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun detailsGroup(
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        categoriesList: List<String>,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            detailsGroup(scale, page, perPage, categoriesList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onGroupCategoriesSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    fun list(
        page: Int = 1,
        perPage: Int = 12,
        categoriesCallback: Callback.Categories
    ) {
        compositeDisposable.add(
            list(page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        categoriesCallback.onCategoriesSuccess(it)
                    },
                    { throwable ->
                        categoriesCallback.onFailure(throwable)
                    }
                ))
    }

    //RxJava
    fun detailsGrid(
        slug: String? = null,
        id: String? = null,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<CategoryDetails> =
        if (!slug.isNullOrEmpty()) detailsCategoryBySlug(
            slug,
            scale,
            page,
            perPage
        ) else detailsCategoryById(id, scale, page, perPage)

    fun detailsPage(
        id: String?,
        scale: String,
        page: Int = 6,
        perPage: Int = 12
    ): Observable<Pair<String?, List<CategoryOffer>>> = structure(id, scale)
        .flatMap(
            { triple: Triple<String?, CategoryDetailsStructureQuery.PremiumHighlight?, List<CategoryDetailsStructureQuery.OfferItem>> ->
                return@flatMap detailsOffer(triple.third, scale, page, perPage)
            },
            { triple: Triple<String?, CategoryDetailsStructureQuery.PremiumHighlight?, List<CategoryDetailsStructureQuery.OfferItem>?>, categoryOfferVOList: List<CategoryOffer> ->
                Triple(triple.first, triple.second, categoryOfferVOList)
            }
        )
        .map { triple: Triple<String?, CategoryDetailsStructureQuery.PremiumHighlight?, List<CategoryOffer>> ->
            val premiumHighlight = triple.second
            val categoryOfferVOList = triple.third
            val highlightsVO = transformHighlightToHighlightVO(
                premiumHighlight?.highlight(),
                premiumHighlight?.callText()
            )

            return@map when {
                highlightsVO != null -> Pair(
                    triple.first, mutableListOf(
                        CategoryOffer(
                            highlights = highlightsVO,
                            componentType = ComponentType.normalize(
                                premiumHighlight?.componentType(),
                                premiumHighlight?.highlight()?.contentType()
                            ),
                            contentType = ContentType.normalizeHighlight(
                                premiumHighlight?.highlight()?.contentType()
                            )
                        )
                    )
                        .plus(categoryOfferVOList)
                )

                else -> Pair(triple.first, categoryOfferVOList)
            }
        }

    fun detailsOfferTitle(
        id: String?,
        offerTitle: String? = null,
        pageComponentType: PageComponentType? = null,
        pageOfferFragmentNavigation: CategoriesDetailsPageOfferFragment.Navigation? = null,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<CategoryOffer> = apolloClient
        .query(builderCategoryDetailsOfferTitleQuery(id, scale, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .filter {
            val recommendedTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsRecommendedTitleOffers()
            val genericTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsGenericTitleOffers()

            val predicateGenericOffer = genericTitleOffers?.contentType() == OfferContentType.TITLE
                    && genericTitleOffers.items()?.resources()?.isNullOrEmpty() == false

            val predicateRecommendedOffers =
                recommendedTitleOffers?.contentType() == OfferContentType.TITLE
                        && recommendedTitleOffers.items()?.resources()?.isNullOrEmpty() == false

            return@filter predicateGenericOffer || predicateRecommendedOffers
        }
        .map {
            val recommendedTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsRecommendedTitleOffers()
            val genericTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsGenericTitleOffers()

            val normalizedComponentType = ComponentType.normalize(pageComponentType)

            val destination = Destination.normalize(
                pageOfferFragmentNavigation?.fragments()?.categoriesDetailsNavigationByPage()
                    ?.identifier(),
                pageOfferFragmentNavigation?.fragments()?.categoriesDetailsNavigationByUrl()?.url()
            )

            val navigation = Navigation.extractSlug(
                pageOfferFragmentNavigation?.fragments()?.categoriesDetailsNavigationByPage()
                    ?.identifier(),
                pageOfferFragmentNavigation?.fragments()?.categoriesDetailsNavigationByUrl()?.url()
            )

            if (genericTitleOffers != null) {
                CategoryOffer(
                    id = genericTitleOffers.id(),
                    title = offerTitle,
                    componentType = normalizedComponentType,
                    contentType = ContentType.normalizeOffer(genericTitleOffers.contentType()),
                    navigation = Navigation(navigation, destination),
                    hasNextPage = genericTitleOffers.items()?.hasNextPage() ?: false,
                    nextPage = genericTitleOffers.items()?.nextPage() ?: 2,
                    titleList = transformGenericTitleResourceToTitleVO(genericTitleOffers.items()?.resources())
                )
            } else {
                CategoryOffer(
                    id = recommendedTitleOffers?.id(),
                    title = recommendedTitleOffers?.items()?.customTitle() ?: offerTitle,
                    componentType = normalizedComponentType,
                    contentType = ContentType.normalizeOffer(recommendedTitleOffers?.contentType()),
                    navigation = Navigation(navigation, destination),
                    hasNextPage = recommendedTitleOffers?.items()?.hasNextPage() ?: false,
                    nextPage = recommendedTitleOffers?.items()?.nextPage() ?: 2,
                    experiment = recommendedTitleOffers?.items()?.abExperiment()?.experiment(),
                    alternative = recommendedTitleOffers?.items()?.abExperiment()?.alternative(),
                    trackId = recommendedTitleOffers?.items()?.abExperiment()?.trackId(),
                    titleList = transformRecommendedTitleOffersResourceToTitleVO(
                        recommendedTitleOffers?.items()?.resources()
                    )
                )
            }
        }
        .filter { it.titleList != null }
        .onExceptionResumeNext(Observable.empty())
        .onErrorResumeNext(Observable.empty())

    fun paginationGrid(
        id: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<CategoryOffer> = apolloClient
        .query(builderCategoryDetailsOfferTitleQuery(id, scale, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .filter {
            val recommendedTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsRecommendedTitleOffers()
            val genericTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsGenericTitleOffers()

            val predicateGenericOffer = genericTitleOffers?.contentType() == OfferContentType.TITLE
                    && genericTitleOffers.items()?.resources()?.isNullOrEmpty() == false

            val predicateRecommendedOffers =
                recommendedTitleOffers?.contentType() == OfferContentType.TITLE
                        && recommendedTitleOffers.items()?.resources()?.isNullOrEmpty() == false

            return@filter predicateGenericOffer || predicateRecommendedOffers
        }
        .map {
            val recommendedTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsRecommendedTitleOffers()
            val genericTitleOffers =
                it.data()?.genericOffer()?.fragments()?.categoryDetailsGenericTitleOffers()


            if (genericTitleOffers != null) {
                return@map CategoryOffer(
                    id = genericTitleOffers.id(),
                    contentType = ContentType.normalizeOffer(genericTitleOffers.contentType()),
                    hasNextPage = genericTitleOffers.items()?.hasNextPage()
                        ?: false,
                    nextPage = genericTitleOffers.items()?.nextPage() ?: 2,
                    titleList = transformGenericTitleResourceToTitleVO(genericTitleOffers.items()?.resources())
                )
            } else {
                return@map CategoryOffer(
                    id = recommendedTitleOffers?.id(),
                    contentType = ContentType.normalizeOffer(recommendedTitleOffers?.contentType()),
                    hasNextPage = recommendedTitleOffers?.items()?.hasNextPage()
                        ?: false,
                    nextPage = recommendedTitleOffers?.items()?.nextPage() ?: 2,
                    experiment = recommendedTitleOffers?.items()?.abExperiment()?.experiment(),
                    alternative = recommendedTitleOffers?.items()?.abExperiment()?.alternative(),
                    trackId = recommendedTitleOffers?.items()?.abExperiment()?.trackId(),
                    titleList = transformRecommendedTitleOffersResourceToTitleVO(
                        recommendedTitleOffers?.items()?.resources()
                    )
                )
            }
        }
        .onExceptionResumeNext(Observable.empty())
        .onErrorResumeNext(Observable.empty())

    fun structure(
        id: String?,
        scale: String
    ): Observable<Triple<String?, CategoryDetailsStructureQuery.PremiumHighlight?, List<CategoryDetailsStructureQuery.OfferItem>>> =
        apolloClient
            .query(builderCategoryDetailsStructureQuery(id, scale))
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                val page = it.data()?.page()

                //Filtro o destaque premium para o conteúdo ser somente do tipo background
                val premiumHighlightFiltered =
                    when (page?.premiumHighlight()?.highlight()?.contentType()) {
                        HighlightContentType.BACKGROUND -> page.premiumHighlight()
                        else -> null
                    }

                //Filtro a lista de ofertas apenas para conter poster
                val offerItemsFiltered = page?.offerItems()?.filter { offerItem ->
                    when (offerItem.fragments().categoriesDetailsPageOfferFragment()
                        ?.componentType()) {
                        PageComponentType.POSTER -> true
                        else -> false
                    }
                } ?: arrayListOf()


                return@map Triple(page?.name(), premiumHighlightFiltered, offerItemsFiltered)
            }

    fun detailsOffer(
        offerItemList: List<CategoryDetailsStructureQuery.OfferItem>,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<List<CategoryOffer>> = Observable
        .merge(convertOfferItemToListObservableOfferItem(offerItemList.plus(offerItemList)))
        .subscribeOn(Schedulers.io())
        .flatMap { offerItem ->
            offerItem.fragments().categoriesDetailsPageOfferFragment().let {
                return@let detailsOfferTitle(
                    it?.offerId(),
                    it?.title(),
                    it?.componentType(),
                    it?.navigation(),
                    scale,
                    page,
                    perPage
                )
            }
        }
        .toList()
        .toObservable()
        .map { categoryOfferVOList ->
            val categoryOfferVOListOrdered = orderCategoryById(offerItemList, categoryOfferVOList)

            //Caso tenha somente uma única oferta, transformo os título dessa oferta para serem uma lista de oferta que
            //irão se comportar como grid visualmente.

            if (categoryOfferVOListOrdered.size == 1) {
                categoryOfferVOListOrdered.first().apply { componentType = ComponentType.GRID }
            }

            return@map categoryOfferVOListOrdered
        }

    fun detailsCategoryBySlug(
        slug: String? = null,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<CategoryDetails> = apolloClient
        .query(builderCategoryDetailBySlugQuery(slug, scale, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val subset = it.data()?.subset()
            val titles = subset?.titles()

            return@map CategoryDetails(
                headline = subset?.headline(),
                nextPage = titles?.nextPage() ?: 1,
                hasNextPage = titles?.hasNextPage() ?: false,
                titleVOList = transformResourceSlugToTitleVO(titles?.resources())
            )
        }

    fun detailsCategoryById(
        id: String? = null,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<CategoryDetails> = apolloClient
        .query(builderCategoryDetailByIdQuery(id, scale, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val subset = it.data()?.subset()
            val titles = subset?.titles()

            return@map CategoryDetails(
                headline = subset?.headline(),
                nextPage = titles?.nextPage() ?: 1,
                hasNextPage = titles?.hasNextPage() ?: false,
                titleVOList = transformResourceIdToTitleVO(titles?.resources())
            )
        }

    fun detailsGroup(
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        categoriesList: List<String>
    ): Observable<List<CategoryDetails>> = Observable
        .fromIterable(categoriesList)
        .subscribeOn(Schedulers.io())
        .flatMap { category ->
            detailsCategoryBySlug(category, scale, page, perPage)
        }
        .toList()
        .toObservable()
        .map {
            return@map it
        }
        .onExceptionResumeNext(Observable.empty())
        .onErrorResumeNext(Observable.empty())

    fun list(
        page: Int = 1,
        perPage: Int = 12
    ): Observable<Triple<Int, Boolean, List<Category>>> = apolloClient
        .query(builderCategoriesQuery(page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val categories = it.data()?.categories()

            val categoriesFiltered: List<Category> =
                transformResourceToCategoryVO(categories?.resources())

            return@map Triple(
                categories?.nextPage() ?: 1,
                categories?.hasNextPage() ?: false,
                categoriesFiltered
            )
        }

    internal fun orderCategoryById(
        offerItemList: List<CategoryDetailsStructureQuery.OfferItem>,
        categoryOfferVOList: List<CategoryOffer>
    ) = mutableListOf<CategoryOffer>().also { categoryOfferVOListOrdered ->
        offerItemList.forEach { offerItem ->
            val pageOfferFragment = offerItem.fragments().categoriesDetailsPageOfferFragment()

            categoryOfferVOList.firstOrNull {
                pageOfferFragment?.offerId() == it.id
                        && pageOfferFragment?.title()?.toLowerCase() == it.title?.toLowerCase()
            }?.apply {
                categoryOfferVOListOrdered.add(this)
            }
        }
    }

    internal fun transformResourceSlugToTitleVO(resourceList: List<CategoryDetailBySlugQuery.Resource>?) =
        resourceList?.map {
            Title(
                originProgramId = it.originProgramId(),
                titleId = it.titleId(),
                headline = it.headline(),
                description = it.description(),
                titleDetails = TitleDetails(contentRating = ContentRating(rating = it.contentRating())),
                poster = when (device) {
                    Device.TV -> it.poster()?.tv()
                    Device.TABLET -> it.poster()?.tablet()
                    else -> it.poster()?.mobile()
                },
                type = Type.normalize(it.type())
            )
        }

    internal fun transformResourceIdToTitleVO(resourceList: List<CategoryDetailByIdQuery.Resource>?) =
        resourceList?.map {
            Title(
                originProgramId = it.originProgramId(),
                titleId = it.titleId(),
                headline = it.headline(),
                poster = when (device) {
                    Device.TV -> it.poster()?.tv()
                    Device.TABLET -> it.poster()?.tablet()
                    else -> it.poster()?.mobile()
                },
                type = Type.normalize(it.type())
            )
        }

    internal fun transformGenericTitleResourceToTitleVO(
        resourceList: List<CategoryDetailsGenericTitleOffers.Resource>?
    ) = resourceList?.map { resource ->
        val titleOffer = resource.fragments().categoryDetailsTitleOffer()

        val poster = when (device) {
            Device.TV -> titleOffer?.poster()?.tv()
            Device.TABLET -> titleOffer?.poster()?.tablet()
            else -> titleOffer?.poster()?.mobile()
        }

        return@map Title(
            format = Format.normalize(titleOffer?.format()),
            originProgramId = titleOffer?.originProgramId(),
            titleId = titleOffer?.titleId(),
            headline = titleOffer?.headline(),
            description = titleOffer?.description(),
            type = Type.normalize(titleOffer?.type()),
            poster = poster,
            background = titleOffer?.cover()?.landscape(),
            abExperiment = AbExperiment(pathUrl = titleOffer?.url()),
            titleDetails = TitleDetails(
                contentRating = ContentRating(
                    titleOffer?.contentRating(),
                    titleOffer?.contentRatingCriteria()?.joinToString(SPLIT)?.capitalize()
                )
            )
        )
    }

    internal fun transformRecommendedTitleOffersResourceToTitleVO(resourceList: List<CategoryDetailsRecommendedTitleOffers.Resource>?) =
        resourceList?.map { resource ->
            val titleOffer = resource.fragments().categoryDetailsTitleOffer()

            val poster = when (device) {
                Device.TV -> titleOffer?.poster()?.tv()
                Device.TABLET -> titleOffer?.poster()?.tablet()
                else -> titleOffer?.poster()?.mobile()
            }

            return@map Title(
                originProgramId = titleOffer?.originProgramId(),
                titleId = titleOffer?.titleId(),
                headline = titleOffer?.headline(),
                description = titleOffer?.description(),
                type = Type.normalize(titleOffer?.type()),
                poster = poster,
                background = titleOffer?.cover()?.landscape(),
                abExperiment = AbExperiment(pathUrl = titleOffer?.url()),
                titleDetails = TitleDetails(
                    contentRating = ContentRating(
                        titleOffer?.contentRating(),
                        titleOffer?.contentRatingCriteria()?.joinToString(SPLIT)?.capitalize()
                    )
                )
            )
        }

    internal fun transformHighlightToHighlightVO(
        highlight: CategoryDetailsStructureQuery.Highlight?,
        callText: String?
    ) = highlight?.let {
        return@let Highlights(
            headlineText = highlight.headlineText(),
            title = highlight.headlineText(),
            callText = callText,
            contentType = ContentType.normalizeHighlight(highlight.contentType()),
            highlightImage = highlight.highlightImage()?.tablet(),
            offerImage = highlight.offerImage()?.mobile()
        )
    }

    internal fun convertOfferItemToListObservableOfferItem(offerItemList: List<CategoryDetailsStructureQuery.OfferItem>?) =
        offerItemList?.map { Observable.defer { Observable.just(it) } }
            ?: arrayListOf()

    internal fun builderCategoryDetailsStructureQuery(id: String?, scale: String) =
        builderImageCategoryDetailsStructure(
            CategoryDetailsStructureQuery
                .builder(), scale
        )
            .id(id ?: "")
            .filter(
                PageMetadataFilter
                    .builder()
                    .type(PageType.CATEGORIES)
                    .build()
            )
            .build()

    internal fun builderCategoryDetailByIdQuery(
        id: String?,
        scale: String,
        page: Int,
        perPage: Int
    ) =
        builderImageTitle(
            CategoryDetailByIdQuery
                .builder(), scale
        )
            .id(id ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderCategoryDetailBySlugQuery(
        slug: String?,
        scale: String,
        page: Int,
        perPage: Int
    ) =
        builderImageTitle(
            CategoryDetailBySlugQuery
                .builder(), scale
        )
            .slug(slug ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderImageTitle(
        categoryDetailBySlugQuery: CategoryDetailBySlugQuery.Builder,
        scale: String
    ) =
        when (device) {
            Device.TV -> categoryDetailBySlugQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))

            Device.TABLET -> categoryDetailBySlugQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    scale
                )
            )

            else -> categoryDetailBySlugQuery.mobilePosterScales(
                MobilePosterScales.safeValueOf(
                    scale
                )
            )
        }

    internal fun builderImageTitle(
        categoryDetailByIdQuery: CategoryDetailByIdQuery.Builder,
        scale: String
    ) =
        when (device) {
            Device.TV -> categoryDetailByIdQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))

            Device.TABLET -> categoryDetailByIdQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    scale
                )
            )

            else -> categoryDetailByIdQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }

    internal fun builderImageCategoryDetailsStructure(
        categoryDetailsStructureQuery: CategoryDetailsStructureQuery.Builder,
        scale: String
    ): CategoryDetailsStructureQuery.Builder =
        when (device) {
            Device.TABLET -> categoryDetailsStructureQuery.highlightImageTabletScales(
                HighlightImageTabletScales.safeValueOf(scale)
            )

            else -> categoryDetailsStructureQuery.highlightOfferImageMobileScales(
                HighlightOfferImageMobileScales.safeValueOf(scale)
            )
        }

    internal fun builderCategoryDetailsOfferTitleQuery(
        id: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ) =
        builderImageCategoryDetailsOfferTitle(
            CategoryDetailsOfferTitleQuery
                .builder(), scale
        )
            .id(id ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderImageCategoryDetailsOfferTitle(
        categoryDetailsOfferTitleQuery: CategoryDetailsOfferTitleQuery.Builder,
        scale: String
    ): CategoryDetailsOfferTitleQuery.Builder =
        when (device) {
            Device.TV -> {
                categoryDetailsOfferTitleQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))
                categoryDetailsOfferTitleQuery.tvCoverLandscapeScales(CoverLandscapeScales.X1080)
            }

            Device.TABLET -> categoryDetailsOfferTitleQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    scale
                )
            )

            else -> categoryDetailsOfferTitleQuery.mobilePosterScales(
                MobilePosterScales.safeValueOf(
                    scale
                )
            )
        }

    internal fun builderCategoriesQuery(page: Int, perPage: Int) = CategoriesQuery
        .builder()
        .page(page)
        .perPage(perPage)
        .build()

    internal fun transformResourceToCategoryVO(resourceList: List<CategoriesQuery.Resource>?) =
        resourceList
            ?.map { resource ->
                val categories = resource.fragments().categoriesFragment()
                val navigationByPage = categories.navigation()?.fragments()?.categoriesPage()
                val navigationBySlug = categories.navigation()?.fragments()?.categoriesSlug()

                return@map Category(
                    Navigation.extractSlug(
                        navigationByPage?.identifier(),
                        navigationBySlug?.slug()
                    ),
                    categories.name(),
                    categories.background(),
                    Destination.normalize(navigationByPage?.identifier(), navigationBySlug?.slug())
                )
            }
            ?.filter { it.destination != Destination.UNKNOWN && it.id != null }
            ?: arrayListOf()
}
