package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.fragment.SeasonedScenesStructure
import com.globo.jarvis.model.*
import com.globo.jarvis.title.scenes.ExcerptsQuery
import com.globo.jarvis.title.scenes.ScenesStructureQuery
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

data class ScenesRepository constructor(
    private val apolloClient: ApolloClient,
    private val seasonRepository: SeasonRepository
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun withThumbs(
        videoId: String?,
        number: Int,
        headline: String?,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int,
        excerptsCallback: Callback.Excerpts
    ) {
        compositeDisposable.add(
            withThumbs(videoId, number, headline, page, perPage, thumbSmall, thumbLarge)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        excerptsCallback.onScenesSuccess(it)
                    },
                    { throwable ->
                        excerptsCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun withSeason(
        titleId: String?,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int,
        scenesCallback: Callback.Scenes
    ) {
        compositeDisposable.add(
            withSeason(titleId, page, perPage, thumbSmall, thumbLarge)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        scenesCallback.onScenesWithSeasonSuccess(it)
                    },
                    { throwable ->
                        scenesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun withoutSeasonDetails(
        titleId: String?,
        page: Int,
        perPage: Int,
        scenesCallback: Callback.Scenes
    ) {
        compositeDisposable.add(
            withoutSeasonDetails(titleId, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        scenesCallback.onScenesWithoutSeasonDetailsSuccess(it)
                    },
                    { throwable ->
                        scenesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun withoutDetails(
        titleId: String?,
        seasonId: String?,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int,
        scenesCallback: Callback.Scenes
    ) {
        compositeDisposable.add(
            withoutDetails(titleId, seasonId, page, perPage, thumbSmall, thumbLarge)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        scenesCallback.onScenesWithoutDetailsSuccess(it)
                    },
                    { throwable ->
                        scenesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun structure(
        titleId: String?,
        seasonId: String?,
        page: Int,
        perPage: Int,
        scenesCallback: Callback.Scenes
    ) {
        compositeDisposable.add(
            structure(titleId, seasonId, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        scenesCallback.onStructureSuccess(it)
                    },
                    { throwable ->
                        scenesCallback.onFailure(throwable)
                    }
                )
        )
    }

    fun detailsScenes(
        scenesPreviewList: List<ScenesPreview>,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int,
        scenesCallback: Callback.Scenes
    ) {
        compositeDisposable.add(
            detailsScenes(scenesPreviewList, page, perPage, thumbSmall, thumbLarge)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        scenesCallback.onScenesDetailsSuccess(it)
                    },
                    { throwable ->
                        scenesCallback.onFailure(throwable)
                    }
                )
        )
    }

    //RxJava
    fun withThumbs(
        videoId: String?,
        number: Int,
        headline: String?,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int
    ): Observable<Scene> =
        apolloClient.query(
            builderGetScenesVideoQuery(
                videoId,
                page,
                perPage,
                thumbSmall,
                thumbLarge
            )
        )
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            return@map Scene(
                videoId,
                number,
                headline,
                convertResourceResponseListToThumbList(it.data()?.video()?.relatedExcerpts()?.resources())
            )
        }

    fun withSeason(
        titleId: String?,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int
    ): Observable<Pair<List<Season>, Triple<Boolean, Int, List<Scene>>>> =
        seasonRepository
            .byScenes(titleId)
            .flatMap(
                { pairSeason: Pair<Season?, List<Season>> ->
                    return@flatMap structure(titleId, pairSeason.first?.id, page, perPage)
                },
                { pairSeason: Pair<Season?, List<Season>>,
                  tripleStructure: Triple<Boolean, Int, List<ScenesPreview>> ->
                    Pair(pairSeason, tripleStructure)
                }
            )
            .flatMap(
                { pair: Pair<Pair<Season?, List<Season>>,
                        Triple<Boolean, Int, List<ScenesPreview>>> ->
                    val tripleStructure = pair.second
                    return@flatMap detailsScenes(
                        tripleStructure.third,
                        page,
                        perPage,
                        thumbSmall,
                        thumbLarge
                    )
                },
                { pair: Pair<Pair<Season?, List<Season>>, Triple<Boolean, Int,
                        List<ScenesPreview>>>, sceneList: List<Scene> ->
                    val pairSeason = pair.first
                    val tripleStructure = pair.second

                    Pair(
                        pairSeason.second,
                        Triple(tripleStructure.first, tripleStructure.second, sceneList)
                    )
                }
            )

    fun withoutSeasonDetails(
        titleId: String?,
        page: Int,
        perPage: Int
    ): Observable<Pair<Pair<Season?, List<Season>>,
            Triple<Boolean, Int, List<ScenesPreview>>>> =
        seasonRepository
            .byScenes(titleId)
            .flatMap(
                { pair: Pair<Season?, List<Season>> ->
                    return@flatMap structure(titleId, pair.first?.id, page, perPage)
                },
                { pairSeason: Pair<Season?, List<Season>>, tripleStructure: Triple<Boolean, Int, List<ScenesPreview>> ->
                    Pair(
                        pairSeason,
                        Triple(
                            tripleStructure.first,
                            tripleStructure.second,
                            tripleStructure.third
                        )
                    )
                }
            )

    fun withoutDetails(
        titleId: String?,
        seasonId: String?,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int
    ): Observable<Triple<Boolean, Int, List<ScenesPreview>>> =
        structure(titleId, seasonId, page, perPage)
            .flatMap(
                { tripleStructure: Triple<Boolean, Int, List<ScenesPreview>> ->
                    return@flatMap detailsScenes(
                        tripleStructure.third,
                        page,
                        perPage,
                        thumbSmall,
                        thumbLarge
                    )
                },
                { tripleStructure: Triple<Boolean, Int, List<ScenesPreview>>, _: List<Scene> ->
                    Triple(
                        tripleStructure.first,
                        tripleStructure.second,
                        tripleStructure.third
                    )
                }
            )

    fun structure(
        titleId: String?,
        seasonId: String?,
        page: Int,
        perPage: Int
    ): Observable<Triple<Boolean, Int, List<ScenesPreview>>> =
        apolloClient.query(
            builderGetScenesStructureQuery(
                titleId,
                seasonId,
                page,
                perPage
            )
        )
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val episodesWithExcerpts = it.data()?.title()?.structure()?.fragments()
                ?.seasonedScenesStructure()?.seasonById()?.episodesWithExcerpts

            return@map Triple(
                episodesWithExcerpts?.hasNextPage() ?: false,
                episodesWithExcerpts?.nextPage() ?: 1,
                convertResourceResponseListToScenesPreviewList(episodesWithExcerpts?.resources()) ?: ArrayList()
            )
        }

    fun detailsScenes(
        listScenesPreview: List<ScenesPreview>,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int
    ): Observable<List<Scene>> =
        Observable
            .merge(convertScenePreviewToListObservableScenePreview(listScenesPreview))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap { scenesPreview ->
                return@flatMap withThumbs(
                    scenesPreview.id,
                    scenesPreview.number ?: 1,
                    scenesPreview.headline,
                    page,
                    perPage,
                    thumbSmall,
                    thumbLarge
                )
            }
            .toList()
            .toObservable()
            .map { scenesList ->
                val scenesListOrdered = mutableListOf<Scene>()

                listScenesPreview.forEach { scenesPreview ->
                    scenesList.firstOrNull { scenesPreview.id == it.id }?.apply {
                        scenesListOrdered.add(this)
                    }
                }

                return@map scenesListOrdered
            }

    internal fun convertResourceResponseListToThumbList(resources: List<ExcerptsQuery.Resource>?) =
        resources?.map { resource ->
            return@map Thumb(
                id = resource.id(),
                title = Title(headline = resource.title().headline()),
                headline = resource.headline(),
                formattedDuration = resource.formattedDuration(),
                kind = Kind.normalize(resource.kind()),
                thumb = resource.thumbSmall(),
                thumbLarge = resource.thumbLarge()
            )
        } ?: arrayListOf()

    internal fun convertScenePreviewToListObservableScenePreview(resources: List<ScenesPreview>?) =
        resources?.map { Observable.defer { Observable.just(it) } }
            ?: arrayListOf()

    internal fun convertResourceResponseListToScenesPreviewList(resources: List<SeasonedScenesStructure.Resource>?) =
        resources?.map { resource ->
            return@map ScenesPreview(
                total = resource.excerpts()?.total() ?: 1,
                number = resource.number() ?: 1,
                headline = resource.video().headline(),
                id = resource.video().id(),
                thumb = resource.video().thumb()
            )
        } ?: arrayListOf()

    internal fun builderGetScenesStructureQuery(
        titleId: String?,
        seasonId: String?,
        page: Int,
        perPage: Int
    ) =
        ScenesStructureQuery
            .builder()
            .titleId(titleId ?: "")
            .seasonId(seasonId ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderGetScenesVideoQuery(
        videoId: String?,
        page: Int,
        perPage: Int,
        thumbSmall: Int,
        thumbLarge: Int
    ) =
        ExcerptsQuery
            .builder()
            .videoId(videoId ?: "")
            .page(page)
            .perPage(perPage)
            .thumbSmall(thumbSmall)
            .thumbLarge(thumbLarge)
            .build()
}
