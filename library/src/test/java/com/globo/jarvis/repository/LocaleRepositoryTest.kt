package com.globo.jarvis.repository

import com.globo.jarvis.BaseUnitTest
import com.globo.jarvis.locale.LocaleQuery
import com.globo.jarvis.model.CountryCode
import io.mockk.spyk
import org.junit.Test

class LocaleRepositoryTest : BaseUnitTest() {
    //Find
    @Test
    fun testFindLocaleBR() {
        enqueueResponse("locale/locale_br.json")

        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        localeRepository
            .find(CountryCode.US, "tenant-default")
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.countryCode == CountryCode.BR
                        && it.tenant == "globo-play"

            }
    }

    @Test
    fun testFindLocaleUS() {
        enqueueResponse("locale/locale_us.json")

        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        localeRepository
            .find(CountryCode.BR, "tenant-default")
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.countryCode == CountryCode.US
                        && it.tenant == "globo-play"

            }
    }

    @Test
    fun testFindInvalid() {
        enqueueResponse("locale/locale_invalid.json")

        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        localeRepository
            .find(CountryCode.BR, "tenant-default")
            .test()
            .apply {
                awaitTerminalEvent()
                assertComplete()
            }
            .assertValue {
                it.countryCode == CountryCode.BR
                        && it.tenant == "tenant-default"

            }
    }

    //Transform LocaleQuery To LocaleVO
    @Test
    fun testTransformLocaleQueryToLocaleVOWhenLocaleQueryValid() {
        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        val locale = localeRepository.transformLocaleQueryToLocaleVO(
            LocaleQuery.Locale("Locale", "globo-play", "US"),
            CountryCode.BR,
            "teste"
        )

        assert(locale.tenant == "globo-play")
        assert(locale.countryCode == CountryCode.US)
    }

    @Test
    fun testTransformLocaleQueryToLocaleVOWhenLocaleQueryNullTenant() {
        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        val locale = localeRepository.transformLocaleQueryToLocaleVO(
            LocaleQuery.Locale("Locale", null, "US"),
            CountryCode.US,
            "teste"
        )

        assert(locale.tenant == "teste")
        assert(locale.countryCode == CountryCode.US)
    }

    @Test
    fun testTransformLocaleQueryToLocaleVOWhenLocaleQueryEmptyTenant() {
        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        val locale = localeRepository.transformLocaleQueryToLocaleVO(
            LocaleQuery.Locale("Locale", "", "US"),
            CountryCode.US,
            "teste"
        )

        assert(locale.tenant == "teste")
        assert(locale.countryCode == CountryCode.US)
    }

    @Test
    fun testTransformLocaleQueryToLocaleVOWhenLocaleQueryNullCountryCode() {
        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        val locale = localeRepository.transformLocaleQueryToLocaleVO(
            LocaleQuery.Locale("Locale", "globo-play", null),
            CountryCode.US,
            "teste"
        )

        assert(locale.tenant == "globo-play")
        assert(locale.countryCode == CountryCode.US)
    }

    @Test
    fun testTransformLocaleQueryToLocaleVOWhenLocaleQueryEmptyCountryCode() {
        val localeRepository =
            spyk(LocaleRepository(apolloClient))

        val locale = localeRepository.transformLocaleQueryToLocaleVO(
            LocaleQuery.Locale("Locale", "globo-play", ""),
            CountryCode.US,
            "teste"
        )

        assert(locale.tenant == "globo-play")
        assert(locale.countryCode == CountryCode.BR)
    }
}


