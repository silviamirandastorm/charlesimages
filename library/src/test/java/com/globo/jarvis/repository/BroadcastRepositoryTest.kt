package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.BroadcastFragment
import com.globo.jarvis.fragment.EPGSlotCoreFragment
import com.globo.jarvis.type.SubscriptionType
import io.mockk.spyk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BroadcastRepositoryTest : BaseUnitTest() {
    private lateinit var repository: BroadcastRepository

    @Before
    fun setup() {
        repository = BroadcastRepository(apolloClient, Device.TABLET)
    }

    @Test
    fun testTransformMediaMediaVO() {

        val broadcastRepository =
            spyk(
                BroadcastRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val broadcastQuery = broadcastRepository.transformMediaMediaVO(
            "idPromotional",
            "idWithDVR",
            "idWithoutDVR",
            "headline",
            buildQueryMedia(),
            "imageOnAir"
        )

        assert(broadcastQuery?.idPromotional == "idPromotional")
        assert(broadcastQuery?.idWithDVR == "idWithDVR")
        assert(broadcastQuery?.idWithoutDVR == "idWithoutDVR")
        assert(broadcastQuery?.headline == "headline")
        assert(broadcastQuery?.imageOnAir == "imageOnAir")
        assert(broadcastQuery?.subscriptionService?.faq?.qrCode?.mobile == "mobile")
        assert(broadcastQuery?.subscriptionService?.faq?.url?.mobile == "mobile")
        assert(broadcastQuery?.subscriptionService?.salesPage?.identifier?.mobile == "mobile")
    }

    @Test
    fun `apply details to channel`() {
        enqueueResponse("broadcast/broadcast_valid.json")
        val idWithDVR = "123112"

        repository
            .details(idWithDVR, null, null, "X285", "X224", "X720", "X1536")
            .test()
            .also {
                it.awaitTerminalEvent()
            }
            .assertComplete()
            .assertValue { true }

    }

    @Test
    fun `throw exception when response query is null`() {
        enqueueResponse("broadcast/broadcast_invalid.json")

        val idWithDVR = "123112"
        repository
            .details(idWithDVR, null, null, "X285", "X224", "X720", "X1536")
            .test()
            .also {
                it.awaitTerminalEvent()
            }
            .assertError { true }
            .assertErrorMessage("Houve um erro ao tentar carregar os detalhes do canal com id $idWithDVR")
    }

    @Test
    fun `transform payTvInfo to null when backend returns payTvServiceId 0`() {
        val channelFragment = getChannelFragment(
            payServiceId = 0
        )

        Assert.assertEquals(
            null,
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.serviceId
        )
    }

    @Test
    fun `transform payTvInfo to null when backend returns payTvServiceId null`() {
        val channelFragment = getChannelFragment(
            payServiceId = null
        )

        Assert.assertEquals(
            null,
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.serviceId
        )
    }

    @Test
    fun `transform payTvServiceId to valid id when backend returns valid result`() {
        val payServiceId = 1234

        val channelFragment = getChannelFragment(
            payServiceId = payServiceId
        )

        Assert.assertEquals(
            payServiceId,
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.serviceId
        )
    }

    @Test
    fun `transform requireUserTeam to false when backend returns invalid result`() {
        val channelFragment = getChannelFragment()

        Assert.assertEquals(
            false,
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.requireUserTeam
        )
    }

    @Test
    fun `transform requireUserTeam to valid when backend returns valid result`() {
        val channelFragment = getChannelFragment(
            requireUserTeam = true
        )

        Assert.assertEquals(
            true,
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.requireUserTeam
        )
    }

    @Test
    fun `transform payTvInfoPath to value returned by backend`() {
        val channelFragment = getChannelFragment(
            payTvInfoPath = "/assista"
        )

        Assert.assertEquals(
            "/assista",
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.internalLink
        )
    }

    @Test
    fun `transform payTvExternalLink to value returned by backend`() {
        val channelFragment = getChannelFragment(
            payTvExternalLink = "http://globo.com/assista"
        )

        Assert.assertEquals(
            "http://globo.com/assista",
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.externalLink
        )
    }


    @Test
    fun `transform payTvExternalLinkLabel to value returned by backend`() {
        val channelFragment = getChannelFragment(
            payTvExternalLinkLabel = "Assista na globosat!"
        )

        Assert.assertEquals(
            "Assista na globosat!",
            repository.transformBroadcastFragmentToBroadcast(channelFragment).payTv?.externalLinkLabel
        )
    }

    @Test
    fun `when trimmed logo is valid use it`() {
        val channelFragment = getChannelFragment(
            trimmedLogo = "logo"
        )

        Assert.assertEquals(
            channelFragment.channel()?.trimmedLogo(),
            repository.transformBroadcastFragmentToBroadcast(channelFragment).trimmedLogo
        )
    }

    @Test
    fun `when trimmed logo is invalid use null`() {
        val channelFragment = getChannelFragment()

        Assert.assertEquals(
            channelFragment.channel()?.trimmedLogo(), null
        )
    }

    @Test
    fun `when broadcast logo is valid use it`() {
        val channelFragment = getChannelFragment(
            broadcastLogo = "logo",
            channelsLogo = "logo2"
        )

        Assert.assertEquals(
            channelFragment.logo(),
            repository.transformBroadcastFragmentToBroadcast(channelFragment).logo
        )
    }

    @Test
    fun `when broadcast logo is invalid use channels logo`() {
        val channelFragment = getChannelFragment(
            broadcastLogo = null,
            channelsLogo = "logo2"
        )

        Assert.assertEquals(
            channelFragment.channel()?.logo(),
            repository.transformBroadcastFragmentToBroadcast(channelFragment).logo
        )
    }

    @Test
    fun `assert mediaId list has loaded`() {
        enqueueResponse("broadcast/broadcast_media_id_valid.json")

        repository.mediaIds()
            .test()
            .also {
                it.awaitTerminalEvent()
            }
            .assertComplete()
            .assertValue { it.isNotEmpty() }
    }

    @Test
    fun `assert currentSlot list has loaded`() {
        enqueueResponse("broadcast/broadcast_current_slot_valid.json")

        repository.currentSlots("", "")
            .test()
            .also {
                it.awaitTerminalEvent()
            }
            .assertComplete()
            .assertValue { it.isNotEmpty() }
    }

    @Test
    fun `transform EpgSlotCoreFragment to BroadCastSlot`() {
        val epgCurrentSlot = getEpgSlotFragment()
        val broadcastSlot = repository.transformToBroadcastSlot(epgCurrentSlot)
        assert(broadcastSlot.programId == epgCurrentSlot.programId())
        assert(broadcastSlot.name == epgCurrentSlot.name())
        assert(broadcastSlot.startTime?.time == epgCurrentSlot.startTime()?.times(1000L))
        assert(broadcastSlot.endTime?.time == epgCurrentSlot.endTime()?.times(1000L))
        assert(broadcastSlot.isLiveBroadcast == epgCurrentSlot.liveBroadcast())
        assert(broadcastSlot.classificationsList == epgCurrentSlot.tags())
    }

    @Test
    fun `transform ignoreAdvertisements to valid when backend returns valid result`() {
        val channelFragment = getChannelFragment(
            ignoreAdvertisements = true
        )

        Assert.assertEquals(
            true,
            repository.transformBroadcastFragmentToBroadcast(channelFragment).ignoreAdvertisements
        )
    }

    @Test
    fun `transform ignoreAdvertisements to false when backend returns invalid result`() {
        val channelFragment = getChannelFragment()

        Assert.assertEquals(
            false,
            repository.transformBroadcastFragmentToBroadcast(channelFragment).ignoreAdvertisements
        )
    }

    private fun getChannelFragment(
        payServiceId: Int? = 6760,
        requireUserTeam: Boolean? = null,
        payTvInfoPath: String? = null,
        payTvExternalLink: String? = null,
        payTvExternalLinkLabel: String? = null,
        broadcastLogo: String? = null,
        channelsLogo: String? = null,
        trimmedLogo: String? = null,
        ignoreAdvertisements: Boolean? = null
    ) = BroadcastFragment(
        "",
        "",
        0,
        "",
        "",
        false,
        false,
        "",
        broadcastLogo,
        "",
        ignoreAdvertisements,
        "",
        BroadcastFragment.Channel(
            "",
            "",
            "",
            channelsLogo,
            trimmedLogo,
            requireUserTeam,
            "$payServiceId",
            "",
            payTvExternalLink,
            payTvExternalLinkLabel,
            payTvInfoPath
        ),
        null,
        null,
        null,
        null
    )

    private fun getEpgSlotFragment() = EPGSlotCoreFragment(
        "",
        "123321",
        "xyz",
        "xyzxyzxyz",
        -123331,
        -33112333,
        122,
        false,
        arrayListOf()
    )

    private fun buildQueryMedia() = BroadcastFragment.Media(
        "",
        0,
        SubscriptionType.ANONYMOUS,
        "headline",
        BroadcastFragment.SubscriptionService(
            "",
            BroadcastFragment.SalesPage(
                "",
                BroadcastFragment.Identifier(
                    "",
                    "mobile"
                )
            ),
            BroadcastFragment.Faq(
                "",
                BroadcastFragment.QrCode(
                    "",
                    "mobile"
                ),
                BroadcastFragment.Url(
                    "",
                    "mobile"
                )
            )
        )
    )
}