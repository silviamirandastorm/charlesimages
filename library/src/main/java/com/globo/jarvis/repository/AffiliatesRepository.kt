package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.affiliates.AffiliateProgramsQuery
import com.globo.jarvis.affiliates.AffiliateRegionsQuery
import com.globo.jarvis.affiliates.AffiliateStatesQuery
import com.globo.jarvis.model.*
import com.globo.jarvis.type.CoverLandscapeScales
import com.globo.jarvis.type.MobilePosterScales
import com.globo.jarvis.type.TVPosterScales
import com.globo.jarvis.type.TabletPosterScales
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AffiliatesRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun states(stateCallback: Callback.State) {
        compositeDisposable.add(
            states()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        stateCallback.onStatesSuccess(it)
                    },
                    { throwable ->
                        stateCallback.onFailure(throwable)
                    }
                ))
    }

    fun regions(regionsCallback: Callback.Region) {
        compositeDisposable.add(
            regions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        regionsCallback.onRegionsSuccess(it)
                    },
                    { throwable ->
                        regionsCallback.onFailure(throwable)
                    }
                ))
    }

    fun regionsByState(acronym: String, regionsCallback: Callback.Region) {
        compositeDisposable.add(
            regionsByState(acronym)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        regionsCallback.onRegionsSuccess(it)
                    },
                    { throwable ->
                        regionsCallback.onFailure(throwable)
                    }
                ))
    }

    fun programs(
        regionSlug: String?,
        posterScale: String,
        coverLandscapeScales: String,
        page: Int = 1,
        perPage: Int = 12,
        affiliateProgramsCallback: Callback.AffiliatePrograms
    ) {
        compositeDisposable.add(
            programs(regionSlug, posterScale, coverLandscapeScales, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        affiliateProgramsCallback.onAffiliateProgramsSuccess(it)
                    },
                    { throwable ->
                        affiliateProgramsCallback.onFailure(throwable)
                    }
                ))
    }

    //RxJava
    fun states(): Observable<List<States>> =
        apolloClient.query(AffiliateStatesQuery.builder().build())
            .rx()
            .map {
                return@map transformStatesQueryAffiliateStateToStates(
                    it.data()?.affiliate()?.affiliateStates()
                )
            }

    fun regions(): Observable<List<Regions>> =
        apolloClient.query(AffiliateRegionsQuery.builder().build())
            .rx()
            .map {
                return@map transformRegionsQueryAffiliateStateToRegions(
                    it.data()?.affiliate()?.affiliateStates()
                )
            }

    fun regionsByState(acronym: String): Observable<List<Regions>> =
        apolloClient.query(AffiliateRegionsQuery.builder().build())
            .rx()
            .map { response ->
                return@map transformRegionsQueryAffiliateStateToRegions(
                    response.data()?.affiliate()?.affiliateStates()
                        ?.filter { it.acronym() == acronym })
            }

    fun programs(
        regionSlug: String?,
        posterScale: String,
        coverLandscapeScales: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<Triple<Boolean, Int?, List<Title>>> =
        apolloClient.query(
            buildAffiliateProgramsQuery(
                regionSlug,
                posterScale,
                coverLandscapeScales,
                page,
                perPage
            )
        )
            .rx()
            .map {
                val titles = it.data()?.affiliate()?.affiliateRegion()?.titles()
                val hasNextPage = titles?.hasNextPage() ?: false
                val nextPage = titles?.nextPage()
                val titleList = transformAffiliateProgramsQueryResourceToStates(titles?.resources())

                return@map Triple(hasNextPage, nextPage, titleList)
            }

    internal fun transformRegionsQueryAffiliateStateToRegions(affiliateStates: List<AffiliateRegionsQuery.AffiliateState>?) =
        affiliateStates?.flatMap { affiliateState ->
            affiliateState.regions()?.map {
                Regions(it.affiliateName(), it.name(), it.slug())
            } ?: emptyList()
        } ?: emptyList()

    internal fun transformStatesQueryAffiliateStateToStates(affiliateStates: List<AffiliateStatesQuery.AffiliateState>?) =
        affiliateStates?.map {
            States(it.name(), it.acronym())
        } ?: emptyList()

    internal fun transformAffiliateProgramsQueryResourceToStates(affiliateStates: List<AffiliateProgramsQuery.Resource>?) =
        affiliateStates?.map {
            Title(
                titleId = it.titleId(),
                headline = it.headline(),
                description = it.description(),
                cover = it.cover()?.landscape(),
                titleDetails = TitleDetails(
                    contentRating = ContentRating(
                        it.contentRating(),
                        it.contentRatingCriteria()?.joinToString(TitleRepository.SPLIT)
                            ?.capitalize()
                    )
                ),
                poster = when (device) {
                    Device.TV -> it.poster()?.tv()
                    Device.TABLET -> it.poster()?.tablet()
                    else -> it.poster()?.mobile()
                }
            )
        } ?: emptyList()

    internal fun buildAffiliateProgramsQuery(
        regionSlug: String?,
        posterScale: String,
        coverLandscapeScales: String,
        page: Int,
        perPage: Int
    ) = builderImageAffiliatePrograms(
        AffiliateProgramsQuery.builder(),
        posterScale,
        coverLandscapeScales
    )
        .regionSlug(regionSlug ?: "")
        .page(page)
        .perPage(perPage)
        .build()

    internal fun builderImageAffiliatePrograms(
        affiliateProgramsQuery: AffiliateProgramsQuery.Builder,
        posterScale: String,
        coverLandscapeScales: String
    ) = when (device) {
        Device.TV -> {
            affiliateProgramsQuery.tvCoverLandscapeScales(
                CoverLandscapeScales.safeValueOf(
                    coverLandscapeScales
                )
            )
            affiliateProgramsQuery.tvPosterScales(TVPosterScales.safeValueOf(posterScale))
        }

        Device.TABLET -> affiliateProgramsQuery.tabletPosterScales(
            TabletPosterScales.safeValueOf(
                posterScale
            )
        )

        else -> affiliateProgramsQuery.mobilePosterScales(MobilePosterScales.safeValueOf(posterScale))
    }
}
