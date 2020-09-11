package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.ABExperimentSearchFragment
import com.globo.jarvis.fragment.ChannelSearchResultFragment
import com.globo.jarvis.fragment.TitleSearchResultFragment
import com.globo.jarvis.fragment.VideoSearchResultFragment
import com.globo.jarvis.model.*
import com.globo.jarvis.search.*
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchRepository constructor(
    private val apolloClient: ApolloClient,
    private val device: Device
) {

    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun all(
        query: String?,
        posterScale: String,
        broadcastImageOnAirScales: String,
        broadcastChannelTrimmedLogoScales: String,
        page: Int = 1,
        perPage: Int = 12,
        searchCallback: Callback.Search
    ) {
        compositeDisposable.add(
            all(
                query,
                posterScale,
                broadcastImageOnAirScales,
                broadcastChannelTrimmedLogoScales,
                page,
                perPage
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        searchCallback.onSearchSuccess(it)
                    },
                    { throwable ->
                        searchCallback.onFailure(throwable)
                    })
        )
    }

    fun videos(
        query: String?,
        broadcastImageOnAirScales: String,
        broadcastChannelTrimmedLogoScales: String,
        page: Int = 1,
        perPage: Int = 12,
        searchCallback: Callback.Search
    ) {
        compositeDisposable.add(
            videos(
                query,
                broadcastImageOnAirScales,
                broadcastChannelTrimmedLogoScales,
                page,
                perPage
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        searchCallback.onSearchVideosSuccess(it)
                    },
                    { throwable ->
                        searchCallback.onFailure(throwable)
                    })
        )
    }

    fun channels(
        query: String?,
        broadcastChannelTrimmedLogoScales: String,
        page: Int = 1,
        perPage: Int = 12,
        searchCallback: Callback.Search
    ) {
        compositeDisposable.add(channels(
            query,
            broadcastChannelTrimmedLogoScales,
            page,
            perPage
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {
                    searchCallback.onSearchChannelsSuccess(it)
                },
                { throwable ->
                    searchCallback.onFailure(throwable)
                })
        )
    }

    fun titles(
        query: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12,
        searchCallback: Callback.Search
    ) {
        compositeDisposable.add(
            titles(query, scale, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        searchCallback.onSearchTitlesSuccess(it)
                    },
                    { throwable ->
                        searchCallback.onFailure(throwable)
                    })
        )
    }

    fun searchTopHits(scale: String, perPage: Int = 3, searchCallback: Callback.Search) {
        compositeDisposable.add(
            searchTopHits(scale, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        searchCallback.onTopHitsSuccess(it)
                    },
                    { throwable ->
                        searchCallback.onFailure(throwable)
                    })
        )
    }

    //RxJava
    fun all(
        query: String?,
        scale: String,
        broadcastImageOnAirScales: String,
        broadcastChannelTrimmedLogoScales: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<Triple<SearchTitles, SearchChannel, SearchVideos>> = apolloClient
        .query(
            builderSearchQuery(
                query,
                scale,
                broadcastImageOnAirScales,
                broadcastChannelTrimmedLogoScales,
                page,
                perPage
            )
        )
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            //Transforma os títulos
            val titleSearchFragment =
                it.data()?.search()?.titleSearch()?.fragments()?.titleSearchResultFragment()
            val abExperimentTitleFragment =
                titleSearchFragment?.abExperiment()?.fragments()?.aBExperimentSearchFragment()
            val titleList = transformTitleSearchFragmentToTitle(
                titleSearchFragment?.collection()?.resources(),
                abExperimentTitleFragment
            )
            val searchTitles = builderSearchTitle(titleSearchFragment?.collection(), titleList)


            //Transforma os canais
            val channelSearchResultFragment =
                it.data()?.search()?.broadcastChannelSearch()?.fragments()
                    ?.channelSearchResultFragment()
            val channelList = transformBroadcastSearchFragmentResourceToChannelList(
                channelSearchResultFragment?.collection()?.resources()
            )
            val searchChannel = builderSearchChannel(
                channelSearchResultFragment?.collection()?.hasNextPage(),
                channelSearchResultFragment?.collection()?.nextPage(),
                channelSearchResultFragment?.collection()?.total(), channelList
            )


            //Transforma os vídeos
            val videoSearchFragment =
                it.data()?.search()?.videoSearch()?.fragments()?.videoSearchResultFragment()
            val abExperimentVideoFragment =
                videoSearchFragment?.abExperiment()?.fragments()?.aBExperimentSearchFragment()
            val thumbList = transformSearchQueryResourceToThumb(
                videoSearchFragment?.collection()?.resources(),
                abExperimentVideoFragment
            )
            val searchVideos = builderSearchVideo(videoSearchFragment?.collection(), thumbList)

            return@map Triple(searchTitles, searchChannel, searchVideos)
        }

    fun videos(
        query: String?,
        broadcastImageOnAirScales: String,
        broadcastChannelTrimmedLogoScales: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<SearchVideos> =
        apolloClient
            .query(
                builderSearchVideoQuery(
                    query,
                    page,
                    perPage,
                    broadcastImageOnAirScales,
                    broadcastChannelTrimmedLogoScales
                )
            )
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                val videoSearchFragment =
                    it.data()?.search()?.videoSearch()?.fragments()?.videoSearchResultFragment()
                val collectionVideo = videoSearchFragment?.collection()
                val resourcesVideos = collectionVideo?.resources()

                val abExperimentVideoFragment =
                    videoSearchFragment?.abExperiment()?.fragments()?.aBExperimentSearchFragment()

                val thumbList =
                    transformSearchQueryResourceToThumb(resourcesVideos, abExperimentVideoFragment)

                return@map builderSearchVideo(collectionVideo, thumbList)
            }

    fun channels(
        query: String?,
        broadcastChannelTrimmedLogoScales: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<SearchChannel> =
        apolloClient
            .query(
                builderSearchChannelQuery(
                    query,
                    page,
                    perPage,
                    broadcastChannelTrimmedLogoScales
                )
            )
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                val channelSearchResultFragmentCollection =
                    it.data()?.search()?.broadcastChannelSearch()?.fragments()
                        ?.channelSearchResultFragment()?.collection()

                val hasNextPage = channelSearchResultFragmentCollection?.hasNextPage()
                val nextPage = channelSearchResultFragmentCollection?.nextPage()
                val total = channelSearchResultFragmentCollection?.total()

                val channelList =
                    transformBroadcastSearchFragmentResourceToChannelList(
                        channelSearchResultFragmentCollection?.resources()
                    )

                return@map builderSearchChannel(
                    hasNextPage,
                    nextPage,
                    total,
                    channelList
                )
            }

    fun titles(
        query: String?,
        scale: String,
        page: Int = 1,
        perPage: Int = 12
    ): Observable<SearchTitles> = apolloClient
        .query(builderSearchTitleQuery(query, scale, page, perPage))
        .rx()
        .subscribeOn(Schedulers.io())
        .map {
            //Transforma os títulos
            val titleSearchFragment =
                it.data()?.search()?.titleSearch()?.fragments()?.titleSearchResultFragment()
            val collectionTitle = titleSearchFragment?.collection()
            val resourcesTitle = titleSearchFragment?.collection()?.resources()

            val abExperimentTitleFragment =
                titleSearchFragment?.abExperiment()?.fragments()?.aBExperimentSearchFragment()
            val titleList =
                transformTitleSearchFragmentToTitle(resourcesTitle, abExperimentTitleFragment)

            return@map builderSearchTitle(collectionTitle, titleList)
        }

    fun searchTopHits(scale: String, perPage: Int = 3): Observable<List<SearchTopHits>> =
        apolloClient
            .query(builderSearchTopHitsQuery(scale, perPage))
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                return@map transformResourceToSearchTopHits(it.data()?.titles()?.resources())
            }


    fun global(query: String?, coverScale: String): Pair<List<Title>, List<Thumb>> = apolloClient
        .query(builderGlobalSearchQuery(query, coverScale))
        .rx()
        .blockingSingle().data()?.search()?.let {
            return Pair(
                transformSearchGlobalQueryToTitle(it.titles()?.resources()),
                transformSearchGlobalQueryToThumb(it.videos()?.resources())
            )
        } ?: Pair(arrayListOf(), arrayListOf())

    internal fun transformAbExperimentFragmentToAbExperiment(
        abExperimentFragment: ABExperimentSearchFragment?,
        url: String?
    ) =
        AbExperiment(
            abExperimentFragment?.experiment(),
            abExperimentFragment?.alternative(),
            url,
            abExperimentFragment?.trackId()
        )

    internal fun builderSearchTitle(
        collection: TitleSearchResultFragment.Collection?,
        titleList: List<Title>
    ) =
        SearchTitles(
            collection?.nextPage() ?: 1,
            collection?.hasNextPage() ?: false,
            collection?.total() ?: 0,
            titleList
        )

    internal fun builderSearchVideo(
        collection: VideoSearchResultFragment.Collection?,
        thumbList: List<Thumb>
    ) =
        SearchVideos(
            collection?.nextPage() ?: 1,
            collection?.hasNextPage() ?: false,
            collection?.total() ?: 0,
            thumbList
        )

    internal fun builderSearchChannel(
        hasNextPage: Boolean?,
        nextPage: Int?,
        total: Int?,
        channelList: List<Channel>
    ) =
        SearchChannel(
            nextPage,
            hasNextPage ?: false,
            total ?: 0,
            channelList
        )

    internal fun transformSearchQueryResourceToThumb(
        resources: List<VideoSearchResultFragment.Resource>?,
        abExperiment: ABExperimentSearchFragment?
    ) =
        resources?.map {
            val videoSearch = it.fragments().videoSearchFragment()
            val broadcast = videoSearch.broadcast()
            val isValidBroadcast = !broadcast?.mediaId().isNullOrEmpty()

            val normalizedKind =
                if (isValidBroadcast) Kind.EVENT else Kind.normalize(videoSearch.kind())

            return@map Thumb(
                id = videoSearch.id(),
                headline = videoSearch.headline(),
                formattedDuration = videoSearch.formattedDuration(),
                duration = videoSearch.duration() ?: 0,
                exhibitedAt = videoSearch.exhibitedAt(),
                thumb = when (normalizedKind) {
                    Kind.LIVE, Kind.EVENT -> broadcast?.imageOnAir() ?: videoSearch.liveThumbnail()
                    else -> videoSearch.thumb()
                },
                abExperiment = transformAbExperimentFragmentToAbExperiment(
                    abExperiment,
                    videoSearch.title().url()
                ),
                title = Title(
                    originProgramId = videoSearch.title().originProgramId(),
                    headline = videoSearch.title().headline()
                ),
                broadcast = Broadcast(
                    media = Media(
                        idWithDVR = videoSearch.broadcast()?.mediaId(),
                        idWithoutDVR = videoSearch.broadcast()?.withoutDVRMediaId()
                    ),
                    channel = Channel(trimmedLogo = broadcast?.channel()?.trimmedLogo())
                ),
                availableFor = AvailableFor.normalize(videoSearch.availableFor()),
                kind = normalizedKind
            )
        } ?: arrayListOf()

    internal fun transformBroadcastSearchFragmentResourceToChannelList(resources: List<ChannelSearchResultFragment.Resource>?) =
        resources?.map {
            val broadcastFragment = it.fragments().broadcasSearchFragment()
            Channel(
                broadcastFragment.id(),
                broadcastFragment.name(),
                broadcastFragment.pageIdentifier(),
                broadcastFragment.trimmedLogo()
            )
        } ?: listOf()


    internal fun transformTitleSearchFragmentToTitle(
        resources: List<TitleSearchResultFragment.Resource>?,
        abExperiment: ABExperimentSearchFragment?
    ) =
        resources?.map {
            val titleSearch = it.fragments().titleSearchFragment()

            return@map Title(
                titleId = titleSearch.titleId(),
                originProgramId = titleSearch.originProgramId(),
                headline = titleSearch.headline(),
                format = Format.normalize(titleSearch.format()),
                type = Type.normalize(titleSearch.type()),
                abExperiment = transformAbExperimentFragmentToAbExperiment(
                    abExperiment,
                    titleSearch.url()
                ),
                poster = when (device) {
                    Device.TV -> titleSearch.poster()?.tv()
                    Device.TABLET -> titleSearch.poster()?.tablet()
                    else -> titleSearch.poster()?.mobile()
                }
            )
        } ?: arrayListOf()

    internal fun transformResourceToSearchTopHits(itemsResponseList: List<SearchTopHitsQuery.Resource>?) =
        itemsResponseList?.map {
            return@map SearchTopHits(
                it.titleId(),
                it.originProgramId(),
                it.headline(),
                it.description(),
                it.poster()?.tv(),
                Format.normalize(it.format()),
                Type.normalize(it.type())
            )
        } ?: arrayListOf()

    internal fun transformSearchGlobalQueryToThumb(resources: List<SearchGlobalQuery.Resource>?) =
        resources?.map {
            val videoSearch = it.fragments().videoGlobalSearch()
            val broadcast = videoSearch.broadcast()
            val isValidBroadcast = !broadcast?.mediaId().isNullOrEmpty()

            val normalizedKind =
                if (isValidBroadcast) Kind.EVENT else Kind.normalize(videoSearch.kind())

            return@map Thumb(
                id = videoSearch.id(),
                headline = videoSearch.headline(),
                duration = videoSearch.duration() ?: 0,
                exhibitedAt = videoSearch.exhibitedAt(),
                thumb = when (normalizedKind) {
                    Kind.LIVE, Kind.EVENT -> videoSearch.liveThumbnail()
                    else -> videoSearch.thumb()
                },
                title = Title(
                    originProgramId = videoSearch.title().originProgramId(),
                    headline = videoSearch.title().headline()
                ),
                broadcast = Broadcast(
                    media = Media(
                        idWithDVR = videoSearch.broadcast()?.mediaId(),
                        idWithoutDVR = videoSearch.broadcast()?.withoutDVRMediaId()
                    )
                ),
                kind = normalizedKind
            )
        } ?: arrayListOf()

    internal fun transformSearchGlobalQueryToTitle(resources: List<SearchGlobalQuery.Resource1>?) =
        resources?.map {
            val titleSearch = it.fragments().titleGlobalSearch()
            return@map Title(
                originProgramId = titleSearch.originProgramId(),
                titleId = titleSearch.titleId(),
                headline = titleSearch.headline(),
                description = titleSearch.description(),
                video = Video(id = titleSearch.originVideoId()),
                releaseYear = titleSearch.releaseYear() ?: 0,
                background = titleSearch.cover()?.landscape(),
                type = Type.normalize(titleSearch.type())
            )
        } ?: arrayListOf()


    private fun builderGlobalSearchQuery(query: String?, coverScale: String) =
        builderImageSearch(SearchGlobalQuery.builder(), coverScale)
            .query(query ?: "")
            .build()

    private fun builderImageSearch(searchQuery: SearchGlobalQuery.Builder, coverScale: String) =
        searchQuery.coverLandscapeScales(CoverLandscapeScales.safeValueOf(coverScale))

    private fun builderSearchQuery(
        query: String?, scale: String,
        broadcastImageOnAirScales: String,
        broadcastChannelTrimmedLogoScales: String,
        page: Int, perPage: Int
    ) =
        builderImageSearch(SearchQuery.builder(), scale)
            .query(query ?: "")
            .page(page)
            .perPage(perPage)
            .broadcastImageOnAirScales(
                BroadcastImageOnAirScales.safeValueOf(
                    broadcastImageOnAirScales
                )
            )
            .broadcastChannelTrimmedLogoScales(
                BroadcastChannelTrimmedLogoScales.safeValueOf(
                    broadcastChannelTrimmedLogoScales
                )
            )
            .build()

    private fun builderSearchTitleQuery(
        query: String?,
        scale: String,
        page: Int,
        perPage: Int
    ) =
        builderImageSearchTitle(SearchTitleQuery.builder(), scale)
            .query(query ?: "")
            .page(page)
            .perPage(perPage)
            .build()

    internal fun builderSearchTopHitsQuery(scale: String, perPage: Int) =
        builderImageSearchTopHits(SearchTopHitsQuery.builder(), scale)
            .perPage(perPage)
            .rule(TitleRules.TOP_HITS)
            .build()

    internal fun builderSearchVideoQuery(
        query: String?, page: Int, perPage: Int,
        broadcastImageOnAirScales: String,
        broadcastChannelTrimmedLogoScales: String
    ) =
        SearchVideoQuery.builder()
            .query(query ?: "")
            .page(page)
            .perPage(perPage)
            .broadcastImageOnAirScales(
                BroadcastImageOnAirScales.safeValueOf(
                    broadcastImageOnAirScales
                )
            )
            .broadcastChannelTrimmedLogoScales(
                BroadcastChannelTrimmedLogoScales.safeValueOf(
                    broadcastChannelTrimmedLogoScales
                )
            )
            .build()

    internal fun builderSearchChannelQuery(
        query: String?,
        page: Int,
        perPage: Int,
        broadcastChannelTrimmedLogoScales: String
    ) =
        SearchChannelQuery.builder()
            .query(query ?: "")
            .page(page)
            .perPage(perPage)
            .broadcastChannelTrimmedLogoScales(
                BroadcastChannelTrimmedLogoScales.safeValueOf(
                    broadcastChannelTrimmedLogoScales
                )
            )
            .build()

    internal fun builderImageSearch(searchQuery: SearchQuery.Builder, scale: String) =
        when (device) {
            Device.TV -> searchQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))

            Device.TABLET -> searchQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    scale
                )
            )

            else -> searchQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }

    internal fun builderImageSearchTitle(
        searchTitleQuery: SearchTitleQuery.Builder,
        scale: String
    ) =
        when (device) {
            Device.TV -> searchTitleQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))

            Device.TABLET -> searchTitleQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    scale
                )
            )

            else -> searchTitleQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }

    internal fun builderImageSearchTopHits(
        searchTopHitsQuery: SearchTopHitsQuery.Builder,
        scale: String
    ) =
        when (device) {
            Device.TV -> searchTopHitsQuery.tvPosterScales(TVPosterScales.safeValueOf(scale))

            Device.TABLET -> searchTopHitsQuery.tabletPosterScales(
                TabletPosterScales.safeValueOf(
                    scale
                )
            )

            else -> searchTopHitsQuery.mobilePosterScales(MobilePosterScales.safeValueOf(scale))
        }
}
