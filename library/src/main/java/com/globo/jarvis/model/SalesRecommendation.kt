package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesRecommendation(
    val userError: SalesError? = null,
    val recommendedProducts: List<SalesProduct>? = null
): Parcelable