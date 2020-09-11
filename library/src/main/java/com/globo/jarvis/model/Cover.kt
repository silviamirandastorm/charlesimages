package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cover(
    val mobile: String? = null,
    val tabletPortrait: String? = null,
    val tabletLand: String? = null,
    val wide: String? = null
) : Parcelable