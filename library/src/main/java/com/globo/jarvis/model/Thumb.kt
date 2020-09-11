package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Thumb(
    val id: String? = null,
    val headline: String? = null,
    val description: String? = null,
    val duration: Int = 0,
    val formattedDuration: String? = null,
    val thumb: String?,
    val thumbLarge: String? = null,
    val exhibitedAt: String? = null,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER,
    val kind: Kind = Kind.UNKNOWN,
    val title: Title? = null,
    val broadcast: Broadcast? = null,
    val abExperiment: AbExperiment? = null
) : Parcelable