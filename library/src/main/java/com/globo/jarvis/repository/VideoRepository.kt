package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.model.*
import com.globo.jarvis.type.MobilePosterScales
import com.globo.jarvis.type.TabletPosterScales
import com.globo.jarvis.video.NextVideoQuery
import com.globo.jarvis.video.VideoQuery
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VideoRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun details(
        videoId: String?,
        posterScale: String,
        videosCallback: Callback.Videos
    ) {
        compositeDisposable.add(
            details(videoId, posterScale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        videosCallback.onDetailsSuccess(it)
                    },
                    { throwable ->
                        videosCallback.onFailure(throwable)
                    }
                ))
    }

    fun next(
        videoId: String?,
        videosCallback: Callback.Videos
    ) {
        compositeDisposable.add(
            next(videoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        videosCallback.onNextVideoSuccess(it)
                    },
                    { throwable ->
                        videosCallback.onFailure(throwable)
                    }
                ))
    }

    //RxJava
    fun details(videoId: String?, posterScale: String): Observable<Video> = apolloClient
        .query(builderVideoQuery(videoId, posterScale))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            return@map transformVideoQueryToVideoVO(it.data()?.video())
        }

    fun next(
        videoId: String?
    ): Observable<Pair<Boolean, NextVideo>> = apolloClient
        .query(buildNextVideoQuery(videoId))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val hasNextVideo = it.data()?.video()?.nextVideo() != null
            val videoVO = transformNextVideoQueryToVideoVO(it.data()?.video())

            return@map Pair(hasNextVideo, videoVO)
        }

    internal fun builderVideoQuery(videoId: String?, posterScale: String) =
        builderVideoImage(VideoQuery.builder(), posterScale)
            .videoId(videoId ?: "")
            .build()

    internal fun buildNextVideoQuery(videoId: String?) = NextVideoQuery.builder()
        .videoId(videoId ?: "")
        .build()

    internal fun builderVideoImage(videoQuery: VideoQuery.Builder, posterScale: String) =
        when (device) {
            Device.TABLET -> videoQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    posterScale
                )
            )

            else -> videoQuery.mobilePosterScales(MobilePosterScales.safeValueOf(posterScale))
        }

    internal fun transformVideoQueryToVideoVO(videoQueryData: VideoQuery.Video?): Video {
        val normalizedKind = Kind.normalize(videoQueryData?.kind())

        return Video(
            id = videoQueryData?.id(),
            availableFor = AvailableFor.normalize(videoQueryData?.availableFor()),
            accessibleOffline = videoQueryData?.accessibleOffline() ?: false,
            headline = videoQueryData?.headline(),
            description = videoQueryData?.description(),
            formattedDuration = videoQueryData?.formattedDuration(),
            duration = videoQueryData?.duration() ?: 0,
            fullyWatchedThreshold = videoQueryData?.fullyWatchedThreshold(),
            kind = normalizedKind,
            thumb = when (normalizedKind) {
                Kind.LIVE -> videoQueryData?.liveThumbnail()
                else -> videoQueryData?.thumb()
            },
            serviceId = videoQueryData?.serviceId() ?: 0,
            contentRating = ContentRating(rating = videoQueryData?.contentRating()),
            title = Title(
                titleId = videoQueryData?.title()?.titleId(),
                genders = transformListName(videoQueryData?.title()?.genresNames()),
                originProgramId = videoQueryData?.title()?.originProgramId(),
                headline = videoQueryData?.title()?.headline(),
                type = Type.normalize(videoQueryData?.title()?.type()),
                poster = when (device) {
                    Device.TABLET -> videoQueryData?.title()?.poster()?.tablet()
                    else -> videoQueryData?.title()?.poster()?.mobile()
                },
                abExperiment = AbExperiment(pathUrl = videoQueryData?.title()?.url()),
                format = Format.normalize(videoQueryData?.title()?.format())
            ),
            subscriptionService = SubscriptionService(
                name = videoQueryData?.subscriptionService()?.name(),
                salesPage = SalesPage(
                    identifier = PageUrl(
                        mobile = videoQueryData?.subscriptionService()?.salesPage()?.identifier()
                            ?.mobile()
                    )
                ),
                faq = SubscriptionServiceFaq(
                    qrCode = PageUrl(
                        mobile = videoQueryData?.subscriptionService()?.faq()?.qrCode()?.mobile()
                    ),
                    url = PageUrl(
                        mobile = videoQueryData?.subscriptionService()?.faq()?.url()?.mobile()
                    )
                )
            )
        )
    }

    internal fun transformListName(nameResponseList: List<String>?) =
        nameResponseList?.joinToString(TitleRepository.SPLIT)

    internal fun transformNextVideoQueryToVideoVO(nextVideoQuery: NextVideoQuery.Video?): NextVideo {
        val normalizedKind = Kind.normalize(nextVideoQuery?.nextVideo()?.kind())

        return NextVideo(
            id = nextVideoQuery?.nextVideo()?.id(),
            headline = nextVideoQuery?.nextVideo()?.headline(),
            duration = nextVideoQuery?.nextVideo()?.duration() ?: 0,
            fullyWatchedThreshold = nextVideoQuery?.nextVideo()?.fullyWatchedThreshold(),
            availableFor = AvailableFor.normalize(nextVideoQuery?.nextVideo()?.availableFor()),
            accessibleOffline = nextVideoQuery?.nextVideo()?.accessibleOffline() ?: false,
            contentRating = ContentRating(rating = nextVideoQuery?.nextVideo()?.contentRating()),
            thumb = when (normalizedKind) {
                Kind.LIVE -> nextVideoQuery?.nextVideo()?.liveThumbnail()
                else -> nextVideoQuery?.nextVideo()?.thumb()
            },
            kind = normalizedKind,
            serviceId = nextVideoQuery?.nextVideo()?.serviceId() ?: 0,
            title = Title(
                titleId = nextVideoQuery?.nextVideo()?.title()?.titleId(),
                abExperiment = AbExperiment(pathUrl = nextVideoQuery?.nextVideo()?.title()?.url()),
                genders = transformListName(nextVideoQuery?.nextVideo()?.title()?.genresNames()),
                originProgramId = nextVideoQuery?.nextVideo()?.title()?.originProgramId(),
                type = Type.normalize(nextVideoQuery?.nextVideo()?.title()?.type()),
                format = Format.normalize(nextVideoQuery?.nextVideo()?.title()?.format())
            ),
            subscriptionService = SubscriptionService(
                name = nextVideoQuery?.nextVideo()?.subscriptionService()?.name(),
                salesPage = SalesPage(
                    identifier = PageUrl(
                        mobile = nextVideoQuery?.nextVideo()?.subscriptionService()?.salesPage()
                            ?.identifier()
                            ?.mobile()
                    )
                ),
                faq = SubscriptionServiceFaq(
                    qrCode = PageUrl(
                        mobile = nextVideoQuery?.nextVideo()?.subscriptionService()?.faq()?.qrCode()
                            ?.mobile()
                    ),
                    url = PageUrl(
                        mobile = nextVideoQuery?.nextVideo()?.subscriptionService()?.faq()?.url()
                            ?.mobile()
                    )
                )
            )
        )
    }
}