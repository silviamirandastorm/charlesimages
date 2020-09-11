package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentRating(val rating: String? = null, val ratingCriteria: String? = null) :
    Parcelable