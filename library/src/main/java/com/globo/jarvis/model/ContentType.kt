package com.globo.jarvis.model

import com.globo.jarvis.type.HighlightContentType
import com.globo.jarvis.type.OfferContentType

enum class ContentType(val value: String) {
    TITLE("title"),

    SERIES("series"),

    MOVIE("movie"),

    VIDEO("video"),

    LIVE("live"),

    SUBSET("subset"),

    SIMULCAST("simulcast"),

    DTV("dtv"),

    BACKGROUND("background"),

    THUMB("thumb"),

    SEE_MORE("see_more"),

    PROMOTIONAL("promotional"),

    UNKNOWN("unknown");

    companion object {
        fun normalizeHighlight(
            highlightContentType: HighlightContentType?,
            type: Type? = null
        ) = when (highlightContentType) {
            HighlightContentType.LIVE -> LIVE

            HighlightContentType.PROMOTIONAL -> PROMOTIONAL

            HighlightContentType.SIMULCAST -> SIMULCAST

            HighlightContentType.SUBSET -> SUBSET

            HighlightContentType.TITLE -> TITLE

            HighlightContentType.BACKGROUND -> BACKGROUND

            HighlightContentType.VIDEO -> if (type == Type.MOVIES) MOVIE else VIDEO

            else -> UNKNOWN
        }

        fun normalizeOffer(offerContentType: OfferContentType?) = when (offerContentType) {
            OfferContentType.VIDEO -> VIDEO

            OfferContentType.TITLE -> TITLE

            else -> UNKNOWN
        }

        fun safeValueOf(value: String): ContentType {
            for (enumValue in values()) {
                if (enumValue.value == value) {
                    return enumValue
                }
            }
            return UNKNOWN
        }
    }
}