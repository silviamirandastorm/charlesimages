package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.model.Cover
import com.globo.jarvis.model.Format
import com.globo.jarvis.model.Type
import com.globo.jarvis.type.CoverLandscapeScales
import com.globo.jarvis.type.SuggestGroups
import com.globo.jarvis.type.TitleFormat
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TitleRepositoryTest : BaseUnitTest() {
    private lateinit var titleRepository: TitleRepository

    private val titleId = "g7vtJRBwg8"
    private val coverScale = "X276"
    private val coverMobileUrl = "https://s2.glbimg.com/TPrZ6IIsFU0CNK4xjQj0mNVx2_4=/0x276/" +
            "https://s2.glbimg.com/RWlWQgterTZFU0mhJxh-jWCl3Fg=/i.s3.glbimg.com/v1/AUTH_c3c" +
            "606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/Y/1oEq18TIOfLbCO3fPpGQ" +
            "/2019-561-se-joga-background.jpg"
    private val coverTabletPortraitUrl = "https://s2.glbimg.com/TPrZ6IIsFU0CNK4xjQj0mNVx2_4" +
            "=/0x276/https://s2.glbimg.com/RWlWQgterTZFU0mhJxh-jWCl3Fg=/i.s3.glbimg.com/v1/" +
            "AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/Y/1oEq18TIOfLb" +
            "CO3fPpGQ/2019-561-se-joga-background.jpg"
    private val coverTabletLandscapeUrl = "https://s2.glbimg.com/TPrZ6IIsFU0CNK4xjQj0mNVx2_" +
            "4=/0x276/https://s2.glbimg.com/RWlWQgterTZFU0mhJxh-jWCl3Fg=/i.s3.glbimg.com/v1" +
            "/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/Y/1oEq18TIOfL" +
            "bCO3fPpGQ/2019-561-se-joga-background.jpg"

    @Before
    fun `init title repository`() {
        titleRepository = spyk(TitleRepository(apolloClient, Device.MOBILE))
    }

    @Test
    fun `test cover success`() {
        enqueueResponse("title/cover_result.json")

        titleRepository.cover(titleId, coverScale)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue { coverVO ->
                coverVO.mobile == coverMobileUrl
                        && coverVO.tabletPortrait == coverTabletPortraitUrl
                        && coverVO.tabletLand == coverTabletLandscapeUrl
            }
    }

    @Test
    fun `test title get details`() {
        enqueueResponse("title/title_details_success.json")

        val testObserver = titleRepository.details("mVZ9nJz5CW", "", "X288").test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()
        testObserver.assertValue { title ->
            title.titleId == "mVZ9nJz5CW" &&
                    title.headline == "Cinquenta Tons De Cinza" &&
                    title.description == "A vida da estudante Anastasia Steele muda para sempre quando ela começa a se relacionar com Christian Grey, um bilionário sadomasoquista, e se torna objeto de submissão dele." &&
                    title.abExperiment?.pathUrl == "/cinquenta-tons-de-cinza/t/mVZ9nJz5CW" &&
                    title.titleDetails?.originalHeadline == "Fifty Shades Of Grey" &&
                    title.titleDetails?.title == "Cinquenta Tons De Cinza" &&
                    title.titleDetails?.formattedDuration == "2h 5min" &&
                    title.titleDetails?.year == "2015" &&
                    title.titleDetails?.country == "Estados Unidos" &&
                    title.titleDetails?.directors == "Sam Taylor-Johnson" &&
                    title.titleDetails?.genders == "Romance, Drama" &&
                    title.titleDetails?.author == "" &&
                    title.titleDetails?.screeWriter == "" &&
                    title.titleDetails?.artDirectors == "" &&
                    title.titleDetails?.summary == "A vida da estudante Anastasia Steele muda para sempre quando ela começa a se relacionar com Christian Grey, um bilionário sadomasoquista, e se torna objeto de submissão dele." &&
                    title.titleDetails?.contentRating?.rating == "16" &&
                    title.titleDetails?.contentRating?.ratingCriteria == "Nudez, sexo" &&
                    title.video?.id == "8367312" &&
                    title.type == Type.MOVIES &&
                    title.format == Format.LONG &&
                    title.accessibleOffline &&
                    title.isEpgActive &&
                    !title.enableEditorialTab &&
                    !title.enableExcerptsTab &&
                    !title.enableEditionsTab &&
                    !title.enableProgramsTab &&
                    !title.enableChapterTab &&
                    !title.enableScenesTab &&
                    !title.enableEpisodesTab &&
                    title.cover == "https://s2.glbimg.com/2pzkLLrc5yd80OmSiZ-15s4UHJk=/270x288/https://s2.glbimg.com/sdLnTjuDKLoMQPstIpn55bJ3gbM=/1815x0:3840x2160/https://s2.glbimg.com/5XPliweavLRI0G-k_TU2Uu3TDk4=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/H/8/GigrEXSmKRJFAgHAihjg/2020-831-filmes-universal-50-tons-de-cinza-background.jpg" &&
                    title.editorialOfferIds.isEmpty()
        }
    }

    @Test
    fun `test details with user logged`() {
        enqueueResponse("title/title_user.json")

        val testObserver = titleRepository.detailsWithUser("mVZ9nJz5CW", true).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()
        testObserver.assertValue { titleUser ->
            titleUser.continueWatching == null && !titleUser.favorited
        }
    }

    @Test
    fun `test details with user anonymous`() {
        val testObserver = titleRepository.detailsWithUser("mVZ9nJz5CW", false).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()
        testObserver.assertValue { titleUser ->
            titleUser.continueWatching == null && !titleUser.favorited
        }
    }

    @Test
    fun `test title suggest offerId`() {
        enqueueResponse("title/title_suggest_offer_id_success.json")

        val testObserver =
            titleRepository.titleSuggestOfferId("mVZ9nJz5CW", TitleFormat.LONG).test()
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()
        testObserver.assertValue { (suggestOfferId, jarvisABExperiment) ->
            suggestOfferId == "d90e1053-63b2-4edd-8f22-d7b2f9f504dd" && jarvisABExperiment == null
        }
    }

    @Test
    fun `test cover with callback success`() {
        val callbackMock = mockk<Callback.Titles>(relaxed = true)
        val coverMock = Cover(coverMobileUrl, coverTabletPortraitUrl, coverTabletLandscapeUrl)

        enqueueResponse("title/cover_result.json")

        titleRepository.cover(titleId, coverScale, callbackMock)

        verify { callbackMock.onCoverSuccess(coverMock) }
    }

    @Test
    fun `test cover with callback failure`() {
        val callbackMock = mockk<Callback.Titles>(relaxed = true)

        enqueueResponse(code = 404)

        titleRepository.cover("", coverScale, callbackMock)

        verify { callbackMock.onFailure(any()) }
    }

    @Test
    fun `test build query cover`() {
        val coverQuery = titleRepository.builderQueryCover(titleId, coverScale)

        with(coverQuery.variables()) {
            assert(titleId() == titleId)
            assert(coverMobile().value == CoverLandscapeScales.safeValueOf(coverScale))
            assert(coverTabletPortrait().value == CoverLandscapeScales.safeValueOf(coverScale))
            assert(coverTabletLandscape().value == CoverLandscapeScales.safeValueOf(coverScale))
        }
    }

    @Test
    fun `test builder suggest for TitleQuery`() {
        val query = titleRepository.builderSuggestForTitleQuery(
            SuggestGroups.TITLE_SCREEN,
            TitleFormat.LONG,
            "mVZ9nJz5CW"
        )

        assert(query.variables().format().value == TitleFormat.LONG)
        assert(query.variables().group().value == SuggestGroups.TITLE_SCREEN)
        assert(query.variables().titleId().value == "mVZ9nJz5CW")
    }

    @Test
    fun `test builder details title query`() {
        val query = titleRepository.builderDetailsTitleQuery("mVZ9nJz5CW", "", "X288")

        assert(query.variables().coverMobile().value?.rawValue() == "X288")
        assert(query.variables().originProgramId().value == "")
        assert(query.variables().titleId().value == "mVZ9nJz5CW")
    }

    @Test
    fun `test builder epg active`() {
        val titleId = "titleId"

        val titleEpgActiveQuery = titleRepository.builderTitleEpgActiveQuery(
            titleId = titleId
        )

        Assert.assertEquals(titleEpgActiveQuery.variables().titleId(), titleId)
    }

    @Test
    fun `test builder format`() {
        val titleId = "titleId"

        val titleEpgActiveQuery = titleRepository.builderTitleFormatQuery(
            titleId = titleId
        )

        Assert.assertEquals(titleEpgActiveQuery.variables().titleId(), titleId)
    }

    @Test
    fun `test retrieve title format`() {
        val titleId = "titleId"

        enqueueResponse("title/titleFormat.json")

        titleRepository.format(titleId).test()
            .assertComplete()
            .assertValue { format ->
                format.value == Format.SOAP_OPERA.value
            }
    }

    @Test
    fun `test retrieve title epg active`() {
        val titleId = "titleId"

        enqueueResponse("title/titleEpgActive.json")

        titleRepository.epgActive(titleId).test()
            .assertComplete()
            .assertValue { isEpgActive ->
                isEpgActive
            }
    }
}