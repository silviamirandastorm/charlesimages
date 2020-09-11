package com.globo.jarvis.model

import com.globo.jarvis.type.TitleFormat


enum class Format(val value: String) {
    SOAP_OPERA("soap_opera"),

    NEWS("news"),

    VARIETIES("varieties"),

    TALK_SHOWS("talk_shows"),

    REALITIES("realities"),

    CARTOONS("cartoons"),

    SPORTS("sports"),

    SHOWS("shows"),

    DOCUMENTARIES("documentaries"),

    SERIES("series"),

    LONG("long"),

    UNKNOWN("unknown");

    companion object {
        fun normalize(titleFormat: TitleFormat?) = when (titleFormat) {
            TitleFormat.SOAP_OPERA -> SOAP_OPERA
            TitleFormat.NEWS -> NEWS
            TitleFormat.VARIETIES -> VARIETIES
            TitleFormat.TALK_SHOWS -> TALK_SHOWS
            TitleFormat.REALITIES -> REALITIES
            TitleFormat.CARTOONS -> CARTOONS
            TitleFormat.SPORTS -> SPORTS
            TitleFormat.SHOWS -> SHOWS
            TitleFormat.DOCUMENTARIES -> DOCUMENTARIES
            TitleFormat.SERIES -> SERIES
            TitleFormat.LONG -> LONG
            else -> UNKNOWN
        }

        fun safeValueOf(rawValue: String): TitleFormat? {
            for (enumValue in TitleFormat.values()) {
                if (enumValue.rawValue() == rawValue) {
                    return enumValue
                }
            }
            return TitleFormat.`$UNKNOWN`
        }
    }
}