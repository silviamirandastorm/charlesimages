package com.globo.jarvis.model

import com.globo.jarvis.BaseUnitTest
import junit.framework.Assert.assertTrue
import org.junit.Test

class NavigationTest : BaseUnitTest() {

    @Test
    fun testNavigationIdentifierValid() {
        assertTrue(Navigation.extractSlug("jornalismo", null) == "jornalismo")
    }

    @Test
    fun testNavigationIdentifierIsNull() {
        assertTrue(Navigation.extractSlug(null, null) == null)
    }

    @Test
    fun testNavigationIdentifierIsEmpty() {
        assertTrue(Navigation.extractSlug("", "") == null)
    }

    @Test
    fun testNavigationUrlMyList() {
        assertTrue(Navigation.extractSlug("", "/minha-lista/") == "minha-lista")
    }

    @Test
    fun testNavigationUrlLocalProgram() {
        assertTrue(Navigation.extractSlug("", "/programas-locais/") == "/programas-locais/")
    }

    @Test
    fun testNavigationUrlCategories() {
        assertTrue(Navigation.extractSlug("", "/categorias/") == "categorias")
    }

    @Test
    fun testNavigationUrlChannel() {
        assertTrue(Navigation.extractSlug("", "/canais/") == "canais")
    }

    @Test
    fun testNavigationUrlInvalid() {
        assertTrue(Navigation.extractSlug("", "/profile/") == null)
    }

    @Test
    fun testNavigationUrlIsNull() {
        assertTrue(Navigation.extractSlug("", null) == null)
    }

    @Test
    fun testNavigationUrlIsEmpty() {
        assertTrue(Navigation.extractSlug("", "") == null)
    }
}