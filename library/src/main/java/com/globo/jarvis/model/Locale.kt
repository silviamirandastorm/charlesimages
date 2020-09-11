package com.globo.jarvis.model

data class Locale(
    val tenant: String = "globo-play",
    val countryCode: CountryCode = CountryCode.BR
)