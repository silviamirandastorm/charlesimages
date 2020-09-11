package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Episode(
    val id: String? = null,
    val headline: String? = null,
    val formattedDate: String? = null,
    val description: String? = null,
    val contentRating: ContentRating? = null,
    val video: Video? = null,
    val duration: Int = 0,
    val formattedDuration: String? = null,
    val thumb: String? = null,
    val watchedProgress: Int = 0,
    val number: Int = 1,
    val seasonNumber: Int = 1,
    val accessibleOffline: Boolean = false,
    val fullyWatched: Boolean = false,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER,
    val serviceId: Int? = null
) : Parcelable
