package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.Device
import com.globo.jarvis.common.formatByPattern
import com.globo.jarvis.fragment.EpisodesList
import com.globo.jarvis.fragment.EpisodesWithRelatedExcerptsList
import com.globo.jarvis.title.episode.EpisodeQuery
import com.globo.jarvis.type.KindType
import com.globo.jarvis.type.MobilePosterScales
import com.globo.jarvis.type.SubscriptionType
import com.globo.jarvis.type.TitleType
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class EpisodeRepositoryTest  : BaseUnitTest(){

    private lateinit var episodeRepositorySpyk: EpisodeRepository

    @Before
    fun setUp() {
        episodeRepositorySpyk =
            spyk(
                EpisodeRepository(
                    apolloClient,
                    mockk(relaxed = true),
                    Device.TABLET
                )
            )
    }

    @Test
    fun testDetail() {

        val videoId = "videoId"
        val scale = MobilePosterScales.X1.rawValue()

        enqueueResponse("episode/detail.json")

        episodeRepositorySpyk.detail(videoId, scale)
            .test()
            .assertValue {
                it.video?.id == "7634176" &&
                        it.video?.headline == "Laços de Família" &&
                        it.video?.title?.titleId == "1b7jV1jcLh" &&
                        it.video?.title?.headline == "The Vampire Diaries"
            }
    }

    @Test
    fun testDetails() {

        val videoId = "videoId"
        val seasonId = "seasonId"

        enqueueResponse("episode/details.json")

        episodeRepositorySpyk.details(videoId, seasonId, 1, 1)
            .test()
            .assertValue {
                it.episodeList?.get(0)?.id == "7634156" &&
                        it.episodeList?.get(0)?.headline == "Piloto" &&
                        it.episodeList?.get(0)?.description == "Lidando com a perda dos pais em um acidente, Elena se atrai pelo misterioso Stefan, um vampiro que vai disputar a alma da jovem com seu irmão mais velho, Damon." &&
                        it.episodeList?.get(0)?.formattedDuration == "42 min" &&
                        it.episodeList?.get(0)?.thumb == "https://s01.video.glbimg.com/x216/7634156.jpg"
            }
    }

    @Test
    fun testBuilderDownloadQuery() {
        val videoId = "video_id"
        val scale = MobilePosterScales.X1.rawValue()

        val query = episodeRepositorySpyk.builderDownloadQuery(videoId, scale)
        Assert.assertEquals(query.variables().videoId(), videoId)
        Assert.assertEquals(query.variables().tabletPosterScales().value?.rawValue(), scale)
    }

    @Test
    fun testBuilderEpisodeAndRelatedExcerptsByDateQuery() {
        val titleId = "title_id"
        val startDate = Date()
        val endDate = Date()
        val datePattern = "yyyy-MM-dd"

        val query = episodeRepositorySpyk.builderEpisodeAndRelatedExcerptsByDateQuery(titleId, startDate, endDate)
        Assert.assertEquals(query.variables().titleId(), titleId)
        Assert.assertEquals(query.variables().startDate(), startDate.formatByPattern(datePattern))
        Assert.assertEquals(query.variables().endDate(), endDate.formatByPattern(datePattern))
    }

    @Test
    fun testBuilderGetEpisodesQuery() {
        val titleId = "title_id"
        val seasonId = "season_id"
        val page = 1
        val perPage = 1

        val query = episodeRepositorySpyk.builderGetEpisodesQuery(titleId, seasonId, page, perPage)
        Assert.assertEquals(query.variables().titleId(), titleId)
        Assert.assertEquals(query.variables().seasonId(), seasonId)
        Assert.assertEquals(query.variables().page(), page)
        Assert.assertEquals(query.variables().perPage(), perPage)
    }

    @Test
    fun testDetailsWithSeasonDetails() {

        val videoId = "videoId"
        val seasonId = "seasonId"
        val page = 1
        val perPage = 1

        enqueueResponse("episode/detailsWithSeasonDetails.json")

        episodeRepositorySpyk.details(videoId, seasonId, page, perPage)
            .test()
            .assertValue {
                it.episodeList?.get(0)?.id == "7634156"
                it.episodeList?.get(0)?.headline == "Piloto"
            }
    }

    @Test
    fun testDetailsWithRelatedExcerptsByDate() {

        val titleId = "titleId"
        val startDate = Calendar.getInstance().time
        val endDate = Calendar.getInstance().time

        enqueueResponse("episode/detailsWithRelatedExcerptsByDate.json")

        episodeRepositorySpyk.detailsWithRelatedExcerptsByDate(titleId, startDate, endDate)
            .test()
            .assertValue { pair ->
                pair.first.id == "8060445" &&
                        pair.first.headline == "Capítulo de 04/11/2019" &&
                        pair.second[0].id == "8060407" &&
                        pair.second[0].headline == "Yohana e Camilo prendem Jô"
            }
    }

    @Test
    fun testTransformExcerptsToThumb() {

        val typeName = "__typename"
        val id = "id"
        val headline = "headline"
        val duration = 5000
        val formattedDuration = "formattedDuration"
        val kindType = KindType.`$UNKNOWN`
        val subscriptionType = SubscriptionType.SUBSCRIBER
        val thumb = "720"

        val title = EpisodesWithRelatedExcerptsList.Title1 (
            typeName,
            id,
            headline
        )

        val resource = EpisodesWithRelatedExcerptsList.Resource1 (
            typeName,
             headline,
             duration,
             formattedDuration,
             kindType,
             id,
             thumb,
             subscriptionType,
             title
        )

        val resourceList = listOf(resource)

        val listOfThumb = episodeRepositorySpyk.transformExcerptsToThumb(resourceList)

        listOfThumb[0].let {
            Assert.assertEquals(it.id, id)
            Assert.assertEquals(it.headline, headline)
            Assert.assertEquals(it.formattedDuration, formattedDuration)
            Assert.assertEquals(it.thumb, thumb)
        }
    }

    @Test
    fun testTransformEpisodesListResourceToEpisodeList() {

        val typeName = "__typename"
        val seasonNumber = 1
        val number = 1

        val id = "id"
        val headline = "headline"
        val description = "description"
        val thumb = "thumb"
        val liveThumbnail = "liveThumbnail"
        val kind = KindType.`$UNKNOWN`
        val contentRating = "contentRating"
        val duration = 120
        val formattedDuration = "formattedDuration"
        val availableFor = SubscriptionType.ANONYMOUS
        val accessibleOffline = true
        val serviceId = 1

        val video = EpisodesList.Video(
            typeName,
            id,
            headline,
            description,
            thumb,
            liveThumbnail,
            kind,
            contentRating,
            duration,
            formattedDuration,
            availableFor,
            accessibleOffline,
            serviceId
        )

        val resource = EpisodesList.Resource(
            typeName,
            seasonNumber,
            number,
            video
        )

        val resouceList = listOf(resource)

        val listOfEpisode = episodeRepositorySpyk.transformEpisodesListResourceToEpisodeList(resouceList)

        listOfEpisode[0].let {
            Assert.assertEquals(it.id, id)
            Assert.assertEquals(it.headline, headline)
            Assert.assertEquals(it.formattedDuration, formattedDuration)
            Assert.assertEquals(it.thumb, thumb)
            Assert.assertEquals(it.seasonNumber, seasonNumber)
        }
    }

    @Test
    fun testTransformEpisodeQueryToEpisode() {

        val typeName = "__typename"
        val seasonNumber = 1
        val number = 1

        val id = "id"
        val headline = "headline"
        val description = "description"
        val thumb = "thumb"
        val liveThumbnail = "liveThumbnail"
        val kind = KindType.`$UNKNOWN`
        val contentRating = "contentRating"
        val duration = 120
        val fullyWatchedThreshold = 100
        val formattedDuration = "formattedDuration"
        val availableFor = SubscriptionType.ANONYMOUS
        val accessibleOffline = true

        val contentRatingCriteria = listOf("criteria1", "criteria2")
        val titleId = "titleId"
        val originProgramId = "originProgramId"
        val type = TitleType.`$UNKNOWN`
        val encrypted = false
        val mobile = "mobile"
        val tablet = "tablet"

        val episodeQuery = EpisodeQuery.Episode(
            typeName,
            number,
            seasonNumber,
            EpisodeQuery.Video(
                typeName,
                id,
                headline,
                description,
                formattedDuration,
                duration,
                fullyWatchedThreshold,
                accessibleOffline,
                contentRating,
                contentRatingCriteria,
                availableFor,
                thumb,
                liveThumbnail,
                kind,
                EpisodeQuery.Title(
                    typeName,
                    titleId,
                    originProgramId,
                    headline,
                    type,
                    encrypted,
                    EpisodeQuery.Poster(
                        typeName,
                        mobile,
                        tablet
                    )
                )
            )
        )

        val episode = episodeRepositorySpyk.transformEpisodeQueryToEpisode(episodeQuery)

        episode.let {
            Assert.assertEquals(it.seasonNumber, seasonNumber)
            Assert.assertEquals(it.video?.headline, headline)
            Assert.assertEquals(it.video?.title?.titleId, titleId)
            Assert.assertEquals(it.video?.title?.poster, tablet)
        }
    }
}