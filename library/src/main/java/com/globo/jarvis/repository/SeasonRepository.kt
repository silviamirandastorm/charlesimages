package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.common.JARVIS_PATTERN_YYYY_MM_DD
import com.globo.jarvis.common.formatByPattern
import com.globo.jarvis.common.lastDayOfMonth
import com.globo.jarvis.fragment.SeasonedEpisodes
import com.globo.jarvis.fragment.SeasonedScenes
import com.globo.jarvis.model.DateRange
import com.globo.jarvis.model.Season
import com.globo.jarvis.title.season.SeasonsChapterQuery
import com.globo.jarvis.title.season.SeasonsEpisodesQuery
import com.globo.jarvis.title.season.SeasonsScenesQuery
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

data class SeasonRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun byChapter(
        titleId: String?,
        seasonsCallback: Callback.Seasons
    ) {
        compositeDisposable.add(
            byChapter(titleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        seasonsCallback.onSeasonByChapterSuccess(it)
                    },
                    { throwable ->
                        seasonsCallback.onFailure(throwable)
                    })
        )
    }

    fun byEpisode(
        titleId: String?,
        seasonsCallback: Callback.Seasons
    ) {
        compositeDisposable.add(
            byEpisode(titleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        seasonsCallback.onSeasonByEpisodeSuccess(it)
                    },
                    { throwable ->
                        seasonsCallback.onFailure(throwable)
                    })
        )
    }

    fun byScenes(
        titleId: String?,
        seasonsCallback: Callback.Seasons
    ) {
        compositeDisposable.add(
            byScenes(titleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        seasonsCallback.onSeasonByScenesSuccess(it)
                    },
                    { throwable ->
                        seasonsCallback.onFailure(throwable)
                    })
        )
    }


    //RxJava
    fun byChapter(titleId: String?): Observable<List<DateRange>> = apolloClient
        .query(builderDatesWithChapterQuery(titleId))
        .rx()
        .subscribeOn(Schedulers.io())
        .map { response ->
            val datesWithContent = response.data()
                ?.title()
                ?.structure()
                ?.fragments()
                ?.dateList()
                ?.datesWithEpisodes()
                ?.resources()

            return@map transformDatesSeasonsChaptersQueryToSeasonVO(datesWithContent)
        }

    fun byEpisode(titleId: String?): Observable<Pair<String, List<Season>>> = apolloClient
        .query(builderGetSeasonsEpisodesQuery(titleId))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val seasonedEpisodes = it.data()?.title()?.structure()?.fragments()?.seasonedEpisodes()

            val listSeasonsFiltered = transformSeasonsEpisodesQueryToSeasonVO(
                seasonedEpisodes?.seasons()?.resources(),
                seasonedEpisodes?.defaultEpisode()
            ).filter { seasonVO -> seasonVO.total >= 1 }

            val defaultEpisode = seasonedEpisodes?.defaultEpisode()?.seasonId()
                ?: listSeasonsFiltered.firstOrNull()?.id

            if (defaultEpisode != null && listSeasonsFiltered.isNotEmpty()) {
                return@map Pair(defaultEpisode, listSeasonsFiltered)
            }

            throw Throwable("Season by episode is empty")
        }

    fun byScenes(titleId: String?): Observable<Pair<Season, List<Season>>> = apolloClient
        .query(builderGetSeasonsScenesQuery(titleId))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val seasonedScenes = it.data()?.title()?.structure()?.fragments()?.seasonedScenes()
            val listSeasonsFiltered = transformSeasonsExcerptsQueryToSeasonVO(
                seasonedScenes?.seasonsWithExcerpts(),
                seasonedScenes?.defaultEpisode()
            ).filter { seasonVO -> seasonVO.total >= 1 }

            val defaultEpisode = seasonedScenes?.defaultEpisode()
                ?: listSeasonsFiltered.firstOrNull()

            if (defaultEpisode != null && listSeasonsFiltered.isNotEmpty()) {
                return@map Pair(listSeasonsFiltered.first(), listSeasonsFiltered)
            }

            throw Throwable("Season by excerpts is empty")
        }

    internal fun builderGetSeasonsEpisodesQuery(titleId: String?) = SeasonsEpisodesQuery
        .builder()
        .titleId(titleId ?: "")
        .build()

    internal fun builderGetSeasonsScenesQuery(titleId: String?) = SeasonsScenesQuery
        .builder()
        .titleId(titleId ?: "")
        .build()

    internal fun builderDatesWithChapterQuery(titleId: String?) =
        SeasonsChapterQuery
            .builder()
            .titleId(titleId ?: "")
            .build()

    internal fun transformSeasonsEpisodesQueryToSeasonVO(
        resources: List<SeasonedEpisodes.Resource>?,
        defaultEpisode: SeasonedEpisodes.DefaultEpisode?
    ) = resources?.map { resource ->
        Season(
            resource.id(),
            resource.number(),
            resource.totalEpisodes() ?: 0,
            defaultEpisode?.seasonId() == resource.id()
        )
    } ?: emptyList()

    internal fun transformSeasonsExcerptsQueryToSeasonVO(
        seasonsWithExcerptList: List<SeasonedScenes.SeasonsWithExcerpt>?,
        defaultEpisode: SeasonedScenes.DefaultEpisode?
    ) = seasonsWithExcerptList
        ?.map { seasonsWithExcerpt ->
            Season(
                seasonsWithExcerpt.id(),
                seasonsWithExcerpt.number(),
                seasonsWithExcerpt.totalEpisodes() ?: 0,
                defaultEpisode?.seasonId() == seasonsWithExcerpt.id()
            )
        } ?: emptyList()

    internal fun transformDatesSeasonsChaptersQueryToSeasonVO(datesWithContent: List<String>?)
            : List<DateRange> {

        val datesWithContentAsCalendar = datesWithContent?.map { dateString ->
            Calendar.getInstance().apply {
                time = dateString.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD)
                set(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val contentCounterMap = datesWithContentAsCalendar
            ?.groupingBy { calendar -> calendar }
            ?.eachCount()

        return contentCounterMap?.map { map ->
            val gteDate = map.key
            val lteDate = gteDate.lastDayOfMonth()

            DateRange(lte = lteDate, gte = gteDate, total = map.value)
        } ?: return emptyList()
    }
}