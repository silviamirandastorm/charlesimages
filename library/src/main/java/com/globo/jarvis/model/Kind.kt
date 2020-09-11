package com.globo.jarvis.model

import com.globo.jarvis.type.KindType

enum class Kind(val value: String) {
    EPISODE("episode"),

    LIVE("live"),

    EVENT("event"),

    EXCERPT("excerpt"),

    SEGMENT("segment"),

    EXTRA("extra"),

    AD("ad"),

    UNKNOWN("unknown");

    companion object {
        fun normalize(kindType: KindType?) =
            when (kindType) {
                KindType.EPISODE -> EPISODE

                KindType.LIVE -> LIVE

                KindType.EXCERPT -> EXCERPT

                KindType.SEGMENT -> SEGMENT

                KindType.EXTRA -> EXTRA

                KindType.AD -> AD

                else -> UNKNOWN
            }

        fun safeValueOf(value: String): Kind {
            for (enumValue in values()) {
                if (enumValue.value == value) {
                    return enumValue
                }
            }
            return UNKNOWN
        }
    }
}