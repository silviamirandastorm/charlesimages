package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NextVideo(
    var id: String? = null,
    val headline: String? = null,
    val description: String? = null,
    var duration: Int = 0,
    val fullyWatchedThreshold: Int? = null,
    var formattedDuration: String? = null,
    var watchedProgress: Int = 0,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER,
    var accessibleOffline: Boolean = false,
    val contentRating: ContentRating? = null,
    val thumb: String? = null,
    val kind: Kind = Kind.UNKNOWN,
    val title: Title? = null,
    val serviceId: Int = 0,
    val subscriptionService: SubscriptionService? = null
) : Parcelable