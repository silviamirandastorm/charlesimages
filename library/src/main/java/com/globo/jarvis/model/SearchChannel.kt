package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchChannel(
    val nextPage: Int? = null,
    val hasNextPage: Boolean = false,
    val totalCount: Int = 0,
    val channelList: List<Channel>? = null
) : Parcelable