package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.categories.CategoriesQuery
import com.globo.jarvis.categoriesdetails.CategoryDetailsOfferTitleQuery
import com.globo.jarvis.fragment.CategoriesFragment
import com.globo.jarvis.fragment.CategoriesPage
import com.globo.jarvis.fragment.CategoriesSlug
import com.globo.jarvis.model.Destination
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertTrue
import org.junit.Test

class CategoriesRepositoryTest : BaseUnitTest() {
    companion object {
        private const val SCALE_X1 = "X1"
        private const val CATEGORY_ID = "32521"
    }

    //List
    @Test
    fun testLoadList() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("categories/categories_valid.json")

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
        val observable = categoriesRepository.list()

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
    fun testLoadListWithoutNextPage() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("categories/categories_without_next_page.json")

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
        val observable = categoriesRepository.list()

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

                (nextPage == 1
                        && hasNextPage
                        && !categoryList.isNullOrEmpty())
            }
    }

    @Test
    fun testLoadListWithoutHasNextPage() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("categories/categories_without_has_next_page.json")

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
        val observable = categoriesRepository.list()

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

                (nextPage == 1
                        && !hasNextPage
                        && categoryList.isEmpty())
            }
    }

    @Test
    fun testLoadListWithDefaultValue() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("categories/categories_valid.json")

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
            .list()
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { categoriesRepository.builderCategoriesQuery(1, 12) }
        verify { categoriesRepository.transformResourceToCategoryVO(any()) }
    }

    @Test
    fun testLoadListWithValue() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("categories/categories_valid.json")

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


    //Builder CategoryDetailsOfferTitleQuery
    @Test
    fun testBuilderCategoryDetailsOfferTitleQueryWithDefaultValue() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
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
        val videoQuery = categoriesRepository
            .builderCategoryDetailsOfferTitleQuery(CATEGORY_ID, SCALE_X1)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assert(videoQuery.variables().id() == "32521")
        assert(videoQuery.variables().page().value == 1)
        assert(videoQuery.variables().perPage().value == 12)

        verify {
            categoriesRepository.builderImageCategoryDetailsOfferTitle(any(), SCALE_X1)
        }
    }

    @Test
    fun testBuilderCategoryDetailsOfferTitleQueryWithoutDefaultValue() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
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
        val videoQuery = categoriesRepository
            .builderCategoryDetailsOfferTitleQuery(CATEGORY_ID, SCALE_X1, 2, 24)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assert(videoQuery.variables().id() == "32521")
        assert(videoQuery.variables().page().value == 2)
        assert(videoQuery.variables().perPage().value == 24)

        verify {
            categoriesRepository.builderImageCategoryDetailsOfferTitle(any(), SCALE_X1)
        }
    }

    @Test
    fun testBuilderCategoryDetailsOfferTitleQueryWithVideoIdIsNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
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
        val videoQuery = categoriesRepository
            .builderCategoryDetailsOfferTitleQuery(null, SCALE_X1)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assert(videoQuery.variables().id() == "")
        assert(videoQuery.variables().page().value == 1)
        assert(videoQuery.variables().perPage().value == 12)

        verify {
            categoriesRepository.builderImageCategoryDetailsOfferTitle(any(), SCALE_X1)
        }
    }


    //Builder Image Offer CategoryDetailsOfferTitle
    @Test
    fun testBuilderImageCategoryDetailsOfferTitleQueryWhenIsMobile() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
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
        val builderImage =
            categoriesRepository.builderImageCategoryDetailsOfferTitle(
                CategoryDetailsOfferTitleQuery.builder().id(""),
                SCALE_X1
            )

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(builderImage.build().variables().mobilePosterScales().value?.rawValue() == "X1")
        assertTrue(builderImage.build().variables().tabletPosterScales().value?.rawValue() == null)
        assertTrue(builderImage.build().variables().tvCoverLandscapeScales().value?.rawValue() == null)
    }

    @Test
    fun testBuilderImageOfferLastWatchedVideosQueryWhenIsTablet() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val categoriesRepository =
            spyk(
                CategoriesRepository(
                    apolloClient,
                    Device.TABLET
                )
            )


        // =========================================================================================
        // Action.
        // =========================================================================================
        val builderImage =
            categoriesRepository.builderImageCategoryDetailsOfferTitle(
                CategoryDetailsOfferTitleQuery.builder().id(""),
                SCALE_X1
            )

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(builderImage.build().variables().tabletPosterScales().value?.rawValue() == "X1")
        assertTrue(builderImage.build().variables().mobilePosterScales().value?.rawValue() == null)
        assertTrue(builderImage.build().variables().tvCoverLandscapeScales().value?.rawValue() == null)
    }

    @Test
    fun testBuilderImageOfferLastWatchedVideosQueryWhenIsTV() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        val categoriesRepository =
            spyk(
                CategoriesRepository(
                    apolloClient,
                    Device.TV
                )
            )


        // =========================================================================================
        // Action.
        // =========================================================================================
        val builderImage =
            categoriesRepository.builderImageCategoryDetailsOfferTitle(
                CategoryDetailsOfferTitleQuery.builder().id(""),
                SCALE_X1
            )

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(builderImage.build().variables().tvPosterScales().value?.rawValue() == "X1")
        assertTrue(builderImage.build().variables().tvCoverLandscapeScales().value?.rawValue() == "X1080")
        assertTrue(builderImage.build().variables().tabletPosterScales().value?.rawValue() == null)
        assertTrue(builderImage.build().variables().mobilePosterScales().value?.rawValue() == null)
    }


    //Builder CategoriesQuery
    @Test
    fun testBuilderCategoriesQuery() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
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
        val categoriesQuery =
            categoriesRepository.builderCategoriesQuery(2, 24)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoriesQuery.variables().page().value == 2)
        assertTrue(categoriesQuery.variables().perPage().value == 24)
    }


    //Transform Resource To CategoryVO
    @Test
    fun testTransformResourceToCategoryVOWhenDestinationPage() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
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
        val categoryList =
            categoriesRepository.transformResourceToCategoryVO(builderCategoriesQueryList())
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
        // Prepare.
        // =========================================================================================
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
        val categoryList =
            categoriesRepository.transformResourceToCategoryVO(builderCategoriesQueryList())
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
        // Prepare.
        // =========================================================================================
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
        val categoryList =
            categoriesRepository.transformResourceToCategoryVO(builderCategoriesQueryList())
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
        // Prepare.
        // =========================================================================================
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
        val categoryList =
            categoriesRepository.transformResourceToCategoryVO(
                listOf(
                    CategoriesQuery.Resource(
                        "Category",
                        CategoriesQuery.Resource.Fragments(
                            CategoriesFragment(
                                "typename",
                                "https://s2.glbimg.com/GhT6Th7dCMCcUpE2Kw-owRYP6Tw=/0x196/https://s2.glbimg.com/EaDw369j5sSaV0nfOkxqS_VC8Bw=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/M/D/mB5K4vRdKwVxJtz8FLUQ/jornalismo.png",
                                "Esportes",
                                CategoriesFragment.Navigation(
                                    "Category",
                                    CategoriesFragment.Navigation.Fragments(
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
        // Prepare.
        // =========================================================================================
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
        val categoryList =
            categoriesRepository.transformResourceToCategoryVO(
                listOf(
                    CategoriesQuery.Resource(
                        "Category",
                        CategoriesQuery.Resource.Fragments(
                            CategoriesFragment(
                                "typename",
                                "https://s2.glbimg.com/GhT6Th7dCMCcUpE2Kw-owRYP6Tw=/0x196/https://s2.glbimg.com/EaDw369j5sSaV0nfOkxqS_VC8Bw=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/M/D/mB5K4vRdKwVxJtz8FLUQ/jornalismo.png",
                                "Esportes",
                                CategoriesFragment.Navigation(
                                    "Category",
                                    CategoriesFragment.Navigation.Fragments(
                                        null,
                                        CategoriesSlug("MenuSlugNavigation", "/perfil/")
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
        // Prepare.
        // =========================================================================================
        val categoriesRepository =
            spyk(
                CategoriesRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val categoryList = categoriesRepository.transformResourceToCategoryVO(null)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(categoryList.isEmpty())
    }

    private fun builderCategoriesQueryList() = listOf(
        CategoriesQuery.Resource(
            "Category",
            CategoriesQuery.Resource.Fragments(
                CategoriesFragment(
                    "typename",
                    "https://s2.glbimg.com/2GfpxI1so8LbAxCVkDO92jRkwu8=/0x196/https://s2.glbimg.com/KnHgA2VCTwjefJrn5jNggfhv5rQ=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/m/6/seYL0IRBiSTYt3bRKfeg/esportes-1-.png",
                    "Jornalismo",
                    CategoriesFragment.Navigation(
                        "Category",
                        CategoriesFragment.Navigation.Fragments(
                            CategoriesPage(
                                "typename",
                                "esportes"
                            ), null
                        )
                    )
                )
            )
        ),
        CategoriesQuery.Resource(
            "Category",
            CategoriesQuery.Resource.Fragments(
                CategoriesFragment(
                    "typename",
                    "https://s2.glbimg.com/GhT6Th7dCMCcUpE2Kw-owRYP6Tw=/0x196/https://s2.glbimg.com/EaDw369j5sSaV0nfOkxqS_VC8Bw=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/M/D/mB5K4vRdKwVxJtz8FLUQ/jornalismo.png",
                    "Esportes",
                    CategoriesFragment.Navigation(
                        "Category",
                        CategoriesFragment.Navigation.Fragments(
                            CategoriesPage(
                                "typename",
                                "jornalismo"
                            ), null
                        )
                    )
                )
            )
        ),
        CategoriesQuery.Resource(
            "Category",
            CategoriesQuery.Resource.Fragments(
                CategoriesFragment(
                    "typename",
                    "https://s2.glbimg.com/nOPoij1Bh8iLepVgjK8X-K8nHlA=/0x196/https://s2.glbimg.com/8r7SuHRXVY0ln1x5rHoXI7i9HDg=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/X/A/W5hSW9Rd202gJRVoBpzg/programas-locais.png",
                    "Minha Lista",
                    CategoriesFragment.Navigation(
                        "Category",
                        CategoriesFragment.Navigation.Fragments(
                            null,
                            CategoriesSlug("MenuSlugNavigation", "/minha-lista/")
                        )
                    )
                )
            )
        ),
        CategoriesQuery.Resource(
            "Category",
            CategoriesQuery.Resource.Fragments(
                CategoriesFragment(
                    "typename",
                    "https://s2.glbimg.com/nOPoij1Bh8iLepVgjK8X-K8nHlA=/0x196/https://s2.glbimg.com/8r7SuHRXVY0ln1x5rHoXI7i9HDg=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/X/A/W5hSW9Rd202gJRVoBpzg/programas-locais.png",
                    "Programas Locais",
                    CategoriesFragment.Navigation(
                        "Category",
                        CategoriesFragment.Navigation.Fragments(
                            null,
                            CategoriesSlug("MenuSlugNavigation", "/programas-locais/")
                        )
                    )
                )
            )
        )
    )

}