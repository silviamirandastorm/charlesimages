package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TitleDetails(
    val title: String? = null,
    val originalHeadline: String? = null,
    val formattedDuration: String? = null,
    val year: String? = null,
    val country: String? = null,
    val directors: String? = null,
    val cast: String? = null,
    val genders: String? = null,
    val author: String? = null,
    val screeWriter: String? = null,
    val artDirectors: String? = null,
    val summary: String? = null,
    val contentRating: ContentRating? = null
) : Parcelable