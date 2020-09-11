package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.common.JARVIS_PATTERN_YYYY_MM_DD
import com.globo.jarvis.common.formatByPattern
import com.globo.jarvis.model.AvailableFor
import io.mockk.spyk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class ChapterRepositoryTest  : BaseUnitTest(){

    private lateinit var chapterRepositorySpyk: ChapterRepository

    @Before
    fun setUp() {
        chapterRepositorySpyk =
            spyk(
                ChapterRepository(
                    apolloClient
                )
            )
    }

    @Test
    fun testChapterByDateRange() {
        val titleId = "titleId"
        val startDate = Date(0)
        val endDate = Date(0)
        val page = 1
        val perPage = 1
        enqueueResponse("chapter/chaptersByDateRange.json")

        chapterRepositorySpyk.byDateRange(
            titleId, startDate, endDate, page, perPage
        ).test()
            .assertComplete()
            .assertValue { triple ->
                triple.second &&
                        triple.third == 2 &&
                        triple.first[0].id == "8811343" &&
                        triple.first[0].headline == "Capítulo de 27/08/2020" &&
                        triple.first[0].description == "Quinzé revela o segredo de Fabrícia para Griselda e Gigante. Solange é convidada para participar de programas de TV. Guaracy conta para Esther que passou a noite com Griselda. Paulo impede o assédio da imprensa a Esther e Vitória. Ellen, Vanessa e Patrícia seguem o plano de Antenor e forjam um vídeo sobre Alexandre. Passam-se duas semanas." &&
                        triple.first[0].thumb == "https://s04.video.glbimg.com/x216/8811343.jpg" &&
                        triple.first[0].duration == 2777961 &&
                        triple.first[0].formattedDuration == "46 min" &&
                        triple.first[0].availableFor == AvailableFor.SUBSCRIBER &&
                        triple.first[0].accessibleOffline &&
                        triple.first[0].exhibitedAt == "2020-08-27T03:00:00Z" &&
                        triple.first[0].serviceId == 6004
            }
    }

    @Test
    fun testChapters() {
        val titleId = "titleId"
        val page = 1
        val perPage = 1
        enqueueResponse("chapter/chapters.json")

        chapterRepositorySpyk.all(
            titleId, page, perPage
        ).test()
            .assertComplete()
            .assertValue { triple ->
                triple.second &&
                        triple.third == 2 &&
                        triple.first[0].id == "8817031" &&
                        triple.first[0].headline == "Capítulo de 29/08/2020" &&
                        triple.first[0].description == "Pereirinha tenta entrar na festa de casamento de Amália e Rafael. Griselda dança valsa com Guaracy. Albertinho e Jackelaine se beijam. Teodora garante a Mônica que Quinzé não participará da vida de seu filho. Wallace convida Dagmar para jantar no Brasileiríssimo. Zuleika pede demissão da Fashion Moto. Patrícia confronta Tereza Cristina. Paulo repreende Danielle quando ela tenta falar com Esther." &&
                        triple.first[0].thumb == "https://s04.video.glbimg.com/x216/8817031.jpg" &&
                        triple.first[0].duration == 3047480 &&
                        triple.first[0].formattedDuration == "50 min" &&
                        triple.first[0].availableFor == AvailableFor.SUBSCRIBER &&
                        triple.first[0].accessibleOffline &&
                        triple.first[0].exhibitedAt == "2020-08-29T03:00:00Z" &&
                        triple.first[0].serviceId == 6004
            }
    }

    @Test
    fun testBuilderTitleEpisodesByDateRangeQuery() {
        val titleId = "titleId"
        val startDate = Date(1583031600000)
        val endDate = Date(1583031600000)
        val page = 1
        val perPage = 1

        val chaptersByDateRangeQuery = chapterRepositorySpyk.builderTitleEpisodesByDateRangeQuery(
            titleId = titleId,
            startDate = startDate,
            endDate = endDate,
            page = page,
            perPage = perPage
        )

        Assert.assertEquals(chaptersByDateRangeQuery.variables().titleId(), titleId)
        Assert.assertEquals(chaptersByDateRangeQuery.variables().startDate(), startDate.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD))
        Assert.assertEquals(chaptersByDateRangeQuery.variables().endDate(), endDate.formatByPattern(JARVIS_PATTERN_YYYY_MM_DD))
        Assert.assertEquals(chaptersByDateRangeQuery.variables().page(), page)
        Assert.assertEquals(chaptersByDateRangeQuery.variables().perPage(), perPage)
    }

}