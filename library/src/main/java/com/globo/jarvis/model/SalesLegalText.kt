package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesLegalText(
    val legalContent: String? = null,
    val contractUrl: String? = null,
    val contractsUrlText: String? = null
): Parcelable