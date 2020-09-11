package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Season(
    val id: String? = null,
    val number: Int = 1,
    val total: Int = 0,
    var isSelected: Boolean = false
) : Parcelable