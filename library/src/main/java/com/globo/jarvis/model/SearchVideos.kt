package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchVideos(
    val nextPage: Int = 0,
    val hasNextPage: Boolean = false,
    val totalCount: Int = 0,
    val thumbList: List<Thumb>? = null
) : Parcelable