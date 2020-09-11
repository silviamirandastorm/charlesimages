package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val id: String? = null,
    val headline: String? = null,
    val description: String? = null,
    val duration: Int = 0,
    val fullyWatchedThreshold: Int? = null,
    val formattedDuration: String? = null,
    val watchedProgress: Int = 0,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER,
    val accessibleOffline: Boolean = false,
    val contentRating: ContentRating? = null,
    val thumb: String? = null,
    val kind: Kind = Kind.UNKNOWN,
    val title: Title? = null,
    val fullWatched: Boolean = false,
    val serviceId: Int = 0,
    val subscriptionService: SubscriptionService? = null
) : Parcelable