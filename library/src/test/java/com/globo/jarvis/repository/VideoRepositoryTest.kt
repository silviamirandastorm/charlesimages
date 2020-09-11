package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.model.AvailableFor
import com.globo.jarvis.model.Kind
import com.globo.jarvis.model.Type
import com.globo.jarvis.type.KindType
import com.globo.jarvis.type.SubscriptionType
import com.globo.jarvis.type.TitleFormat
import com.globo.jarvis.type.TitleType
import com.globo.jarvis.video.NextVideoQuery
import com.globo.jarvis.video.VideoQuery
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test

class VideoRepositoryTest : BaseUnitTest() {
    companion object {
        private const val SCALE_X1 = "X1"
        private const val VIDEO_ID = "234234"
    }

    //Details
    @Test
    fun testDetails() {
        enqueueResponse("video/video_valid.json")

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        videoRepository
            .details(VIDEO_ID, SCALE_X1)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.id == "8345894"
                        && it.headline == "Episódio 1"
                        && it.description == "Um grupo de mulheres une forças para ajudar umas às outras e enfrentar os vários desafios da vida além de resolver um caso de assassinato."
                        && it.formattedDuration == "40 min"
                        && it.duration == 2427826
                        && it.availableFor == AvailableFor.SUBSCRIBER
                        && it.accessibleOffline
                        && it.contentRating?.rating == "14"
                        && it.thumb == "https://s03.video.glbimg.com/x216/8345894.jpg"
                        && it.kind == Kind.EPISODE
                        && it.title?.originProgramId == "12166"
                        && it.title?.titleId == "HcnFN8snnj"
                        && it.title?.type == Type.SERIES
                        && it.title?.headline == "Entre Segredos e Mentiras"
                        && it.title?.abExperiment?.pathUrl == "url"
                        && it.title?.poster == "https://s2.glbimg.com/oMCupkLK3OTSYN-p4Kd-44HUUPg=/100x152/https://s2.glbimg.com/TRx764Jo0JO9r6f5ENc57GwOc9g=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/N/N/sf8t89QCGlAzmUdSYUBA/2020-791-series-spiral-entre-segredos-e-mentiras-poster.jpg"
                        && it.serviceId == 6807
                        && it.subscriptionService != null
                        && it.subscriptionService?.name == "subscriptionName"
                        && it.subscriptionService?.faq != null
                        && it.subscriptionService?.faq?.qrCode != null
                        && it.subscriptionService?.faq?.url != null
                        && it.subscriptionService?.faq?.url?.mobile == "https://ajuda.globo/globoplay/faq"
                        && it.subscriptionService?.faq?.qrCode?.mobile == "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png"
                        && it.subscriptionService?.salesPage != null
                        && it.subscriptionService?.salesPage?.identifier != null
                        && it.subscriptionService?.salesPage?.identifier?.mobile != null
                        && it.subscriptionService?.salesPage?.identifier?.mobile == "urlMobile"
            }

    }

    @Test
    fun testDetailsCalledBuilderVideoQuery() {
        enqueueResponse("video/video_valid.json")

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        videoRepository
            .details(VIDEO_ID, SCALE_X1)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }

        verify {
            videoRepository.builderVideoQuery(VIDEO_ID, SCALE_X1)
        }
    }


    //Next Video
    @Test
    fun testNextVideo() {
        enqueueResponse("video/next_video_valid.json")

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        videoRepository
            .next(VIDEO_ID)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val hasNextVideo = it.first
                val video = it.second

                hasNextVideo
                        && video.id == "8345233"
                        && video.headline == "Episódio 2"
                        && video.duration == 2529093
                        && video.availableFor == AvailableFor.SUBSCRIBER
                        && video.accessibleOffline
                        && video.contentRating?.rating == "14"
                        && video.thumb == "https://s02.video.glbimg.com/x216/8345233.jpg"
                        && video.kind == Kind.EPISODE
                        && video.title?.originProgramId == "12166"
                        && video.title?.titleId == "HcnFN8snnj"
                        && video.title?.abExperiment?.pathUrl == "url"
                        && video.title?.type == Type.SERIES
                        && video.serviceId == 6807
                        && video.subscriptionService != null
                        && video.subscriptionService?.name == "subscriptionName"
                        && video.subscriptionService?.faq != null
                        && video.subscriptionService?.faq?.qrCode != null
                        && video.subscriptionService?.faq?.url != null
                        && video.subscriptionService?.faq?.url?.mobile == "https://ajuda.globo/globoplay/faq"
                        && video.subscriptionService?.faq?.qrCode?.mobile == "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png"
                        && video.subscriptionService?.salesPage != null
                        && video.subscriptionService?.salesPage?.identifier != null
                        && video.subscriptionService?.salesPage?.identifier?.mobile != null
                        && video.subscriptionService?.salesPage?.identifier?.mobile == "urlMobile"
            }
    }

    @Test
    fun testNextVideoWithVideoNull() {
        enqueueResponse("video/next_video_with_video_null.json")

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        videoRepository
            .next(VIDEO_ID)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val hasNextVideo = it.first
                val video = it.second

                !hasNextVideo && video.id == null
            }
    }

    @Test
    fun testNextVideoWithNextVideoNull() {
        enqueueResponse("video/next_video_with_next_video_null.json")

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        videoRepository
            .next(VIDEO_ID)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                val hasNextVideo = it.first
                val video = it.second

                !hasNextVideo && video.id == null
            }
    }

    @Test
    fun testNextVideoCalledBuildNextVideoQuery() {
        enqueueResponse("video/next_video_valid.json")

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        videoRepository
            .next(VIDEO_ID)
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }

        verify {
            videoRepository.buildNextVideoQuery(VIDEO_ID)
        }
    }

    //Builder VideoQuery
    @Test
    fun testBuilderVideoQuery() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val videoQuery = videoRepository
            .builderVideoQuery(VIDEO_ID, SCALE_X1)

        assert(videoQuery.variables().videoId() == VIDEO_ID)

        verify {
            videoRepository.builderVideoImage(any(), SCALE_X1)
        }
    }

    @Test
    fun testBuilderVideoQueryWithVideoIdIsNull() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val videoQuery = videoRepository
            .builderVideoQuery(null, SCALE_X1)

        assert(videoQuery.variables().videoId() == "")

        verify {
            videoRepository.builderVideoImage(any(), SCALE_X1)
        }
    }


    //Build NextVideoQuery
    @Test
    fun testBuildNextVideoQuery() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val nextVideoQuery = videoRepository
            .buildNextVideoQuery(VIDEO_ID)

        assert(nextVideoQuery.variables().videoId() == VIDEO_ID)
    }

    @Test
    fun testBuildNextVideoQueryWithVideoIdNull() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val nextVideoQuery = videoRepository
            .buildNextVideoQuery(null)

        assert(nextVideoQuery.variables().videoId() == "")
    }


    //Builder VideoImage
    @Test
    fun testBuilderVideoImageWhenMobile() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val nextVideoQuery = videoRepository
            .builderVideoImage(VideoQuery.builder().videoId(""), SCALE_X1)
            .build()

        assert(nextVideoQuery.variables().mobilePosterScales().value?.rawValue() == "X1")
    }

    @Test
    fun testBuilderVideoImageWhenTablet() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.TABLET
                )
            )

        val nextVideoQuery = videoRepository
            .builderVideoImage(VideoQuery.builder().videoId(""), SCALE_X1)
            .build()

        assert(nextVideoQuery.variables().tabletPosterScales().value?.rawValue() == "X1")
    }

    //Transform VideoQuery To VideoVO
    @Test
    fun testTransformVideoQueryToVideoVOWhenDeviceMobile() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val videoQuery = videoRepository.transformVideoQueryToVideoVO(builderVideoQuery())

        assert(videoQuery.id == "8345894")
        assert(videoQuery.headline == "Episódio 1")
        assert(videoQuery.description == "Um grupo de mulheres une forças para ajudar umas às outras e enfrentar os vários desafios da vida além de resolver um caso de assassinato.")
        assert(videoQuery.formattedDuration == "40 min")
        assert(videoQuery.duration == 2427826)
        assert(videoQuery.availableFor == AvailableFor.SUBSCRIBER)
        assert(videoQuery.accessibleOffline)
        assert(videoQuery.contentRating?.rating == "14")
        assert(videoQuery.thumb == "https://s03.video.glbimg.com/x216/8345894.jpg")
        assert(videoQuery.kind == Kind.EPISODE)
        assert(videoQuery.title?.originProgramId == "12166")
        assert(videoQuery.title?.titleId == "HcnFN8snnj")
        assert(videoQuery.title?.type == Type.SERIES)
        assert(videoQuery.title?.headline == "Entre Segredos e Mentiras")
        assert(videoQuery.title?.abExperiment?.pathUrl == "url")
        assert(videoQuery.title?.poster == "https://s2.glbimg.com/oMCupkLK3OTSYN-p4Kd-44HUUPg=/100x152/https://s2.glbimg.com/TRx764Jo0JO9r6f5ENc57GwOc9g=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/N/N/sf8t89QCGlAzmUdSYUBA/2020-791-series-spiral-entre-segredos-e-mentiras-poster.jpg")
        assert(videoQuery.serviceId == 6807)
        assert(videoQuery.subscriptionService != null)
        assert(videoQuery.subscriptionService?.faq != null)
        assert(videoQuery.subscriptionService?.faq?.qrCode != null)
        assert(videoQuery.subscriptionService?.faq?.url != null)
        assert(videoQuery.subscriptionService?.faq?.url?.mobile == "https://ajuda.globo/globoplay/faq")
        assert(videoQuery.subscriptionService?.faq?.qrCode?.mobile == "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png")
        assert(videoQuery.subscriptionService?.salesPage != null)
        assert(videoQuery.subscriptionService?.salesPage?.identifier != null)
        assert(videoQuery.subscriptionService?.salesPage?.identifier?.mobile != null)
        assert(videoQuery.subscriptionService?.salesPage?.identifier?.mobile == "urlMobile")
    }

    @Test
    fun testTransformVideoQueryToVideoVOWhenDeviceTablet() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.TABLET
                )
            )

        val videoQuery = videoRepository.transformVideoQueryToVideoVO(builderVideoQuery())

        assert(videoQuery.id == "8345894")
        assert(videoQuery.headline == "Episódio 1")
        assert(videoQuery.description == "Um grupo de mulheres une forças para ajudar umas às outras e enfrentar os vários desafios da vida além de resolver um caso de assassinato.")
        assert(videoQuery.formattedDuration == "40 min")
        assert(videoQuery.duration == 2427826)
        assert(videoQuery.availableFor == AvailableFor.SUBSCRIBER)
        assert(videoQuery.accessibleOffline)
        assert(videoQuery.contentRating?.rating == "14")
        assert(videoQuery.thumb == "https://s03.video.glbimg.com/x216/8345894.jpg")
        assert(videoQuery.kind == Kind.EPISODE)
        assert(videoQuery.title?.originProgramId == "12166")
        assert(videoQuery.title?.titleId == "HcnFN8snnj")
        assert(videoQuery.title?.type == Type.SERIES)
        assert(videoQuery.title?.headline == "Entre Segredos e Mentiras")
        assert(videoQuery.title?.abExperiment?.pathUrl == "url")
        assert(videoQuery.title?.poster == "https://s2.glbimg.com/VZP6BwQHtT-yKzgvV1G4qQcfSf4=/144x212/https://s2.glbimg.com/TRx764Jo0JO9r6f5ENc57GwOc9g=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/N/N/sf8t89QCGlAzmUdSYUBA/2020-791-series-spiral-entre-segredos-e-mentiras-poster.jpg")
        assert(videoQuery.serviceId == 6807)
        assert(videoQuery.subscriptionService != null)
        assert(videoQuery.subscriptionService?.faq != null)
        assert(videoQuery.subscriptionService?.faq?.qrCode != null)
        assert(videoQuery.subscriptionService?.faq?.url != null)
        assert(videoQuery.subscriptionService?.faq?.url?.mobile == "https://ajuda.globo/globoplay/faq")
        assert(videoQuery.subscriptionService?.faq?.qrCode?.mobile == "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png")
        assert(videoQuery.subscriptionService?.salesPage != null)
        assert(videoQuery.subscriptionService?.salesPage?.identifier != null)
        assert(videoQuery.subscriptionService?.salesPage?.identifier?.mobile != null)
        assert(videoQuery.subscriptionService?.salesPage?.identifier?.mobile == "urlMobile")
    }

    //Transform NextVideoQuery To VideoVO
    @Test
    fun testTransformNextVideoQueryToVideoVO() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.MOBILE
                )
            )

        val nextVideoQuery =
            videoRepository.transformNextVideoQueryToVideoVO(builderNextVideoQuery())

        assert(nextVideoQuery.id == "8345894")
        assert(nextVideoQuery.headline == "Episódio 1")
        assert(nextVideoQuery.duration == 2427826)
        assert(nextVideoQuery.availableFor == AvailableFor.SUBSCRIBER)
        assert(nextVideoQuery.accessibleOffline)
        assert(nextVideoQuery.contentRating?.rating == "14")
        assert(nextVideoQuery.thumb == "https://s03.video.glbimg.com/x216/8345894.jpg")
        assert(nextVideoQuery.kind == Kind.EPISODE)
        assert(nextVideoQuery.title?.originProgramId == "12166")
        assert(nextVideoQuery.title?.titleId == "HcnFN8snnj")
        assert(nextVideoQuery.title?.abExperiment?.pathUrl == "url")
        assert(nextVideoQuery.serviceId == 6807)
        assert(nextVideoQuery.subscriptionService != null)
        assert(nextVideoQuery.subscriptionService?.faq != null)
        assert(nextVideoQuery.subscriptionService?.faq?.qrCode != null)
        assert(nextVideoQuery.subscriptionService?.faq?.url != null)
        assert(nextVideoQuery.subscriptionService?.faq?.url?.mobile == "https://ajuda.globo/globoplay/faq")
        assert(nextVideoQuery.subscriptionService?.faq?.qrCode?.mobile == "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png")
        assert(nextVideoQuery.subscriptionService?.salesPage != null)
        assert(nextVideoQuery.subscriptionService?.salesPage?.identifier != null)
        assert(nextVideoQuery.subscriptionService?.salesPage?.identifier?.mobile != null)
        assert(nextVideoQuery.subscriptionService?.salesPage?.identifier?.mobile == "urlMobile")
    }

    //Transform NextVideoQuery To VideoVO When Device Is A Tablet
    @Test
    fun testTransformNextVideoQueryToVideoVOWhenDeviceTablet() {

        val videoRepository =
            spyk(
                VideoRepository(
                    apolloClient,
                    Device.TABLET
                )
            )

        val nextVideoQuery =
            videoRepository.transformNextVideoQueryToVideoVO(builderNextVideoQuery())

        assert(nextVideoQuery.id == "8345894")
        assert(nextVideoQuery.headline == "Episódio 1")
        assert(nextVideoQuery.duration == 2427826)
        assert(nextVideoQuery.availableFor == AvailableFor.SUBSCRIBER)
        assert(nextVideoQuery.accessibleOffline)
        assert(nextVideoQuery.contentRating?.rating == "14")
        assert(nextVideoQuery.thumb == "https://s03.video.glbimg.com/x216/8345894.jpg")
        assert(nextVideoQuery.kind == Kind.EPISODE)
        assert(nextVideoQuery.title?.originProgramId == "12166")
        assert(nextVideoQuery.title?.titleId == "HcnFN8snnj")
        assert(nextVideoQuery.title?.abExperiment?.pathUrl == "url")
        assert(nextVideoQuery.serviceId == 6807)
        assert(nextVideoQuery.subscriptionService != null)
        assert(nextVideoQuery.subscriptionService?.faq != null)
        assert(nextVideoQuery.subscriptionService?.faq?.qrCode != null)
        assert(nextVideoQuery.subscriptionService?.faq?.url != null)
        assert(nextVideoQuery.subscriptionService?.faq?.url?.mobile == "https://ajuda.globo/globoplay/faq")
        assert(nextVideoQuery.subscriptionService?.faq?.qrCode?.mobile == "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png")
    }

    private fun builderVideoQuery() = VideoQuery.Video(
        "Video",
        "8345894",
        "Episódio 1",
        "Um grupo de mulheres une forças para ajudar umas às outras e enfrentar os vários desafios da vida além de resolver um caso de assassinato.",
        "40 min",
        2427826,
        2007826,
        SubscriptionType.SUBSCRIBER,
        true,
        "14",
        listOf(),
        "https://s03.video.glbimg.com/x216/8345894.jpg",
        "https://s03.video.glbimg.com/x216/8345894.jpg",
        KindType.EPISODE,
        6807,
        VideoQuery.Title(
            "Title",
            "12166",
            "HcnFN8snnj",
            "url",
            TitleType.SERIE,
            listOf("Comédia"),
            "Entre Segredos e Mentiras",
            TitleFormat.SERIES,
            VideoQuery.Poster(
                "",
                "https://s2.glbimg.com/oMCupkLK3OTSYN-p4Kd-44HUUPg=/100x152/https://s2.glbimg.com/TRx764Jo0JO9r6f5ENc57GwOc9g=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/N/N/sf8t89QCGlAzmUdSYUBA/2020-791-series-spiral-entre-segredos-e-mentiras-poster.jpg",
                "https://s2.glbimg.com/VZP6BwQHtT-yKzgvV1G4qQcfSf4=/144x212/https://s2.glbimg.com/TRx764Jo0JO9r6f5ENc57GwOc9g=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/N/N/sf8t89QCGlAzmUdSYUBA/2020-791-series-spiral-entre-segredos-e-mentiras-poster.jpg"
            )
        ),
        VideoQuery.SubscriptionService(
            "SubscriptionService",
            "subscriptionName",
            VideoQuery.SalesPage(
                "SubscriptionServiceSalesPage",
                VideoQuery.Identifier(
                    "SubscriptionServiceSalesPageIdentifier",
                    "urlMobile"
                )
            ),
            VideoQuery.Faq(
                "SubscriptionServiceFAQ",
                VideoQuery.QrCode(
                    "SubscriptionServiceFAQQRCodeURLs",
                    "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png"
                ),
                VideoQuery.Url(
                    "SubscriptionServiceFAQPageURLs",
                    "https://ajuda.globo/globoplay/faq"
                )
            )
        )
    )

    private fun builderNextVideoQuery() = NextVideoQuery.Video(
        "Video",
        NextVideoQuery.NextVideo(
            "Video",
            "8345894",
            "Episódio 1",
            "Um grupo de mulheres une forças para ajudar umas às outras e enfrentar os vários desafios da vida além de resolver um caso de assassinato.",
            "",
            2427826,
            2027826,
            SubscriptionType.SUBSCRIBER,
            true,
            "14",
            "https://s03.video.glbimg.com/x216/8345894.jpg",
            "https://s03.video.glbimg.com/x216/8345894.jpg",
            KindType.EPISODE,
            6807,
            NextVideoQuery.Title(
                "Title",
                "12166",
                "HcnFN8snnj",
                TitleFormat.SERIES,
                TitleType.SERIE,
                "url",
                listOf("Comédia"),
                "Entre Segredos e Mentiras",
                NextVideoQuery.Poster(
                    "",
                    "https://s2.glbimg.com/oMCupkLK3OTSYN-p4Kd-44HUUPg=/100x152/https://s2.glbimg.com/TRx764Jo0JO9r6f5ENc57GwOc9g=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/N/N/sf8t89QCGlAzmUdSYUBA/2020-791-series-spiral-entre-segredos-e-mentiras-poster.jpg",
                    "https://s2.glbimg.com/VZP6BwQHtT-yKzgvV1G4qQcfSf4=/144x212/https://s2.glbimg.com/TRx764Jo0JO9r6f5ENc57GwOc9g=/i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/N/N/sf8t89QCGlAzmUdSYUBA/2020-791-series-spiral-entre-segredos-e-mentiras-poster.jpg"
                )
            ),
            NextVideoQuery.SubscriptionService(
                "SubscriptionService",
                "subscriptionName",
                NextVideoQuery.SalesPage(
                    "SubscriptionServiceSalesPage",
                    NextVideoQuery.Identifier(
                        "SubscriptionServiceSalesPageIdentifier",
                        "urlMobile"
                    )
                ),
                NextVideoQuery.Faq(
                    "SubscriptionServiceFAQ",
                    NextVideoQuery.QrCode(
                        "SubscriptionServiceFAQQRCodeURLs",
                        "https://s3.glbimg.com/v1/AUTH_2caf29d99e86401197555831070efae8/secure/qrCode-premiere-mobile.png"
                    ),
                    NextVideoQuery.Url(
                        "SubscriptionServiceFAQPageURLs",
                        "https://ajuda.globo/globoplay/faq"
                    )
                )
            )
        )
    )
}