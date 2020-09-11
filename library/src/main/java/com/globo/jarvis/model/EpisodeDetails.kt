package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeDetails(
    var hasNextPage: Boolean = false,
    var nextPage: Int = 1,
    var episodeList: List<Episode>?,
    var originProgramId: String? = null
) : Parcelable