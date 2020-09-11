package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendedTitleOffer(
    val titleId: String? = null,
    val headline: String? = null,
    val poster: String? = null
) : Parcelable