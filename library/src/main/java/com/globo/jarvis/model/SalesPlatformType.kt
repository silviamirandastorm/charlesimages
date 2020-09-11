package com.globo.jarvis.model

import com.globo.jarvis.type.SalesPlatform

enum class SalesPlatformType(val value: String) {

    WEB("WEB"),
    APPLE("APPLE"),
    GOOGLE("GOOGLE"),
    ROKU("ROKU"),
    VIVO("VIVO"),
    NONE("NONE");

    companion object {

        fun normalize(paymentFrequencyType: SalesPlatform?) =
            when (paymentFrequencyType) {
                SalesPlatform.WEB -> WEB
                SalesPlatform.APPLE -> APPLE
                SalesPlatform.GOOGLE -> GOOGLE
                SalesPlatform.ROKU -> ROKU
                SalesPlatform.VIVO -> VIVO
                else -> NONE
            }

        fun safeValueOf(value: String): SalesPlatformType {
            for (enumValue in values()) {
                if (enumValue.value == value) {
                    return enumValue
                }
            }
            return NONE
        }
    }
}