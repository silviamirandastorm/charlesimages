package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesUserConditions(
    val action: String? = null,
    val trialPeriod: Int? = null
): Parcelable