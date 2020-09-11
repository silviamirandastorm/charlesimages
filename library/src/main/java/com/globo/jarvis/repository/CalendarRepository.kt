package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.common.JARVIS_PATTERN_YYYY_MM_DD
import com.globo.jarvis.common.formatByPattern
import com.globo.jarvis.title.calendar.DatesQuery
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

data class CalendarRepository constructor(private val apolloClient: ApolloClient) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun all(
        titleId: String?,
        calendarCallback: Callback.Dates
    ) {
        compositeDisposable.add(
            all(titleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        calendarCallback.onDatesSuccess(it)
                    },
                    { throwable ->
                        calendarCallback.onFailure(throwable)
                    }
                ))
    }

    //RxJava
    fun all(titleId: String?): Observable<Pair<List<Calendar>, List<String>>> =
        apolloClient
            .query(builderDatesWithContentQuery(titleId))
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                val episodeList = it.data()?.title()?.structure()?.fragments()?.episodeList()

                val datesWithContent =
                    episodeList?.datesWithVideos()?.resources() ?: arrayListOf()

                val datesWithVideos = arrayListOf<Calendar>()

                datesWithContent.forEach { dateAsString ->
                    val calendarDate = Calendar.getInstance().apply {
                        time = dateAsString.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    datesWithVideos.add(calendarDate)
                }

                return@map Pair(datesWithVideos, datesWithContent)
            }

    internal fun builderDatesWithContentQuery(titleId: String?) =
        DatesQuery
            .builder()
            .titleId(titleId ?: "")
            .build()

}