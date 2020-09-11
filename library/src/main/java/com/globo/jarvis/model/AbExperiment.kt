package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AbExperiment(
    val experiment: String? = null,
    val alternative: String? = null,
    val pathUrl: String? = null,
    val trackId: String? = null
) : Parcelable