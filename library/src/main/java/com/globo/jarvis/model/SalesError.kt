package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesError(
    val type: String? = null,
    val message: String? = null,
    val faq: SalesFaq? = null,
    val sourceChannel: SalesPlatformType? = null
): Parcelable