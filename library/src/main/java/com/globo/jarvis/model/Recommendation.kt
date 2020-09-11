package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Recommendation(
    var titleId: String? = null,
    val programId: String? = null,
    val headline: String? = null,
    val description: String? = null,
    val logo: String? = null,
    val cover: String? = null,
    val videoId: String? = null,
    val format: Format = Format.UNKNOWN,
    val type: Type = Type.UNKNOWN,
    val abExperiment: AbExperiment? = null
) : Parcelable