package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.channels.BroadcastChannelsQuery
import com.globo.jarvis.type.BroadcastChannelFilters
import com.globo.jarvis.type.BroadcastChannelTrimmedLogoScales
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ChannelsRepositoryTest : BaseUnitTest() {
    companion object {
        private const val SCALE_LOGO = "X224"
    }

    private lateinit var channelsRepository: ChannelsRepository

    @Before
    fun setup() {
        channelsRepository = spyk(ChannelsRepository(apolloClient))
    }

    @Test
    fun testAll() {
        enqueueResponse("channels/channels_default_values.json")

        channelsRepository
            .all(broadcastChannelTrimmedLogoScales = SCALE_LOGO)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val nextPage = it.first
                val hasNextPage = it.second
                val broadcastChannels = it.third

                val channelGlobo = broadcastChannels.getOrNull(0)
                val channelPremiere = broadcastChannels.getOrNull(1)

                nextPage == null
                        && !hasNextPage
                broadcastChannels.size == 5
                        && channelGlobo?.id == "196"
                        && channelGlobo.name == "TV Globo"
                        && channelGlobo.pageIdentifier == "globo"
                        && channelGlobo.trimmedLogo == "https://s2.glbimg.com/zxW9kW0nkGzXlfJm-OR5F_2b0XI=/fit-in/448x224/https://s2.glbimg.com/ADsu7q5pwuO91NFrVKCN4ukWuww=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/m/l/XdyOZSTAygXlmlT36uLQ/globo.png"
                        && channelPremiere?.id == "1995"
                        && channelPremiere.name == "Premiere"
                        && channelPremiere.pageIdentifier == "premiere"
                        && channelPremiere.trimmedLogo == "https://s2.glbimg.com/s6I0il6n_mAPFfamLfrXvx6e78Q=/fit-in/448x224/https://s2.glbimg.com/tOzge14WRNKI9re5vfunjO6VmMQ=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/4/Y/0bUQrARCmg9BwBIxcZRw/logo-premiere285x285px.png"
            }
    }

    @Test
    fun testAllWithoutHasNextPage() {
        enqueueResponse("channels/channels_without_has_next_page.json")

        channelsRepository
            .all(broadcastChannelTrimmedLogoScales = SCALE_LOGO)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val hasNextPage = it.second
                !hasNextPage
            }
    }

    @Test
    fun testAllJsonInvalid() {
        enqueueResponse("channels/channels_invalid.json")

        channelsRepository
            .all(broadcastChannelTrimmedLogoScales = SCALE_LOGO)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val nextPage = it.first
                val hasNextPage = it.second
                val broadcastChannels = it.third

                nextPage == null
                        && !hasNextPage
                        && broadcastChannels.isEmpty()
            }
    }

    @Test
    fun testAllWithoutNextPage() {
        enqueueResponse("channels/channels_without_next_page.json")

        channelsRepository
            .all(broadcastChannelTrimmedLogoScales = SCALE_LOGO)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.first == null
            }
    }

    @Test
    fun testPage() {
        enqueueResponse("channels/channels_default_values.json")

        channelsRepository.all(page = 20, broadcastChannelTrimmedLogoScales = SCALE_LOGO)

        verify {
            channelsRepository.buildQueryChannels(20, 12, SCALE_LOGO, null)
        }
    }

    @Test
    fun testPerPage() {
        enqueueResponse("channels/channels_default_values.json")

        channelsRepository.all(perPage = 100, broadcastChannelTrimmedLogoScales = SCALE_LOGO)

        verify {
            channelsRepository.buildQueryChannels(1, 100, SCALE_LOGO, null)
        }
    }

    @Test
    fun testFilter() {
        enqueueResponse("channels/channels_default_values.json")

        channelsRepository.all(
            broadcastChannelFilters = BroadcastChannelFilters.WITH_PAGES,
            broadcastChannelTrimmedLogoScales = SCALE_LOGO
        )

        verify {
            channelsRepository.buildQueryChannels(
                1,
                12,
                SCALE_LOGO,
                BroadcastChannelFilters.WITH_PAGES
            )
        }
    }

    @Test
    fun testTransformBroadcastListToChannelListWithValidChannels() {
        val queryChannels = channelsRepository
            .transformBroadcastListToChannelList(
                listOf(
                    BroadcastChannelsQuery.Resource(
                        "typename",
                        "24532",
                        "Tv Globo",
                        "globo",
                        "https://s2.glbimg.com/Tff181WH3pUydHz1CZByilDvDls=/fit-in/84x42/https://s2.glbimg.com/kL_UPRW5kuYPAJ4-MVU4GI2a-Qc=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/p/r/AzV1BMTgScfblhcB7iHw/globo-png.png"
                    )
                )
            )

        val channelGlobo = queryChannels.getOrNull(0)


        assert(channelGlobo?.id == "24532")
        assert(channelGlobo?.name == "Tv Globo")
        assert(channelGlobo?.pageIdentifier == "globo")
        assert(channelGlobo?.trimmedLogo == "https://s2.glbimg.com/Tff181WH3pUydHz1CZByilDvDls=/fit-in/84x42/https://s2.glbimg.com/kL_UPRW5kuYPAJ4-MVU4GI2a-Qc=/trim/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/p/r/AzV1BMTgScfblhcB7iHw/globo-png.png")
    }

    @Test
    fun testTransformBroadcastListToChannelListWithNullChannels() {
        val queryChannels = channelsRepository.transformBroadcastListToChannelList(null)

        assert(queryChannels.isEmpty())
    }

    @Test
    fun testTransformBroadcastListToChannelListWithEmptyChannels() {
        val queryChannels = channelsRepository.transformBroadcastListToChannelList(listOf())
        assert(queryChannels.isEmpty())
    }

    @Test
    fun testBuildQueryChannelsWithFilter() {
        val queryChannels = channelsRepository
            .buildQueryChannels(1, 12, SCALE_LOGO, BroadcastChannelFilters.WITH_PAGES)

        assert(queryChannels.variables().page().value == 1)
        assert(queryChannels.variables().perPage().value == 12)
        assert(
            queryChannels.variables()
                .broadcastChannelTrimmedLogoScales().value == BroadcastChannelTrimmedLogoScales.X224
        )
        assert(
            queryChannels.variables()
                .filtersInput().value?.filter() == BroadcastChannelFilters.WITH_PAGES
        )
    }

    @Test
    fun testBuildQueryChannelsWithoutFilter() {
        val queryChannels = channelsRepository
            .buildQueryChannels(1, 12, SCALE_LOGO, null)


        assert(queryChannels.variables().page().value == 1)
        assert(queryChannels.variables().perPage().value == 12)
        assert(queryChannels.variables().filtersInput().value?.filter() == null)
        assert(
            queryChannels.variables()
                .broadcastChannelTrimmedLogoScales().value == BroadcastChannelTrimmedLogoScales.X224
        )
    }
}