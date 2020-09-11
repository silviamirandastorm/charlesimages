package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.EpisodeListStructureFragment
import com.globo.jarvis.fragment.SeasonedStructureFragment
import com.globo.jarvis.fragment.SuggestPlayNextTitle
import com.globo.jarvis.model.AbExperiment
import com.globo.jarvis.model.Format
import com.globo.jarvis.model.Recommendation
import com.globo.jarvis.model.Type
import com.globo.jarvis.recommendation.RecommendationTitlesQuery
import com.globo.jarvis.type.*
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test

class SuggestRepositoryTest : BaseUnitTest() {

    private lateinit var suggestRepository: SuggestRepository

    companion object {
        private const val SCALE_X1 = "X1"
        private const val SCALE_X1856 = "X1856"
    }

    @Before
    fun `init title repository`() {
        suggestRepository = spyk(SuggestRepository(apolloClient, Device.MOBILE))
    }

    @Test
    fun `test title get suggest by offerId success flow`() {
        // Prepare.
        // =========================================================================================
        enqueueResponse("recommendation/title_suggestion_by_offer_id.json")

        // Action.
        // =========================================================================================
        val testObserver =
            suggestRepository.titleSuggestByOfferId("title_id", "offer_id", SCALE_X1).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        // Assert.
        // =========================================================================================
        testObserver.assertValue { (listOfRecommendedTitleOffer, abExperiment) ->

            val firstTitleOffer = listOfRecommendedTitleOffer.first()

            listOfRecommendedTitleOffer.size == 12 &&
                    firstTitleOffer.headline == "Regressão" &&
                    firstTitleOffer.poster == "https://s2.glbimg.com/9OTeBgBmE0Ya694Sxl77qtXkoeU=/100x152/https://s2.glbimg.com/StQpCg2V9JYjscI9EilCeQlk9rg=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/o/A/d6XnJpSbaHly8AFHYTSQ/2020-794-filme-cdc-regressao-poster.jpg" &&
                    firstTitleOffer.titleId == "HY9Pw8PBqP" &&
                    abExperiment == null
        }
    }

    @Test
    fun `test title suggest by offerId error flow`() {
        // Prepare.
        // =========================================================================================
        enqueueResponse("recommendation/invalid_title_suggestion_by_offer_id.json")

        // Action.
        // =========================================================================================
        val testObserver =
            suggestRepository.titleSuggestByOfferId("", "", SCALE_X1).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        // Assert.
        // =========================================================================================
        testObserver.assertValue { (listOfRecommendedTitleOffer, abExperiment) ->
            listOfRecommendedTitleOffer.isEmpty() && abExperiment == null
        }
    }

    /** Titles Top Hits **/
    @Test
    fun `test load all recommendation success flow`() {
        // Prepare.
        // =========================================================================================
        enqueueResponse("recommendation/valid.json")

        // Asserts.
        // =========================================================================================
        assertRecommendation(
            suggestRepository.titlesTopHits(
                scale = SCALE_X1,
                coverScale = SCALE_X1856
            ).test()
        ).assertValue {
            val recommendationVO = it.firstOrNull()

            it.isNotEmpty()
                    && recommendationVO?.titleId == "mh6BzqCQVy"
                    && recommendationVO.programId == "12082"
                    && recommendationVO.headline == "Big Brother Brasil"
                    && recommendationVO.description == "A casa mais vigiada do Brasil recebe participantes que topam ficar confinados por três meses em busca de um prêmio milionário."
                    && recommendationVO.logo == "https://s2.glbimg.com/wmrRkwQeTT4rOHoyD5jY0EVdEe8=/876x496/https://s2.glbimg.com/4cT-bngRiyq-6V6eOcvlTlbC64s=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/1/Z/508ghhQqypiIUOBZpp5w/2020-748-realities-big-brother-brasil-20-tv-globo-logo.png"
                    && recommendationVO.cover == "https://s2.glbimg.com/GsCZTGF1KLiYs-6bvhYdK944gq4=/0x2160/https://s2.glbimg.com/T7K_c4W_to0gJiNRgJIBpJVBf2I=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/u/a/WCTZoCS3ulpaPbztyTlw/2020-748-realities-big-brother-brasil-20-tv-globo-background.jpg"
                    && recommendationVO.format.value == Format.REALITIES.value
                    && recommendationVO.type.value == Type.PROGRAM.value
                    && recommendationVO.abExperiment?.pathUrl == "url"
        }
    }

    @Test
    fun `test load all called builder query`() {
        // Action.
        // =========================================================================================
        suggestRepository.titlesTopHits(scale = SCALE_X1, coverScale = SCALE_X1856, perPage = 24)

        // Asserts.
        // =========================================================================================
        verify {
            suggestRepository.builderRecommendationQuery(any(), any(), any(), 24)
        }
    }

