package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.common.JARVIS_PATTERN_YYYY_MM_DD
import com.globo.jarvis.common.formatByPattern
import com.globo.jarvis.fragment.EpisodesList
import com.globo.jarvis.fragment.EpisodesWithRelatedExcerptsList
import com.globo.jarvis.model.*
import com.globo.jarvis.title.episode.EpisodeQuery
import com.globo.jarvis.title.episode.EpisodesQuery
import com.globo.jarvis.title.episode.EpisodesWithRelatedExcerptsByDateQuery
import com.globo.jarvis.type.MobilePosterScales
import com.globo.jarvis.type.TabletPosterScales
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class EpisodeRepository constructor(
    private val apolloClient: ApolloClient,
    private val seasonRepository: SeasonRepository,
    private val device: Device
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun detail(
        videoId: String?,
        scale: String,
        episodesCallback: Callback.Episodes
    ) {
        compositeDisposable.add(
            detail(videoId, scale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        episodesCallback.onDetailSuccess(it)
                    },
                    { throwable ->
                        episodesCallback.onFailure(throwable)
                    }
                ))
    }

    //Callback
    fun detailsWithSeason(
        titleId: String?,
        page: Int,
        perPage: Int,
        episodesCallback: Callback.Episodes
    ) {
        compositeDisposable.add(
            detailsWithSeason(titleId, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        episodesCallback.onDetailsWithSeasonSuccess(it)
                    },
                    { throwable ->
                        episodesCallback.onFailure(throwable)
                    }
                ))
    }

    //Callback
    fun detailsWithRelatedExcerptsByDate(
        titleId: String?,
        startDate: Date,
        endDate: Date,
        calendarCallback: Callback.Dates
    ) {
        compositeDisposable.add(
            detailsWithRelatedExcerptsByDate(titleId, startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        calendarCallback.onEpisodeAndRelatedExcerptsByDateSuccess(it)
                    },
                    { throwable ->
                        calendarCallback.onFailure(throwable)
                    }
                ))
    }


    //RxJava
    fun detailsWithSeason(
        titleId: String?,
        page: Int,
        perPage: Int
    ): Observable<Triple<String, List<Season>, EpisodeDetails>> =
        seasonRepository
            .byEpisode(titleId)
            .flatMap(
                {
                    return@flatMap details(
                        titleId,
                        it.first,
                        page,
                        perPage
                    )
                },
                { pair: Pair<String, List<Season>>, episodeDetails: EpisodeDetails ->
                    Triple(pair.first, pair.second, episodeDetails)
                }
            )

    //RxJava
    fun details(
        titleId: String?,
        seasonId: String?,
        page: Int,
        perPage: Int
    ): Observable<EpisodeDetails> = apolloClient
        .query(builderGetEpisodesQuery(titleId, seasonId, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            it.data()
            val episodes = it.data()?.title()?.structure()?.fragments()
                ?.episodesList()?.seasonById()?.episodes()

            return@map EpisodeDetails(
                hasNextPage = episodes?.hasNextPage() ?: false,
                nextPage = episodes?.nextPage() ?: 1,
                episodeList = transformEpisodesListResourceToEpisodeList(episodes?.resources()),
                originProgramId = it.data()?.title()?.originProgramId()
            )
        }


    // RxJava
    fun detail(videoId: String?, scale: String): Observable<Episode> = apolloClient
        .query(builderDownloadQuery(videoId, scale))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            return@map transformEpisodeQueryToEpisode(it.data()?.episode())
        }

    fun detailsWithRelatedExcerptsByDate(
        titleId: String?,
        startDate: Date,
        endDate: Date
    ): Observable<Pair<Video, List<Thumb>>> = apolloClient
        .query(
            builderEpisodeAndRelatedExcerptsByDateQuery(titleId, startDate, endDate)
        )
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val episodeRelatedExcerptsList =
                it.data()?.title()?.structure()?.fragments()?.episodesWithRelatedExcerptsList()

            val video =
                episodeRelatedExcerptsList?.episodes()?.resources()?.firstOrNull()?.video()
            val videos = episodeRelatedExcerptsList?.videos()?.resources()

            return@map Pair(
                transformEpisodeToCalendarVideo(video),
                transformExcerptsToThumb(videos)
            )
        }

    internal fun transformEpisodeToCalendarVideo(
        video: EpisodesWithRelatedExcerptsList.Video?
    ): Video =
        video.let {
            Video(
                id = it?.id(),
                formattedDuration = it?.formattedDuration(),
                accessibleOffline = it?.accessibleOffline() ?: false,
                description = it?.description(),
                duration = it?.duration() ?: 0,
                availableFor = AvailableFor.normalize(it?.availableFor()),
                headline = it?.headline(),
                thumb = it?.thumb(),
                title = Title(
                    titleId = it?.title()?.titleId(),
                    originProgramId = it?.title()?.originProgramId()
                ),
                serviceId = it?.serviceId() ?: 0
            )
        }

    internal fun builderDownloadQuery(videoId: String?, scale: String) =
        builderDownloadImage(EpisodeQuery.builder(), scale)
            .videoId(videoId ?: "")
            .build()

    internal fun builderEpisodeAndRelatedExcerptsByDateQuery(
        titleId: String?,
        startDate: Date,
        endDate: Date,
        perPage: Int = 1
    ) =
        EpisodesWithRelatedExcerptsByDateQuery
            .builder()
            .titleId(titleId ?: "")
            .startDate(startDate.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD))
            .endDate(endDate.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD))
            .perPage(perPage)
            .build()

    internal fun builderGetEpisodesQuery(
        titleId: String?,
        seasonId: String?,
        page: Int,
        perPage: Int
    ) = EpisodesQuery
        .builder()
        .titleId(titleId ?: "")
        .seasonId(seasonId ?: "")
        .page(page)
        .perPage(perPage)
        .build()

    internal fun transformExcerptsToThumb(
        videos: List<EpisodesWithRelatedExcerptsList.Resource1>?
    ) = videos?.map {
        Thumb(
            id = it.id(),
            headline = it.headline(),
            formattedDuration = it.formattedDuration(),
            thumb = it.thumb(),
            kind = Kind.normalize(it.kind()),
            availableFor = AvailableFor.normalize(it.availableFor())
        )
    }?.filter { (it.kind != Kind.EPISODE) } ?: arrayListOf()

    internal fun builderDownloadImage(downloadQuery: EpisodeQuery.Builder, scale: String) =
        when (device) {
            Device.TABLET -> downloadQuery.tabletPosterScales(TabletPosterScales.safeValueOf(scale))
            else -> downloadQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }

    internal fun transformEpisodesListResourceToEpisodeList(responseList: List<EpisodesList.Resource>?) =
        responseList?.map { resource ->
            val normalizedKind = Kind.normalize(resource.video().kind())

            Episode(
                id = resource.video().id(),
                headline = resource.video().headline(),
                description = resource.video().description(),
                number = resource.number() ?: 1,
                seasonNumber = resource.seasonNumber() ?: 1,
                contentRating = ContentRating(rating = resource.video().contentRating()),
                formattedDuration = resource.video().formattedDuration(),
                duration = resource.video().duration() ?: 0,
                accessibleOffline = resource.video().accessibleOffline() ?: false,
                thumb = when (normalizedKind) {
                    Kind.LIVE -> resource.video().liveThumbnail()
                    else -> resource.video().thumb()
                },
                availableFor = AvailableFor.normalize(resource.video().availableFor()),
                serviceId = resource.video().serviceId()
            )
        } ?: arrayListOf()

    internal fun transformEpisodeQueryToEpisode(downloadQueryEpisode: EpisodeQuery.Episode?): Episode {
        val normalizedKind = Kind.normalize(downloadQueryEpisode?.video()?.kind())

        return Episode(
            number = downloadQueryEpisode?.number() ?: 1,
            seasonNumber = downloadQueryEpisode?.seasonNumber() ?: 0,
            video = Video(
                id = downloadQueryEpisode?.video()?.id(),
                headline = downloadQueryEpisode?.video()?.headline(),
                description = downloadQueryEpisode?.video()?.description(),
                duration = downloadQueryEpisode?.video()?.duration() ?: 0,
                formattedDuration = downloadQueryEpisode?.video()?.formattedDuration(),
                fullyWatchedThreshold = downloadQueryEpisode?.video()?.fullyWatchedThreshold(),
                accessibleOffline = downloadQueryEpisode?.video()?.accessibleOffline() ?: false,
                contentRating = ContentRating(downloadQueryEpisode?.video()?.contentRating()),
                availableFor = AvailableFor.normalize(downloadQueryEpisode?.video()?.availableFor()),
                thumb = when (normalizedKind) {
                    Kind.LIVE -> downloadQueryEpisode?.video()?.liveThumbnail()
                    else -> downloadQueryEpisode?.video()?.thumb()
                },
                kind = normalizedKind,
                title = Title(
                    headline = downloadQueryEpisode?.video()?.title()?.headline(),
                    titleId = downloadQueryEpisode?.video()?.title()?.titleId().toString(),
                    originProgramId = downloadQueryEpisode?.video()?.title()?.originProgramId(),
                    type = Type.normalize(downloadQueryEpisode?.video()?.title()?.type()),
                    poster = when (device) {
                        Device.TABLET -> downloadQueryEpisode?.video()?.title()?.poster()?.tablet()
                        else -> downloadQueryEpisode?.video()?.title()?.poster()?.mobile()
                    }
                )
            )
        )
    }
}

