package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.common.JARVIS_PATTERN_YYYY_MM_DD
import com.globo.jarvis.common.formatByPattern
import com.globo.jarvis.fragment.ChaptersByDateRangeList
import com.globo.jarvis.fragment.ChaptersList
import com.globo.jarvis.model.*
import com.globo.jarvis.title.chapter.ChaptersByDateRangeQuery
import com.globo.jarvis.title.chapter.ChaptersQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ChapterRepository constructor(private val apolloClient: ApolloClient) {

    private val compositeDisposable by lazy { CompositeDisposable() }

    // Callback
    fun byDateRange(
        titleId: String?,
        startDate: Date?,
        endDate: Date?,
        page: Int,
        perPage: Int,
        chaptersCallback: Callback.Chapters
    ) = compositeDisposable.add(
        byDateRange(titleId, startDate, endDate, page, perPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {triple ->
                    chaptersCallback.onChapterByDateRangeSuccess(triple)
                },
                { throwable ->
                    chaptersCallback.onFailure(throwable)
                }
            )
    )

    // RX
    fun byDateRange(
        titleId: String?,
        startDate: Date?,
        endDate: Date?,
        page: Int,
        perPage: Int
    ) = apolloClient
        .query(builderTitleEpisodesByDateRangeQuery(titleId, startDate, endDate, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val chaptersListStructure = it.data()?.title()?.structure()?.fragments()?.chaptersByDateRangeList()
            val hasNextPage = chaptersListStructure?.episodes()?.hasNextPage() ?: false
            val nextPage = chaptersListStructure?.episodes()?.nextPage() ?: 1
            val listOfChapter = transformChapterQueryToChapter(chaptersListStructure?.episodes()?.resources())
            Triple(listOfChapter, hasNextPage, nextPage)
        }

    // Callback
    fun all(
        titleId: String?,
        page: Int,
        perPage: Int,
        chaptersCallback: Callback.Chapters
    ) = compositeDisposable.add(
        all(titleId, page, perPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {triple ->
                    chaptersCallback.onChaptersSuccess(triple)
                },
                { throwable ->
                    chaptersCallback.onFailure(throwable)
                }
            )
    )

    // RX
    fun all(
        titleId: String?,
        page: Int,
        perPage: Int
    ) =  apolloClient
        .query(builderChaptersListQuery(titleId, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            val resourceChapterList = it.data()?.title()?.structure()?.fragments()?.chaptersList()
            val hasNextPage = resourceChapterList?.episodes()?.hasNextPage() ?: false
            val nextPage = resourceChapterList?.episodes()?.nextPage() ?: 1

            return@map Triple(
                transformEditionQueryToChapter(resourceChapterList),
                hasNextPage,
                nextPage
            )
        }

    internal fun transformChapterQueryToChapter(chaptersByDateRangeList: List<ChaptersByDateRangeList.Resource>?): List<Chapter> {
        return chaptersByDateRangeList?.map { episode ->
            episode.video().let { video ->
                Chapter(
                    id = video.id(),
                    headline = video.headline(),
                    description = video.description(),
                    duration = video.duration() ?: 0,
                    formattedDuration = video.formattedDuration(),
                    thumb = video.thumb(),
                    availableFor = AvailableFor.normalize(video.availableFor()),
                    accessibleOffline = video.accessibleOffline() == true,
                    exhibitedAt = video.exhibitedAt(),
                    serviceId = video.serviceId()
                )
            }
        } ?: listOf()
    }

    internal fun transformEditionQueryToChapter(chaptersList: ChaptersList?): List<Chapter> {
            val episodes = chaptersList?.episodes()?.resources()
            return episodes?.map { episode ->
                episode.video().let { video ->
                    Chapter(
                        id = video.id(),
                        headline = video.headline(),
                        description = video.description(),
                        duration = video.duration() ?: 0,
                        formattedDuration = video.formattedDuration(),
                        thumb = video.thumb(),
                        availableFor = AvailableFor.normalize(video.availableFor()),
                        accessibleOffline = video.accessibleOffline() == true,
                        exhibitedAt = video.exhibitedAt(),
                        serviceId = video.serviceId()
                    )
                }
            } ?: listOf()
    }

    internal fun builderTitleEpisodesByDateRangeQuery(
        titleId: String?,
        startDate: Date?,
        endDate: Date?,
        page: Int,
        perPage: Int
    ) = ChaptersByDateRangeQuery.builder()
        .titleId(titleId ?: "")
        .startDate(startDate?.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD) ?: "")
        .endDate(endDate?.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD) ?: "")
        .page(page)
        .perPage(perPage)
        .build()

    internal fun builderChaptersListQuery(
        titleId: String?,
        page: Int,
        perPage: Int
    ) =
        ChaptersQuery.builder()
            .titleId(titleId ?: "")
            .page(page)
            .perPage(perPage)
            .build()
}