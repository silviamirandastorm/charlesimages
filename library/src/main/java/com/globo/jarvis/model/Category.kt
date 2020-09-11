package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val id: String? = null, val name: String? = null, val background: String? = null,
    val destination: Destination = Destination.UNKNOWN
) : Parcelable
