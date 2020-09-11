package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.common.JARVIS_PATTERN_YYYY_MM_DD
import com.globo.jarvis.common.formatByPattern
import com.globo.jarvis.common.formatCoordinate
import com.globo.jarvis.epg.EpgQuery
import com.globo.jarvis.epg.EpgsQuery
import com.globo.jarvis.fragment.EpgFragment
import com.globo.jarvis.model.AvailableFor
import com.globo.jarvis.model.Epg
import com.globo.jarvis.model.EpgSlot
import com.globo.jarvis.model.Media
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class EpgRepository(
    private val apolloClient: ApolloClient,
    private val device: Device
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    @JvmOverloads
    fun details(
        mediaId: String?,
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        scaleImageOnAir: String,
        date: Date? = null,
        channelCallback: Callback.Epgs
    ) {
        compositeDisposable.add(
            details(mediaId, latitude, longitude, broadcastChannelLogoScales, scaleImageOnAir, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { epgVO ->
                        channelCallback.onEpgSuccess(epgVO)
                    },
                    { throwable ->
                        channelCallback.onFailure(throwable)
                    })
        )
    }

    @JvmOverloads
    fun all(
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        scaleImageOnAir: String,
        scaleCoverWide: String,
        date: Date? = null,
        epgsCallback: Callback.Epgs
    ) {
        compositeDisposable.add(
            all(
                latitude,
                longitude,
                broadcastChannelLogoScales,
                scaleImageOnAir,
                scaleCoverWide,
                date
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { epgVOtList ->
                        epgsCallback.onEpgListSuccess(epgVOtList)
                    },
                    { throwable ->
                        epgsCallback.onFailure(throwable)
                    })
        )
    }

    //RxJava
    @JvmOverloads
    fun all(
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        scaleImageOnAir: String,
        scaleCoverWide: String,
        date: Date? = null
    ): Observable<List<Epg>> = apolloClient
        .query(buildQueryEpgs(date, broadcastChannelLogoScales, scaleImageOnAir, scaleCoverWide))
        .rx()
        .map { responseGetEpgsData ->
            return@map transformBroadcastListToEpgsVOList(responseGetEpgsData.data()?.broadcasts())
        }
        .flatMapIterable { epgVOList -> epgVOList }
        .concatMap { epgVO ->
            if (epgVO.geofenced) {
                return@concatMap details(
                    epgVO.media?.idWithDVR,
                    latitude,
                    longitude,
                    broadcastChannelLogoScales,
                    scaleImageOnAir,
                    date
                )
            }
            return@concatMap Observable.defer { Observable.just(epgVO) }
        }
        .toList()
        .toObservable()


    //Detalhes do epg
    @JvmOverloads
    fun details(
        mediaId: String?,
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        scaleImageOnAir: String,
        date: Date? = null
    ): Observable<Epg> {
        val coordinatesData = if (latitude != null && longitude != null) CoordinatesData
            .builder()
            .lat(latitude.formatCoordinate())
            .long_(longitude.formatCoordinate())
            .build()
        else null

        val queryBroadcasts = buildQueryEpg(
            date,
            mediaId,
            coordinatesData,
            broadcastChannelLogoScales,
            scaleImageOnAir
        )

        return apolloClient
            .query(queryBroadcasts)
            .rx()
            .map { responseGetEpgQuery ->
                return@map transformEpgFragmentToEpgVO(
                    responseGetEpgQuery.data()?.broadcast()?.fragments()?.epgFragment()
                )
            }
    }

    internal fun transformBroadcastListToEpgsVOList(getEpgsQueryBroadcastList: List<EpgsQuery.Broadcast>?) =
        getEpgsQueryBroadcastList?.map {
            transformEpgFragmentToEpgVO(it.fragments().epgFragment())
        } ?: arrayListOf()

    internal fun transformEpgFragmentToEpgVO(epgFragment: EpgFragment?) = run {
        val epgId = epgFragment?.media()?.title()?.titleId()?.takeIf { it.isNotEmpty() }
            ?: epgFragment?.channel()?.id()

        val logo = epgFragment?.logo()?.takeIf { it.isNotEmpty() }
            ?: epgFragment?.channel()?.logo()

        val imageOnAir = epgFragment?.imageOnAir()

        Epg(
            epgId,
            epgFragment?.channel()?.name(),
            logo,
            epgFragment?.geofencing() ?: false,
            transformMediaResponseToMedia(
                epgFragment?.media(),
                epgFragment?.mediaId(),
                epgFragment?.media()?.headline(),
                imageOnAir
            ),
            transformEntryToEpgSlotVO(epgFragment?.epgByDate()?.entries(), imageOnAir)
        )
    }

    internal fun transformEntryToEpgSlotVO(
        entryList: List<EpgFragment.Entry>?,
        imageOnAir: String?
    ) =
        entryList?.map { entry ->
            return@map EpgSlot(
                titleId = entry.titleId(),
                name = entry.name(),
                description = entry.description(),
                metadata = entry.metadata(),
                startTime = Date((entry.startTime()?.toLong() ?: 0) * 1000L),
                endTime = Date((entry.endTime()?.toLong() ?: 0) * 1000L),
                duration = entry.durationInMinutes() ?: 0,
                alternativeTimeList = entry.alternativeTime(),
                isLiveBroadcast = entry.liveBroadcast() ?: false,
                classificationsList = entry.tags(),
                contentRating = entry.contentRating(),
                contentRatingCriteria = entry.contentRatingCriteria(),
                cover = entry.title()?.cover()?.tv(),
                imageOnAir = imageOnAir
            )

        } ?: arrayListOf()

    internal fun transformMediaResponseToMedia(
        mediaResponse: EpgFragment.Media?,
        mediaId: String?,
        headline: String?,
        imageOnAir: String?
    ) = mediaResponse?.let {
        val availableFor = transformSubscriptionTypeToAvailableFor(
            mediaResponse.availableFor()
                ?: SubscriptionType.ANONYMOUS
        )

        return@let Media(
            idWithDVR = mediaId,
            availableFor = availableFor,
            headline = headline,
            imageOnAir = imageOnAir
        )
    }

    internal fun transformSubscriptionTypeToAvailableFor(subscriptionType: SubscriptionType) =
        when (subscriptionType) {
            SubscriptionType.LOGGED_IN -> AvailableFor.LOGGED_IN
            SubscriptionType.ANONYMOUS -> AvailableFor.ANONYMOUS
            else -> AvailableFor.SUBSCRIBER
        }

    internal fun buildQueryEpg(
        date: Date?,
        mediaId: String?,
        coordinatesData: CoordinatesData?,
        broadcastChannelLogoScales: String,
        scaleImageOnAir: String
    ) = EpgQuery.builder()
        .mediaId(mediaId ?: "")
        .coordinates(coordinatesData)
        .date(
            date?.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD) ?: Date().formatByPattern(
                JARVIS_PATTERN_YYYY_MM_DD
            )
        )
        .broadcastChannelLogoScales(BroadcastChannelLogoScales.valueOf(broadcastChannelLogoScales))
        .broadcastImageOnAirScales(BroadcastImageOnAirScales.safeValueOf(scaleImageOnAir))
        .build()

    internal fun buildQueryEpgs(
        date: Date?,
        broadcastChannelLogoScales: String,
        scaleImageOnAir: String,
        scaleCoverLandscape: String
    ) = EpgsQuery.builder()
        .date(
            date?.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD) ?: Date().formatByPattern(
                JARVIS_PATTERN_YYYY_MM_DD
            )
        )
        .broadcastChannelLogoScales(BroadcastChannelLogoScales.valueOf(broadcastChannelLogoScales))
        .broadcastImageOnAirScales(BroadcastImageOnAirScales.safeValueOf(scaleImageOnAir))
        .coverLandscapeScales(CoverLandscapeScales.safeValueOf(scaleCoverLandscape))
        .build()
}