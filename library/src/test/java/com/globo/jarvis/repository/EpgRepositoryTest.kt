package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.EpgFragment
import com.globo.jarvis.model.AvailableFor
import com.globo.jarvis.model.EpgSlot
import com.globo.jarvis.type.SubscriptionType
import io.mockk.every
import io.mockk.spyk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EpgRepositoryTest : BaseUnitTest() {
    private lateinit var epgRepository: EpgRepository

    private val channelMock = EpgFragment.Channel(
        "__typename",
        "channel_id",
        "channel_name",
        "channel_logo"
    )

    private val mediaMock = EpgFragment.Media(
        "__typename",
        EpgFragment.Title(
            "__typename",
            "titleId"
        ),
        SubscriptionType.SUBSCRIBER,
        "media_headline"
    )

    private val epgByDateMock = EpgFragment.EpgByDate(
        "__typename",
        listOf(
            EpgFragment.Entry(
                "__typename",
                "entry_titleId",
                "entry_programId",
                "entry_name",
                "entry_metadata",
                "entry_description",
                0,
                0,
                0,
                true,
                listOf("tag1", "tag2", "tag3"),
                listOf("0", "1", "2"),
                "entry_contentRating",
                listOf("crc1", "crc2", "crc3"),
                EpgFragment.Title1(
                    "__typename",
                    EpgFragment.Cover(
                        "__typename",
                        "tv"
                    )
                )
            )
        )
    )

    @Before
    fun `Init EPG repository`() {
        epgRepository = spyk(EpgRepository(apolloClient, Device.MOBILE))
    }

    @Test
    fun `When Channel Media and EpgByDate are null transform to EpgVO`() {

        val epgFragmentMock = EpgFragment(
            "__typename",
            "mediaId",
            true,
            "imageOnAirMobile",
            null,
            null,
            null,
            null
        )

        val epg = epgRepository.transformEpgFragmentToEpgVO(epgFragmentMock)

        assert(epg.name == null)
        assert(epg.logo == null)
        assert(epg.geofenced)
        assert(epg.media == null)
        assert(epg.epgSlotVOList == arrayListOf<EpgSlot>())
    }

    @Test
    fun `when channel is valid but media and epgByDate are null transform to EpgVO`() {

        val epgFragmentMock = EpgFragment(
            "__typename",
            "mediaId",
            true,
            "imageOnAirMobile",
            "",
            channelMock,
            null,
            null
        )

        val epg = epgRepository.transformEpgFragmentToEpgVO(epgFragmentMock)

        assert(epg.id == channelMock.id())
        assert(epg.name == channelMock.name())
        assert(epg.logo == channelMock.logo())
        assert(epg.geofenced)
        assert(epg.media == null)
        assert(epg.epgSlotVOList == arrayListOf<EpgSlot>())
    }

    @Test
    fun `When Channel and Media are valid but EpgByDate is null transform to EpgVO`() {

        val epgFragmentMock = EpgFragment(
            "__typename",
            "mediaId",
            true,
            "imageOnAirMobile",
            "",
            channelMock,
            mediaMock,
            null
        )

        every { epgRepository.transformSubscriptionTypeToAvailableFor(any()) } returns AvailableFor.SUBSCRIBER

        val epg = epgRepository.transformEpgFragmentToEpgVO(epgFragmentMock)

        assert(epg.id == mediaMock.title().titleId())
        assert(epg.name == channelMock.name())
        assert(epg.logo == channelMock.logo())
        assert(epg.geofenced)
        assert(epg.media?.idWithDVR == epgFragmentMock.mediaId())
        assert(epg.media?.availableFor == AvailableFor.SUBSCRIBER)
        assert(epg.media?.headline == mediaMock.headline())
        assert(epg.media?.imageOnAir == epgFragmentMock.imageOnAir())
        assert(epg.epgSlotVOList == arrayListOf<EpgSlot>())
    }

    @Test
    fun `When Channel Media and EpgByDate are valid transform to EpgVO`() {

        val epgFragmentMock = EpgFragment(
            "__typename",
            "mediaId",
            true,
            "imageOnAirMobile",
            "",
            channelMock,
            mediaMock,
            epgByDateMock
        )

        every { epgRepository.transformSubscriptionTypeToAvailableFor(any()) } returns AvailableFor.SUBSCRIBER

        val epg = epgRepository.transformEpgFragmentToEpgVO(epgFragmentMock)

        assert(epg.id == mediaMock.title().titleId())
        assert(epg.name == channelMock.name())
        assert(epg.logo == channelMock.logo())
        assert(epg.geofenced)
        assert(epg.media?.idWithDVR == epgFragmentMock.mediaId())
        assert(epg.media?.availableFor == AvailableFor.SUBSCRIBER)
        assert(epg.media?.headline == mediaMock.headline())
        assert(epg.media?.imageOnAir == epgFragmentMock.imageOnAir())
        assert(epg.epgSlotVOList?.first()?.titleId == epgByDateMock.entries()?.first()?.titleId())
        assert(epg.epgSlotVOList?.first()?.name == epgByDateMock.entries()?.first()?.name())
        assert(
            epg.epgSlotVOList?.first()?.description == epgByDateMock.entries()?.first()
                ?.description()
        )
        assert(
            epg.epgSlotVOList?.first()?.metadata == epgByDateMock.entries()?.first()?.metadata()
        )
        assert(epg.epgSlotVOList?.first()?.startTime?.time == 0L)
        assert(epg.epgSlotVOList?.first()?.endTime?.time == 0L)
        assert(
            epg.epgSlotVOList?.first()?.duration == epgByDateMock.entries()?.first()
                ?.durationInMinutes()
        )
        assert(
            epg.epgSlotVOList?.first()?.isLiveBroadcast == epgByDateMock.entries()?.first()
                ?.liveBroadcast()
        )
        assert(
            epg.epgSlotVOList?.first()?.isLiveBroadcast == epgByDateMock.entries()?.first()
                ?.liveBroadcast()
        )
        assert(
            epg.epgSlotVOList?.first()?.classificationsList?.size == epgByDateMock.entries()
                ?.first()?.tags()?.size
        )
        assert(
            epg.epgSlotVOList?.first()?.contentRating == epgByDateMock.entries()?.first()
                ?.contentRating()
        )
        assert(
            epg.epgSlotVOList?.first()?.contentRatingCriteria?.size == epgByDateMock.entries()
                ?.first()?.contentRatingCriteria()?.size
        )
    }

    @Test
    fun `When titleId is valid use it`() {
        val channelFragment = getEpgFragment(
            titleId = "title id",
            channelId = "channel id"
        )

        Assert.assertEquals(
            channelFragment.media()?.title()?.titleId(),
            epgRepository.transformEpgFragmentToEpgVO(channelFragment).id
        )
    }

    @Test
    fun `When titleId is invalid use channel id`() {
        val channelFragment = getEpgFragment(
            titleId = null,
            channelId = "id"
        )

        Assert.assertEquals(
            channelFragment.channel()?.id(),
            epgRepository.transformEpgFragmentToEpgVO(channelFragment).id
        )
    }

    @Test
    fun `When broadcast logo is valid use it`() {
        val channelFragment = getEpgFragment(
            broadcastLogo = "logo",
            channelsLogo = "logo42"
        )

        Assert.assertEquals(
            channelFragment.logo(),
            epgRepository.transformEpgFragmentToEpgVO(channelFragment).logo
        )
    }

    @Test
    fun `When broadcast logo is invalid use channels logo`() {
        val channelFragment = getEpgFragment(
            broadcastLogo = null,
            channelsLogo = "logo28"
        )

        Assert.assertEquals(
            channelFragment.channel()?.logo(),
            epgRepository.transformEpgFragmentToEpgVO(channelFragment).logo
        )
    }

    private fun getEpgFragment(
        broadcastLogo: String? = null,
        channelsLogo: String? = null,
        titleId: String? = null,
        channelId: String = ""
    ) = EpgFragment(
        "",
        "",
        false,
        "",
        broadcastLogo,
        EpgFragment.Channel("", channelId, "", channelsLogo),
        EpgFragment.Media("", EpgFragment.Title("", titleId), null, ""),
        null
    )
}