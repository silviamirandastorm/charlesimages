package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class EpgSlot(
    val titleId: String?,
    val name: String?,
    val description: String?,
    val metadata: String?,
    val startTime: Date?,
    val endTime: Date?,
    val duration: Int = 0,
    val alternativeTimeList: List<String>?,
    val isLiveBroadcast: Boolean,
    val classificationsList: List<String>?,
    val contentRating: String?,
    val contentRatingCriteria: List<String>?,
    val cover: String?,
    val imageOnAir: String?
) : Parcelable



