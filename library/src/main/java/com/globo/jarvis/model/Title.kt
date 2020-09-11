package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Title(
    val originProgramId: String? = null,
    val titleId: String? = null,
    val headline: String? = null,
    val title: String? = null,
    val description: String? = null,
    val poster: String? = null,
    val background: String? = null,
    val cover: String? = null,
    val titleDetails: TitleDetails? = null,
    var video: Video? = null,
    val genders: String? = null,
    val releaseYear: Int = 0,
    val contentType: ContentType = ContentType.UNKNOWN,
    val type: Type = Type.UNKNOWN,
    val logo: String? = null,
    val format: Format = Format.UNKNOWN,
    val editorialOfferIds: List<String> = listOf(),
    val abExperiment: AbExperiment? = null,
    val enableEpisodesTab: Boolean = false,
    val enableScenesTab: Boolean = false,
    val enableChapterTab: Boolean = false,
    val enableExcerptsTab: Boolean = false,
    val enableProgramsTab: Boolean = false,
    val enableEditionsTab: Boolean = false,
    val enableEditorialTab: Boolean = false,
    val enableRecommendationTab: Boolean = false,
    val accessibleOffline: Boolean = false,
    val isEpgActive: Boolean = true
) : Parcelable
