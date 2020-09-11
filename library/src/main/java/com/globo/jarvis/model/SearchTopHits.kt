package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchTopHits(
    var titleId: String? = null,
    val programId: String? = null,
    val headline: String? = null,
    val description: String? = null,
    val poster: String? = null,
    val format: Format = Format.UNKNOWN,
    val type: Type = Type.UNKNOWN
) : Parcelable