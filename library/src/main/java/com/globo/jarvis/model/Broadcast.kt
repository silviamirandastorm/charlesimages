package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Broadcast(
    val id: String? = null,
    val transmissionId: Int? = null,
    val name: String? = null,
    val logo: String? = null,
    val trimmedLogo: String? = null,
    val salesPageId: String? = null,
    val salesPageCallToAction: String? = null,
    val geofencing: Boolean = false,
    val geoblocked: Boolean = false,
    val hasError: Boolean = false,
    val promotionalText: String? = null,
    val ignoreAdvertisements: Boolean = false,
    val payTv: PayTv? = null,
    val affiliateSignal: AffiliateSignal? = null,
    val media: Media? = null,
    val broadcastSlotList: List<BroadcastSlot>? = null,
    val channel: Channel? = null,
    var authorizationStatus: AuthorizationStatus = AuthorizationStatus.UNAUTHORIZED
) : Parcelable
