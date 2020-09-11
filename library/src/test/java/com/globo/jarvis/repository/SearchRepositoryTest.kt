package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.*
import com.globo.jarvis.model.AvailableFor
import com.globo.jarvis.model.Kind
import com.globo.jarvis.model.Type
import com.globo.jarvis.search.SearchGlobalQuery
import com.globo.jarvis.search.SearchTopHitsQuery
import com.globo.jarvis.type.KindType
import com.globo.jarvis.type.SubscriptionType
import com.globo.jarvis.type.TitleFormat
import com.globo.jarvis.type.TitleType
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test

class SearchRepositoryTest : BaseUnitTest() {
    companion object {
        private const val QUERY = "mercenario"
        private const val POSTER_SCALE = "X1"
        private const val IMAGE_ON_AIR_SCALE = "X720"
        private const val CHANNEL_TRIMMED_LOGO_SCALE = "X224"
        private const val PAGE_DEFAULT = 1
        private const val PER_PAGE_DEFAULT = 24
    }

    @Test
    fun transformSearchQueryResourceToThumbCheckSecondItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val thumb =
            searchRepository.transformSearchQueryResourceToThumb(
                recoverSearchQueryVideoQuery(),
                recoverAbExperiment()
            )
                .lastOrNull()

        assert(thumb?.id == "3065536")
        assert(thumb?.title?.headline == "Globo Esporte MT")
        assert(thumb?.headline == "Transmissão ao Vi")
        assert(thumb?.duration == 0)
        assert(thumb?.exhibitedAt == "2014-03-19T23:32:00Z")
        assert(thumb?.thumb == "https://s2.glbimg.com/_Y0FqTiV57jf2Xp_CHron77IcII=/0x216/https://s2.glbimg.com/2JaDCs2FslvxWxR77j2978kuEd8=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/l/J78Zh9SIWTxXVrDBza2A/onairglobo.jpg")
        assert(thumb?.title?.originProgramId == "5259")
        assert(thumb?.kind == Kind.EVENT)
        assert(thumb?.availableFor == AvailableFor.ANONYMOUS)
    }

    //Search
    @Test
    fun transformSearchQueryResourceToThumbWhenListNullAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformSearchQueryResourceToThumb(
                null,
                recoverAbExperiment()
            ).isEmpty()
        )
    }

    @Test
    fun transformSearchQueryResourceToThumbWhenListEmptyAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformSearchQueryResourceToThumb(
                emptyList(),
                recoverAbExperiment()
            ).isEmpty()
        )
    }

    @Test
    fun testLoadSearchQueryWithValidResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/commom/search_commom_word_with_valid_query.json")

        val testObserver = searchRepository.all(
            QUERY,
            POSTER_SCALE,
            IMAGE_ON_AIR_SCALE,
            CHANNEL_TRIMMED_LOGO_SCALE
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            val searchTitle = it.first
            val searchChannel = it.second
            val searchVideos = it.third

            searchTitle.nextPage == 2
                    && searchTitle.hasNextPage
                    && searchTitle.totalCount == 38
                    && searchTitle.titleList?.isNullOrEmpty() == false

            searchChannel.nextPage == null
                    && !searchChannel.hasNextPage
                    && searchChannel.totalCount == 1
                    && searchChannel.channelList?.isNullOrEmpty() == false

            searchVideos.nextPage == 2
                    && searchVideos.hasNextPage
                    && searchVideos.totalCount == 41522
                    && searchVideos.thumbList?.isNullOrEmpty() == false
        }
    }

    @Test
    fun testLoadSearchQueryWithEmptyResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/commom/search_commom_word_with_empty_response.json")

        val testObserver = searchRepository.all(
            QUERY,
            POSTER_SCALE,
            IMAGE_ON_AIR_SCALE,
            CHANNEL_TRIMMED_LOGO_SCALE
        ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.values().isEmpty()
    }


    //Search Video
    @Test
    fun testLoadSearchVideoWithValidResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/video/search_video_with_valid_query.json")

        val testObserver =
            searchRepository.videos(
                QUERY,
                IMAGE_ON_AIR_SCALE,
                CHANNEL_TRIMMED_LOGO_SCALE,
                PAGE_DEFAULT,
                PER_PAGE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.thumbList?.size == 24
                    && it.thumbList!![0].headline.equals("Exposição imersiva encanta fãs do Batman em São Paulo")
                    && it.thumbList!![0].title?.originProgramId.equals("8478")
        }
    }

    @Test
    fun testLoadSearchVideoWithEmptyResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/video/search_video_with_empty_response.json")

        val testObserver =
            searchRepository.videos(
                QUERY,
                IMAGE_ON_AIR_SCALE,
                CHANNEL_TRIMMED_LOGO_SCALE,
                PAGE_DEFAULT,
                PER_PAGE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.values().isEmpty()
    }

    //Search Channel
    @Test
    fun testLoadSearchChannelWitoutNextPage() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/channel/search_channel_with_valid_query.json")

        val testObserver =
            searchRepository.channels(
                QUERY,
                CHANNEL_TRIMMED_LOGO_SCALE,
                PAGE_DEFAULT,
                PER_PAGE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.nextPage == null
            !it.hasNextPage
            it.totalCount == 20
                    && it.channelList?.size == 1
                    && it.channelList?.get(0)?.id == "196"
                    && it.channelList?.get(0)?.name == "TV Globo"
                    && it.channelList?.get(0)?.pageIdentifier == "tvglobo"
                    && it.channelList?.get(0)?.trimmedLogo == "https://s2.glbimg.com/eDR6TSRvsi8skz4ezGzLA4b48H4=/fit-in/48x24/https://s2.glbimg.com/9Wy1I7Dz4-R6xhNVpPqTLzd0mME=/trim/filters:fill(transparent,false)/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/V/q/33CD65RVK44W5BSLbx1g/rede-globo.png"
        }
    }

    @Test
    fun testLoadSearchChannelWithNextPage() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/channel/search_channel_with_next_page.json")

        val testObserver =
            searchRepository.channels(
                QUERY,
                CHANNEL_TRIMMED_LOGO_SCALE,
                PAGE_DEFAULT,
                PER_PAGE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.nextPage == 2
            it.hasNextPage
            it.totalCount == 20
                    && it.channelList?.size == 1
                    && it.channelList?.get(0)?.id == "196"
                    && it.channelList?.get(0)?.name == "TV Globo"
                    && it.channelList?.get(0)?.pageIdentifier == "tvglobo"
                    && it.channelList?.get(0)?.trimmedLogo == "https://s2.glbimg.com/eDR6TSRvsi8skz4ezGzLA4b48H4=/fit-in/48x24/https://s2.glbimg.com/9Wy1I7Dz4-R6xhNVpPqTLzd0mME=/trim/filters:fill(transparent,false)/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/V/q/33CD65RVK44W5BSLbx1g/rede-globo.png"
        }
    }

    @Test
    fun testLoadSearchChannelWithEmptyResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/channel/search_channel_with_empty_response.json")

        val testObserver =
            searchRepository.channels(
                QUERY,
                CHANNEL_TRIMMED_LOGO_SCALE,
                PAGE_DEFAULT,
                PER_PAGE_DEFAULT
            ).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.values().isEmpty()
    }


    //Search Title
    @Test
    fun testLoadSearchTitleWithValidResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/title/search_title_with_valid_query.json")

        val testObserver =
            searchRepository.titles(QUERY, POSTER_SCALE, PAGE_DEFAULT, PER_PAGE_DEFAULT).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.titleList?.size == 4
                    && it.titleList!![0].headline.equals("A Batalha De Riddick")
                    && it.titleList!![0].originProgramId.equals("10122")
        }
    }

    @Test
    fun testLoadSearchTitleWithEmptyResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/title/search_title_with_empty_response.json")

        val testObserver =
            searchRepository.titles(QUERY, POSTER_SCALE, PAGE_DEFAULT, PER_PAGE_DEFAULT).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.values().isEmpty()
    }


    //Global Search
    @Test
    fun testLoadSearchGlobalWithValidResponseCallingTransform() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/global/search_global_with_valid_query.json")

        searchRepository.global(QUERY, POSTER_SCALE)
        verify { searchRepository.transformSearchGlobalQueryToTitle(any()) }
        verify { searchRepository.transformSearchGlobalQueryToThumb(any()) }
    }

    @Test
    fun testLoadSearchGlobalWithValidResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/global/search_global_with_valid_query.json")

        val searchSync = searchRepository.global(QUERY, POSTER_SCALE)
        assert(
            searchSync.first.isEmpty()
        )
        assert(
            searchSync.second.size == 2
                    && searchSync.second[0].headline.equals("Agora na Globo")
                    && searchSync.second[0].title?.originProgramId.equals("9095")
        )
    }

    @Test
    fun testLoadSearchGlobalWithInvalidResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/global/search_global_with_invalid_query.json")

        val searchSync = searchRepository.global(QUERY, POSTER_SCALE)
        assert(searchSync.first.isEmpty())
        assert(searchSync.second.isEmpty())
    }


    //Search Top Hits
    @Test
    fun testLoadSearchTitleTopHits() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        searchRepository.searchTopHits(POSTER_SCALE, PER_PAGE_DEFAULT)
        verify { searchRepository.builderSearchTopHitsQuery("X1", 24) }
    }

    @Test
    fun testLoadSearchTitleTopHitsWithValidResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/tophits/search_title_top_hits_with_valid_query.json")

        val testObserver = searchRepository.searchTopHits(POSTER_SCALE).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.assertValue {
            it.size == 3
                    && it[0].headline == "A Dona do Pedaço"
                    && it[1].headline == "Bom Sucesso"
                    && it[2].headline == "Jornal Nacional"
        }
    }

    @Test
    fun testLoadSearchTitleTopHitsWithEmptyResponse() {
        val searchRepository =
            spyk(
                SearchRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        enqueueResponse("search/tophits/search_title_top_hits_with_empty_response.json")

        val testObserver = searchRepository.searchTopHits(POSTER_SCALE).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        testObserver.values().isEmpty()
    }


    //Transform SearchVideoQueryResource To Thumb
    @Test
    fun transformSearchQueryResourceToThumbWhenListValidAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val titleList =
            searchRepository.transformSearchQueryResourceToThumb(
                recoverSearchQueryVideoQuery(),
                recoverAbExperiment()
            )

        assert(titleList.isNotEmpty())
        assert(titleList.size == 2)
    }

    @Test
    fun transformSearchQueryResourceToThumbCheckFirstItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val thumb =
            searchRepository.transformSearchQueryResourceToThumb(
                recoverSearchQueryVideoQuery(),
                recoverAbExperiment()
            )
                .firstOrNull()

        assert(thumb?.id == "8083473")
        assert(thumb?.title?.headline == "Bom Dia Cidade – Sul de Minas")
        assert(thumb?.headline == "Engenheira agrônoma dá dicas para culti de samambaias em casa")
        assert(thumb?.duration == 30264)
        assert(thumb?.formattedDuration == "30 seg")
        assert(thumb?.exhibitedAt == "2019-11-13T12:14:00Z")
        assert(thumb?.thumb == "https://s02.video.glbimg.com/x216/8083473.jpg")
        assert(thumb?.title?.originProgramId == "5707")
        assert(thumb?.kind == Kind.EXCERPT)
        assert(thumb?.availableFor == AvailableFor.LOGGED_IN)
    }

    //Transform SearchVideoQueryResource To Thumb
    @Test
    fun transformSearchVideoQueryResourceToThumbWhenListValidAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val titleList =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(),
                recoverAbExperiment()
            )

        assert(titleList.isNotEmpty())
        assert(titleList.size == 2)
    }

    @Test
    fun transformSearchVideoQueryResourceToThumbCheckFirstItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val thumb =
            searchRepository.transformSearchQueryResourceToThumb(
                recoverSearchQueryVideoQuery(), recoverAbExperiment()
            )
                .firstOrNull()

        assert(thumb?.id == "8083473")
        assert(thumb?.title?.headline == "Bom Dia Cidade – Sul de Minas")
        assert(thumb?.headline == "Engenheira agrônoma dá dicas para culti de samambaias em casa")
        assert(thumb?.duration == 30264)
        assert(thumb?.formattedDuration == "30 seg")
        assert(thumb?.exhibitedAt == "2019-11-13T12:14:00Z")
        assert(thumb?.thumb == "https://s02.video.glbimg.com/x216/8083473.jpg")
        assert(thumb?.title?.originProgramId == "5707")
        assert(thumb?.kind == Kind.EXCERPT)
        assert(thumb?.availableFor == AvailableFor.LOGGED_IN)
    }

    @Test
    fun transformSearchVideoQueryResourceToThumbCheckSecondItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val thumb =
            searchRepository.transformSearchQueryResourceToThumb(
                recoverSearchQueryVideoQuery(), recoverAbExperiment()
            )
                .lastOrNull()

        assert(thumb?.id == "3065536")
        assert(thumb?.title?.headline == "Globo Esporte MT")
        assert(thumb?.headline == "Transmissão ao Vi")
        assert(thumb?.duration == 0)
        assert(thumb?.exhibitedAt == "2014-03-19T23:32:00Z")
        assert(thumb?.thumb == "https://s2.glbimg.com/_Y0FqTiV57jf2Xp_CHron77IcII=/0x216/https://s2.glbimg.com/2JaDCs2FslvxWxR77j2978kuEd8=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/l/J78Zh9SIWTxXVrDBza2A/onairglobo.jpg")
        assert(thumb?.title?.originProgramId == "5259")
        assert(thumb?.kind == Kind.EVENT)
        assert(thumb?.availableFor == AvailableFor.ANONYMOUS)
    }

    @Test
    fun transformSearchVideoQueryResourceToThumbWhenListNullAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformSearchQueryResourceToThumb(
                null,
                recoverAbExperiment()
            ).isEmpty()
        )
    }

    @Test
    fun transformSearchVideoQueryResourceToThumbWhenListEmptyAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformSearchQueryResourceToThumb(
                emptyList(),
                recoverAbExperiment()
            ).isEmpty()
        )
    }


    //Transform SearchQueryResource1 To Title
    @Test
    fun transformSearchQueryResourceToTitleToTitleWhenListValidAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val topHits =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(),
                recoverAbExperiment()
            )

        assert(topHits.isNotEmpty())
        assert(topHits.size == 2)
    }

    @Test
    fun transformSearchQueryResourceToTitleTitleCheckFirstItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(),
                recoverAbExperiment()
            )
                .firstOrNull()

        assert(title?.originProgramId == "11730")
        assert(title?.titleId == "GS1KG9Tnrv")
        assert(title?.headline == "Xand Avião – Errejota Ao Vi")
        assert(title?.type == Type.PROGRAM)
    }

    @Test
    fun transformSearchQueryResourceToTitleCheckSecondItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(),
                recoverAbExperiment()
            )
                .lastOrNull()

        assert(title?.originProgramId == "11384")
        assert(title?.titleId == "kwM9KVSz5z")
        assert(title?.headline == "The Good Wife")
        assert(title?.type == Type.SERIES)
    }

    @Test
    fun transformSearchQueryResourceToTitleCheckPosterMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(),
                recoverAbExperiment()
            )
                .firstOrNull()

        assert(title?.poster == "https://s2.glbimg.com/XKo4FPgrtxngA-KzyE0nzVl8r9U=/100x152/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg")
    }

    @Test
    fun transformSearchQueryResourceToTitleCheckPosterTablet() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.TABLET))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(),
                recoverAbExperiment()
            )
                .firstOrNull()
        assert(title?.poster == "https://s2.glbimg.com/DKlZBiUchmfY6SP7VWP3nIntJRg=/144x212/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg")
    }

    @Test
    fun transformSearchQueryResourceToTitleCheckPosterTv() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.TABLET))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(),
                recoverAbExperiment()
            )
                .firstOrNull()
        assert(title?.poster == "https://s2.glbimg.com/DKlZBiUchmfY6SP7VWP3nIntJRg=/144x212/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg")
    }

    @Test
    fun transformSearchQueryResourceToTitleWhenListNullAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformTitleSearchFragmentToTitle(
                null,
                recoverAbExperiment()
            ).isEmpty()
        )
    }

    @Test
    fun transformSearchQueryResource1ToTitleWhenListEmptyAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformTitleSearchFragmentToTitle(
                emptyList(),
                recoverAbExperiment()
            ).isEmpty()
        )
    }


    //transformResource SearchTitleQueryResource Title
    @Test
    fun transformSearchTitleQueryResourceToTitleWhenListValidAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val topHits =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(), recoverAbExperiment()
            )

        assert(topHits.isNotEmpty())
        assert(topHits.size == 2)
    }

    @Test
    fun transformSearchTitleQueryResourceToTitleCheckFirstItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(), recoverAbExperiment()
            )
                .firstOrNull()

        assert(title?.originProgramId == "11730")
        assert(title?.titleId == "GS1KG9Tnrv")
        assert(title?.headline == "Xand Avião – Errejota Ao Vi")
        assert(title?.type == Type.PROGRAM)
    }

    @Test
    fun transformSearchTitleQueryResourceToTitleCheckSecondItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(), recoverAbExperiment()
            )
                .lastOrNull()

        assert(title?.originProgramId == "11384")
        assert(title?.titleId == "kwM9KVSz5z")
        assert(title?.headline == "The Good Wife")
        assert(title?.type == Type.SERIES)
    }

    @Test
    fun transformSearchTitleQueryResourceToTitleCheckPosterMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(), recoverAbExperiment()
            )
                .firstOrNull()

        assert(title?.poster == "https://s2.glbimg.com/XKo4FPgrtxngA-KzyE0nzVl8r9U=/100x152/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg")
    }

    @Test
    fun transformSearchTitleQueryResourceToTitleCheckPosterTablet() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.TABLET))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(), recoverAbExperiment()
            )
                .firstOrNull()
        assert(title?.poster == "https://s2.glbimg.com/DKlZBiUchmfY6SP7VWP3nIntJRg=/144x212/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg")
    }

    @Test
    fun transformSearchTitleQueryResourceToTitleCheckPosterTv() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.TV))

        val title =
            searchRepository.transformTitleSearchFragmentToTitle(
                recoverSearchQueryResource1(), recoverAbExperiment()
            )
                .firstOrNull()
        assert(title?.poster == "https://s2.glbimg.com/kaQqa6bP6kKyNmDGhK2bObdTjZM=/146x216/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg")
    }

    @Test
    fun transformSearchTitleQueryResourceToTitleWhenListNullAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformTitleSearchFragmentToTitle(
                null,
                recoverAbExperiment()
            ).isEmpty()
        )
    }

    @Test
    fun transformSearchTitleQueryResourceToTitleWhenListEmptyAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(
            searchRepository.transformTitleSearchFragmentToTitle(
                emptyList(),
                recoverAbExperiment()
            ).isEmpty()
        )
    }


    //TransformResource To TopHits
    @Test
    fun transformResourceToTopHitsWhenListValidAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val topHits =
            searchRepository.transformResourceToSearchTopHits(recoverTopHitsQueryResource())

        assert(topHits.isNotEmpty())
        assert(topHits.size == 2)
    }

    @Test
    fun transformResourceToTopHitsCheckFirstItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val topHits =
            searchRepository.transformResourceToSearchTopHits(recoverTopHitsQueryResource())
                .firstOrNull()


        assert(topHits?.headline == "A Dona do Pedaço")
    }

    @Test
    fun transformResourceToTopHitsCheckSecondItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val topHits =
            searchRepository.transformResourceToSearchTopHits(recoverTopHitsQueryResource())
                .lastOrNull()

        assert(topHits?.headline == "Bom Sucesso")
    }

    @Test
    fun transformResourceToTopHitsWhenListNullAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(searchRepository.transformResourceToSearchTopHits(null).isEmpty())
    }

    @Test
    fun transformResourceToTopHitsWhenListEmptyAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(searchRepository.transformResourceToSearchTopHits(emptyList()).isEmpty())
    }

    //Transform GlobalSearchQueryResource To Thumb
    @Test
    fun transformGlobalSearchQueryResourceToThumbWhenListValidAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val titleList =
            searchRepository.transformSearchGlobalQueryToThumb(
                recoverGlobalSearchQueryResource()
            )

        assert(titleList.isNotEmpty())
        assert(titleList.size == 2)
    }

    @Test
    fun transformGlobalSearchQueryResourceToThumbCheckFirstItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val thumb =
            searchRepository.transformSearchGlobalQueryToThumb(
                recoverGlobalSearchQueryResource()
            )
                .firstOrNull()

        assert(thumb?.id == "8083473")
        assert(thumb?.title?.headline == "Bom Dia Cidade – Sul de Minas")
        assert(thumb?.headline == "Engenheira agrônoma dá dicas para culti de samambaias em casa")
        assert(thumb?.duration == 200267)
        assert(thumb?.exhibitedAt == "2019-11-13T12:14:00Z")
        assert(thumb?.thumb == "thumbnail")
        assert(thumb?.title?.headline == "Bom Dia Cidade – Sul de Minas")
        assert(thumb?.kind == Kind.EVENT)
    }

    @Test
    fun transformGlobalSearchQueryResourceToThumbCheckSecondItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val thumb =
            searchRepository.transformSearchGlobalQueryToThumb(
                recoverGlobalSearchQueryResource()
            )
                .lastOrNull()

        assert(thumb?.id == "3065536")
        assert(thumb?.title?.headline == "Globo Esporte MT")
        assert(thumb?.headline == "Transmissão ao Vi")
        assert(thumb?.duration == 0)
        assert(thumb?.exhibitedAt == "2014-03-19T23:32:00Z")
        assert(thumb?.thumb == "https://live-thumbs.video.globo.com/gemt/snapshot")
        assert(thumb?.title?.originProgramId == "5259")
        assert(thumb?.kind == Kind.EVENT)
    }

    @Test
    fun transformGlobalSearchQueryResourceToThumbWhenListNullAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(searchRepository.transformSearchGlobalQueryToThumb(null).isEmpty())
    }

    @Test
    fun transformGlobalSearchQueryResourceToThumbWhenListEmptyAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(searchRepository.transformSearchGlobalQueryToThumb(emptyList()).isEmpty())
    }


    //Transform GlobalSearchQueryResource To Title
    @Test
    fun transformGlobalSearchQueryResourceToTitleWhenListValidAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val titleList =
            searchRepository.transformSearchGlobalQueryToTitle(
                recoverGlobalSearchQueryResource1()
            )

        assert(titleList.isNotEmpty())
        assert(titleList.size == 2)
    }

    @Test
    fun transformGlobalSearchQueryResourceToTitleCheckFirstItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformSearchGlobalQueryToTitle(
                recoverGlobalSearchQueryResource1()
            )
                .firstOrNull()

        assert(title?.originProgramId == "10122")
        assert(title?.titleId == "WWmRTCCqbp")
        assert(title?.headline == "Os Mercenários")
        assert(title?.description == "Um grupo de mercenários liderados por Barney Ross é contratado para derrubar o ditador Garza da Ilha de Vilena. No entanto, ao chegarem ao local, percebem que estão em desvantagem contra as forças de Garza e querem ir embora. Mas, ao conhecer Sandra, que luta contra o governo, eles mudam de ideia.")
        assert(title?.releaseYear == 2010)
        assert(title?.video?.id == "6981724")
        assert(title?.type == Type.MOVIES)
        assert(title?.background == "https://s2.glbimg.com/ASNGJScPoYwN54LwbmbaNpAA2GU=/100x152/https://s2.glbimg.com/Rb2s6_3K05dSj7Y2_uFekb-vcaU=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/T/7/jlMwdUTQqDzw8Kt0Nj8w/2018-073-urgente-media-kit-filmes-internacionais-telecine-parte-2-the-expendables-filme-poster-hd.jpg")
    }

    @Test
    fun transformGlobalSearchQueryResourceToTitleCheckSecondItem() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        val title =
            searchRepository.transformSearchGlobalQueryToTitle(
                recoverGlobalSearchQueryResource1()
            )
                .lastOrNull()

        assert(title?.originProgramId == "8884")
        assert(title?.titleId == "XKFkSFB7cS")
        assert(title?.headline == "É de Casa")
        assert(title?.description == "Pode entrar que ‘É de Casa’! Esse é o espírito da atração que tem como tema central a vida dentro da casa e reúne assuntos ligados à moda, decoração, serviço e cotidiano.")
        assert(title?.releaseYear == 0)
        assert(title?.video?.id == null)
        assert(title?.type == Type.PROGRAM)
        assert(title?.background == "https://s2.glbimg.com/d9IoSACtwXvgRnvhtaxg4FPF9kc=/0x720/https://s2.glbimg.com/sqOS0I_CDYtXsBKwAK8pPOSn44E=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/0/G/CAT8qKRtaH9E7Z3V9ZbQ/8884-e-de-casa-background.jpg")
    }

    @Test
    fun transformGlobalSearchQueryResourceToTitleWhenListNullAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(searchRepository.transformSearchGlobalQueryToTitle(null).isEmpty())
    }

    @Test
    fun transformGlobalSearchQueryResourceToTitleWhenListEmptyAndMobile() {
        val searchRepository =
            spyk(SearchRepository(apolloClient, Device.MOBILE))

        assert(searchRepository.transformSearchGlobalQueryToTitle(emptyList()).isEmpty())
    }

    private fun recoverSearchQueryResource1() = listOf(
        TitleSearchResultFragment.Resource(
            "typeName", TitleSearchResultFragment.Resource.Fragments(
                TitleSearchFragment(
                    "Title",
                    TitleFormat.SHOWS,
                    "11730",
                    "GS1KG9Tnrv",
                    "Xand Avião – Errejota Ao Vi",
                    TitleType.TV_PROGRAM,
                    "",
                    TitleSearchFragment.Poster(
                        "Poster",
                        "https://s2.glbimg.com/XKo4FPgrtxngA-KzyE0nzVl8r9U=/100x152/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg",
                        "https://s2.glbimg.com/DKlZBiUchmfY6SP7VWP3nIntJRg=/144x212/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg",
                        "https://s2.glbimg.com/kaQqa6bP6kKyNmDGhK2bObdTjZM=/146x216/https://s2.glbimg.com/qDhUz_qf4duqosaKyZaG05mLtL8=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/N/B/QGbGEoRnODZHdGFcwVQQ/2019-468-som-livre-xand-aviao-errejota-poster.jpg"
                    )
                )
            )
        ),
        TitleSearchResultFragment.Resource(
            "typeName", TitleSearchResultFragment.Resource.Fragments(
                TitleSearchFragment(
                    "Title",
                    TitleFormat.SHOWS,
                    "11384",
                    "kwM9KVSz5z",
                    "The Good Wife",
                    TitleType.SERIE,
                    "",
                    TitleSearchFragment.Poster(
                        "Poster",
                        "https://s2.glbimg.com/NhbvvKHJsgr-izTsUKB9IAhsb2o=/100x152/https://s2.glbimg.com/99qNF2hPYMqfHCSJNtnxazF9f9w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/8/R/MieJBTT2ePubA5vIh7RA/2019-333-midia-kit-the-good-wife-poster.jpg",
                        "https://s2.glbimg.com/phdxBYnh1U6xQvJjsQqi374bad8=/144x212/https://s2.glbimg.com/99qNF2hPYMqfHCSJNtnxazF9f9w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/8/R/MieJBTT2ePubA5vIh7RA/2019-333-midia-kit-the-good-wife-poster.jpg",
                        "https://s2.glbimg.com/DAAV5lF6miARL3oI4LlOy2bn2yg=/146x216/https://s2.glbimg.com/99qNF2hPYMqfHCSJNtnxazF9f9w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/8/R/MieJBTT2ePubA5vIh7RA/2019-333-midia-kit-the-good-wife-poster.jpg"
                    )
                )
            )
        )
    )

    private fun recoverGlobalSearchQueryResource1() = listOf(
        SearchGlobalQuery.Resource1(
            "TitleCollection",
            SearchGlobalQuery.Resource1.Fragments(
                TitleGlobalSearch(
                    "Title",
                    "10122",
                    "WWmRTCCqbp",
                    "Os Mercenários",
                    "Um grupo de mercenários liderados por Barney Ross é contratado para derrubar o ditador Garza da Ilha de Vilena. No entanto, ao chegarem ao local, percebem que estão em desvantagem contra as forças de Garza e querem ir embora. Mas, ao conhecer Sandra, que luta contra o governo, eles mudam de ideia.",
                    2010,
                    "6981724",
                    TitleType.MOVIE,
                    TitleGlobalSearch.Cover(
                        "Cover",
                        "https://s2.glbimg.com/ASNGJScPoYwN54LwbmbaNpAA2GU=/100x152/https://s2.glbimg.com/Rb2s6_3K05dSj7Y2_uFekb-vcaU=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/T/7/jlMwdUTQqDzw8Kt0Nj8w/2018-073-urgente-media-kit-filmes-internacionais-telecine-parte-2-the-expendables-filme-poster-hd.jpg"
                    )
                )
            )
        ),
        SearchGlobalQuery.Resource1(
            "TitleCollection",
            SearchGlobalQuery.Resource1.Fragments(
                TitleGlobalSearch(
                    "Title",
                    "8884",
                    "XKFkSFB7cS",
                    "É de Casa",
                    "Pode entrar que ‘É de Casa’! Esse é o espírito da atração que tem como tema central a vida dentro da casa e reúne assuntos ligados à moda, decoração, serviço e cotidiano.",
                    null,
                    null,
                    TitleType.TV_PROGRAM,
                    TitleGlobalSearch.Cover(
                        "Cover",
                        "https://s2.glbimg.com/d9IoSACtwXvgRnvhtaxg4FPF9kc=/0x720/https://s2.glbimg.com/sqOS0I_CDYtXsBKwAK8pPOSn44E=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/0/G/CAT8qKRtaH9E7Z3V9ZbQ/8884-e-de-casa-background.jpg"
                    )
                )
            )
        )
    )

    private fun recoverGlobalSearchQueryResource() = listOf(
        SearchGlobalQuery.Resource(
            "VideoCollection",
            SearchGlobalQuery.Resource.Fragments(
                VideoGlobalSearch(
                    "Video",
                    "8083473",
                    "Engenheira agrônoma dá dicas para culti de samambaias em casa",
                    200267,
                    KindType.EXCERPT,
                    "https://s02.video.glbimg.com/x216/8083473.jpg",
                    "thumbnail",
                    "2019-11-13T12:14:00Z",
                    VideoGlobalSearch.Broadcast("Title", "5707", "1212"),
                    VideoGlobalSearch.Title("Title", "5707", "Bom Dia Cidade – Sul de Minas")
                )
            )
        ),
        SearchGlobalQuery.Resource(
            "VideoCollection",
            SearchGlobalQuery.Resource.Fragments(
                VideoGlobalSearch(
                    "Video",
                    "3065536",
                    "Transmissão ao Vi",
                    null,
                    KindType.LIVE,
                    "https://s01.video.glbimg.com/x216/3065536.jpg",
                    "https://live-thumbs.video.globo.com/gemt/snapshot",
                    "2014-03-19T23:32:00Z",
                    VideoGlobalSearch.Broadcast("Title", "5707", "1212"),
                    VideoGlobalSearch.Title("Title", "5259", "Globo Esporte MT")
                )
            )
        )
    )

    private fun recoverAbExperiment() =
        ABExperimentSearchFragment("typeName", "experiment", "trackId", "pathUrl", "alternative")

    private fun recoverSearchQueryVideoQuery() = listOf(
        VideoSearchResultFragment.Resource(
            "typeName", VideoSearchResultFragment.Resource.Fragments(
                VideoSearchFragment(
                    "VideoCollection",
                    "8083473",
                    "Engenheira agrônoma dá dicas para culti de samambaias em casa",
                    30264,
                    "30 seg",
                    KindType.EXCERPT,
                    SubscriptionType.LOGGED_IN,
                    "https://s02.video.glbimg.com/x216/8083473.jpg",
                    null,
                    "2019-11-13T12:14:00Z",
                    null,
                    VideoSearchFragment.Title("Title", "", "5707", "Bom Dia Cidade – Sul de Minas")
                )
            )
        ),
        VideoSearchResultFragment.Resource(
            "typeName", VideoSearchResultFragment.Resource.Fragments(
                VideoSearchFragment(
                    "VideoCollection",
                    "3065536",
                    "Transmissão ao Vi",
                    null,
                    null,
                    KindType.LIVE,
                    SubscriptionType.ANONYMOUS,
                    "https://s01.video.glbimg.com/x216/3065536.jpg",
                    "https://live-thumbs.video.globo.com/gemt/snapshot",
                    "2014-03-19T23:32:00Z",
                    VideoSearchFragment.Broadcast(
                        "Title",
                        "5707",
                        "1212",
                        "https://s2.glbimg.com/_Y0FqTiV57jf2Xp_CHron77IcII=/0x216/https://s2.glbimg.com/2JaDCs2FslvxWxR77j2978kuEd8=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/l/J78Zh9SIWTxXVrDBza2A/onairglobo.jpg",
                        VideoSearchFragment.Channel(
                            "typename",
                            "https://s2.glbimg.com/-nO5YcQqoHlSGAKobaJCkvo9Ep4=/fit-in/168x84/https://s2.glbimg.com/ADsu7q5pwuO91NFrVKCN4ukWuww=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/m/l/XdyOZSTAygXlmlT36uLQ/globo.png"
                        )
                    ),
                    VideoSearchFragment.Title("Title", "", "5259", "Globo Esporte MT")
                )
            )
        )
    )

    private fun recoverTopHitsQueryResource() = listOf(
        SearchTopHitsQuery.Resource(
            "Title",
            "123456",
            "abcdef",
            "A Dona do Pedaço",
            "Aclamado pela crítica",
            TitleFormat.MADE_FOR_TV,
            TitleType.TV_PROGRAM,
            null
        ),
        SearchTopHitsQuery.Resource(
            "Title",
            "789012",
            "qwerty",
            "Bom Sucesso",
            "Aclamado pela crítica",
            TitleFormat.MADE_FOR_TV,
            TitleType.TV_PROGRAM,
            null
        )
    )

}