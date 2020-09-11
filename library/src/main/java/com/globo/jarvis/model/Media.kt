package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(
    val idWithDVR: String? = null,
    val idWithoutDVR: String? = null,
    val idPromotional: String? = null,
    val headline: String? = null,
    val serviceId: Int = 0,
    val imageOnAir: String? = null,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER,
    val subscriptionService: SubscriptionService? = null
) : Parcelable