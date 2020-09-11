package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PageUrl(
    val mobile: String? = null
) : Parcelable