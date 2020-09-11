package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.model.AvailableFor
import com.globo.jarvis.model.Kind
import com.globo.jarvis.model.Type
import com.globo.jarvis.type.*
import com.globo.jarvis.user.LastWatchedVideosQuery
import com.globo.jarvis.user.MyListQuery
import io.mockk.*
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryTest : BaseUnitTest() {
    companion object {
        private const val SCALE_X1 = "X1"
        private const val GLB_ID = "234234"
    }

    //Lasted Videos
    @Test
    fun testLastVideosValid() {
        enqueueResponse("continuewatching/lasted_videos_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .lastVideos(GLB_ID, SCALE_X1, 1)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val nextPage = it.first
                val hasNextPage = it.second
                val continueWatchingVO = it.third.firstOrNull()

                (nextPage == 2
                        && hasNextPage
                        && it.third.isNotEmpty()
                        && it.third.size == 10
                        && continueWatchingVO?.id == "8104868"
                        && continueWatchingVO.watchedProgress == 1563005)
                        && !continueWatchingVO.fullWatched
            }
    }

    @Test
    fun testLastVideosInvalid() {
        enqueueResponse("continuewatching/lasted_videos_invalid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .lastVideos(GLB_ID, SCALE_X1, 1)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.third.isEmpty()
            }
    }

    @Test
    fun testLastVideosWithGlbIdNull() {
        enqueueResponse("continuewatching/lasted_videos_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .lastVideos(null, SCALE_X1, 1)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.third.isEmpty()
            }
    }

    @Test
    fun testLastVideosWithGlbIdEmpty() {
        enqueueResponse("continuewatching/lasted_videos_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .lastVideos("", SCALE_X1, 1)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.third.isEmpty()
            }
    }

    @Test
    fun testLastVideosCheckDefaultPageAndPerPage() {
        enqueueResponse("continuewatching/lasted_videos_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository.lastVideos(GLB_ID, SCALE_X1)

        verify {
            userRepository.builderOfferContinueWatchingQuery(1, 6, "X1")
        }
    }

    @Test
    fun testLastedVideosCheckPage() {
        enqueueResponse("continuewatching/lasted_videos_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository.lastVideos(GLB_ID, SCALE_X1, 2, 12)

        verify {
            userRepository.builderOfferContinueWatchingQuery(2, 12, "X1")
        }
    }


    //MyList
    @Test
    fun testMyListRxValid() {
        enqueueResponse("mylist/my_list_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .myList(SCALE_X1, 1, 10)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val titleMyList = it.titleMyList
                val title = titleMyList?.firstOrNull()

                (titleMyList?.isNotEmpty() == true
                        && titleMyList.size == 10
                        && title?.titleId == "jxbWwPsRvj"
                        && title.originProgramId == "11227"
                        && title.headline == "Supernatural"
                        && title.poster == "https://s2.glbimg.com/uBj20fuigtG6fuPBjWMcvQclRTE=/100x152/https://s2.glbimg.com/AqeMj7-8MpTLopGvPzwqYghKalE=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/F/A8ypw5Qqyluy5DR4NX0Q/2019-278-series-warner1-supernatural-poster.jpg"
                        && title.type == Type.SERIES)
            }
    }

    @Test
    fun testMyListDefaultValues() {
        enqueueResponse("mylist/my_list_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .myList(SCALE_X1)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }

        verify {
            userRepository.builderMyListQuery(1, 12, SCALE_X1)
        }
    }

    @Test
    fun testMyListCallbackValid() {
        enqueueResponse("mylist/my_list_valid.json")

        val slot = slot<Callback.User>()
        val userCallback: Callback.User = mockk(relaxed = true)

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        every {
            userRepository.myList(
                SCALE_X1,
                1,
                10,
                capture(slot)
            )
        } answers {
            slot.captured.onMyListSuccess(mockk())
        }

        userRepository.myList(
            SCALE_X1,
            1,
            10,
            userCallback
        )

        verify {
            userCallback.onMyListSuccess(any())
        }
    }

    @Test
    fun testMyListRxEmpty() {
        enqueueResponse("mylist/my_list_empty.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .myList(SCALE_X1, 1, 10)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.titleMyList?.isEmpty() == true
            }
    }

    @Test
    fun testMyListRxInvalid() {
        enqueueResponse("mylist/my_list_invalid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        assert(userRepository
            .myList(SCALE_X1, 1, 10)
            .test()
            .apply {
                awaitTerminalEvent()
            }
            .errorCount() == 1)
    }

    @Test
    fun testMyListCallbackInvalid() {
        enqueueResponse("mylist/my_list_invalid.json")

        val exceptionMock: Exception = mockk()
        val slot = slot<Callback.User>()
        val userCallback: Callback.User = mockk(relaxed = true)

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        every {
            userRepository.myList(
                SCALE_X1,
                1,
                10,
                capture(slot)
            )
        } answers {
            slot.captured.onFailure(exceptionMock)
        }

        userRepository.myList(
            SCALE_X1,
            1,
            10,
            userCallback
        )

        verify {
            userCallback.onFailure(exceptionMock)
        }
    }

    @Test
    fun testMyListRxCheckDefaultPageAndPerPage() {
        enqueueResponse("mylist/my_list_valid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .myList(SCALE_X1, 1, 10)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }

        verify {
            userRepository.builderMyListQuery(1, 10, "X1")
        }
    }


    //Add My List
    @Test
    fun testAddMyListRxValid() {
        enqueueResponse("mylist/added_my_list.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .addToMyList("jxbWwPsRvj")
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it
            }
    }

    @Test
    fun testAddMyListRxWithTitleIdNull() {
        enqueueResponse("mylist/not_added_my_list.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .addToMyList(null)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                !it
            }
    }

    @Test
    fun testAddMyListRxWithTitleIdEmpty() {
        enqueueResponse("mylist/not_added_my_list.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .addToMyList("")
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                !it
            }
    }

    @Test
    fun testAddMyListRxInvalid() {
        enqueueResponse("mylist/added_my_list_invalid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        assert(userRepository
            .addToMyList(null)
            .test()
            .apply {
                awaitTerminalEvent()
            }
            .errorCount() == 1)
    }


    //Add My List
    @Test
    fun testDeleteMyListRxValid() {
        enqueueResponse("mylist/deleted_my_list.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .deleteMyList("jxbWwPsRvj")
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it
            }
    }

    @Test
    fun testDeleteMyListRxWithTitleIdNull() {
        enqueueResponse("mylist/not_deleted_my_list.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .deleteMyList(null)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                !it
            }
    }

    @Test
    fun testDeleteMyListRxWithTitleIdEmpty() {
        enqueueResponse("mylist/not_deleted_my_list.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        userRepository
            .deleteMyList("")
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                !it
            }
    }

    @Test
    fun testDeleteMyListRxInvalid() {
        enqueueResponse("mylist/deleted_my_list_invalid.json")

        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        assert(userRepository
            .deleteMyList(null)
            .test()
            .apply {
                awaitTerminalEvent()
            }
            .errorCount() == 1)
    }


    //Builder AddMyList Query
    @Test
    fun testBuilderAddMyListQuery() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val addMyListMutation = userRepository.builderAddMyListQuery("jxbWwPsRvj")

        assert(addMyListMutation.variables().input().titleId() == "jxbWwPsRvj")
    }


    //Builder DeleteMyList Query
    @Test
    fun testBuilderDeleteMyListQuery() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val deleteMyListMutation = userRepository.builderDeleteMyListQuery("jxbWwPsRvj")

        assert(deleteMyListMutation.variables().input().titleId() == "jxbWwPsRvj")
    }


    //Builder MyList Query
    @Test
    fun testBuilderMyListQuery() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val deleteMyListMutation = userRepository.builderMyListQuery(1, 10, SCALE_X1)

        assert(deleteMyListMutation.variables().page().value == 1)
        assert(deleteMyListMutation.variables().perPage().value == 10)
    }


    //Builder ImageTitle
    @Test
    fun testBuilderImageTitleWhenMobile() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val myListQuery = userRepository.builderImageTitle(MyListQuery.builder(), SCALE_X1)

        assert(
            myListQuery.build().variables()
                .mobilePosterScales().value == MobilePosterScales.safeValueOf(
                SCALE_X1
            )
        )
    }

    @Test
    fun testBuilderImageTitleWhenTablet() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.TABLET
                )
            )

        val myListQuery = userRepository.builderImageTitle(MyListQuery.builder(), SCALE_X1)

        assert(
            myListQuery.build().variables()
                .tabletPosterScales().value == TabletPosterScales.safeValueOf(
                SCALE_X1
            )
        )
    }

    @Test
    fun testBuilderImageTitleWhenTv() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.TV
                )
            )

        val myListQuery = userRepository.builderImageTitle(MyListQuery.builder(), SCALE_X1)

        assert(
            myListQuery.build().variables().tvPosterScales().value == TVPosterScales.safeValueOf(
                SCALE_X1
            )
        )
    }

    //Transform ResourceResponse To TitleMyListVO
    @Test
    fun testTransformResourceResponseToTitleMyListVOWhenTypeSerieAndMobile() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val titleVOList = userRepository
            .transformResourceResponseToTitleMyListVO(
                recoverMyListQueryResource()
            )

        val title = titleVOList.firstOrNull()

        assertTrue(titleVOList.isNotEmpty())
        assertTrue(title?.originProgramId == "11227")
        assertTrue(title?.titleId == "jxbWwPsRvj")
        assertTrue(title?.headline == "Supernatural")
        assertTrue(title?.type == Type.SERIES)
        assertTrue(title?.poster == "https://s2.glbimg.com/uBj20fuigtG6fuPBjWMcvQclRTE=/100x152/https://s2.glbimg.com/AqeMj7-8MpTLopGvPzwqYghKalE=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/F/A8ypw5Qqyluy5DR4NX0Q/2019-278-series-warner1-supernatural-poster.jpg")
    }

    @Test
    fun testTransformResourceResponseToTitleMyListVOWhenTypeSerieAndTablet() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.TABLET
                )
            )

        val titleVOList = userRepository
            .transformResourceResponseToTitleMyListVO(
                recoverMyListQueryResource()
            )

        val title = titleVOList.firstOrNull()

        assertTrue(titleVOList.isNotEmpty())
        assertTrue(title?.originProgramId == "11227")
        assertTrue(title?.titleId == "jxbWwPsRvj")
        assertTrue(title?.headline == "Supernatural")
        assertTrue(title?.type == Type.SERIES)
        assertTrue(title?.poster == "https://s2.glbimg.com/9nOjEZ53wpNtdE3i9NkOkEFfO8s=/144x212/https://s2.glbimg.com/AqeMj7-8MpTLopGvPzwqYghKalE=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/F/A8ypw5Qqyluy5DR4NX0Q/2019-278-series-warner1-supernatural-poster.jpg")
    }

    @Test
    fun testTransformResourceResponseToTitleMyListVOWhenTypeSerieAndTv() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.TV
                )
            )

        val titleVOList = userRepository
            .transformResourceResponseToTitleMyListVO(
                recoverMyListQueryResource()
            )

        val title = titleVOList.firstOrNull()

        assertTrue(titleVOList.isNotEmpty())
        assertTrue(title?.originProgramId == "11227")
        assertTrue(title?.titleId == "jxbWwPsRvj")
        assertTrue(title?.headline == "Supernatural")
        assertTrue(title?.type == Type.SERIES)
        assertTrue(title?.poster == "https://s2.glbimg.com/ZAjP6FKPNptqqWwZ6xMySSOASeU=/146x216/https://s2.glbimg.com/AqeMj7-8MpTLopGvPzwqYghKalE=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/F/A8ypw5Qqyluy5DR4NX0Q/2019-278-series-warner1-supernatural-poster.jpg")
    }


    //Transform GenericVideoOffersResource To ContinueWatchingVO

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOTablet() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.TABLET
                )
            )

        val continueWatchingVOList = userRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                recoverLastWatchedVideosQueryList()
            )

        val continueWatchingVO = continueWatchingVOList.firstOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.title?.logo == "https://s2.glbimg.com/xwsswliPq3bz6RmppzgQmz0GtaQ=/304x171/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png")
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOTv() {
        val userRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.TV
                )
            )

        val continueWatchingVOList = userRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                recoverLastWatchedVideosQueryList()
            )

        val continueWatchingVO = continueWatchingVOList.firstOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.title?.logo == "https://s2.glbimg.com/4tJmsUMCCHOfZdSQDtlf3JofAms=/292x165/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png")
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOMobile() {
        val continueWatchingRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val continueWatchingVOList = continueWatchingRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                recoverLastWatchedVideosQueryList()
            )

        val continueWatchingVO = continueWatchingVOList.lastOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.title?.logo == "https://s2.glbimg.com/jbxaivgBDVQh5-IYq-qZ3WQPHGw=/262x147/https://s2.glbimg.com/oK3-1oXHUaIOsDjg2PPqa8rjwUc=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/D/6/wqpVQ4TOC0QakGwuLBYQ/2019-581-series-adiantas-ilha-de-ferro-t2-logo.png")
    }


    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOWhenDurationInvalid() {
        val continueWatchingRepository =
            spyk(
                UserRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val continueWatchingVOList = continueWatchingRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                listOf(
                    LastWatchedVideosQuery.Resource(
                        "Video", "8104868", SubscriptionType.SUBSCRIBER,
                        "A Casa de Cera", null, "14",
                        3116568, "1h 52min", KindType.EPISODE,
                        "https://s01.video.glbimg.com/x216/8104868.jpg",
                        null, LastWatchedVideosQuery.Title(
                            "Title",
                            "10122", "QDBKzvPXh6",
                            "A Casa de Cera",
                            TitleType.MOVIE,
                            "/eramos-seis/t/FDMcMpvPhM",
                            LastWatchedVideosQuery.Logo(
                                "Logo",
                                "https://s2.glbimg.com/-utXsUjuUc4v05jskUHvZAmYLMQ=/262x147/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/xwsswliPq3bz6RmppzgQmz0GtaQ=/304x171/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/4tJmsUMCCHOfZdSQDtlf3JofAms=/292x165/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png"
                            )
                        )
                    )
                )
            )

        val continueWatchingVO = continueWatchingVOList.firstOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.duration == 0)
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOWhenDurationValid() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        val continueWatchingVOList = continueWatchingRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                listOf(
                    LastWatchedVideosQuery.Resource(
                        "Video", "8104868", SubscriptionType.SUBSCRIBER,
                        "A Casa de Cera", 6763256, "14",
                        3116568, "1h 52min", KindType.EPISODE,
                        "https://s01.video.glbimg.com/x216/8104868.jpg",
                        null, LastWatchedVideosQuery.Title(
                            "Title",
                            "10122", "QDBKzvPXh6",
                            "A Casa de Cera", TitleType.MOVIE,
                            "/eramos-seis/t/FDMcMpvPhM",
                            LastWatchedVideosQuery.Logo(
                                "Logo",
                                "https://s2.glbimg.com/-utXsUjuUc4v05jskUHvZAmYLMQ=/262x147/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/xwsswliPq3bz6RmppzgQmz0GtaQ=/304x171/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/4tJmsUMCCHOfZdSQDtlf3JofAms=/292x165/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png"
                            )
                        )
                    )
                )
            )

        val continueWatchingVO = continueWatchingVOList.firstOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.duration == 6763256)
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOWhenWatchedProgressInvalid() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        val continueWatchingVOList = continueWatchingRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                listOf(
                    LastWatchedVideosQuery.Resource(
                        "Video", "8104868", SubscriptionType.SUBSCRIBER,
                        "A Casa de Cera", 6763256, "14",
                        null, "1h 52min", KindType.EPISODE,
                        "https://s01.video.glbimg.com/x216/8104868.jpg",
                        null, LastWatchedVideosQuery.Title(
                            "Title",
                            "10122", "QDBKzvPXh6",
                            "A Casa de Cera", TitleType.MOVIE,
                            "/eramos-seis/t/FDMcMpvPhM",
                            LastWatchedVideosQuery.Logo(
                                "Logo",
                                "https://s2.glbimg.com/-utXsUjuUc4v05jskUHvZAmYLMQ=/262x147/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/xwsswliPq3bz6RmppzgQmz0GtaQ=/304x171/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/4tJmsUMCCHOfZdSQDtlf3JofAms=/292x165/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png"
                            )
                        )
                    )
                )
            )

        val continueWatchingVO = continueWatchingVOList.firstOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.watchedProgress == 0)
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOWhenWatchedProgressValid() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        val continueWatchingVOList = continueWatchingRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                listOf(
                    LastWatchedVideosQuery.Resource(
                        "Video", "8104868", SubscriptionType.SUBSCRIBER,
                        "A Casa de Cera", 6763256, "14",
                        3116568, "1h 52min", KindType.EPISODE,
                        "https://s01.video.glbimg.com/x216/8104868.jpg",
                        null, LastWatchedVideosQuery.Title(
                            "Title",
                            "10122", "QDBKzvPXh6",
                            "A Casa de Cera", TitleType.MOVIE,
                            "/eramos-seis/t/FDMcMpvPhM",
                            LastWatchedVideosQuery.Logo(
                                "Logo",
                                "https://s2.glbimg.com/-utXsUjuUc4v05jskUHvZAmYLMQ=/262x147/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/xwsswliPq3bz6RmppzgQmz0GtaQ=/304x171/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                                "https://s2.glbimg.com/4tJmsUMCCHOfZdSQDtlf3JofAms=/292x165/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png"
                            )
                        )
                    )
                )
            )

        val continueWatchingVO = continueWatchingVOList.firstOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.watchedProgress == 3116568)
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVO() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        val continueWatchingVOList = continueWatchingRepository
            .transformLastWatchedVideosToContinueWatchingVO(
                recoverLastWatchedVideosQueryList()
            )

        val continueWatchingVO = continueWatchingVOList.firstOrNull()

        assertTrue(continueWatchingVOList.isNotEmpty())
        assertTrue(continueWatchingVO?.id == "8104868")
        assertTrue(continueWatchingVO?.headline == "Se Eu Fechar os Olhos Agora")
        assertTrue(continueWatchingVO?.description == "A Terra é azul?")
        assertTrue(continueWatchingVO?.duration == 6763256)
        assertTrue(continueWatchingVO?.watchedProgress == 3116568)
        assertTrue(continueWatchingVO?.formattedDuration == "1h 52min")
        assertTrue(continueWatchingVO?.thumb == "https://s01.video.glbimg.com/x216/8104868.jpg")
        assertTrue(continueWatchingVO?.title?.logo == "https://s2.glbimg.com/-utXsUjuUc4v05jskUHvZAmYLMQ=/262x147/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png")
        assertTrue(continueWatchingVO?.rating == "14")
        assertTrue(continueWatchingVO?.availableFor == AvailableFor.SUBSCRIBER)
        assertTrue(continueWatchingVO?.kind == Kind.EPISODE)
        assertTrue(continueWatchingVO?.abExperiment?.pathUrl == "/eramos-seis/t/FDMcMpvPhM")
        assertTrue(continueWatchingVO?.title?.originProgramId == "10122")
        assertTrue(continueWatchingVO?.title?.titleId == "QDBKzvPXh6")
        assertTrue(continueWatchingVO?.title?.type == Type.MOVIES)
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOWhenListNull() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        assertTrue(
            continueWatchingRepository.transformLastWatchedVideosToContinueWatchingVO(
                null
            ).isEmpty()
        )
    }

    @Test
    fun testTransformLastWatchedVideosToContinueWatchingVOWhenListEmpty() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        assertTrue(
            continueWatchingRepository
                .transformLastWatchedVideosToContinueWatchingVO(emptyList()).isEmpty()
        )
    }


    //Builder Offer ContinueWatching Query
    @Test
    fun testBuilderOfferContinueWatchingQuery() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        val lastWatchedVideosQuery =
            continueWatchingRepository.builderOfferContinueWatchingQuery(2, 14, SCALE_X1)

        assertTrue(lastWatchedVideosQuery.variables().page().value == 2)
        assertTrue(lastWatchedVideosQuery.variables().perPage().value == 14)
        assertTrue(lastWatchedVideosQuery.variables().mobileLogoScales().value?.rawValue() == "X1")
    }


    //Builder Image Offer ContinueWatching Query
    @Test
    fun testBuilderImageOfferLastWatchedVideosQueryWhenIsMobile() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.MOBILE
            )

        val builderImage =
            continueWatchingRepository.builderImageOfferContinueWatchingQuery(
                LastWatchedVideosQuery.builder(),
                SCALE_X1
            )

        assertTrue(builderImage.build().variables().mobileLogoScales().value?.rawValue() == "X1")
        assertTrue(builderImage.build().variables().tabletLogoScales().value?.rawValue() == null)
        assertTrue(builderImage.build().variables().logoTVScales().value?.rawValue() == null)
    }

    @Test
    fun testBuilderImageOfferLastWatchedVideosQueryWhenIsTablet() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.TABLET
            )

        val builderImage =
            continueWatchingRepository.builderImageOfferContinueWatchingQuery(
                LastWatchedVideosQuery.builder(),
                SCALE_X1
            )

        assertTrue(builderImage.build().variables().tabletLogoScales().value?.rawValue() == "X1")
        assertTrue(builderImage.build().variables().logoTVScales().value?.rawValue() == null)
        assertTrue(builderImage.build().variables().mobileLogoScales().value?.rawValue() == null)
    }

    @Test
    fun testBuilderImageOfferLastWatchedVideosQueryWhenIsTV() {
        val continueWatchingRepository =
            UserRepository(
                apolloClient,
                Device.TV
            )

        val builderImage =
            continueWatchingRepository.builderImageOfferContinueWatchingQuery(
                LastWatchedVideosQuery.builder(), SCALE_X1
            )

        assertTrue(builderImage.build().variables().logoTVScales().value?.rawValue() == "X1")
        assertTrue(builderImage.build().variables().tabletLogoScales().value?.rawValue() == null)
        assertTrue(builderImage.build().variables().mobileLogoScales().value?.rawValue() == null)
    }

    private fun recoverLastWatchedVideosQueryList() = listOf(
        LastWatchedVideosQuery.Resource(
            "Video", "8104868", SubscriptionType.SUBSCRIBER,
            "A Terra é azul?", 6763256, "14",
            3116568, "1h 52min", KindType.EPISODE,
            "https://s01.video.glbimg.com/x216/8104868.jpg",
            null, LastWatchedVideosQuery.Title(
                "Title",
                "10122", "QDBKzvPXh6",
                "Se Eu Fechar os Olhos Agora", TitleType.MOVIE,
                "/eramos-seis/t/FDMcMpvPhM",
                LastWatchedVideosQuery.Logo(
                    "Logo",
                    "https://s2.glbimg.com/-utXsUjuUc4v05jskUHvZAmYLMQ=/262x147/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                    "https://s2.glbimg.com/xwsswliPq3bz6RmppzgQmz0GtaQ=/304x171/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png",
                    "https://s2.glbimg.com/4tJmsUMCCHOfZdSQDtlf3JofAms=/292x165/https://s2.glbimg.com/-M4rkxyk4ar1IyCq3ThoPq_u5iQ=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/A/I/xoUB2zR2ujHwcCZwolEQ/2019-562-filmes-warner-out-a-casa-de-cera-logo.png"
                )
            )
        ),

        LastWatchedVideosQuery.Resource(
            "Video", "7146575", SubscriptionType.SUBSCRIBER,
            "Ninguém é uma Ilha", 3167456, "16",
            3116568, "52 min", KindType.EPISODE,
            "https://s04.video.glbimg.com/x216/7146575.jpg",
            null, LastWatchedVideosQuery.Title(
                "Title",
                "10874", "ZdCNg87C7p",
                "Ilha de Ferro", TitleType.SERIE,
                "/eramos-seis/t/FDMcMpvPhM",
                LastWatchedVideosQuery.Logo(
                    "Logo",
                    "https://s2.glbimg.com/jbxaivgBDVQh5-IYq-qZ3WQPHGw=/262x147/https://s2.glbimg.com/oK3-1oXHUaIOsDjg2PPqa8rjwUc=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/D/6/wqpVQ4TOC0QakGwuLBYQ/2019-581-series-adiantas-ilha-de-ferro-t2-logo.png",
                    "https://s2.glbimg.com/PtTLGMQbLracuF-Ce1zeuqBO4ew=/304x171/https://s2.glbimg.com/oK3-1oXHUaIOsDjg2PPqa8rjwUc=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/D/6/wqpVQ4TOC0QakGwuLBYQ/2019-581-series-adiantas-ilha-de-ferro-t2-logo.png",
                    "https://s2.glbimg.com/1Gvb1v5ibFPkL4kbBI3LEaKenMs=/292x165/https://s2.glbimg.com/oK3-1oXHUaIOsDjg2PPqa8rjwUc=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/D/6/wqpVQ4TOC0QakGwuLBYQ/2019-581-series-adiantas-ilha-de-ferro-t2-logo.png"
                )
            )
        )
    )

    private fun recoverMyListQueryResource() = listOf(
        MyListQuery.Resource(
            "Title", "11227", "jxbWwPsRvj",
            "Supernatural", TitleType.SERIE,
            MyListQuery.Poster(
                "Poster",
                "https://s2.glbimg.com/uBj20fuigtG6fuPBjWMcvQclRTE=/100x152/https://s2.glbimg.com/AqeMj7-8MpTLopGvPzwqYghKalE=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/F/A8ypw5Qqyluy5DR4NX0Q/2019-278-series-warner1-supernatural-poster.jpg",
                "https://s2.glbimg.com/9nOjEZ53wpNtdE3i9NkOkEFfO8s=/144x212/https://s2.glbimg.com/AqeMj7-8MpTLopGvPzwqYghKalE=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/F/A8ypw5Qqyluy5DR4NX0Q/2019-278-series-warner1-supernatural-poster.jpg",
                "https://s2.glbimg.com/ZAjP6FKPNptqqWwZ6xMySSOASeU=/146x216/https://s2.glbimg.com/AqeMj7-8MpTLopGvPzwqYghKalE=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2019/h/F/A8ypw5Qqyluy5DR4NX0Q/2019-278-series-warner1-supernatural-poster.jpg"
            )
        ),
        MyListQuery.Resource(
            "Title", "11925", "VTBdTB5hP1",
            "Doctor Who", TitleType.SERIE,
            MyListQuery.Poster(
                "Poster",
                "https://s2.glbimg.com/saHt6zmesW62tnQD9Cb8tG4YRBY=/100x152/https://s2.glbimg.com/4ZoyHqO0DAqs0KwP8IDKKA8lcHM=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/O/B/hKxFP5REuFFEIIGtT4Zw/2020-756-serie-bbc-doctor-who-poster.jpg",
                "https://s2.glbimg.com/B24viz7HBb-6EVg0Y_6mVyEvEjI=/144x212/https://s2.glbimg.com/4ZoyHqO0DAqs0KwP8IDKKA8lcHM=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/O/B/hKxFP5REuFFEIIGtT4Zw/2020-756-serie-bbc-doctor-who-poster.jpg",
                "https://s2.glbimg.com/0ls1U2QgBfPN9eu8hHV2eLUB7Tk=/146x216/https://s2.glbimg.com/4ZoyHqO0DAqs0KwP8IDKKA8lcHM=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/O/B/hKxFP5REuFFEIIGtT4Zw/2020-756-serie-bbc-doctor-who-poster.jpg"
            )
        )
    )

}


