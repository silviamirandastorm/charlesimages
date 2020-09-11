package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Excerpt(
    val id: String? = null,
    val headline: String? = null,
    val title: String? = null,
    val description: String? = null,
    val number: Int = 1,
    val thumbList: List<Thumb>? = null,
    val duration: Int = 0,
    val formattedDuration: String? = null,
    val accessibleOffline: Boolean = false,
    val thumbSmall: String? = null,
    val thumbLarge: String? = null,
    val formattedRemainingTime: String? = null,
    val watchedProgress: Int = 0,
    val exhibitedAt: String? = null,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER
) : Parcelable