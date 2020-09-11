package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesPaymentInfo(
    val callText: String? = null,
    val currency: String? = null,
    val price: String? = null,
    val numberOfInstallments: Int? = 0,
    val installmentValue: String? = null,
    val frequency: SalesFrequency? = null
): Parcelable