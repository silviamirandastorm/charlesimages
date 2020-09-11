package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AffiliateSignal(
    val id: String? = null,
    val dtvChannel: String? = null,
    val dtvHDID: String? = null,
    val dtvID: String? = null
) : Parcelable