package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Scene(
    val id: String? = null,
    val number: Int = 1,
    val title: String? = null,
    val thumbList: List<Thumb>?
) : Parcelable