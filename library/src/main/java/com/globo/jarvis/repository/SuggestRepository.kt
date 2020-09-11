package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.Device.TABLET
import com.globo.jarvis.Device.TV
import com.globo.jarvis.fragment.RecommendedTitle
import com.globo.jarvis.fragment.SuggestPlayNextTitle
import com.globo.jarvis.model.*
import com.globo.jarvis.recommendation.RecommendationTitlesQuery
import com.globo.jarvis.recommendation.SuggestForTitleQuery
import com.globo.jarvis.recommendation.SuggestPlayNextByOfferQuery
import com.globo.jarvis.title.recommendation.SuggestTitlesByOfferQuery
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SuggestRepository(
    private val apolloClient: ApolloClient,
    private val device: Device
) {

    private val compositeDisposable by lazy { CompositeDisposable() }

    // =============================================================================================
    // Functions with response by callback.
    // =============================================================================================

    @JvmOverloads
    fun titlesTopHits(
        rules: TitleRules = TitleRules.TOP_HITS,
        scale: String,
        coverScale: String,
        perPage: Int = 12,
        recommendationCallback: Callback.Recommendations
    ) {
        compositeDisposable.add(
            titlesTopHits(rules, scale, coverScale, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        recommendationCallback.onRecommendedChannels(it)
                    },
                    { throwable ->
                        recommendationCallback.onFailure(throwable)
                    }
                ))
    }

    @JvmOverloads
    fun titleSuggestByOfferId(
        titleId: String?,
        offerId: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        titleSuggestionCallback: Callback.TitlesSuggestion
    ) {
        compositeDisposable.add(
            titleSuggestByOfferId(titleId, offerId, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        titleSuggestionCallback.onAllSuccess(it)
                    },

                    { throwable ->
                        titleSuggestionCallback.onFailure(throwable)
                    }
                ))
    }

    fun nextTitle(
        titleId: String?,
        format: TitleFormat?,
        group: SuggestGroups?,
        coverScale: String,
        page: Int = 1,
        perPage: Int = 1,
        recommendationCallback: Callback.Recommendations
    ) {
        compositeDisposable.add(
            nextTitle(titleId, format, group, coverScale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        recommendationCallback.onRecommendedTitle(it)
                    },
                    { throwable ->
                        recommendationCallback.onFailure(throwable)
                    }
                ))
    }

    fun nextTitle(
        offerId: String?,
        titleId: String?,
        coverScale: String,
        page: Int = 1,
        perPage: Int = 1,
        recommendationCallback: Callback.Recommendations
    ) {
        compositeDisposable.add(
            nextTitle(offerId, titleId, coverScale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        recommendationCallback.onRecommendedTitle(it)
                    },
                    { throwable ->
                        recommendationCallback.onFailure(throwable)
                    }
                ))
    }

    // =============================================================================================
    // Functions with response by RxJava.
    // =============================================================================================

    @JvmOverloads
    fun titlesTopHits(
        rules: TitleRules = TitleRules.TOP_HITS,
        scale: String,
        coverScale: String,
        perPage: Int = 12
    ): Observable<List<Recommendation>> = Rx2Apollo
        .from(apolloClient.query(builderRecommendationQuery(rules, scale, coverScale, perPage)))
        .subscribeOn(Schedulers.io())
        .map {
            return@map transformRecommendationQueryToRecommendationVO(
                it.data()?.titles()?.resources()
            )
        }

    @JvmOverloads
    fun titleSuggestByOfferId(
        titleId: String?,
        offerId: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<Pair<List<RecommendedTitleOffer>, AbExperiment?>> =
        apolloClient
            .query(builderSuggestByOfferIdQuery(titleId, offerId, page, perPage, scale))
            .rx()
            .subscribeOn(Schedulers.io())
            .map { response ->
                val responseFragments = response.data()?.genericOffer()?.fragments()

                val titleOfferList =
                    responseFragments?.titlesFromOffer()?.paginatedItems()?.resources()
                        ?.map { title ->
                            transformToRecommendedTitleOffer(title.fragments().recommendedTitle())
                        }

                val titleRecommendedOfferList =
                    responseFragments?.titlesFromRecommendedOffer()?.items()?.resources()
                        ?.map { title ->
                            transformToRecommendedTitleOffer(title.fragments().recommendedTitle())
                        }

                val jarvisABExperiment =
                    response.data()?.genericOffer()?.fragments()?.titlesFromRecommendedOffer()
                        ?.items()?.abExperiment()

                val abExperiment = jarvisABExperiment?.let { ab ->
                    AbExperiment(
                        experiment = ab.experiment(),
                        alternative = ab.alternative(),
                        trackId = ab.trackId()
                    )
                }

                val titleSuggestionOffers = when {
                    !titleRecommendedOfferList.isNullOrEmpty() -> titleRecommendedOfferList
                    !titleOfferList.isNullOrEmpty() -> titleOfferList
                    else -> emptyList()
                }

                return@map Pair(titleSuggestionOffers, abExperiment)
            }

    fun nextTitle(
        titleId: String?,
        format: TitleFormat?,
        group: SuggestGroups?,
        coverScale: String,
        page: Int = 1,
        perPage: Int = 1
    ): Observable<Pair<AbExperiment?, Recommendation>> = apolloClient
        .query(builderOfferSuggestQuery(titleId, format, group))
        .rx()
        .subscribeOn(Schedulers.io())
        .flatMap(
            { response ->
                val bestFitSuggest = response.data()?.user()?.suggest()?.bestFit()

                val offerId = bestFitSuggest?.resource()?.fragments()
                    ?.suggestTitleOfferFragment()?.id()

                val recommendedOfferId = bestFitSuggest?.resource()?.fragments()
                    ?.suggestTitleRecommendedOfferFragment()?.id()

                nextTitle(offerId ?: recommendedOfferId, titleId, coverScale, page, perPage)
            },
            { response, nextRecommendation ->
                val jarvisABExperiment =
                    response.data()?.user()?.suggest()?.bestFit()?.abExperiment()

                val abExperiment = jarvisABExperiment?.let { ab ->
                    AbExperiment(
                        experiment = ab.experiment(),
                        alternative = ab.alternative(),
                        trackId = ab.trackId()
                    )
                }

                return@flatMap Pair(abExperiment, nextRecommendation)
            })

    fun nextTitle(
        offerId: String?,
        titleId: String?,
        coverScale: String,
        page: Int = 1,
        perPage: Int = 1
    ): Observable<Recommendation> = apolloClient
        .query(builderNextTitleQuery(offerId, titleId, coverScale, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val genericOfferFragment = it.data()?.genericOffer()?.fragments()

            val offerTitle =
                genericOfferFragment?.suggestPlayNextTitlesFromOffer()
                    ?.paginatedItems()
                    ?.resources()?.firstOrNull()?.fragments()?.suggestPlayNextTitle()

            val recommendedOfferTitle =
                genericOfferFragment?.suggestPlayNextTitlesFromRecommendedOffer()
                    ?.items()
                    ?.resources()?.firstOrNull()?.fragments()?.suggestPlayNextTitle()

            val jarvisABExperiment =
                genericOfferFragment?.suggestPlayNextTitlesFromRecommendedOffer()
                    ?.items()?.abExperiment()

            val abExperiment = jarvisABExperiment?.let { ab ->
                AbExperiment(
                    experiment = ab.experiment(),
                    alternative = ab.alternative(),
                    trackId = ab.trackId(),
                    pathUrl = offerTitle?.url() ?: recommendedOfferTitle?.url()
                )
            }

            return@map transformNextSuggestedTitleFragmentToRecommendationVO(
                offerTitle ?: recommendedOfferTitle, abExperiment
            )
        }

    // =============================================================================================
    // Transform Functions.
    // =============================================================================================

    internal fun transformToRecommendedTitleOffer(recommendedTitle: RecommendedTitle?): RecommendedTitleOffer {
        return RecommendedTitleOffer(
            titleId = recommendedTitle?.titleId(),
            headline = recommendedTitle?.headline(),
            poster = when (device) {
                TV -> recommendedTitle?.poster()?.tv()
                TABLET -> recommendedTitle?.poster()?.tablet()
                else -> recommendedTitle?.poster()?.mobile()
            }
        )
    }

    internal fun transformRecommendationQueryToRecommendationVO(resources: List<RecommendationTitlesQuery.Resource>?) =
        resources?.map {
            Recommendation(
                titleId = it.titleId(),
                programId = it.originProgramId(),
                headline = it.headline(),
                description = it.description(),
                logo = it.logo()?.tv(),
                cover = it.cover()?.landscape(),
                format = Format.normalize(it.format()),
                abExperiment = AbExperiment(pathUrl = it.url()),
                type = Type.normalize(it.type())
            )
        } ?: arrayListOf()

    fun transformNextSuggestedTitleFragmentToRecommendationVO(
        titleFragment: SuggestPlayNextTitle?,
        abExperiment: AbExperiment?
    ): Recommendation {
        val seasonedStructureVideoId: String? =
            titleFragment?.structure()?.fragments()?.seasonedStructureFragment()
                ?.defaultEpisode()?.video()?.id()
        val episodeListStructureVideoId: String? = titleFragment?.structure()?.fragments()
            ?.episodeListStructureFragment()?.defaultEpisode()?.video()?.id()
        return Recommendation(
            titleId = titleFragment?.titleId(),
            programId = titleFragment?.originVideoId(),
            headline = titleFragment?.headline(),
            description = titleFragment?.description(),
            cover = when (device) {
                TV -> titleFragment?.cover()?.tv()
                TABLET -> titleFragment?.cover()?.tablet()
                else -> titleFragment?.cover()?.mobile()
            },
            abExperiment = abExperiment,
            videoId = seasonedStructureVideoId ?: episodeListStructureVideoId,
            type = Type.normalize(titleFragment?.type()),
            format = Format.normalize(titleFragment?.format())
        )
    }

    // =============================================================================================
    // Builder Apollo Query Functions.
    // =============================================================================================

    internal fun builderRecommendationTitles(
        recommendationTitlesQuery: RecommendationTitlesQuery.Builder,
        scale: String,
        coverScale: String
    ) =
        recommendationTitlesQuery.apply {
            tvLogoScales(TVLogoScales.safeValueOf(scale))
            tvCoverLandscapeScales(CoverLandscapeScales.safeValueOf(coverScale))
        }

    internal fun builderRecommendationQuery(
        rules: TitleRules,
        scale: String,
        coverScale: String,
        perPage: Int
    ) =
        builderRecommendationTitles(
            RecommendationTitlesQuery.builder().rule(rules),
            scale,
            coverScale
        )
            .perPage(perPage)
            .build()

    internal fun builderSuggestByOfferIdQuery(
        titleId: String?,
        offerId: String?,
        page: Int,
        perPage: Int,
        scale: String
    ) =
        builderImageSuggestByOfferIdQuery(SuggestTitlesByOfferQuery.builder(), scale)
            .titleId(titleId ?: "")
            .offerId(offerId ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderImageSuggestByOfferIdQuery(
        suggestByOfferIdQuery: SuggestTitlesByOfferQuery.Builder,
        scale: String
    ) =
        when (device) {
            TV -> suggestByOfferIdQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))

            TABLET -> suggestByOfferIdQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(scale)
            )

            else -> suggestByOfferIdQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }

    internal fun builderOfferSuggestQuery(
        titleId: String?,
        titleFormat: TitleFormat?,
        group: SuggestGroups?
    ) = SuggestForTitleQuery.builder()
        .titleId(titleId)
        .group(group)
        .format(titleFormat)
        .build()

    internal fun builderNextTitleQuery(
        offerId: String?,
        titleId: String?,
        scale: String,
        page: Int,
        perPage: Int
    ) = builderImageNextTitle(SuggestPlayNextByOfferQuery.builder(), scale)
        .offerId(offerId ?: "")
        .titleId(titleId ?: "")
        .page(page)
        .perPage(perPage)
        .build()

    internal fun builderImageNextTitle(
        suggestPlayNextByOfferQuery: SuggestPlayNextByOfferQuery.Builder,
        scale: String
    ) =
        when (device) {
            TABLET -> suggestPlayNextByOfferQuery.tabletCoverScales(
                CoverLandscapeScales.safeValueOf(
                    scale
                )
            )

            TV -> suggestPlayNextByOfferQuery.tvCoverScales(
                CoverWideScales.safeValueOf(
                    scale
                )
            )

            else -> suggestPlayNextByOfferQuery.mobileCoverScales(CoverLandscapeScales.safeValueOf(scale))
        }
}