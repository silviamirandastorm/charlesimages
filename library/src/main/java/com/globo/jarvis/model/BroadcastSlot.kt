package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class BroadcastSlot(
    val programId: String? = null,
    val name: String? = null,
    val metadata: String? = null,
    val startTime: Date? = null,
    val endTime: Date? = null,
    val duration: Int = 0,
    val portrait: String? = null,
    val tabletLandscape: String? = null,
    val tabletPortrait: String? = null,
    val wide: String? = null,
    val isLiveBroadcast: Boolean = false,
    val classificationsList: List<String>? = null
) : Parcelable
