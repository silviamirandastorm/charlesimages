package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesFaq(
    val link: String? = null,
    val text: String? = null
): Parcelable