package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.fragment.SeasonedScenesStructure
import com.globo.jarvis.title.scenes.ExcerptsQuery
import com.globo.jarvis.type.KindType
import io.mockk.spyk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ScenesRepositoryTest  : BaseUnitTest() {

    private lateinit var scenesRepositorySpyk: ScenesRepository

    @Before
    fun setUp() {
        scenesRepositorySpyk =
            spyk(
                ScenesRepository(
                    apolloClient,
                    SeasonRepository(apolloClient, Device.TABLET)
                )
            )
    }

    @Test
    fun testAll() {
        val titleId = "titleId"
        val headline = "headline"
        val number = 1
        val page = 1
        val perPage = 1
        val thumbSmall = 1
        val thumbLarge = 1

        enqueueResponse("scenes/scenesStructure.json")

        scenesRepositorySpyk.withThumbs(titleId, number, headline, page, perPage, thumbSmall, thumbLarge)
            .test()
            .assertComplete()
            .assertValue { scene ->
                scene.title == headline &&
                    scene.thumbList?.get(0)?.headline == "Trailer - Marielle: O Documentário - Série Original Globoplay" &&
                    scene.thumbList?.get(0)?.thumbLarge == "https://s03.video.glbimg.com/x216/8396498.jpg"
            }
    }

    @Test
    fun testWithoutDetails() {
        val titleId = "titleId"
        val seasonId = "seasonId"
        val page = 1
        val perPage = 1
        val thumbSmall = 1
        val thumbLarge = 1

        enqueueResponse("scenes/scenesVideo.json")
        enqueueResponse("scenes/scenesStructure.json")

        scenesRepositorySpyk.withoutDetails(titleId, seasonId, page, perPage, thumbSmall, thumbLarge)
            .test()
            .assertComplete()
            .assertValue { triple ->
                triple.third[0].id == "8382555" &&
                        triple.third[0].headline == "Maré 27 de julho de 1979 - Lapa 14 de março de 2018" &&
                        triple.third[0].thumb == "https://s04.video.glbimg.com/x216/8382555.jpg"
            }
    }


    @Test
    fun testStructure() {
        val titleId = "titleId"
        val seasonId = "seasonId"
        val page = 1
        val perPage = 1

        enqueueResponse("scenes/scenesVideo.json")

        scenesRepositorySpyk.structure(titleId, seasonId, page, perPage)
            .test()
            .assertComplete()
            .assertValue { triple ->
                triple.third[0].id == "8382555" &&
                        triple.third[0].headline == "Maré 27 de julho de 1979 - Lapa 14 de março de 2018" &&
                        triple.third[0].thumb == "https://s04.video.glbimg.com/x216/8382555.jpg"

            }
    }

    @Test
    fun testWithoutSeasonDetails() {
        val titleId = "titleId"
        val page = 1
        val perPage = 1

        enqueueResponse("scenes/seasonScenes.json")
        enqueueResponse("scenes/scenesVideo.json")

        scenesRepositorySpyk.withoutSeasonDetails(titleId, page, perPage)
            .test()
            .assertComplete()
            .assertValue { pair ->
                pair.first.first?.id == "8gDWZfR6MR" &&
                        pair.second.third[0].id == "8382555" &&
                        pair.second.third[0].headline == "Maré 27 de julho de 1979 - Lapa 14 de março de 2018" &&
                        pair.second.third[0].thumb == "https://s04.video.glbimg.com/x216/8382555.jpg"
            }
    }

    @Test
    fun testWithSeason() {
        val titleId = "titleId"
        val page = 1
        val perPage = 1
        val thumbSmall = 1
        val thumbLarge = 1

        enqueueResponse("scenes/seasonScenes.json")
        enqueueResponse("scenes/scenesVideo.json")
        enqueueResponse("scenes/scenesStructure.json")

        scenesRepositorySpyk.withSeason(titleId, page, perPage, thumbSmall, thumbLarge)
            .test()
            .assertComplete()
            .assertValue { pair ->
                pair.first[0].id == "8gDWZfR6MR" &&
                        pair.second.third[0].id == "8382555" &&
                        pair.second.third[0].title == "Maré 27 de julho de 1979 - Lapa 14 de março de 2018" &&
                        pair.second.third[0].thumbList?.get(0)?.headline == "Trailer - Marielle: O Documentário - Série Original Globoplay"
                        pair.second.third[0].thumbList?.get(0)?.thumb == "https://s03.video.glbimg.com/x216/8396498.jpg"
            }
    }

    @Test
    fun testConvertResourceResponseListToThumbList() {

        val typeName = "typename"
        val videoId = "video_id"
        val headline = "headline"
        val formattedDuration = "formatted_duration"
        val thumbSmall = "thumb_small"
        val thumbLarge = "thumb_large"
        val kind = KindType.`$UNKNOWN`
        val title = ExcerptsQuery.Title(typeName, headline)

        val resource1 = ExcerptsQuery.Resource(typeName, videoId, headline, title, formattedDuration, thumbSmall, thumbLarge, kind)
        val listThumb = scenesRepositorySpyk.convertResourceResponseListToThumbList(listOf(resource1))

        Assert.assertEquals(listThumb[0].id, videoId)
        Assert.assertEquals(listThumb[0].title?.headline, headline)
        Assert.assertEquals(listThumb[0].formattedDuration, formattedDuration)
        Assert.assertEquals(listThumb[0].thumb, thumbSmall)
        Assert.assertEquals(listThumb[0].thumbLarge, thumbLarge)
    }

    @Test
    fun testConvertResourceResponseListToScenesPreviewList() {

        val typeName = "typename"
        val number = 1
        val total = 1
        val videoId = "video_id"
        val headline = "headline"
        val thumb = "thumb"

        val excerpts = SeasonedScenesStructure.Excerpts(typeName, total)
        val video = SeasonedScenesStructure.Video(typeName, videoId, headline, thumb)
        val resource1 = SeasonedScenesStructure.Resource(typeName, number, excerpts, video)

        val listScenesPreview = scenesRepositorySpyk.convertResourceResponseListToScenesPreviewList(listOf(resource1))

        Assert.assertEquals(listScenesPreview[0].id, videoId)
        Assert.assertEquals(listScenesPreview[0].headline, headline)
        Assert.assertEquals(listScenesPreview[0].thumb, thumb)
    }
    
    @Test
    fun testBuilderGetScenesStructureQuery() {
        val titleId = "title_id"
        val seasonId = "season_id"
        val page = 1
        val perPage = 1

        val sceneQuery = scenesRepositorySpyk.builderGetScenesStructureQuery(titleId, seasonId, page, perPage)

        Assert.assertEquals(sceneQuery.variables().titleId(), titleId)
        Assert.assertEquals(sceneQuery.variables().seasonId(), seasonId)
        Assert.assertEquals(sceneQuery.variables().page(), page)
        Assert.assertEquals(sceneQuery.variables().perPage(), perPage)
    }

    @Test
    fun testBuilderGetScenesVideoQuery() {
        val videoId = "title_id"
        val page = 1
        val perPage = 1
        val thumbSmall = 1
        val thumbLarge = 1

        val sceneQuery = scenesRepositorySpyk.builderGetScenesVideoQuery(videoId, page, perPage, thumbSmall, thumbLarge)

        Assert.assertEquals(sceneQuery.variables().videoId(), videoId)
        Assert.assertEquals(sceneQuery.variables().page(), page)
        Assert.assertEquals(sceneQuery.variables().perPage(), perPage)
        Assert.assertEquals(sceneQuery.variables().thumbSmall(), thumbSmall)
        Assert.assertEquals(sceneQuery.variables().thumbLarge(), thumbLarge)
    }

}