package com.globo.jarvis.model

enum class Destination(val value: String) {
    MY_LIST("MY_LIST"),

    CATEGORIES("CATEGORIES"),

    CHANNEL("CHANNEL"),

    LOCAL_PROGRAM("LOCAL_PROGRAM"),

    CATEGORY_DETAILS("CATEGORY_DETAILS"),

    UNKNOWN("UNKNOWN");

    companion object {
        const val RESPONSE_LOCAL_PROGRAM = "programas-locais"
        const val RESPONSE_MY_LIST = "minha-lista"
        const val RESPONSE_CATEGORIES = "categorias"
        const val RESPONSE_CHANNEL = "canais"

        fun safeValueOf(value: String): Destination {
            for (enumValue in values()) {
                if (enumValue.value == value) {
                    return enumValue
                }
            }
            return UNKNOWN
        }

        fun normalize(identifier: String?, url: String?) = when {
            !identifier.isNullOrEmpty() -> CATEGORY_DETAILS

            url?.contains(RESPONSE_MY_LIST) == true -> MY_LIST

            url?.contains(RESPONSE_CATEGORIES) == true -> CATEGORIES

            url?.contains(RESPONSE_CHANNEL) == true -> CHANNEL

            url?.contains(RESPONSE_LOCAL_PROGRAM) == true -> LOCAL_PROGRAM

            else -> UNKNOWN
        }
    }
}