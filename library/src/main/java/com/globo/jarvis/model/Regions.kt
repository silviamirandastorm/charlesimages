package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Regions(val affiliateName: String, val name: String, val slug: String) : Parcelable
