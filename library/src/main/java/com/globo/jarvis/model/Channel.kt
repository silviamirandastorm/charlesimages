package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Channel(
    val id: String? = null,
    val name: String? = null,
    val pageIdentifier: String? = null,
    val trimmedLogo: String? = null
) : Parcelable
