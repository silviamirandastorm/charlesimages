package com.globo.jarvis.model

import com.globo.jarvis.type.SubscriptionType

enum class AvailableFor(val value: String) {
    ANONYMOUS("anonimo"),

    /**
     * User is logged but is not a subscriber
     */
    LOGGED_IN("nao-assinante"),

    /**
     * User is logged and is a subscriber
     */
    SUBSCRIBER("assinante");


    companion object {
        fun safeValueOf(value: String?): AvailableFor {
            for (enumValue in values()) {
                if (enumValue.value == value) {
                    return enumValue
                }
            }
            return ANONYMOUS
        }

        fun normalize(subscriptionType: SubscriptionType?) = when (subscriptionType) {
            SubscriptionType.LOGGED_IN -> LOGGED_IN

            SubscriptionType.SUBSCRIBER -> SUBSCRIBER

            else -> ANONYMOUS
        }
    }
}