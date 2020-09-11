package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Offer(
    val id: String? = null,
    val title: String? = null,
    val headlineText: String? = null,
    val callText: String? = null,
    val experiment: String? = null,
    val alternative: String? = null,
    val trackId: String? = null,
    val navigation: Navigation? = null,
    val advantage: Advantage? = null,
    var titleList: List<Title>? = null,
    val continueWatchingList: List<ContinueWatching>? = null,
    val thumbList: List<Thumb>? = null,
    val highlights: Highlights? = null,
    val abExperiment: AbExperiment? = null,
    val componentType: ComponentType = ComponentType.UNKNOWN,
    val contentType: ContentType = ContentType.UNKNOWN
) : Parcelable

