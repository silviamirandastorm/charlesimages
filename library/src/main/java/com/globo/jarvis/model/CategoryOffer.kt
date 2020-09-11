package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryOffer(
    val id: String? = null,
    val originProgramId: String? = null,
    val titleId: String? = null,
    val title: String? = null,
    val description: String? = null,
    var componentType: ComponentType = ComponentType.UNKNOWN,
    val contentType: ContentType = ContentType.UNKNOWN,
    val type: Type = Type.UNKNOWN,
    val navigation: Navigation? = null,
    var nextPage: Int = 0,
    var hasNextPage: Boolean = false,
    var titleList: List<Title>? = null,
    val highlights: Highlights? = null,
    val contentRating: ContentRating? = null,
    val experiment: String? = null,
    val alternative: String? = null,
    val convertUrl: String? = null,
    val trackId: String? = null,
    val subsetId: String? = null,
    val placeHolder: String? = null,
    val headline: String? = null,
    val poster: String? = null,
    val background: String? = null,
    val titleFormat: Format = Format.UNKNOWN
) : Parcelable

