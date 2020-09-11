package com.globo.jarvis.model

import com.globo.jarvis.type.TitleType

enum class Type constructor(val value: String) {

    MOVIES("MOVIES"),

    SERIES("SERIES"),

    PROGRAM("PROGRAM"),

    EDITION("EDITION"),

    CHAPTER("CHAPTER"),

    EXCERPT_TOP_HITS("EXCERPT_TOP_HITS"),

    EXCERPT_TOP_HITS_ALL_TIMES("EXCERPT_TOP_HITS_ALL_TIMES"),

    UNKNOWN("UNKNOWN");


    companion object {
        fun normalize(titleType: TitleType?) = when (titleType) {
            TitleType.MOVIE -> MOVIES

            TitleType.SERIE -> SERIES

            TitleType.TV_PROGRAM -> PROGRAM

            else -> UNKNOWN
        }

        fun safeValueOf(value: String?): Type {
            for (enumValue in values()) {
                if (enumValue.value == value?.toUpperCase()) {
                    return enumValue
                }
            }
            return UNKNOWN
        }
    }
}
