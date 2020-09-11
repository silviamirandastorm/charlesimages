package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.model.*
import com.globo.jarvis.type.*
import com.globo.jarvis.user.AddMyListMutation
import com.globo.jarvis.user.DeleteMyListMutation
import com.globo.jarvis.user.LastWatchedVideosQuery
import com.globo.jarvis.user.MyListQuery
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device

) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun lastVideos(
        glbId: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 6,
        userCallback: Callback.User
    ) {
        compositeDisposable.add(
            lastVideos(glbId, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        userCallback.onMyLastedWatchedVideosSuccess(it)
                    },
                    { throwable ->
                        userCallback.onFailure(throwable)
                    })
        )
    }

    fun myList(scale: String, page: Int, perPage: Int, userCallback: Callback.User) {
        compositeDisposable.add(myList(scale, page, perPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {
                    userCallback.onMyListSuccess(it)
                },
                { throwable ->
                    userCallback.onFailure(throwable)
                })
        )
    }

    fun addToMyList(titleId: String?, userCallback: Callback.User) {
        compositeDisposable.add(addToMyList(titleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {
                    userCallback.onAddMyListSuccess(it)
                },
                { throwable ->
                    userCallback.onFailure(throwable)
                })
        )
    }

    fun deleteMyList(titleId: String?, userCallback: Callback.User) {
        compositeDisposable.add(deleteMyList(titleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {
                    userCallback.onDeleteMyListSuccess(it)
                },
                { throwable ->
                    userCallback.onFailure(throwable)
                })
        )
    }

    //RxJava
    fun lastVideos(
        glbId: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 6
    ): Observable<Triple<Int?, Boolean, List<ContinueWatching>>> =
        if (!glbId.isNullOrEmpty()) apolloClient
            .query(builderOfferContinueWatchingQuery(page, perPage, scale))
            .rx()
            .subscribeOn(Schedulers.io())
            .filter {
                return@filter it.data()?.user()?.myLastWatchedVideos()?.resources()
                    ?.isNotEmpty() == true
            }
            .map {
                val nextPage = it.data()?.user()?.myLastWatchedVideos()?.nextPage()
                val hasNextPage = it.data()?.user()?.myLastWatchedVideos()?.hasNextPage() ?: false

                return@map Triple(
                    nextPage,
                    hasNextPage,
                    transformLastWatchedVideosToContinueWatchingVO(
                        it.data()?.user()?.myLastWatchedVideos()?.resources()
                    )
                )
            }
            .onExceptionResumeNext(Observable.defer {
                Observable.just(
                    Triple(
                        null,
                        false,
                        ArrayList<ContinueWatching>()
                    )
                )
            })
            .onErrorResumeNext(Observable.defer {
                Observable.just(
                    Triple(
                        null,
                        false,
                        ArrayList<ContinueWatching>()
                    )
                )
            })
        else Observable.defer {
            Observable.just(
                Triple(
                    null,
                    false,
                    ArrayList<ContinueWatching>()
                )
            )
        }

    fun myList(scale: String, page: Int = 1, perPage: Int = 12): Observable<MyList> = apolloClient
        .query(builderMyListQuery(page, perPage, scale))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val myListTitles = it.data()?.user()?.myListTitles()

            return@map MyList(
                myListTitles?.nextPage() ?: 1,
                myListTitles?.hasNextPage() ?: false,
                transformResourceResponseToTitleMyListVO(myListTitles?.resources())
            )
        }

    fun addToMyList(titleId: String?): Observable<Boolean> = apolloClient
        .mutate(builderAddMyListQuery(titleId))
        .rx()
        .map {
            it.data()?.addTitleToMyList()
        }

    fun deleteMyList(titleId: String?): Observable<Boolean> = apolloClient
        .mutate(builderDeleteMyListQuery(titleId))
        .rx()
        .map {
            it.data()?.deleteTitleFromMyList()
        }

    internal fun builderAddMyListQuery(titleId: String?) =
        AddMyListMutation
            .builder()
            .input(
                MyListInput
                    .builder()
                    .titleId(titleId)
                    .build()
            )
            .build()


    internal fun builderDeleteMyListQuery(titleId: String?) =
        DeleteMyListMutation
            .builder()
            .input(
                MyListInput
                    .builder()
                    .titleId(titleId)
                    .build()
            )
            .build()


    internal fun builderMyListQuery(page: Int, perPage: Int, scale: String) =
        builderImageTitle(MyListQuery.builder(), scale)
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderImageTitle(myListQuery: MyListQuery.Builder, scale: String) =
        when (device) {
            Device.TV -> myListQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))

            Device.TABLET -> myListQuery.tabletPosterScales(TabletPosterScales.safeValueOf(scale))

            else -> myListQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }

    internal fun transformResourceResponseToTitleMyListVO(resourcesResponseList: List<MyListQuery.Resource>?) =
        resourcesResponseList?.map {
            return@map Title(
                originProgramId = it.originProgramId(),
                titleId = it.titleId(),
                headline = it.headline(),
                poster = when (device) {
                    Device.TABLET -> it.poster()?.tablet()
                    Device.TV -> it.poster()?.tv()
                    else -> it.poster()?.mobile()
                },
                type = Type.normalize(it.type())
            )
        } ?: arrayListOf()

    internal fun transformLastWatchedVideosToContinueWatchingVO(resourceList: List<LastWatchedVideosQuery.Resource>?) =
        resourceList?.map { videoOffer ->
            val normalizedType = Type.normalize(videoOffer.title().type())


            val logo = when(device) {
                Device.TV -> videoOffer.title().logo()?.tv()
                Device.TABLET -> videoOffer.title().logo()?.tablet()
                else -> videoOffer.title().logo()?.mobile()
            }

            return@map ContinueWatching(
                id = videoOffer.id(),
                headline = videoOffer.title().headline(),
                description = videoOffer.headline(),
                duration = videoOffer.duration() ?: 0,
                watchedProgress = videoOffer.watchedProgress() ?: 0,
                formattedDuration = videoOffer.formattedDuration(),
                thumb = videoOffer.thumb(),
                rating = videoOffer.contentRating(),
                availableFor = AvailableFor.normalize(videoOffer.availableFor()),
                abExperiment = AbExperiment(pathUrl = videoOffer.title().url()),
                kind = Kind.normalize(videoOffer.kind()),
                title = Title(
                    originProgramId = videoOffer.title().originProgramId(),
                    titleId = videoOffer.title().titleId(),
                    type = normalizedType,
                    logo = logo
                )
            )
        } ?: arrayListOf()

    //Offer Continue Watching Query
    internal fun builderOfferContinueWatchingQuery(page: Int, perPage: Int, scale: String) =
        builderImageOfferContinueWatchingQuery(LastWatchedVideosQuery.builder(), scale)
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderImageOfferContinueWatchingQuery(
        continueWatchingQuery: LastWatchedVideosQuery.Builder,
        scale: String
    ) =
        when (device) {
            Device.TV -> continueWatchingQuery.logoTVScales(TVLogoScales.safeValueOf(scale))

            Device.TABLET -> continueWatchingQuery.tabletLogoScales(
                TabletLogoScales.safeValueOf(
                    scale
                )
            )

            else -> continueWatchingQuery.mobileLogoScales(MobileLogoScales.safeValueOf(scale))
        }
}
