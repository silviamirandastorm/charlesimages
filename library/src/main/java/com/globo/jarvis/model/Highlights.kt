package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Highlights(
    val id: String? = null,
    val idDvr: String? = null,
    val titleId: String? = null,
    val programId: String? = null,
    val title: String? = null,
    val headlineText: String? = null,
    val callText: String? = null,
    val highlightImage: String? = null,
    val offerImage: String? = null,
    val logo: String? = null,
    val buttonText: String? = null,
    val thumb: String? = null,
    val rating: String? = null,
    val carrierEnable: Boolean = false,
    val type: Type = Type.UNKNOWN,
    val contentType: ContentType = ContentType.UNKNOWN,
    val format: Format = Format.UNKNOWN,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER
) : Parcelable