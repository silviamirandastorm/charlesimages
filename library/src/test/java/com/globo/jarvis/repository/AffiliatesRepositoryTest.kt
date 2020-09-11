package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.affiliates.AffiliateProgramsQuery
import com.globo.jarvis.affiliates.AffiliateRegionsQuery
import com.globo.jarvis.affiliates.AffiliateStatesQuery
import com.globo.jarvis.type.CoverLandscapeScales
import com.globo.jarvis.type.MobilePosterScales
import com.globo.jarvis.type.TVPosterScales
import com.globo.jarvis.type.TabletPosterScales
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test

class AffiliatesRepositoryTest : BaseUnitTest() {

    //All States
    @Test
    fun testStatesValid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/states_valid.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.states()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val states = it.firstOrNull()

                it.isNotEmpty()
                        && it.size == 27
                        && states?.name == "Acre"
                        && states.acronym == "AC"
            }
    }

    @Test
    fun testStatesInvalid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/states_invalid.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================
        val statesList = affiliatesRepository.states()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assert(errorCount() == 1)
            }
    }

    @Test
    fun testStatesNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/states_null.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.states()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.isEmpty()
            }
    }

    @Test
    fun testStatesEmpty() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/states_empty.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.states()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.isEmpty()
            }
    }

    //Transform StatesQueryAffiliateState To States
    @Test
    fun testBuildQueryStatesValid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateState = listOf(
            AffiliateStatesQuery.AffiliateState("AffiliateState", "AC", "Acre"),
            AffiliateStatesQuery.AffiliateState("AffiliateState", "AL", "Alagoas")
        )
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList =
            affiliatesRepository.transformStatesQueryAffiliateStateToStates(affiliateState)

        // =========================================================================================
        // Assert.
        // =========================================================================================


        assert(statesList.firstOrNull()?.name == "Acre")
        assert(statesList.firstOrNull()?.acronym == "AC")

        assert(statesList.lastOrNull()?.name == "Alagoas")
        assert(statesList.lastOrNull()?.acronym == "AL")
    }

    @Test
    fun testBuildQueryStatesWithNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))


        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.transformStatesQueryAffiliateStateToStates(null)

        // =========================================================================================
        // Assert.
        // =========================================================================================

        assert(statesList.isEmpty())
    }

    @Test
    fun testBuildQueryStatesWithEmtpy() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList =
            affiliatesRepository.transformStatesQueryAffiliateStateToStates(emptyList())

        // =========================================================================================
        // Assert.
        // =========================================================================================

        assert(statesList.isEmpty())
    }


    //All Regions
    @Test
    fun testRegionsValid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/regions_valid.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.regions()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val regions = it.firstOrNull()

                it.isNotEmpty()
                        && it.size == 83
                        && regions?.name == "Acre"
                        && regions.slug == "acre"
                        && regions.affiliateName == "Rede Amazônica"
            }
    }

    @Test
    fun testRegionsByState() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/regions_valid.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.regionsByState("RJ")

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val regions = it.firstOrNull()

                it.isNotEmpty()
                        && it.size == 4
                        && regions?.name == "Norte Fluminense"
                        && regions.slug == "norte-fluminense"
                        && regions.affiliateName == "Inter TV RJ"
            }
    }

    @Test
    fun testRegionsByStateInvalid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/regions_valid.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.regionsByState("asda")

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val regions = it.firstOrNull()

                it.isEmpty()
            }
    }

    @Test
    fun testRegionsByStateIsEmpty() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/regions_valid.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.regionsByState("")

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val regions = it.firstOrNull()

                it.isEmpty()
            }
    }

    @Test
    fun testRegionsInvalid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/regions_invalid.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.regions()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assert(errorCount() == 1)
            }
    }

    @Test
    fun testRegionsNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/regions_null.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.regions()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.isEmpty()
            }
    }

    @Test
    fun testRegionsEmpty() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("affiliate/regions_empty.json")
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList = affiliatesRepository.regions()

        // =========================================================================================
        // Assert.
        // =========================================================================================

        statesList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.isEmpty()
            }
    }

    //Transform RegionsQueryAffiliateState To Regions
    @Test
    fun testTransformRegionsQueryAffiliateStateToRegionsWithValid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))
        val affiliateState = listOf(
            AffiliateRegionsQuery.AffiliateState(
                "Affiliate",
                "RJ",
                listOf(
                    AffiliateRegionsQuery.Region(
                        "AffiliateState",
                        "Norte Fluminense",
                        "norte-fluminense",
                        "Inter TV RJ"
                    ),
                    AffiliateRegionsQuery.Region(
                        "AffiliateState",
                        "Região dos Lagos",
                        "regiao-dos-lagos",
                        "Inter TV RJ"
                    )
                )
            )
        )

        // =========================================================================================
        // Action.
        // =========================================================================================

        val statesList =
            affiliatesRepository.transformRegionsQueryAffiliateStateToRegions(affiliateState)

        // =========================================================================================
        // Assert.
        // =========================================================================================

        val firstRegion = statesList.firstOrNull()
        val lastRegion = statesList.lastOrNull()

        assert(firstRegion?.name == "Norte Fluminense")
        assert(firstRegion?.slug == "norte-fluminense")
        assert(firstRegion?.affiliateName == "Inter TV RJ")


        assert(lastRegion?.name == "Região dos Lagos")
        assert(lastRegion?.slug == "regiao-dos-lagos")
        assert(lastRegion?.affiliateName == "Inter TV RJ")
    }

    @Test
    fun testTransformRegionsQueryAffiliateStateToRegionsWithNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        // =========================================================================================
        // Action.
        // =========================================================================================

        val regionsList = affiliatesRepository.transformRegionsQueryAffiliateStateToRegions(null)

        // =========================================================================================
        // Assert.
        // =========================================================================================

        assert(regionsList.isEmpty())
    }

    @Test
    fun testTransformRegionsQueryAffiliateStateToRegionsWithEmpty() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliatesRepository = spyk(AffiliatesRepository(apolloClient, Device.MOBILE))


        // =========================================================================================
        // Action.
        // =========================================================================================

        val regionsList =
            affiliatesRepository.transformRegionsQueryAffiliateStateToRegions(emptyList())

        // =========================================================================================
        // Assert.
        // =========================================================================================

        assert(regionsList.isEmpty())
    }


    //All Programs
    @Test
    fun testAffiliateProgramsValidWhenMobile() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.MOBILE))
        enqueueResponse("affiliate/affiliateprograms_without_next_page.json")

        // =========================================================================================
        // Action.
        // =========================================================================================

        val titleList = affiliateProgramsRepository.programs("regionSlug", "X0_75", "")

        // =========================================================================================
        // Assert.
        // =========================================================================================

        titleList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val title = it.third.firstOrNull()

                it.third.isNotEmpty()
                        && !it.first
                        && it.second == null
                        && it.third.size == 7
                        && title?.titleId == "9QtyG5nMX8"
                        && title.headline == "Bom Dia Rio"
                        && title.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade."
                        && title.titleDetails?.contentRating?.rating == "16"
                        && title.titleDetails?.contentRating?.ratingCriteria == "Violencia, drogas"
                        && title.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                        && title.poster == "https://s2.glbimg.com/BcEdGYu3cQxDWzzd1vp4cgvS0PU=/100x152/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
            }
    }

    @Test
    fun testAffiliateProgramsValidWhenTablet() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TABLET))
        enqueueResponse("affiliate/affiliateprograms_without_next_page.json")

        // =========================================================================================
        // Action.
        // =========================================================================================

        val titleList = affiliateProgramsRepository.programs("regionSlug", "X0_75", "")

        // =========================================================================================
        // Assert.
        // =========================================================================================

        titleList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val title = it.third.firstOrNull()

                it.third.isNotEmpty()
                        && !it.first
                        && it.second == null
                        && it.third.size == 7
                        && title?.titleId == "9QtyG5nMX8"
                        && title.headline == "Bom Dia Rio"
                        && title.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade."
                        && title.titleDetails?.contentRating?.rating == "16"
                        && title.titleDetails?.contentRating?.ratingCriteria == "Violencia, drogas"
                        && title.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                        && title.poster == "https://s2.glbimg.com/4NYF3cBcmPjaYMUpycFZgCckPvE=/144x212/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
            }
    }

    @Test
    fun testAffiliateProgramsValidWhenTv() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))
        enqueueResponse("affiliate/affiliateprograms_without_next_page.json")


        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.programs("regionSlug", "X0_75", "X348")


        // =========================================================================================
        // Assert.
        // =========================================================================================
        titleList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val title = it.third.firstOrNull()

                it.third.isNotEmpty()
                        && !it.first
                        && it.second == null
                        && it.third.size == 7
                        && title?.titleId == "9QtyG5nMX8"
                        && title.headline == "Bom Dia Rio"
                        && title.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade."
                        && title.titleDetails?.contentRating?.rating == "16"
                        && title.titleDetails?.contentRating?.ratingCriteria == "Violencia, drogas"
                        && title.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                        && title.poster == "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
            }
    }

    @Test
    fun testAffiliateProgramsValidWithNextPage() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))
        enqueueResponse("affiliate/affiliateprograms_with_next_page.json")


        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.programs("regionSlug", "X0_75", "X348")


        // =========================================================================================
        // Assert.
        // =========================================================================================
        titleList
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val title = it.third.firstOrNull()

                it.third.isNotEmpty()
                        && it.first
                        && it.second == 2
                        && it.third.size == 7
                        && title?.titleId == "9QtyG5nMX8"
                        && title.headline == "Bom Dia Rio"
                        && title.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade."
                        && title.titleDetails?.contentRating?.rating == "16"
                        && title.titleDetails?.contentRating?.ratingCriteria == "Violencia, drogas"
                        && title.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                        && title.poster == "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
            }
    }

    @Test
    fun testAffiliatePrograms() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))
        enqueueResponse("affiliate/affiliateprograms_without_next_page.json")


        // =========================================================================================
        // Action.
        // =========================================================================================
        affiliateProgramsRepository.programs("regionSlug", "X0_75", "").test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }


        // =========================================================================================
        // Assert.
        // =========================================================================================
        verify {
            affiliateProgramsRepository.buildAffiliateProgramsQuery(
                "regionSlug",
                "X0_75",
                "",
                1,
                12
            )
        }
    }

    @Test
    fun testAffiliateProgramsWithPagination() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))
        enqueueResponse("affiliate/affiliateprograms_without_next_page.json")


        // =========================================================================================
        // Action.
        // =========================================================================================
        affiliateProgramsRepository.programs("regionSlug", "X0_75", "", 2, 24).test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }


        // =========================================================================================
        // Assert.
        // =========================================================================================
        verify {
            affiliateProgramsRepository.buildAffiliateProgramsQuery(
                "regionSlug",
                "X0_75",
                "",
                2,
                24
            )
        }
    }


    //Transform StatesQueryAffiliateState To States
    @Test
    fun testTransformStatesQueryAffiliateStateToStatesWhenMobile() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.MOBILE))

        val affiliateProgramsQueryResourceList = listOf(
            AffiliateProgramsQuery.Resource(
                "Title",
                "9QtyG5nMX8",
                "Bom Dia Rio",
                "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.",
                "16",
                listOf("violencia", "drogas"),
                AffiliateProgramsQuery.Cover(
                    "Cover",
                    "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                ),
                AffiliateProgramsQuery.Poster(
                    "Poster",
                    "https://s2.glbimg.com/BcEdGYu3cQxDWzzd1vp4cgvS0PU=/100x152/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/4NYF3cBcmPjaYMUpycFZgCckPvE=/144x212/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
                )
            )
        )


        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.transformAffiliateProgramsQueryResourceToStates(
            affiliateProgramsQueryResourceList
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        val title = titleList.firstOrNull()
        assert(title?.titleId == "9QtyG5nMX8")
        assert(title?.headline == "Bom Dia Rio")
        assert(title?.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.")
        assert(title?.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg")
        assert(title?.titleDetails?.contentRating?.rating == "16")
        assert(title?.titleDetails?.contentRating?.ratingCriteria == "Violencia, drogas")
        assert(title?.poster == "https://s2.glbimg.com/BcEdGYu3cQxDWzzd1vp4cgvS0PU=/100x152/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg")
    }

    @Test
    fun testTransformStatesQueryAffiliateStateToStatesWhenTablet() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TABLET))

        val affiliateProgramsQueryResourceList = listOf(
            AffiliateProgramsQuery.Resource(
                "Title",
                "9QtyG5nMX8",
                "Bom Dia Rio",
                "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.",
                "16",
                listOf("violencia", "drogas"),
                AffiliateProgramsQuery.Cover(
                    "Cover",
                    "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                ),
                AffiliateProgramsQuery.Poster(
                    "Poster",
                    "https://s2.glbimg.com/BcEdGYu3cQxDWzzd1vp4cgvS0PU=/100x152/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/4NYF3cBcmPjaYMUpycFZgCckPvE=/144x212/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
                )
            )
        )


        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.transformAffiliateProgramsQueryResourceToStates(
            affiliateProgramsQueryResourceList
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        val title = titleList.firstOrNull()
        assert(title?.titleId == "9QtyG5nMX8")
        assert(title?.headline == "Bom Dia Rio")
        assert(title?.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.")
        assert(title?.titleDetails?.contentRating?.rating == "16")
        assert(title?.titleDetails?.contentRating?.ratingCriteria == "Violencia, drogas")
        assert(title?.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg")
        assert(title?.poster == "https://s2.glbimg.com/4NYF3cBcmPjaYMUpycFZgCckPvE=/144x212/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg")
    }

    @Test
    fun testTransformStatesQueryAffiliateStateToStatesWhenTv() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))

        val affiliateProgramsQueryResourceList = listOf(
            AffiliateProgramsQuery.Resource(
                "Title",
                "9QtyG5nMX8",
                "Bom Dia Rio",
                "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.",
                "16",
                listOf("violencia", "drogas"),
                AffiliateProgramsQuery.Cover(
                    "Cover",
                    "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                ),
                AffiliateProgramsQuery.Poster(
                    "Poster",
                    "https://s2.glbimg.com/BcEdGYu3cQxDWzzd1vp4cgvS0PU=/100x152/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/4NYF3cBcmPjaYMUpycFZgCckPvE=/144x212/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
                )
            )
        )


        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.transformAffiliateProgramsQueryResourceToStates(
            affiliateProgramsQueryResourceList
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        val title = titleList.firstOrNull()
        assert(title?.titleId == "9QtyG5nMX8")
        assert(title?.headline == "Bom Dia Rio")
        assert(title?.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.")
        assert(title?.titleDetails?.contentRating?.rating == "16")
        assert(title?.titleDetails?.contentRating?.ratingCriteria == "Violencia, drogas")
        assert(title?.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg")
        assert(title?.poster == "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg")
    }

    @Test
    fun testTransformStatesQueryAffiliateStateToStatesWhenContentRatingNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))

        val affiliateProgramsQueryResourceList = listOf(
            AffiliateProgramsQuery.Resource(
                "Title",
                "9QtyG5nMX8",
                "Bom Dia Rio",
                "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.",
                null,
                null,
                AffiliateProgramsQuery.Cover(
                    "Cover",
                    "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                ),
                AffiliateProgramsQuery.Poster(
                    "Poster",
                    "https://s2.glbimg.com/BcEdGYu3cQxDWzzd1vp4cgvS0PU=/100x152/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/4NYF3cBcmPjaYMUpycFZgCckPvE=/144x212/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
                )
            )
        )


        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.transformAffiliateProgramsQueryResourceToStates(
            affiliateProgramsQueryResourceList
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        val title = titleList.firstOrNull()
        assert(title?.titleId == "9QtyG5nMX8")
        assert(title?.headline == "Bom Dia Rio")
        assert(title?.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.")
        assert(title?.titleDetails?.contentRating?.rating == null)
        assert(title?.titleDetails?.contentRating?.ratingCriteria == null)
        assert(title?.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg")
        assert(title?.poster == "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg")
    }

    @Test
    fun testTransformStatesQueryAffiliateStateToStatesWhenContentRatingEmpty() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))

        val affiliateProgramsQueryResourceList = listOf(
            AffiliateProgramsQuery.Resource(
                "Title",
                "9QtyG5nMX8",
                "Bom Dia Rio",
                "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.",
                "",
                listOf(),
                AffiliateProgramsQuery.Cover(
                    "Cover",
                    "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg"
                ),
                AffiliateProgramsQuery.Poster(
                    "Poster",
                    "https://s2.glbimg.com/BcEdGYu3cQxDWzzd1vp4cgvS0PU=/100x152/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/4NYF3cBcmPjaYMUpycFZgCckPvE=/144x212/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg",
                    "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg"
                )
            )
        )


        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.transformAffiliateProgramsQueryResourceToStates(
            affiliateProgramsQueryResourceList
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        val title = titleList.firstOrNull()
        assert(title?.titleId == "9QtyG5nMX8")
        assert(title?.headline == "Bom Dia Rio")
        assert(title?.description == "Telejornal matutino focado na prestação de serviços. Oferece informações sobre o tempo e o trânsito nas principais vias da cidade.")
        assert(title?.titleDetails?.contentRating?.rating == "")
        assert(title?.titleDetails?.contentRating?.ratingCriteria == "")
        assert(title?.cover == "https://s2.glbimg.com/t9hhLI-yP_pQ3oEdbWBk8RUv7fA=/0x720/https://s2.glbimg.com/0QsyJsDZwRaMObZzltIv0tfKq6w=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2018/U/j/3VleIuTRedPxIAlW7B7g/811-bom-dia-rio-background.jpg")
        assert(title?.poster == "https://s2.glbimg.com/YprRXhzLPaNfZGFc0qZ8LxESLXk=/146x216/https://s2.glbimg.com/4y2H1uDPenMBxTdJycYzD1hNBKc=/s3.glbimg.com/v1/AUTH_180b9dd048d9434295d27c4b6dadc248/media_kit/6f/77/2a6ef96ea9e7a8da4a81304b31d1.jpg")
    }

    @Test
    fun testTransformStatesQueryAffiliateStateToStatesWhenListNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))

        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.transformStatesQueryAffiliateStateToStates(null)

        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(titleList.isEmpty())
    }

    @Test
    fun testTransformStatesQueryAffiliateStateToStatesWhenListEmpty() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))

        // =========================================================================================
        // Action.
        // =========================================================================================
        val titleList = affiliateProgramsRepository.transformStatesQueryAffiliateStateToStates(
            emptyList()
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(titleList.isEmpty())
    }


    //Build Affiliate Programs Query
    @Test
    fun testBuildAffiliateProgramsQueryWhenMobile() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.MOBILE))


        // =========================================================================================
        // Action.
        // =========================================================================================
        val affiliateProgramsQuery = affiliateProgramsRepository.buildAffiliateProgramsQuery(
            "regionSlug",
            "X0_75",
            "X348",
            2,
            24
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(affiliateProgramsQuery.variables().regionSlug() == "regionSlug")
        assert(
            affiliateProgramsQuery.variables()
                .mobilePosterScales().value == MobilePosterScales.X0_75
        )
        assert(
            affiliateProgramsQuery.variables().tabletPosterScales().value == null
        )
        assert(
            affiliateProgramsQuery.variables().tvPosterScales().value == null
        )
        assert(
            affiliateProgramsQuery.variables().tvCoverLandscapeScales().value == null
        )
        assert(affiliateProgramsQuery.variables().page() == 2)
        assert(affiliateProgramsQuery.variables().perPage() == 24)
    }

    @Test
    fun testBuildAffiliateProgramsQueryWhenTablet() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TABLET))


        // =========================================================================================
        // Action.
        // =========================================================================================
        val affiliateProgramsQuery = affiliateProgramsRepository.buildAffiliateProgramsQuery(
            "regionSlug",
            "X1_5",
            "X348",
            2,
            24
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(affiliateProgramsQuery.variables().regionSlug() == "regionSlug")
        assert(
            affiliateProgramsQuery.variables()
                .mobilePosterScales().value == null
        )

        assert(
            affiliateProgramsQuery.variables()
                .tabletPosterScales().value == TabletPosterScales.X1_5
        )
        assert(
            affiliateProgramsQuery.variables().tvPosterScales().value == null
        )
        assert(
            affiliateProgramsQuery.variables().tvCoverLandscapeScales().value == null
        )
        assert(affiliateProgramsQuery.variables().page() == 2)
        assert(affiliateProgramsQuery.variables().perPage() == 24)
    }

    @Test
    fun testBuildAffiliateProgramsQueryWhenTv() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))


        // =========================================================================================
        // Action.
        // =========================================================================================
        val affiliateProgramsQuery = affiliateProgramsRepository.buildAffiliateProgramsQuery(
            "regionSlug",
            "X1_5",
            "X348",
            2,
            24
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(affiliateProgramsQuery.variables().regionSlug() == "regionSlug")
        assert(
            affiliateProgramsQuery.variables()
                .mobilePosterScales().value == null
        )
        assert(
            affiliateProgramsQuery.variables()
                .tabletPosterScales().value == null
        )
        assert(
            affiliateProgramsQuery.variables().tvPosterScales().value == TVPosterScales.X1_5
        )
        assert(
            affiliateProgramsQuery.variables()
                .tvCoverLandscapeScales().value == CoverLandscapeScales.X348
        )
        assert(affiliateProgramsQuery.variables().page() == 2)
        assert(affiliateProgramsQuery.variables().perPage() == 24)
    }


    //Builder Image AffiliatePrograms
    @Test
    fun testBuilderImageAffiliateProgramsWhenMobile() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.MOBILE))


        // =========================================================================================
        // Action.
        // =========================================================================================
        val affiliateProgramsQueryBuild = affiliateProgramsRepository.builderImageAffiliatePrograms(
            AffiliateProgramsQuery.builder().regionSlug("regionSlug"),
            "X0_75",
            "X348"
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(
            affiliateProgramsQueryBuild.build().variables()
                .mobilePosterScales().value == MobilePosterScales.X0_75
        )
        assert(
            affiliateProgramsQueryBuild.build().variables().tabletPosterScales().value == null
        )
        assert(
            affiliateProgramsQueryBuild.build().variables().tvPosterScales().value == null
        )
        assert(
            affiliateProgramsQueryBuild.build().variables().tvCoverLandscapeScales().value == null
        )
    }

    @Test
    fun testBuilderImageAffiliateProgramsWhenTablet() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TABLET))


        // =========================================================================================
        // Action.
        // =========================================================================================
        val affiliateProgramsQueryBuild = affiliateProgramsRepository.builderImageAffiliatePrograms(
            AffiliateProgramsQuery.builder().regionSlug("regionSlug"),
            "X1_5",
            "X348"
        )

        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(
            affiliateProgramsQueryBuild.build().variables()
                .mobilePosterScales().value == null
        )

        assert(
            affiliateProgramsQueryBuild.build().variables()
                .tabletPosterScales().value == TabletPosterScales.X1_5
        )
        assert(
            affiliateProgramsQueryBuild.build().variables().tvPosterScales().value == null
        )
        assert(
            affiliateProgramsQueryBuild.build().variables().tvCoverLandscapeScales().value == null
        )
    }

    @Test
    fun testBuilderImageAffiliateProgramsWhenTv() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val affiliateProgramsRepository =
            spyk(AffiliatesRepository(apolloClient, Device.TV))


        // =========================================================================================
        // Action.
        // =========================================================================================
        val affiliateProgramsQueryBuild = affiliateProgramsRepository.builderImageAffiliatePrograms(
            AffiliateProgramsQuery.builder().regionSlug("regionSlug"),
            "X1_5",
            "X348"
        )


        // =========================================================================================
        // Assert.
        // =========================================================================================
        assert(
            affiliateProgramsQueryBuild.build().variables()
                .mobilePosterScales().value == null
        )
        assert(
            affiliateProgramsQueryBuild.build().variables()
                .tabletPosterScales().value == null
        )
        assert(
            affiliateProgramsQueryBuild.build().variables()
                .tvPosterScales().value == TVPosterScales.X1_5
        )
        assert(
            affiliateProgramsQueryBuild.build().variables()
                .tvCoverLandscapeScales().value == CoverLandscapeScales.X348
        )
    }


}