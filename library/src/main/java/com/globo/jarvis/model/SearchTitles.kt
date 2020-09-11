package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchTitles(
    val nextPage: Int = 0,
    val hasNextPage: Boolean = false,
    val totalCount: Int = 0,
    val titleList: List<Title>? = null
) : Parcelable