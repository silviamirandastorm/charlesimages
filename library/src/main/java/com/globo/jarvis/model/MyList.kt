package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyList(
    val nextPage: Int = 0,
    val hasNextPage: Boolean = false,
    val titleMyList: List<Title>?
) : Parcelable