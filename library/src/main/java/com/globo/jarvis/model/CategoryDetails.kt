package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryDetails(
    val headline: String? = null, val navigation: Navigation? = null,
    val nextPage: Int = 1, val hasNextPage: Boolean = false,
    val titleVOList: List<Title>? = null
) : Parcelable