    @Test
    fun `test load all default per page`() {
        // Action.
        // =========================================================================================
        suggestRepository.titlesTopHits(scale = SCALE_X1, coverScale = SCALE_X1856)

        // Asserts.
        // =========================================================================================
        verify {
            suggestRepository.builderRecommendationQuery(any(), any(), any(), 12)
        }
    }

    @Test
    fun `test load all recommendation with content invalid`() {
        // Prepare.
        // =========================================================================================
        enqueueResponse("recommendation/invalid_content.json")

        // Action.
        // =========================================================================================
        val testObserver =
            suggestRepository.titlesTopHits(scale = SCALE_X1, coverScale = SCALE_X1856).test()

        // Asserts.
        // =========================================================================================
        assert(testObserver.errorCount() == 1)
    }

    @Test
    fun `test next title get suggest by offerId success flow`() {
        // Prepare.
        // =========================================================================================
        enqueueResponse("recommendation/next_title_suggestion_by_offer_id.json")

        // Action.
        // =========================================================================================
        val testObserver =
            suggestRepository.nextTitle("offer_id", "title_id", SCALE_X1856, 0, 1).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        // Assert.
        // =========================================================================================
        testObserver.assertValue { recommendationVO ->
            recommendationVO.titleId == "HfN7TnrKHV"
                    && recommendationVO.videoId == "4584051"
                    && recommendationVO.headline == "Totalmente Demais"
                    && recommendationVO.description == "Em um conto de fadas moderno, a novela conta a história de Eliza. A vida da vendedora de flores se transforma quando ela conhece Arthur, dono de uma agência de modelos."
                    && recommendationVO.cover == "https://s2.glbimg.com/qDXDRiJOulqrxDD--dTp1eeUMpw=/0x1856/https://s2.glbimg.com/5w1UxhTb-Gp2t4fCLZcZfizmzII=/0x0:3840x1740/https://s2.glbimg.com/0afo4p1ejIDGCCpwfv-PJAULVoI=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/4/B/AvxE6UTJaCPbfSUGctvg/8943-totalmente-demais-background.jpg"
                    && recommendationVO.format.value == Format.SOAP_OPERA.value
                    && recommendationVO.type.value == Type.PROGRAM.value
        }
    }

    @Test
    fun `test next title get suggest by offerId recommended offer success flow`() {
        // Prepare.
        // =========================================================================================
        enqueueResponse("recommendation/next_title_suggestion_by_offer_id_recommended_offer.json")

        // Action.
        // =========================================================================================
        val testObserver =
            suggestRepository.nextTitle("offer_id", "title_id", SCALE_X1856, 0, 1).test()

        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()

        // Assert.
        // =========================================================================================
        testObserver.assertValue { recommendationVO ->
            recommendationVO.titleId == "HfN7TnrKHV"
                    && recommendationVO.videoId == "4584051"
                    && recommendationVO.headline == "Totalmente Demais"
                    && recommendationVO.description == "Em um conto de fadas moderno, a novela conta a história de Eliza. A vida da vendedora de flores se transforma quando ela conhece Arthur, dono de uma agência de modelos."
                    && recommendationVO.cover == "https://s2.glbimg.com/qDXDRiJOulqrxDD--dTp1eeUMpw=/0x1856/https://s2.glbimg.com/5w1UxhTb-Gp2t4fCLZcZfizmzII=/0x0:3840x1740/https://s2.glbimg.com/0afo4p1ejIDGCCpwfv-PJAULVoI=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/4/B/AvxE6UTJaCPbfSUGctvg/8943-totalmente-demais-background.jpg"
                    && recommendationVO.format.value == Format.SOAP_OPERA.value
                    && recommendationVO.type.value == Type.PROGRAM.value
                    && recommendationVO.abExperiment?.pathUrl == "url"
        }
    }

    @Test
    fun `test transform to recommendedTitleOffer with null parameter`() {
        val transformation = suggestRepository.transformToRecommendedTitleOffer(null)

        assert(transformation.titleId == null)
        assert(transformation.poster == null)
        assert(transformation.headline == null)
    }

    /** Transform RecommendationQuery To RecommendationVO **/
    @Test
    fun `test transform recommendation query to recommendationVO with content invalid`() {
        // Asserts.
        // =========================================================================================
        suggestRepository.transformRecommendationQueryToRecommendationVO(null).isEmpty()
    }

