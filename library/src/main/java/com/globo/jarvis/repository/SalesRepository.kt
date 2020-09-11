package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.*
import com.globo.jarvis.model.*
import com.globo.jarvis.model.SalesFaq
import com.globo.jarvis.model.SalesPaymentInfo
import com.globo.jarvis.model.SalesProduct
import com.globo.jarvis.sales.*
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SalesRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun landingPage(
        salesId: String,
        titleId: String? = null,
        scale: String,
        coverScale: String,
        page: Int,
        perPage: Int,
        salesCallback: Callback.Sales
    ) {
        compositeDisposable.add(
            landingPage(
                salesId,
                titleId,
                scale,
                coverScale,
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
                        salesCallback.onLandingPageSuccess(it)
                    },
                    { throwable ->
                        salesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun structure(
        salesId: String,
        titleId: String?,
        scale: String,
        coverScale: String,
        salesCallback: Callback.Sales
    ) {
        compositeDisposable.add(
            structure(
                salesId,
                titleId,
                scale,
                coverScale
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        salesCallback.onStructureSuccess(it)
                    },
                    { throwable ->
                        salesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun recommendation(
        serviceId: String?,
        trimmedLogoScale: String,
        salesCallback: Callback.Sales
    ) {
        compositeDisposable.add(
            recommendation(
                serviceId,
                trimmedLogoScale
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        salesCallback.onRecommendationSuccess(it)
                    },
                    { throwable ->
                        salesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun defaultRecommendation(
        serviceId: String?,
        trimmedLogoScale: String,
        salesCallback: Callback.Sales
    ) {
        compositeDisposable.add(
            defaultRecommendation(
                serviceId,
                trimmedLogoScale
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        salesCallback.onRecommendationSuccess(it)
                    },
                    { throwable ->
                        salesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun detailsOffer(
        offerItemList: List<SalesQuery.OfferItem>,
        page: Int,
        perPage: Int,
        scale: String,
        salesCallback: Callback.Sales
    ) {
        compositeDisposable.add(
            detailsOffer(
                offerItemList,
                page,
                perPage,
                scale
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        salesCallback.onDetailsOfferSuccess(it)
                    },
                    { throwable ->
                        salesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun detailsOfferHighlights(
        highlightId: String?,
        headlineText: String?,
        callText: String?,
        leftAligned: Boolean,
        scale: String,
        pageComponentType: PageComponentType?,
        salesCallback: Callback.Sales
    ) {
        compositeDisposable.add(
            detailsOfferHighlights(
                highlightId,
                headlineText,
                callText,
                leftAligned,
                scale,
                pageComponentType
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        salesCallback.onDetailsOfferHighlightsSuccess(it)
                    },
                    { throwable ->
                        salesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun detailsGenericOffer(
        offerId: String?,
        title: String?,
        pageComponentType: PageComponentType?,
        scale: String,
        page: Int,
        perPage: Int,
        salesCallback: Callback.Sales
    ) {
        compositeDisposable.add(
            detailsGenericOffer(
                offerId,
                title,
                pageComponentType,
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
                        salesCallback.onDetailsGenericOfferSuccess(it)
                    },
                    { throwable ->
                        salesCallback.onFailure(throwable)
                    }
                )
        )
    }


    //RxJava
    fun landingPage(
        salesId: String,
        titleId: String? = null,
        scale: String,
        coverScale: String,
        page: Int,
        perPage: Int
    ): Observable<Triple<Offer, List<Offer>, String?>> =
        structure(salesId, titleId, scale, coverScale)
            .flatMap(
                { (offers, _, _) ->
                    return@flatMap if (offers.second?.isNullOrEmpty() == false) {
                        detailsOffer(
                            offers.second as List<SalesQuery.OfferItem>,
                            page,
                            perPage,
                            scale
                        )
                    } else {
                        Observable.defer { Observable.just(listOf<Offer>()) }
                    }
                },
                { (offers, subscriptionServiceId, title), offerVOList: List<Offer> ->
                    Triple(Pair(offers.first, offerVOList), subscriptionServiceId, title)
                }
            )
            .map { (offers, subscriptionServiceId, title) ->

                val premiumHighlight = offers.first
                val offerVOList = offers.second

                val offerVOPremiumHighlights = Offer(
                    componentType = ComponentType.normalize(
                        premiumHighlight?.componentType()
                            ?: PageComponentType.PREMIUMHIGHLIGHT
                    ),
                    contentType = ContentType.normalizeHighlight(
                        premiumHighlight?.highlight()?.contentType()
                    ),
                    highlights = transformPremiumHighlightToHighlights(
                        premiumHighlight,
                        title
                    )
                )

                return@map Triple(offerVOPremiumHighlights, offerVOList, subscriptionServiceId)
            }

    internal fun structure(
        salesId: String,
        titleId: String?,
        scale: String,
        coverScale: String
    ): Observable<Triple<Pair<SalesQuery.PremiumHighlight?, List<SalesQuery.OfferItem>?>, String?,
            SalesQuery.Title?>> = apolloClient
        .query(builderLadingPageQuery(salesId, titleId, scale, coverScale))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val page = it.data()?.page()
                ?: throw Exception(it.errors().firstOrNull()?.message())

            val title = it.data()?.title()

            val premiumHighlight = it.data()?.page()?.premiumHighlight()

            val subscriptionServiceId = it.data()?.page()?.subscriptionServiceId()

            val offerItemsFiltered = page.offerItems()?.filterNotNull()?.filter { offerItem ->
                val componentType = offerItem.fragments().pageSalesOfferFragment()?.componentType()
                    ?: offerItem.fragments().pageSalesHighlightFragment()?.componentType()

                when (componentType) {
                    PageComponentType.POSTER -> true
                    PageComponentType.OFFERHIGHLIGHT -> true
                    else -> false
                }
            } ?: arrayListOf()

            Triple(Pair(premiumHighlight, offerItemsFiltered), subscriptionServiceId, title)
        }

    internal fun detailsOffer(
        offerItemList: List<SalesQuery.OfferItem>,
        page: Int,
        perPage: Int,
        scale: String
    ): Observable<List<Offer>> = Observable
        .merge(
            convertLadingPageQueryOfferItemListObservableLadingPageQueryOfferItemList(
                offerItemList
            )
        )
        .subscribeOn(Schedulers.io())
        .concatMap { offerItem ->
            val pageHighlightFragment = offerItem.fragments().pageSalesHighlightFragment()
            val pageOfferFragment = offerItem.fragments().pageSalesOfferFragment()

            return@concatMap if (pageHighlightFragment != null)
                detailsOfferHighlights(
                    pageHighlightFragment.highlightId(),
                    pageHighlightFragment.headline(),
                    pageHighlightFragment.callText(),
                    pageHighlightFragment.leftAligned() ?: false,
                    scale,
                    pageHighlightFragment.componentType()
                )
            else detailsGenericOffer(
                pageOfferFragment?.offerId(),
                pageOfferFragment?.title(),
                pageOfferFragment?.componentType(),
                scale,
                page,
                perPage
            )
        }
        .toList()
        .toObservable()

    internal fun detailsOfferHighlights(
        highlightId: String?, headlineText: String?, callText: String?,
        leftAligned: Boolean, scale: String, pageComponentType: PageComponentType?
    ): Observable<Offer> = apolloClient
        .query(builderLadingPageHighlightOfferQuery(highlightId, scale))
        .rx()
        .subscribeOn(Schedulers.io())
        .filter {
            it.data()?.highlight()?.contentType()?.rawValue()?.toUpperCase() ==
                    HighlightContentType.PROMOTIONAL.rawValue()
        }
        .map {
            val highlight = it.data()?.highlight()

            return@map Offer(
                headlineText = headlineText,
                callText = callText,
                componentType = ComponentType.normalize(
                    pageComponentType,
                    leftAligned,
                    device == Device.TABLET
                ),
                contentType = ContentType.normalizeHighlight(highlight?.contentType()),
                highlights = transformHighlightToHighlightVO(highlight)
            )
        }
        .onExceptionResumeNext(Observable.empty())
        .onErrorResumeNext(Observable.empty())

    internal fun detailsGenericOffer(
        offerId: String?,
        title: String?,
        pageComponentType: PageComponentType?,
        scale: String,
        page: Int,
        perPage: Int
    ): Observable<Offer> = apolloClient
        .query(builderLadingPageGenericOfferQuery(offerId, scale, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .filter {
            val salesGenericTitleOffers =
                it.data()?.genericOffer()?.fragments()?.salesGenericTitleOffers()

            return@filter salesGenericTitleOffers?.contentType() == OfferContentType.TITLE
                    && salesGenericTitleOffers.items()?.resources()?.isNotEmpty() == true
        }
        .map {
            val salesGenericTitleOffers =
                it.data()?.genericOffer()?.fragments()?.salesGenericTitleOffers()

            return@map Offer(
                title = title,
                titleList = transformResourceToTitleVO(
                    salesGenericTitleOffers?.items()?.resources()
                ),
                componentType = ComponentType.normalize(pageComponentType),
                contentType = ContentType.normalizeOffer(salesGenericTitleOffers?.contentType())
            )
        }
        .onExceptionResumeNext(Observable.empty())
        .onErrorResumeNext(Observable.empty())

    internal fun transformHighlightToHighlightVO(
        highlight: SalesHighlightsOfferQuery.Highlight?
    ): Highlights {
        val highlightOfferImageVO = when (device) {
            Device.TABLET -> highlight?.offerImage()?.tablet()
            else -> highlight?.offerImage()?.mobile()
        }

        val highlightLogoVO = when (device) {
            Device.TABLET -> highlight?.logo()?.tablet()
            else -> highlight?.logo()?.mobile()
        }

        return Highlights(offerImage = highlightOfferImageVO, logo = highlightLogoVO)
    }

    internal fun transformPremiumHighlightToHighlights(
        premiumHighlight: SalesQuery.PremiumHighlight?,
        title: SalesQuery.Title? = null
    ): Highlights {
        val premiumHighlightImageVO = title?.let {
            when (device) {
                Device.TABLET -> title.cover()?.landscape()
                else -> title.cover()?.portrait()
            }
        } ?: when (device) {
            Device.TABLET -> premiumHighlight?.highlight()?.highlightImage()?.tablet()
            else -> premiumHighlight?.highlight()?.highlightImage()?.mobile()
        }

        val premiumHighlightLogoVO = when (device) {
            Device.TABLET -> premiumHighlight?.highlight()?.logo()?.tablet()
            else -> premiumHighlight?.highlight()?.logo()?.mobile()
        }

        return Highlights(
            callText = premiumHighlight?.callText(),
            headlineText = premiumHighlight?.highlight()?.headlineText(),
            buttonText = premiumHighlight?.buttonText(),
            highlightImage = premiumHighlightImageVO,
            logo = premiumHighlightLogoVO
        )
    }

    internal fun convertLadingPageQueryOfferItemListObservableLadingPageQueryOfferItemList(
        getLadingPageQueryOfferItem: List<SalesQuery.OfferItem>?
    ) =
        getLadingPageQueryOfferItem?.map {
            Observable.defer { Observable.just(it) }
        } ?: arrayListOf()

    internal fun transformResourceToTitleVO(resourceList: List<SalesGenericTitleOffers.Resource>?) =
        resourceList?.map {
            val salesTitleOffer = it.fragments().salesTitleOffer()

            Title(
                headline = salesTitleOffer?.headline(),
                poster = when (device) {
                    Device.TABLET -> salesTitleOffer?.poster()?.tablet()
                    else -> salesTitleOffer?.poster()?.mobile()
                }
            )
        } ?: arrayListOf()

    internal fun builderLadingPageQuery(
        salesId: String,
        titleId: String?,
        scale: String,
        coverScale: String
    ) =
        builderImageLadingPage(SalesQuery.builder(), scale, coverScale)
            .id(salesId)
            .filter(PageMetadataFilter.builder().type(PageType.SALES).build())
            .titleId(titleId ?: "")
            .build()

    internal fun builderLadingPageGenericOfferQuery(
        offerId: String?,
        scale: String,
        page: Int,
        perPage: Int
    ) =
        builderImageLadingPageGenericOffer(
            SalesGenericOfferQuery.builder(), scale)
            .id(offerId ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderLadingPageHighlightOfferQuery(highlightId: String?, scale: String) =
        builderImageLadingPageHighlightOffer(
            SalesHighlightsOfferQuery.builder(), scale)
            .id(highlightId ?: "")
            .build()

    internal fun builderImageLadingPageGenericOffer(
        salesGenericOfferQuery: SalesGenericOfferQuery.Builder,
        scale: String
    ): SalesGenericOfferQuery.Builder =
        when (device) {
            Device.TABLET -> salesGenericOfferQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(scale)
            )
            else -> salesGenericOfferQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }

    internal fun builderImageLadingPageHighlightOffer(
        SalesHighlightsOfferQuery: SalesHighlightsOfferQuery.Builder,
        scale: String
    ): SalesHighlightsOfferQuery.Builder =
        when (device) {
            Device.TABLET -> SalesHighlightsOfferQuery
                .highlightOfferImageTabletScales(HighlightOfferImageTabletScales.safeValueOf(scale))
                .highlightLogoTabletScales(HighlightLogoTabletScales.safeValueOf(scale))

            else -> SalesHighlightsOfferQuery
                .highlightOfferImageMobileScales(HighlightOfferImageMobileScales.safeValueOf(scale))
                .highlightLogoMobileScales(HighlightLogoMobileScales.safeValueOf(scale))
        }

    internal fun builderImageLadingPage(
        SalesQuery: SalesQuery.Builder,
        scale: String,
        coverScale: String
    ): SalesQuery.Builder =
        when (device) {
            Device.TABLET -> SalesQuery
                .highlightLogoTabletScales(HighlightLogoTabletScales.safeValueOf(scale))
                .highlightImageTabletScales(HighlightImageTabletScales.safeValueOf(scale))
                .coverLandscapeScales(CoverLandscapeScales.safeValueOf(coverScale))

            else -> SalesQuery
                .highlightLogoMobileScales(HighlightLogoMobileScales.safeValueOf(scale))
                .highlightImageMobileScales(HighlightImageMobileScales.safeValueOf(scale))
                .coverPortraitScales(CoverPortraitScales.safeValueOf(coverScale))
        }

    /* Sales Recommendation */

    fun recommendation(
        serviceId: String?,
        broadcastChannelTrimmedLogoScale: String,
        salesPlatform: SalesPlatform = SalesPlatform.GOOGLE
    ): Observable<SalesRecommendation> = apolloClient
        .query(
            builderSalesRecommendationQuery(
                serviceId,
                salesPlatform,
                broadcastChannelTrimmedLogoScale
            )
        )
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            transformSalesRecommendationQueryIntoSalesRecommendation(
                it.data()?.salesRecommendation()
            )
        }

    internal fun builderSalesRecommendationQuery(
        serviceId: String?,
        platform: SalesPlatform,
        broadcastChannelTrimmedLogoScale: String
    ) = SalesRecommendationQuery.builder()
        .serviceId(serviceId)
        .platform(platform)
        .trimmedLogoScale(
            BroadcastChannelTrimmedLogoScales.safeValueOf(
                broadcastChannelTrimmedLogoScale
            )
        )
        .build()

    internal fun transformSalesRecommendationQueryIntoSalesRecommendation(
        salesRecommendationQuery: SalesRecommendationQuery.SalesRecommendation?
    ) = salesRecommendationQuery?.let { salesRecommendation ->
        SalesRecommendation(
            userError = transformUserErrorFragmentToSalesUserError(
                salesRecommendation.salesUserError()?.fragments()?.salesUserError()),
            recommendedProducts = transformSalesRecommendedProductsQueryToSalesRecommendedProducts(
                salesRecommendation.recommendedProducts()
            )
        )
    }

    internal fun transformSalesRecommendedProductsQueryToSalesRecommendedProducts(
        recommendedProductsQuery: List<SalesRecommendationQuery.RecommendedProduct>?
    ) = recommendedProductsQuery?.map { recommendedProduct ->
        val productError = recommendedProduct.productError()?.fragments()?.salesProductError()
        val userConditions = recommendedProduct.userConditions().fragments().salesUserCondition()
        val product = recommendedProduct.product().fragments().salesProduct()
        val paymentInfo = product.paymentInfo().fragments().salesPaymentInfo()
        val legalText = product.legalText()?.fragments()?.salesProductLegalText()
        val productFaq = product.faq()?.fragments()?.salesFaq()
        SalesProduct(
            name = product.name(),
            productId = product.productId(),
            productError = transformProductErrorFragmentToSalesError(productError),
            offerText = product.offerText(),
            paymentInfo = transformDefaultPaymentInfoFragmentToPaymentInfo(paymentInfo),
            benefits = product.benefits(),
            productFaq = transformSalesFaqFragmentToSalesSalesFaq(productFaq),
            channels = transformBroadcastChannelFragmentToBroadcastChannels(product.channels()),
            androidSku = product.androidSku(),
            userConditions = transformUserConditionsFragmentToUserConditions(userConditions),
            legalText = transformSalesProductLegalTextFragmentSalesLegalText(legalText)
        )
    }

    /* Sales Recommendation Default */

    fun defaultRecommendation(
        serviceId: String?,
        broadcastChannelTrimmedLogoScale: String,
        salesPlatform: SalesPlatform = SalesPlatform.GOOGLE
    ): Observable<SalesRecommendation> = apolloClient
        .query(
            builderDefaultSalesRecommendationQuery(
                serviceId = serviceId,
                platform = salesPlatform,
                broadcastChannelTrimmedLogoScale = broadcastChannelTrimmedLogoScale
            )
        )
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            transformDefaultSalesRecommendationQueryIntoSalesRecommendation(
                it.data()?.defaultSalesRecommendation()
            )
        }

    internal fun builderDefaultSalesRecommendationQuery(
        serviceId: String?,
        platform: SalesPlatform,
        broadcastChannelTrimmedLogoScale: String
    ) = DefaultSalesRecommendationQuery.builder()
        .serviceId(serviceId)
        .platform(platform)
        .trimmedLogoScale(
            BroadcastChannelTrimmedLogoScales.safeValueOf(
                broadcastChannelTrimmedLogoScale
            )
        )
        .build()

    internal fun transformDefaultSalesRecommendationQueryIntoSalesRecommendation(
        recommendationDefaultQuery: DefaultSalesRecommendationQuery.DefaultSalesRecommendation?
    ) = recommendationDefaultQuery?.let { defaultRecommendation ->
        SalesRecommendation(
            userError = transformUserErrorFragmentToSalesUserError(
                defaultRecommendation.salesUserError()?.fragments()?.salesUserError()
            ),
            recommendedProducts = transformDefaultRecommendedProductsQueryToSalesRecommendedProducts(
                defaultRecommendation.recommendedProducts()
            )
        )
    }

    internal fun transformDefaultRecommendedProductsQueryToSalesRecommendedProducts(
        recommendedDefaultProductsQuery: List<DefaultSalesRecommendationQuery.RecommendedProduct>?
    ) = recommendedDefaultProductsQuery?.map { defaultRecommendedProduct ->
        val productError =
            defaultRecommendedProduct.productError()?.fragments()?.salesProductError()
        val userConditions =
            defaultRecommendedProduct.userConditions().fragments().salesUserCondition()
        val product = defaultRecommendedProduct.product().fragments().salesProduct()
        val paymentInfo = product.paymentInfo().fragments().salesPaymentInfo()
        val legalText = product.legalText()?.fragments()?.salesProductLegalText()
        val productFaq = product.faq()?.fragments()?.salesFaq()
        SalesProduct(
            name = product.name(),
            productId = product.productId(),
            productError = transformProductErrorFragmentToSalesError(productError),
            offerText = product.offerText(),
            paymentInfo = transformDefaultPaymentInfoFragmentToPaymentInfo(paymentInfo),
            benefits = product.benefits(),
            productFaq = transformSalesFaqFragmentToSalesSalesFaq(productFaq),
            channels = transformBroadcastChannelFragmentToBroadcastChannels(product.channels()),
            androidSku = product.androidSku(),
            userConditions = transformUserConditionsFragmentToUserConditions(userConditions),
            legalText = transformSalesProductLegalTextFragmentSalesLegalText(legalText)
        )
    }

    /* Sales recommendation transforms */

    internal fun transformDefaultPaymentInfoFragmentToPaymentInfo(
        paymentInfoFragment: com.globo.jarvis.fragment.SalesPaymentInfo?
    ) = paymentInfoFragment?.let { paymentInfo ->
        val frequency = paymentInfo.frequency().fragments().salesPaymentFrequency()
        val installments = paymentInfo.installmentInfo()?.fragments()?.salesInstallmentInfo()
        SalesPaymentInfo(
            callText = paymentInfo.callText(),
            currency = paymentInfo.currency(),
            price = paymentInfo.price(),
            frequency = transformSalesFrequencyFragmentToSalesFrequency(frequency),
            numberOfInstallments = installments?.numberOfInstallments(),
            installmentValue = installments?.installmentValue()
        )
    }

    internal fun transformSalesFrequencyFragmentToSalesFrequency(
        salesPaymentFrequency: SalesPaymentFrequency?
    ) = salesPaymentFrequency?.let {
        SalesFrequency(
            id = it.id(),
            periodicityLabel = it.periodicityLabel(),
            timeUnitLabel = it.timeUnitLabel()
        )
    }

    internal fun transformBroadcastChannelFragmentToBroadcastChannels(
        salesChannelsFragmentList: List<com.globo.jarvis.fragment.SalesProduct.Channel>?
    ) = salesChannelsFragmentList?.map { salesChannel ->
        val channel = salesChannel.fragments().salesChannels()
        Broadcast(
            name = channel.name(),
            trimmedLogo = channel.trimmedLogo()
        )
    }

    internal fun transformSalesProductLegalTextFragmentSalesLegalText(
        legalTextFragment: SalesProductLegalText?
    ) = legalTextFragment?.let { salesLegalText ->
        SalesLegalText(
            legalContent = salesLegalText.legalContent(),
            contractUrl = salesLegalText.contractUrl(),
            contractsUrlText = salesLegalText.contractsUrlText()
        )
    }

    internal fun transformUserConditionsFragmentToUserConditions(
        userConditionsFragment: SalesUserCondition?
    ) = userConditionsFragment?.let { userCondition ->
        SalesUserConditions(
            action = userCondition.action(),
            trialPeriod = userCondition.eligibleTrialPeriod()
        )
    }

    internal fun transformProductErrorFragmentToSalesError(
        salesProductErrorFragment: SalesProductError?
    ) = salesProductErrorFragment?.let { error ->
        val channelError = error.metadata()?.fragments()?.changeChannelError()
        val salesFaq = error.faq()?.fragments()?.salesFaq()
        SalesError(
            type = error.type(),
            message = error.message(),
            faq = transformSalesFaqFragmentToSalesSalesFaq(salesFaq),
            sourceChannel = channelError?.sourceChannel()?.let {
                SalesPlatformType.normalize(it)
            }
        )
    }

    internal fun transformUserErrorFragmentToSalesUserError(
        salesUserErrorFragment: SalesUserError?
    ) = salesUserErrorFragment?.let { userError ->
        val salesFaq = userError.faq()?.fragments()?.salesFaq()
        SalesError(
            type = userError.type(),
            message = userError.message(),
            faq = transformSalesFaqFragmentToSalesSalesFaq(salesFaq)
        )
    }

    internal fun transformSalesFaqFragmentToSalesSalesFaq(
        salesFaqFragment: com.globo.jarvis.fragment.SalesFaq?
    ) = salesFaqFragment?.let { salesFaq ->
        SalesFaq(
            text = salesFaq.text(),
            link = salesFaq.links().mobile()
        )
    }
}