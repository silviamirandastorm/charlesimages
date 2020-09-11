package com.globo.jarvis.model

enum class CountryCode(val value: String) {
    BR("BR"),
    US("US");

    companion object {
        fun safeValueOf(value: String?): CountryCode {
            for (enumValue in values()) {
                if (enumValue.value == value?.toUpperCase()) {
                    return enumValue
                }
            }
            return BR
        }
    }
}