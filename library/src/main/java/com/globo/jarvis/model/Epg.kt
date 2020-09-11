package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Epg(
    val id: String? = null,
    val name: String? = null,
    val logo: String? = null,
    val geofenced: Boolean = false,
    val media: Media? = null,
    val epgSlotVOList: List<EpgSlot>? = null
) : Parcelable