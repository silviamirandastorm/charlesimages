package com.globo.jarvis.model

import com.globo.jarvis.type.HighlightContentType
import com.globo.jarvis.type.PageComponentType

enum class ComponentType(val value: String) {
    RAILS_CONTINUE_WATCHING("rails_continue_watching"),

    RAILS_THUMB("rails_thumb"),

    RAILS_POSTER("rails_poster"),

    RAILS_CHANNELS("rails_channels"),

    RAILS_CATEGORIES("rails_categories"),

    GRID("grid"),

    HIGHLIGHT("highlight"),

    HIGHLIGHT_LEFT("highlight_left"),

    HIGHLIGHT_RIGHT("highlight_right"),

    PREMIUM_HIGHLIGHT("premium_highlight"),

    SUBSCRIPTION_ADVANTAGE("subscription_advantage"),

    SALES_PLAN("sales_plan"),

    BANNER("banner"),

    TAKE_OVER("take_over"),

    UNKNOWN("unknown");

    companion object {

        fun normalize(
            pageComponentType: PageComponentType?,
            leftAligned: Boolean = false,
            isTablet: Boolean = false
        ) = when (pageComponentType) {
            PageComponentType.TAKEOVER -> TAKE_OVER

            PageComponentType.CONTINUEWATCHING -> RAILS_CONTINUE_WATCHING

            PageComponentType.POSTER -> RAILS_POSTER

            PageComponentType.THUMB -> RAILS_THUMB

            PageComponentType.OFFERHIGHLIGHT -> when {
                isTablet -> if (leftAligned) HIGHLIGHT_LEFT else HIGHLIGHT_RIGHT

                else -> HIGHLIGHT
            }

            PageComponentType.PREMIUMHIGHLIGHT -> PREMIUM_HIGHLIGHT

            else -> UNKNOWN
        }

        fun normalize(
            pageComponentType: PageComponentType?,
            highlightContentType: HighlightContentType?
        ) =
            if (pageComponentType == PageComponentType.PREMIUMHIGHLIGHT && highlightContentType == HighlightContentType.BACKGROUND) BANNER else UNKNOWN

        fun safeValueOf(value: String): ComponentType {
            for (enumValue in values()) {
                if (enumValue.value == value) {
                    return enumValue
                }
            }
            return UNKNOWN
        }
    }
}