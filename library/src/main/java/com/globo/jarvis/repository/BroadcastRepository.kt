package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.broadcast.BroadcastCurrentSlotsQuery
import com.globo.jarvis.broadcast.BroadcastMediaIdsQuery
import com.globo.jarvis.broadcast.BroadcastQuery
import com.globo.jarvis.broadcast.BroadcastsQuery
import com.globo.jarvis.common.formatCoordinate
import com.globo.jarvis.fragment.BroadcastFragment
import com.globo.jarvis.fragment.EPGSlotCoreFragment
import com.globo.jarvis.model.*
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class BroadcastRepository(private val apolloClient: ApolloClient, private val device: Device) {
    private val compositeDisposable by lazy { CompositeDisposable() }


    //Callback
    @JvmOverloads
    fun currentSlots(
        mediaId: String?,
        transmissionId: String?,
        limit: Int = 2,
        callback: Callback.BroadcastCurrentSlots
    ) {
        compositeDisposable.add(
            currentSlots(mediaId, transmissionId, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { slots ->
                        callback.onBroadcastCurrentSlotsSuccess(slots)
                    },
                    { throwable ->
                        callback.onFailure(throwable)
                    }
                ))
    }

    @JvmOverloads
    fun all(
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String,
        coverScales: String,
        limit: Int = 2,
        broadcastCallback: Callback.Broadcasts
    ) {
        compositeDisposable.add(
            all(
                latitude,
                longitude,
                broadcastChannelLogoScales,
                broadcastChannelTrimmedLogoScales,
                broadcastImageOnAirScales,
                coverScales,
                limit
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { broadcastVOList ->
                        if (broadcastVOList.isNotEmpty()) {
                            broadcastCallback.onBroadcastsSuccess(broadcastVOList)
                            return@subscribe
                        }

                        broadcastCallback.onFailure(Throwable("Lista de broadcasat vazia"))
                    },
                    { throwable ->
                        broadcastCallback.onFailure(throwable)
                    }
                ))
    }

    @JvmOverloads
    fun details(
        mediaId: String?,
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String,
        coverScales: String,
        limit: Int = 2,
        broadcastCallback: Callback.Broadcasts
    ) {
        compositeDisposable.add(
            details(
                mediaId,
                latitude,
                longitude,
                broadcastChannelLogoScales,
                broadcastChannelTrimmedLogoScales,
                broadcastImageOnAirScales,
                coverScales,
                limit
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { broadcast ->
                        broadcastCallback.onBroadcastSuccess(broadcast)
                    },
                    { throwable ->
                        broadcastCallback.onFailure(throwable)
                    }
                ))
    }

    //Rx
    @JvmOverloads
    fun all(
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String,
        coverScales: String,
        limit: Int = 2
    ): Observable<List<Broadcast>> =
        apolloClient.query(
            buildQueryBroadcasts(
                broadcastChannelLogoScales,
                broadcastChannelTrimmedLogoScales,
                broadcastImageOnAirScales
            )
        )
            .rx()
            .map { return@map transformBroadcastListToBroadcastList(it.data()?.broadcasts()) }
            .flatMapIterable { broadcastList -> broadcastList }
            .concatMap { broadcastVO ->
                if (broadcastVO.geofencing) {
                    return@concatMap details(
                        broadcastVO.media?.idWithDVR,
                        latitude,
                        longitude,
                        broadcastChannelLogoScales,
                        broadcastChannelTrimmedLogoScales,
                        broadcastImageOnAirScales,
                        coverScales,
                        limit
                    )
                        .onExceptionResumeNext(Observable.defer {
                            Observable.just((broadcastVO.copy(hasError = true)))
                        })
                        .onErrorResumeNext(Observable.defer {
                            Observable.just((broadcastVO.copy(hasError = true)))
                        })
                }
                return@concatMap Observable.defer { Observable.just(broadcastVO) }
            }
            .toList()
            .toObservable()

    @JvmOverloads
    fun details(
        mediaId: String?,
        latitude: Double?,
        longitude: Double?,
        broadcastChannelLogoScales: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String,
        coverScales: String,
        limit: Int = 2
    ): Observable<Broadcast> {
        val coordinatesData = if (latitude != null && longitude != null) CoordinatesData
            .builder()
            .lat(latitude.formatCoordinate())
            .long_(longitude.formatCoordinate())
            .build()
        else null

        val queryBroadcasts = builderQueryBroadcast(
            mediaId,
            limit,
            broadcastChannelLogoScales,
            broadcastChannelTrimmedLogoScales,
            broadcastImageOnAirScales,
            coverScales,
            coordinatesData
        )

        return apolloClient.query(queryBroadcasts)
            .rx()
            .map { responseGetBroadcastQuery ->
                val channelFragment =
                    responseGetBroadcastQuery.data()?.broadcast()?.fragments()?.broadcastFragment()
                        ?: throw Throwable("Houve um erro ao tentar carregar os detalhes do canal com id $mediaId")

                return@map transformBroadcastFragmentToBroadcast(channelFragment)
            }
    }

    fun mediaIds(): Observable<List<String>> = apolloClient
        .query(buildMediaIdQuery())
        .rx()
        .flatMapIterable { it.data()?.broadcasts() }
        .map { it.mediaId() }
        .toList()
        .toObservable()

    fun mediaIds(callback: Callback.BroadcastMediaIds) {
        compositeDisposable.add(
            mediaIds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { mediaIds ->
                        callback.onBroadcastMediaIdsSuccess(mediaIds)
                    },
                    { throwable ->
                        callback.onFailure(throwable)
                    }
                ))
    }

    fun currentSlots(
        mediaId: String?,
        transmissionId: String?,
        limit: Int = 2
    ): Observable<List<BroadcastSlot>> = apolloClient
        .query(buildCurrentSlotsQuery(mediaId, transmissionId, limit))
        .rx()
        .flatMapIterable { it.data()?.epgCurrentSlots() }
        .map { transformToBroadcastSlot(it.fragments().ePGSlotCoreFragment()) }
        .toList()
        .toObservable()

    internal fun transformBroadcastListToBroadcastList(broadcastQueryList: MutableList<BroadcastsQuery.Broadcast>?) =
        broadcastQueryList?.map {
            transformBroadcastFragmentToBroadcast(it.fragments().broadcastFragment())
        } ?: listOf()

    internal fun transformBroadcastFragmentToBroadcast(broadcastFragment: BroadcastFragment?) =
        run {
            val logo = broadcastFragment?.logo()?.takeIf { it.isNotEmpty() }
                ?: broadcastFragment?.channel()?.logo()


            Broadcast(
                broadcastFragment?.channel()?.id(),
                broadcastFragment?.transmissionId(),
                broadcastFragment?.channel()?.name(),
                logo,
                broadcastFragment?.channel()?.trimmedLogo(),
                broadcastFragment?.salesPageIdentifiers()?.mobile(),
                broadcastFragment?.salesPageCallToAction(),
                broadcastFragment?.geofencing() ?: false,
                broadcastFragment?.geoblocked() ?: false,
                false,
                broadcastFragment?.promotionalText(),
                broadcastFragment?.ignoreAdvertisements() ?: false,
                transformPayTvInfo(broadcastFragment?.channel()),
                transformAffiliateSignal(broadcastFragment?.affiliateSignal()),
                transformMediaMediaVO(
                    broadcastFragment?.promotionalMediaId(),
                    broadcastFragment?.mediaId(),
                    broadcastFragment?.withoutDVRMediaId(),
                    broadcastFragment?.media()?.headline(),
                    broadcastFragment?.media(),
                    broadcastFragment?.imageOnAir()
                ),
                transformEpgCurrentSlotsToBroadcastSlot(broadcastFragment?.epgCurrentSlots())
            )
        }

    internal fun transformPayTvInfo(channel: BroadcastFragment.Channel?) =
        channel?.payTvServiceId()?.toIntOrNull()?.takeIf { it != 0 }?.let { serviceId ->
            PayTv(
                serviceId,
                channel.payTvUsersMessage(),
                channel.payTvInfoLink(),
                channel.payTvExternalLink(),
                channel.payTvExternalLinkLabel(),
                channel.requireUserTeam() ?: false
            )
        }

    internal fun transformAffiliateSignal(affiliateSignal: BroadcastFragment.AffiliateSignal?) =
        AffiliateSignal(
            affiliateSignal?.id(),
            affiliateSignal?.dtvChannel(),
            affiliateSignal?.dtvHDID(),
            affiliateSignal?.dtvID()
        )

    internal fun transformEpgCurrentSlotsToBroadcastSlot(epgCurrentSlotList: MutableList<BroadcastFragment.EpgCurrentSlot>?) =
        epgCurrentSlotList?.map { epgCurrentSlot ->
            return@map BroadcastSlot(
                epgCurrentSlot.programId(), epgCurrentSlot.name(), epgCurrentSlot.metadata(),
                Date((epgCurrentSlot.startTime()?.toLong() ?: 0) * 1000L),
                Date((epgCurrentSlot.endTime()?.toLong() ?: 0) * 1000L),
                epgCurrentSlot.durationInMinutes() ?: 0,
                epgCurrentSlot.title()?.cover()?.mobile(),
                epgCurrentSlot.title()?.cover()?.tabletLandscape(),
                epgCurrentSlot.title()?.cover()?.tabletPortrait(),
                epgCurrentSlot.title()?.cover()?.tv(),
                epgCurrentSlot.liveBroadcast() ?: false,
                epgCurrentSlot.tags()
            )
        } ?: listOf()

    internal fun transformMediaMediaVO(
        idPromotional: String?,
        idWithDVR: String?,
        idWithoutDVR: String?,
        headline: String?,
        media: BroadcastFragment.Media?,
        imageOnAir: String?
    ) = media?.let {
        val availableFor = transformSubscriptionTypeToAvailableFor(
            it.availableFor()
                ?: SubscriptionType.ANONYMOUS
        )

        return@let Media(
            idWithDVR,
            idWithoutDVR,
            idPromotional,
            headline,
            it.serviceId() ?: 0,
            imageOnAir,
            availableFor,
            subscriptionService = SubscriptionService(
                salesPage = SalesPage(
                    identifier = PageUrl(
                        mobile = media.subscriptionService()?.salesPage()
                            ?.identifier()
                            ?.mobile()
                    )
                ),
                faq = SubscriptionServiceFaq(
                    qrCode = PageUrl(
                        mobile = media.subscriptionService()?.faq()?.qrCode()
                            ?.mobile()
                    ),
                    url = PageUrl(
                        mobile = media.subscriptionService()?.faq()?.url()
                            ?.mobile()
                    )
                )
            )
        )
    }

    internal fun transformSubscriptionTypeToAvailableFor(subscriptionType: SubscriptionType) =
        when (subscriptionType) {
            SubscriptionType.LOGGED_IN -> AvailableFor.LOGGED_IN
            SubscriptionType.ANONYMOUS -> AvailableFor.ANONYMOUS
            else -> AvailableFor.SUBSCRIBER
        }

    internal fun transformToBroadcastSlot(epgCurrentSlot: EPGSlotCoreFragment) =
        BroadcastSlot(
            epgCurrentSlot.programId(),
            epgCurrentSlot.name(),
            epgCurrentSlot.metadata(),
            Date((epgCurrentSlot.startTime()?.toLong() ?: 0) * 1000L),
            Date((epgCurrentSlot.endTime()?.toLong() ?: 0) * 1000L),
            epgCurrentSlot.durationInMinutes() ?: 0,
            isLiveBroadcast = epgCurrentSlot.liveBroadcast() == true,
            classificationsList = epgCurrentSlot.tags()
        )

    private fun buildQueryBroadcasts(
        broadcastChannelLogoScales: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String
    ) =
        BroadcastsQuery.builder()
            .broadcastChannelLogoScales(
                BroadcastChannelLogoScales.valueOf(
                    broadcastChannelLogoScales
                )
            )
            .broadcastChannelTrimmedLogoScales(
                BroadcastChannelTrimmedLogoScales.safeValueOf(
                    broadcastChannelTrimmedLogoScales
                )
            )
            .broadcastImageOnAirScales(
                BroadcastImageOnAirScales.safeValueOf(
                    broadcastImageOnAirScales
                )
            )
            .build()

    internal fun builderQueryBroadcast(
        mediaId: String?,
        limit: Int,
        broadcastChannelLogoScales: String,
        broadcastChannelTrimmedLogoScales: String,
        broadcastImageOnAirScales: String,
        coverScales: String,
        coordinatesData: CoordinatesData?
    ) =
        BroadcastQuery.builder()
            .apply {
                mediaId(mediaId.orEmpty())
                coordinates(coordinatesData)
                limit(limit)
                broadcastChannelLogoScales(
                    BroadcastChannelLogoScales.valueOf(
                        broadcastChannelLogoScales
                    )
                )
                broadcastChannelTrimmedLogoScales(
                    BroadcastChannelTrimmedLogoScales.safeValueOf(
                        broadcastChannelTrimmedLogoScales
                    )
                )
                broadcastImageOnAirScales(
                    BroadcastImageOnAirScales.safeValueOf(
                        broadcastImageOnAirScales
                    )
                )
                when (device) {
                    Device.TV -> coverWideScales(CoverWideScales.safeValueOf(coverScales))

                    Device.TABLET -> coverLandscapeScales(
                        CoverLandscapeScales.safeValueOf(
                            coverScales
                        )
                    )

                    else -> coverPortraitScales(CoverPortraitScales.safeValueOf(coverScales))
                }
            }.build()

    private fun buildMediaIdQuery() = BroadcastMediaIdsQuery.builder().build()

    private fun buildCurrentSlotsQuery(mediaId: String?, transmissionId: String?, limit: Int) =
        BroadcastCurrentSlotsQuery
            .builder()
            .mediaId(mediaId.orEmpty())
            .limit(limit)
            .transmissionId(transmissionId.orEmpty())
            .build()
}