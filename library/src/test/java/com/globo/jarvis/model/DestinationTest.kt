package com.globo.jarvis.model

import com.globo.jarvis.BaseUnitTest
import junit.framework.Assert.assertTrue
import org.junit.Test

class DestinationTest : BaseUnitTest() {

    @Test
    fun testNavigationIdentifierValid() {
        assertTrue(Destination.normalize("jornalismo", null) == Destination.CATEGORY_DETAILS)
    }

    @Test
    fun testNavigationIdentifierIsNull() {
        assertTrue(Destination.normalize(null, null) == Destination.UNKNOWN)
    }

    @Test
    fun testNavigationIdentifierIsEmpty() {
        assertTrue(Destination.normalize("", "") == Destination.UNKNOWN)
    }

    @Test
    fun testNavigationUrlMyList() {
        assertTrue(Destination.normalize("", "/minha-lista/") == Destination.MY_LIST)
    }

    @Test
    fun testNavigationUrlLocalProgram() {
        assertTrue(Destination.normalize("", "/programas-locais/") == Destination.LOCAL_PROGRAM)
    }

    @Test
    fun testNavigationUrlCategories() {
        assertTrue(Destination.normalize("", "/categorias/") == Destination.CATEGORIES)
    }

    @Test
    fun testNavigationUrlChannel() {
        assertTrue(Destination.normalize("", "/canais/") == Destination.CHANNEL)
    }

    @Test
    fun testNavigationUrlInvalid() {
        assertTrue(Destination.normalize("", "/profile/") == Destination.UNKNOWN)
    }

    @Test
    fun testNavigationUrlIsNull() {
        assertTrue(Destination.normalize("", null) == Destination.UNKNOWN)
    }

    @Test
    fun testNavigationUrlIsEmpty() {
        assertTrue(Destination.normalize("", "") == Destination.UNKNOWN)
    }
}