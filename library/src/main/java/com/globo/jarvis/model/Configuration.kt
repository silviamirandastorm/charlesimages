package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Configuration(
    val valueString: String? = null,
    val valueBoolean: Boolean = false,
    val valueNumber: Double = 0.0
) : Parcelable