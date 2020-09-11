package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesFrequency(
    val id: String? = null,
    val periodicityLabel: String? = null,
    val timeUnitLabel: String? = null
): Parcelable