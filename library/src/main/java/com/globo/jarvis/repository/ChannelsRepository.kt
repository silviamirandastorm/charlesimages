package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.channels.BroadcastChannelsQuery
import com.globo.jarvis.model.Channel
import com.globo.jarvis.type.BroadcastChannelFilters
import com.globo.jarvis.type.BroadcastChannelInput
import com.globo.jarvis.type.BroadcastChannelTrimmedLogoScales
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChannelsRepository(private val apolloClient: ApolloClient) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    fun all(
        page: Int = 1,
        perPage: Int = 12,
        broadcastChannelTrimmedLogoScales: String,
        broadcastChannelFilters: BroadcastChannelFilters? = null
    ): Observable<Triple<Int?, Boolean, List<Channel>>> =
        apolloClient.query(
            buildQueryChannels(
                page,
                perPage,
                broadcastChannelTrimmedLogoScales,
                broadcastChannelFilters
            )
        )
            .rx()
            .map {
                val broadcastChannels = it.data()?.broadcastChannels()
                val nextPage = broadcastChannels?.nextPage()
                val hasNextPage = broadcastChannels?.hasNextPage() ?: false

                return@map Triple(
                    nextPage,
                    hasNextPage,
                    transformBroadcastListToChannelList(broadcastChannels?.resources())
                )
            }

    fun all(
        page: Int = 1,
        perPage: Int = 12,
        broadcastChannelTrimmedLogoScales: String,
        broadcastChannelFilters: BroadcastChannelFilters? = null,
        channelsCallback: Callback.Channels
    ) {
        compositeDisposable.add(
            all(page, perPage, broadcastChannelTrimmedLogoScales, broadcastChannelFilters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { triple ->
                        channelsCallback.onChannelsSuccess(triple)
                    },
                    { throwable ->
                        channelsCallback.onFailure(throwable)
                    }
                ))
    }

    internal fun transformBroadcastListToChannelList(resources: List<BroadcastChannelsQuery.Resource>?) =
        resources?.map {
            Channel(it.id(), it.name(), it.pageIdentifier(), it.trimmedLogo())
        } ?: listOf()

    internal fun buildQueryChannels(
        page: Int,
        perPage: Int,
        broadcastChannelTrimmedLogoScales: String,
        broadcastChannelFilters: BroadcastChannelFilters?
    ) =
        BroadcastChannelsQuery
            .builder()
            .page(page)
            .perPage(perPage)
            .broadcastChannelTrimmedLogoScales(
                BroadcastChannelTrimmedLogoScales.safeValueOf(
                    broadcastChannelTrimmedLogoScales
                )
            )
            .filtersInput(
                BroadcastChannelInput
                    .builder()
                    .filter(broadcastChannelFilters)
                    .build()
            )
            .build()
}