    @Test
    fun `test transform recommendation query to recommendationVO with content valid`() {
        // Prepare.
        // =========================================================================================
        val recommendationQueryList = listOf(
            RecommendationTitlesQuery.Resource(
                "Title",
                "12082",
                "mh6BzqCQVy",
                "Big Brother Brasil",
                "A casa mais vigiada do Brasil recebe participantes que topam ficar confinados por três meses em busca de um prêmio milionário.",
                TitleFormat.REALITIES,
                TitleType.TV_PROGRAM,
                "AbExperimentUrl",
                RecommendationTitlesQuery.Logo(
                    "Logo",
                    "https://s2.glbimg.com/wmrRkwQeTT4rOHoyD5jY0EVdEe8=/876x496/https://s2.glbimg.com/4cT-bngRiyq-6V6eOcvlTlbC64s=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/1/Z/508ghhQqypiIUOBZpp5w/2020-748-realities-big-brother-brasil-20-tv-globo-logo.png"
                ),
                RecommendationTitlesQuery.Cover(
                    "Cover",
                    "https://s2.glbimg.com/GsCZTGF1KLiYs-6bvhYdK944gq4=/0x2160/https://s2.glbimg.com/T7K_c4W_to0gJiNRgJIBpJVBf2I=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/u/a/WCTZoCS3ulpaPbztyTlw/2020-748-realities-big-brother-brasil-20-tv-globo-background.jpg"
                )
            )
        )

        // Action.
        // =========================================================================================
        val recommendationVOList =
            suggestRepository.transformRecommendationQueryToRecommendationVO(recommendationQueryList)
        val recommendationVO = recommendationVOList.firstOrNull()

        // Asserts.
        // =========================================================================================
        assert(recommendationVO?.titleId == "mh6BzqCQVy")
        assert(recommendationVO?.programId == "12082")
        assert(recommendationVO?.headline == "Big Brother Brasil")
        assert(recommendationVO?.description == "A casa mais vigiada do Brasil recebe participantes que topam ficar confinados por três meses em busca de um prêmio milionário.")
        assert(recommendationVO?.logo == "https://s2.glbimg.com/wmrRkwQeTT4rOHoyD5jY0EVdEe8=/876x496/https://s2.glbimg.com/4cT-bngRiyq-6V6eOcvlTlbC64s=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/1/Z/508ghhQqypiIUOBZpp5w/2020-748-realities-big-brother-brasil-20-tv-globo-logo.png")
        assert(recommendationVO?.cover == "https://s2.glbimg.com/GsCZTGF1KLiYs-6bvhYdK944gq4=/0x2160/https://s2.glbimg.com/T7K_c4W_to0gJiNRgJIBpJVBf2I=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/u/a/WCTZoCS3ulpaPbztyTlw/2020-748-realities-big-brother-brasil-20-tv-globo-background.jpg")
        assert(recommendationVO?.format?.value == Format.REALITIES.value)
        assert(recommendationVO?.type?.value == Type.PROGRAM.value)
        assert(recommendationVO?.abExperiment?.pathUrl == "AbExperimentUrl")
    }

    @Test
    fun `test transform next title query to recommendationVO with content valid`() {
        // Prepare.
        // =========================================================================================
        val nextTitleQueryList = SuggestPlayNextTitle(
            "Title",
            "Totalmente Demais",
            null,
            "Em um conto de fadas moderno, a novela conta a história de Eliza. A vida da vendedora de flores se transforma quando ela conhece Arthur, dono de uma agência de modelos.",
            "HfN7TnrKHV",
            TitleType.TV_PROGRAM,
            TitleFormat.SOAP_OPERA,
            null,
            SuggestPlayNextTitle.Structure(
                "EpisodeListStructure",
                SuggestPlayNextTitle.Structure.Fragments(
                    SeasonedStructureFragment(
                        "EpisodeListStructure",
                        SeasonedStructureFragment.DefaultEpisode(
                            "Episode",
                            SeasonedStructureFragment.Video("Video", "4584051")
                        )
                    ),
                    EpisodeListStructureFragment(
                        "EpisodeListStructure",
                        EpisodeListStructureFragment.DefaultEpisode(
                            "Episode",
                            EpisodeListStructureFragment.Video("Video", "4584051")
                        )
                    )
                )
            ),
            SuggestPlayNextTitle.Cover(
                "Cover",
                "https://s2.glbimg.com/qDXDRiJOulqrxDD--dTp1eeUMpw=/0x1856/https://s2.glbimg.com/5w1UxhTb-Gp2t4fCLZcZfizmzII=/0x0:3840x1740/https://s2.glbimg.com/0afo4p1ejIDGCCpwfv-PJAULVoI=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/4/B/AvxE6UTJaCPbfSUGctvg/8943-totalmente-demais-background.jpg",
                null,
                null
            )
        )

        val abExperiment = AbExperiment("experiment", "alternative", "url", "trackId")

        // Action.
        // =========================================================================================
        val recommendationVO =
            suggestRepository.transformNextSuggestedTitleFragmentToRecommendationVO(
                nextTitleQueryList,
                abExperiment
            )

        // Asserts.
        // =========================================================================================
        assert(recommendationVO.titleId == "HfN7TnrKHV")
        assert(recommendationVO.headline == "Totalmente Demais")
        assert(recommendationVO.description == "Em um conto de fadas moderno, a novela conta a história de Eliza. A vida da vendedora de flores se transforma quando ela conhece Arthur, dono de uma agência de modelos.")
        assert(recommendationVO.cover == "https://s2.glbimg.com/qDXDRiJOulqrxDD--dTp1eeUMpw=/0x1856/https://s2.glbimg.com/5w1UxhTb-Gp2t4fCLZcZfizmzII=/0x0:3840x1740/https://s2.glbimg.com/0afo4p1ejIDGCCpwfv-PJAULVoI=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/4/B/AvxE6UTJaCPbfSUGctvg/8943-totalmente-demais-background.jpg")
        assert(recommendationVO.format.value == Format.SOAP_OPERA.value)
        assert(recommendationVO.type.value == Type.PROGRAM.value)
        assert(recommendationVO.videoId == "4584051")
        assert(recommendationVO.abExperiment != null)
        assert(recommendationVO.abExperiment?.alternative == "alternative")
        assert(recommendationVO.abExperiment?.experiment == "experiment")
        assert(recommendationVO.abExperiment?.pathUrl == "url")
        assert(recommendationVO.abExperiment?.trackId == "trackId")
    }

