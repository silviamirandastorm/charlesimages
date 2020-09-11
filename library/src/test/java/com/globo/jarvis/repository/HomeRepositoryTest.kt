package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.CategoriesOffer
import com.globo.jarvis.fragment.HomeCategories
import com.globo.jarvis.fragment.HomePage
import com.globo.jarvis.fragment.HomeSlug
import com.globo.jarvis.home.HomeQuery
import com.globo.jarvis.model.Destination
import com.globo.jarvis.type.HighlightImageMobileScales
import com.globo.jarvis.type.HighlightImageTVScales
import com.globo.jarvis.type.HighlightImageTabletScales
import com.globo.jarvis.type.PageType
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeRepositoryTest : BaseUnitTest() {
    companion object {
        const val SCALE_DEFAULT = "X1"
        const val SCALE_X0_75 = "X0_75"
        const val SCALE_X1_5 = "X1_5"
        const val SCALE_X3 = "X3"
        const val SCALE_X2 = "X2"
        const val SCALE_X4 = "X4"
        const val SCALE_UNKNOWN = "\$UNKNOWN"
    }

    private lateinit var homeRepository: HomeRepository

    @Before
    fun setup() {
        homeRepository = spyk(HomeRepository(apolloClient, Device.MOBILE))
    }

    // Structure
    @Test
    fun testStructureList() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("home/structure_valid.json")

        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable = homeRepository.structure("home-free", SCALE_DEFAULT)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { homeRepository.builderHomeQuery("home-free", SCALE_DEFAULT) }

        observable
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val page = it.first
                val premiumHighlight = it.second
                val offerItems = it.third

                (page == "home-assinante" && premiumHighlight != null && !offerItems.isNullOrEmpty())
            }
    }

    @Test
    fun testStructureListPageInvalid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("home/structure_invalid.json")

        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable = homeRepository.structure("home-free", SCALE_DEFAULT)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { homeRepository.builderHomeQuery("home-free", SCALE_DEFAULT) }

        observable
            .test()
            .also {
                it.awaitTerminalEvent()
            }
            .assertError { true }
            .assertErrorMessage("Não foi possível carregar a estrutura da home!")
    }


    // Offer CategoriesList
    @Test
    fun testOfferCategoriesList() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("home/offer_categories_valid.json")

        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable = homeRepository.categories("1223")

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        observable
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val nextPage = it.first
                val hasNextPage = it.second
                val categoryList = it.third

                (nextPage == 2
                        && hasNextPage
                        && !categoryList.isNullOrEmpty())
            }
    }

    @Test
    fun testOfferCategoriesListWithoutNextPage() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("home/offer_categories_without_next_page.json")

        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable = homeRepository.categories("23453")

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        observable
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val nextPage = it.first
                val hasNextPage = it.second
                val categoryList = it.third

                (nextPage == null
                        && hasNextPage
                        && !categoryList.isNullOrEmpty())
            }
    }

    @Test
    fun testOfferCategoriesListWithoutHasNextPage() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("home/offer_categories_without_has_next_page.json")

        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable =
            homeRepository.categories("2342")

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        observable
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val nextPage = it.first
                val hasNextPage = it.second
                val categoryList = it.third

                (nextPage == null
                        && !hasNextPage
                        && categoryList.isEmpty())
            }
    }

    @Test
    fun testOfferCategoriesListWithDefaultValue() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("home/offer_categories_valid.json")


        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable =
            homeRepository.categories("2342")


        // =========================================================================================
        // Action.
        // =========================================================================================
        observable
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { homeRepository.builderOfferCategoriesQuery("2342", 1, 12) }
        verify { homeRepository.transformResourceToCategoryVO(any()) }
    }

    @Test
    fun testOfferCategoriesListWithValue() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("home/offer_categories_valid.json")

        val categoriesRepository =
            spyk(
                CategoriesRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        // =========================================================================================
        // Action.
        // =========================================================================================
        categoriesRepository
            .list(2, 20)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { categoriesRepository.builderCategoriesQuery(2, 20) }
        verify { categoriesRepository.transformResourceToCategoryVO(any()) }
    }


    //Builder CategoriesQuery
    @Test
    fun testBuilderCategoriesQuery() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoriesQuery = homeRepository.builderOfferCategoriesQuery("12342", 2, 24)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoriesQuery.variables().offerId() == "12342")
        assertTrue(categoriesQuery.variables().page().value == 2)
        assertTrue(categoriesQuery.variables().perPage().value == 24)
    }

    @Test
    fun testBuilderCategoriesQueryWithOfferIdNull() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoriesQuery = homeRepository.builderOfferCategoriesQuery(null, 2, 24)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoriesQuery.variables().offerId() == "")
        assertTrue(categoriesQuery.variables().page().value == 2)
        assertTrue(categoriesQuery.variables().perPage().value == 24)
    }


    //Transform Resource To CategoryVO
    @Test
    fun testTransformResourceToCategoryVOWhenDestinationPage() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoryList =
            homeRepository.transformResourceToCategoryVO(builderCategoriesQueryList())
        val category = categoryList[0]

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoryList.isNotEmpty())
        assertTrue(category.destination == Destination.CATEGORY_DETAILS)
        assertTrue(category.id == "esportes")
        assertTrue(category.name == "Jornalismo")
        assertTrue(category.background == "https://s2.glbimg.com/2GfpxI1so8LbAxCVkDO92jRkwu8=/0x196/https://s2.glbimg.com/KnHgA2VCTwjefJrn5jNggfhv5rQ=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/m/6/seYL0IRBiSTYt3bRKfeg/esportes-1-.png")
    }

    @Test
    fun testTransformResourceToCategoryVOWhenMyList() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoryList =
            homeRepository.transformResourceToCategoryVO(builderCategoriesQueryList())
        val category = categoryList[2]

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoryList.isNotEmpty())
        assertTrue(category.destination == Destination.MY_LIST)
        assertTrue(category.id == "minha-lista")
        assertTrue(category.name == "Minha Lista")
        assertTrue(category.background == "https://s2.glbimg.com/nOPoij1Bh8iLepVgjK8X-K8nHlA=/0x196/https://s2.glbimg.com/8r7SuHRXVY0ln1x5rHoXI7i9HDg=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/X/A/W5hSW9Rd202gJRVoBpzg/programas-locais.png")
    }

    @Test
    fun testTransformResourceToCategoryVOWhenDestinationLocalProgram() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoryList =
            homeRepository.transformResourceToCategoryVO(builderCategoriesQueryList())
        val category = categoryList[3]

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoryList.isNotEmpty())
        assertTrue(category.destination == Destination.LOCAL_PROGRAM)
        assertTrue(category.id == "/programas-locais/")
        assertTrue(category.name == "Programas Locais")
        assertTrue(category.background == "https://s2.glbimg.com/nOPoij1Bh8iLepVgjK8X-K8nHlA=/0x196/https://s2.glbimg.com/8r7SuHRXVY0ln1x5rHoXI7i9HDg=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/X/A/W5hSW9Rd202gJRVoBpzg/programas-locais.png")
    }

    @Test
    fun testTransformResourceToCategoryVOWhenNavigationIsNull() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoryList =
            homeRepository.transformResourceToCategoryVO(
                listOf(
                    CategoriesOffer.Resource(
                        "Category",
                        CategoriesOffer.Resource.Fragments(
                            HomeCategories(
                                "typename",
                                "https://s2.glbimg.com/GhT6Th7dCMCcUpE2Kw-owRYP6Tw=/0x196/https://s2.glbimg.com/EaDw369j5sSaV0nfOkxqS_VC8Bw=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/M/D/mB5K4vRdKwVxJtz8FLUQ/jornalismo.png",
                                "Esportes",
                                HomeCategories.Navigation(
                                    "Category",
                                    HomeCategories.Navigation.Fragments(
                                        null,
                                        null
                                    )
                                )
                            )
                        )
                    )
                )
            )

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoryList.isEmpty())
    }

    @Test
    fun testTransformResourceToCategoryVOWhenIdIsNull() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoryList =
            homeRepository.transformResourceToCategoryVO(
                listOf(
                    CategoriesOffer.Resource(
                        "Category",
                        CategoriesOffer.Resource.Fragments(
                            HomeCategories(
                                "typename",
                                "https://s2.glbimg.com/GhT6Th7dCMCcUpE2Kw-owRYP6Tw=/0x196/https://s2.glbimg.com/EaDw369j5sSaV0nfOkxqS_VC8Bw=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/M/D/mB5K4vRdKwVxJtz8FLUQ/jornalismo.png",
                                "Esportes",
                                HomeCategories.Navigation(
                                    "Category",
                                    HomeCategories.Navigation.Fragments(
                                        null,
                                        HomeSlug("MenuSlugNavigation", "/perfil/")
                                    )
                                )
                            )
                        )
                    )
                )
            )

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoryList.isEmpty())
    }

    @Test
    fun testTransformResourceToCategoryVOWhenResourceNull() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val categoryList = homeRepository.transformResourceToCategoryVO(null)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoryList.isEmpty())
    }

    //test builder HomeQuery
    @Test
    fun testBuilderHomeQueryUserLoggedIn() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)


        // =========================================================================================
        // Action.
        // =========================================================================================
        val builder = homeRepository.builderHomeQuery("home-free", SCALE_DEFAULT)


        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertEquals(builder.variables().id(), "home-free")
        assertEquals(builder.variables().filter().value?.subscriptionType(), null)
        assertEquals(builder.variables().filter().value?.subset(), null)
        assertEquals(
            builder.variables().filter().value?.type()?.rawValue(),
            PageType.HOME.rawValue()
        )
    }


    //Builder Image HomeQuery Mobile
    @Test
    fun testBuilderImageHomeQueryMobileDefault() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)


        // =========================================================================================
        // Action.
        // =========================================================================================
        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_DEFAULT)

        assertEquals(
            builderImage.highlightImageMobileScales(HighlightImageMobileScales.X1),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryMobileX0_75() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X0_75)
        assertEquals(
            builderImage.highlightImageMobileScales(HighlightImageMobileScales.X0_75),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryMobileX1_5() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X1_5)
        assertEquals(
            builderImage.highlightImageMobileScales(HighlightImageMobileScales.X1_5),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryMobileX2() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X2)
        assertEquals(
            builderImage.highlightImageMobileScales(HighlightImageMobileScales.X2),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryMobileX3() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X3)
        assertEquals(
            builderImage.highlightImageMobileScales(HighlightImageMobileScales.X3),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryMobileX4() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X4)
        assertEquals(
            builderImage.highlightImageMobileScales(HighlightImageMobileScales.X4),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryMobileUNKNOWN() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.MOBILE)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_UNKNOWN)
        assertEquals(
            builderImage.highlightImageMobileScales(HighlightImageMobileScales.`$UNKNOWN`),
            builderImage
        )
    }


    //Builder Image HomeQuery Tablet
    @Test
    fun testBuilderImageHomeQueryTabletX1() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_DEFAULT)
        assertEquals(
            builderImage.highlightImageTabletScales(HighlightImageTabletScales.X1),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryTabletX1_5() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)


        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X1_5)
        assertEquals(
            builderImage.highlightImageTabletScales(HighlightImageTabletScales.X1_5),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryTabletX0_75() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)


        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X0_75)
        assertEquals(
            builderImage.highlightImageTabletScales(HighlightImageTabletScales.X0_75),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryTabletX2() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)


        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X2)
        assertEquals(
            builderImage.highlightImageTabletScales(HighlightImageTabletScales.X2),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryTabletX3() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X3)
        assertEquals(
            builderImage.highlightImageTabletScales(HighlightImageTabletScales.X3),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryTabletX4() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)


        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X4)
        assertEquals(
            builderImage.highlightImageTabletScales(HighlightImageTabletScales.X4),
            builderImage
        )
    }

    @Test
    fun testBuilderImageHomeQueryUnknown() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)


        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_UNKNOWN)
        assertEquals(
            builderImage.highlightImageTabletScales(HighlightImageTabletScales.`$UNKNOWN`),
            builderImage
        )
    }


    //Builder Image HomeQuery Tv
    @Test
    fun testBuilderImageHomeQueryTvX1() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TABLET)


        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_DEFAULT)
        assertEquals(builderImage.highlightImageTvScales(HighlightImageTVScales.X1), builderImage)
    }

    @Test
    fun testBuilderImageHomeQueryTvX1_5() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TV)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X1_5)
        assertEquals(builderImage.highlightImageTvScales(HighlightImageTVScales.X1_5), builderImage)
    }


    @Test
    fun testBuilderImageHomeQueryTvX3() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TV)

        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_X3)
        assertEquals(builderImage.highlightImageTvScales(HighlightImageTVScales.X3), builderImage)
    }

    @Test
    fun testBuilderImageHomeQueryTvUnknown() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val homeRepository = HomeRepository(apolloClient, Device.TV)


        val homeQuery = HomeQuery.builder()
        val builderImage = homeRepository.builderImageHomeQuery(homeQuery, SCALE_UNKNOWN)
        assertEquals(
            builderImage.highlightImageTvScales(HighlightImageTVScales.`$UNKNOWN`),
            builderImage
        )
    }


    private fun builderCategoriesQueryList() = listOf(
        CategoriesOffer.Resource(
            "Category",
            CategoriesOffer.Resource.Fragments(
                HomeCategories(
                    "typename",
                    "https://s2.glbimg.com/2GfpxI1so8LbAxCVkDO92jRkwu8=/0x196/https://s2.glbimg.com/KnHgA2VCTwjefJrn5jNggfhv5rQ=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/m/6/seYL0IRBiSTYt3bRKfeg/esportes-1-.png",
                    "Jornalismo",
                    HomeCategories.Navigation(
                        "Category",
                        HomeCategories.Navigation.Fragments(HomePage("typename", "esportes"), null)
                    )
                )
            )
        ),
        CategoriesOffer.Resource(
            "Category",
            CategoriesOffer.Resource.Fragments(
                HomeCategories(
                    "typename",
                    "https://s2.glbimg.com/GhT6Th7dCMCcUpE2Kw-owRYP6Tw=/0x196/https://s2.glbimg.com/EaDw369j5sSaV0nfOkxqS_VC8Bw=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/M/D/mB5K4vRdKwVxJtz8FLUQ/jornalismo.png",
                    "Esportes",
                    HomeCategories.Navigation(
                        "Category",
                        HomeCategories.Navigation.Fragments(
                            HomePage("typename", "jornalismo"),
                            null
                        )
                    )
                )
            )
        ),
        CategoriesOffer.Resource(
            "Category",
            CategoriesOffer.Resource.Fragments(
                HomeCategories(
                    "typename",
                    "https://s2.glbimg.com/nOPoij1Bh8iLepVgjK8X-K8nHlA=/0x196/https://s2.glbimg.com/8r7SuHRXVY0ln1x5rHoXI7i9HDg=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/X/A/W5hSW9Rd202gJRVoBpzg/programas-locais.png",
                    "Minha Lista",
                    HomeCategories.Navigation(
                        "Category",
                        HomeCategories.Navigation.Fragments(
                            null,
                            HomeSlug("MenuSlugNavigation", "/minha-lista/")
                        )
                    )
                )
            )
        ),
        CategoriesOffer.Resource(
            "Category",
            CategoriesOffer.Resource.Fragments(
                HomeCategories(
                    "typename",
                    "https://s2.glbimg.com/nOPoij1Bh8iLepVgjK8X-K8nHlA=/0x196/https://s2.glbimg.com/8r7SuHRXVY0ln1x5rHoXI7i9HDg=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/X/A/W5hSW9Rd202gJRVoBpzg/programas-locais.png",
                    "Programas Locais",
                    HomeCategories.Navigation(
                        "Category",
                        HomeCategories.Navigation.Fragments(
                            null,
                            HomeSlug("MenuSlugNavigation", "/programas-locais/")
                        )
                    )
                )
            )
        )
    )

}
