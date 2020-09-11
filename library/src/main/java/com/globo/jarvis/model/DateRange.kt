package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class DateRange(
    val id: String? = null,
    val date: String? = null,
    val total: Int = 0,
    val lte: Calendar? = null,
    val gte: Calendar? = null
) : Parcelable
