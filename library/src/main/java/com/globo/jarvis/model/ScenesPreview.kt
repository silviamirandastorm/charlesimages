package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScenesPreview(
    val id: String? = null,
    val number: Int = 1,
    val total: Int = 1,
    val headline: String? = null,
    val thumb: String?
) : Parcelable