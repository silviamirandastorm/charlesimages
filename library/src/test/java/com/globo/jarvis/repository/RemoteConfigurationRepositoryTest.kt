package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.fragment.*
import com.globo.jarvis.remoteconfig.RemoteConfigQuery
import com.globo.jarvis.type.RemoteConfigGroups
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoteConfigurationRepositoryTest : BaseUnitTest() {
    private lateinit var repository: RemoteConfigRepository

    @Before
    fun setup() {
        repository = spyk(RemoteConfigRepository(apolloClient))
    }

    // All
    @Test
    fun testAllWithJsonValid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================

        enqueueAsyncResponses(
            Triple(
                "test",
                "remoteconfig/remoteconfig_test.json",
                200
            ),
            Triple(
                "sales",
                "remoteconfig/remoteconfig_sales.json",
                200
            )
        )

        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable = repository.all("ANDROID_MOBILE", "test", "sales")

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { repository.detailsRemoteConfig("ANDROID_MOBILE", "test") }
        verify { repository.detailsRemoteConfig("ANDROID_MOBILE", "sales") }

        observable
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.isNotEmpty()
            }
    }

    @Test
    fun testAllWithJsonInvalid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================
        enqueueResponse("remoteconfig/remoteconfig_invalid.json")

        // =========================================================================================
        // Action.
        // =========================================================================================
        val observable = repository.all("ANDROID_MOBILE", "test")

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { repository.detailsRemoteConfig("ANDROID_MOBILE", "test") }

        observable
            .test()
            .assertError { true }
    }

    //Details Remote Config
    @Test
    fun testDetailsRemoteConfig() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        repository.detailsRemoteConfig("ANDROID_MOBILE", "test")

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        verify { repository.buildRemoteConfigQuery("ANDROID_MOBILE", "test") }
    }


    //Build Remote ConfigQuery
    @Test
    fun testBuildRemoteConfigQuery() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val remoteConfigQuery = repository.buildRemoteConfigQuery("ANDROID_MOBILE", "test")

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(remoteConfigQuery.variables().group().rawValue() == RemoteConfigGroups.ANDROID_MOBILE.rawValue())
        assertTrue(remoteConfigQuery.variables().scope().value == "test")
    }


    //Transform Configs To Configuration
    @Test
    fun testTransformConfigsToConfigurationWithListValid() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val configurationList =
            repository.transformConfigsToConfiguration(builderRemoteConfigQueryConfig())

        // =========================================================================================
        // Asserts.
        // =========================================================================================

        assertTrue(configurationList.getOrNull(0)?.first == "TEXT_TEST")
        assertTrue(configurationList.getOrNull(0)?.second?.valueString == "Text!")

        assertTrue(configurationList.getOrNull(1)?.first == "BOOL_TEST")
        assertTrue(configurationList.getOrNull(1)?.second?.valueBoolean == true)

        assertTrue(configurationList.getOrNull(2)?.first == "NUMBER_TEST")
        assertTrue(configurationList.getOrNull(2)?.second?.valueNumber == 123.0)

        assertTrue(configurationList.getOrNull(3)?.first == "DATE_TEST")
        assertTrue(configurationList.getOrNull(3)?.second?.valueString == "2020-07-08")

        assertTrue(configurationList.getOrNull(4)?.first == "DATE_HOUR_TEST")
        assertTrue(configurationList.getOrNull(4)?.second?.valueString == "2020-07-12T13:56:00.000Z")

        assertTrue(configurationList.getOrNull(5)?.first == "JSON_TEST")
        assertTrue(configurationList.getOrNull(5)?.second?.valueString == "{\"key_json\": \"value\"}")

        assertTrue(configurationList.getOrNull(6)?.first == "COLOR_TEST")
        assertTrue(configurationList.getOrNull(6)?.second?.valueString == "#fff")

        assertTrue(configurationList.getOrNull(7)?.first == "IMAGE_TEST")
        assertTrue(configurationList.getOrNull(7)?.second?.valueString == "https://s2.glbimg.com/Cc-4wCDMB9rNqK14buahh1OYSs0=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/2/T/AaowLHTUu0Oqe7YUonTA/2020-791-spiral-einstein-destaque-foco-direita.jpg")
    }

    @Test
    fun testTransformConfigsToConfigurationWithListIsNull() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val configurationList =
            repository.transformConfigsToConfiguration(null)

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(configurationList.isEmpty())
        assertTrue(configurationList.count { it.first.isEmpty() } == 0)
    }

    @Test
    fun testTransformConfigsToConfigurationWithListIsEmpty() {
        // =========================================================================================
        // Action.
        // =========================================================================================
        val configurationList =
            repository.transformConfigsToConfiguration(emptyList())

        // =========================================================================================
        // Asserts.
        // =========================================================================================
        assertTrue(configurationList.isEmpty())
        assertTrue(configurationList.count { it.first.isEmpty() } == 0)
    }

    //Transform RemoteConfigQueryResource To Configuration
    @Test
    fun testTransformRemoteConfigQueryResourceToConfigurationValid() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================

        val remoteConfigQueryResource = RemoteConfigQuery.Resource(
            "typename",
            RemoteConfigQuery.Resource.Fragments(
                StringConfiguration(
                    "typename",
                    "TEXT_TEST",
                    "Text!"
                ), null, null, null, null, null, null, null
            )
        )

        // =========================================================================================
        // Action.
        // =========================================================================================
        val configuration =
            repository.transformRemoteConfigQueryResourceToConfiguration(remoteConfigQueryResource)

        // =========================================================================================
        // Asserts.
        // =========================================================================================

        assertTrue(configuration.first == "TEXT_TEST")
        assertTrue(configuration.second?.valueString == "Text!")
    }

    @Test
    fun testTransformRemoteConfigQueryResourceToConfigurationKeyIsNull() {
        // =========================================================================================
        // Prepare.
        // =========================================================================================

        val remoteConfigQueryResource = RemoteConfigQuery.Resource(
            "typename",
            RemoteConfigQuery.Resource.Fragments(
                StringConfiguration(
                    "typename",
                    "TEXT_TEST",
                    "Text!"
                ), null, null, null, null, null, null, null
            )
        )

        // =========================================================================================
        // Action.
        // =========================================================================================
        val configuration =
            repository.transformRemoteConfigQueryResourceToConfiguration(remoteConfigQueryResource)

        // =========================================================================================
        // Asserts.
        // =========================================================================================

        assertTrue(configuration.first == "TEXT_TEST")
        assertTrue(configuration.second?.valueString == "Text!")
    }


    private fun builderRemoteConfigQueryConfig() =
        listOf(
            RemoteConfigQuery.Resource(
                "typename",
                RemoteConfigQuery.Resource.Fragments(
                    StringConfiguration(
                        "typename",
                        "TEXT_TEST",
                        "Text!"
                    ), null, null, null, null, null, null, null
                )
            ),
            RemoteConfigQuery.Resource(
                "typename",
                RemoteConfigQuery.Resource.Fragments(
                    null,
                    BooleanConfiguration("typename", "BOOL_TEST", true),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            ),
            RemoteConfigQuery.Resource(
                "typename",
                RemoteConfigQuery.Resource.Fragments(
                    null,
                    null,
                    NumberConfiguration("typename", "NUMBER_TEST", 123.0),
                    null,
                    null,
                    null,
                    null,
                    null
                )
            ),
            RemoteConfigQuery.Resource(
                "typename",
                RemoteConfigQuery.Resource.Fragments(
                    null,
                    null,
                    null,
                    DateConfiguration("typename", "DATE_TEST", "2020-07-08"),
                    null,
                    null,
                    null,
                    null
                )
            ),
            RemoteConfigQuery.Resource(
                "typename", RemoteConfigQuery.Resource.Fragments(
                    null,
                    null,
                    null,
                    null,
                    DateTimeConfiguration(
                        "typename",
                        "DATE_HOUR_TEST",
                        "2020-07-12T13:56:00.000Z"
                    ), null, null, null
                )
            ),
            RemoteConfigQuery.Resource(
                "typename",
                RemoteConfigQuery.Resource.Fragments(
                    null,
                    null,
                    null,
                    null,
                    null,
                    JsonConfiguration(
                        "typename",
                        "JSON_TEST",
                        "{\"key_json\": \"value\"}"
                    ), null, null
                )
            ),
            RemoteConfigQuery.Resource(
                "typename",
                RemoteConfigQuery.Resource.Fragments(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ColorConfiguration(
                        "typename",
                        "COLOR_TEST",
                        "#fff"
                    ), null
                )
            ),
            RemoteConfigQuery.Resource(
                "typename", RemoteConfigQuery.Resource.Fragments(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ImageConfiguration(
                        "typename",
                        "IMAGE_TEST",
                        "https://s2.glbimg.com/Cc-4wCDMB9rNqK14buahh1OYSs0=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/2/T/AaowLHTUu0Oqe7YUonTA/2020-791-spiral-einstein-destaque-foco-direita.jpg"
                    )
                )
            ),
            RemoteConfigQuery.Resource(
                "typename", RemoteConfigQuery.Resource.Fragments(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ImageConfiguration(
                        "typename",
                        "",
                        "https://s2.glbimg.com/Cc-4wCDMB9rNqK14buahh1OYSs0=/https://i.s3.glbimg.com/v1/AUTH_c3c606ff68e7478091d1ca496f9c5625/internal_photos/bs/2020/2/T/AaowLHTUu0Oqe7YUonTA/2020-791-spiral-einstein-destaque-foco-direita.jpg"
                    )
                )
            )
        )

}