    /** Builder Recommendation Titles **/
    @Test
    fun `test builder recommendation titles`() {
        val recommendationQuery =
            suggestRepository.builderRecommendationTitles(
                RecommendationTitlesQuery.builder(),
                SCALE_X1,
                SCALE_X1856
            ).build()

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assert(
            recommendationQuery.variables()
                .tvLogoScales().value?.rawValue() == TVLogoScales.safeValueOf(SCALE_X1).rawValue()
        )
        assert(
            recommendationQuery.variables()
                .tvCoverLandscapeScales().value?.rawValue() == CoverLandscapeScales.safeValueOf(
                SCALE_X1856
            ).rawValue()
        )
    }

    @Test
    fun `test builder offer suggest`() {
        val offerSuggestQuery =
            suggestRepository.builderOfferSuggestQuery(
                "title_id",
                TitleFormat.SERIES,
                SuggestGroups.PLAY_NEXT
            )

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assert(
            offerSuggestQuery.variables()
                .format().value?.rawValue() == TitleFormat.SERIES.rawValue()
        )
        assert(offerSuggestQuery.variables().titleId().value == "title_id")
    }

    @Test
    fun `test builder next title`() {
        val nextTitleQuery =
            suggestRepository.builderNextTitleQuery(
                "offer_id",
                "title_id",
                SCALE_X1856,
                0,
                1
            )

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assert(nextTitleQuery.variables().titleId() == "title_id")
        assert(nextTitleQuery.variables().offerId() == "offer_id")
        assert(nextTitleQuery.variables().mobileCoverScales().value?.rawValue() == SCALE_X1856)
        assert(nextTitleQuery.variables().page().value == 0)
        assert(nextTitleQuery.variables().perPage().value == 1)
    }

    /** Builder Recommendation Query **/
    @Test
    fun `test builder recommendation query`() {
        val recommendationQuery =
            suggestRepository.builderRecommendationQuery(
                TitleRules.TOP_HITS,
                SCALE_X1,
                SCALE_X1856, 12
            )

        assert(recommendationQuery.variables().perPage().value == 12)
        assert(
            recommendationQuery.variables()
                .rule().value?.rawValue() == TitleRules.TOP_HITS.rawValue()
        )
    }

    @Test
    fun `test builder suggest by offerId query`() {
        val query =
            suggestRepository.builderSuggestByOfferIdQuery("title_id", "offer_id", 1, 12, SCALE_X1)

        assert(query.variables().titleId() == "title_id")
        assert(query.variables().offerId() == "offer_id")
        assert(query.variables().page().value == 1)
        assert(query.variables().perPage().value == 12)
        assert(query.variables().mobilePosterScales().value?.rawValue() == SCALE_X1)
    }

    private fun assertRecommendation(testObserver: TestObserver<List<Recommendation>>)
            : TestObserver<List<Recommendation>> {
        testObserver.awaitTerminalEvent()
        testObserver.assertComplete()
        return testObserver
    }
}