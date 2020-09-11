package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ContinueWatching(
    val id: String? = null,
    val headline: String? = null,
    val description: String? = null,
    val duration: Int = 0,
    val watchedProgress: Int = 0,
    val thumb: String? = null,
    val rating: String? = null,
    val formattedRemainingTime: String? = null,
    val formattedDuration: String? = null,
    val kind: Kind = Kind.UNKNOWN,
    val availableFor: AvailableFor = AvailableFor.SUBSCRIBER,
    val fullWatched: Boolean = false,
    val title: Title? = null,
    val abExperiment: AbExperiment? = null
) : Parcelable