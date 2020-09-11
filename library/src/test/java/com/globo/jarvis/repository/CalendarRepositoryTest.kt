package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import io.mockk.spyk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class CalendarRepositoryTest  : BaseUnitTest(){

    private lateinit var calendarRepositorySpyk: CalendarRepository

    @Before
    fun setUp() {
        calendarRepositorySpyk =
            spyk(
                CalendarRepository(
                    apolloClient
                )
            )
    }

    @Test
    fun testAll() {
        val titleId = "titleId"

        enqueueResponse("calendar/datesWithContent.json")

        calendarRepositorySpyk.all(titleId).test()
            .assertComplete()
            .assertValue { pair ->
                pair.first[0].get(Calendar.DAY_OF_MONTH) == 2 &&
                        pair.first[0].get(Calendar.MONTH) == 11 &&
                        pair.first[0].get(Calendar.YEAR) == 2019 &&
                        pair.second[0] == "2019-12-02"
            }
    }

    @Test
    fun testBuilderEpisodeAndRelatedExcerptsByDateQuery() {
        val titleId = "title_id"

        val calendarQuery = calendarRepositorySpyk.builderDatesWithContentQuery(titleId)

        Assert.assertEquals(calendarQuery.variables().titleId(), titleId)
    }

}