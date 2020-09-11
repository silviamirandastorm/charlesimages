package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chapter(
    val id: String? = null,
    val headline: String? = null,
    val thumb: String? = null,
    val description: String? = null,
    val duration: Int = 0,
    val formattedDuration: String? = null,
    val accessibleOffline: Boolean = false,
    val exhibitedAt: String? = null,
    val watchedProgress: Long = 0,
    var fullWatched: Boolean = false,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER,
    val serviceId: Int? = null

) : Parcelable