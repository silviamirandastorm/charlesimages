package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TitleUser(val continueWatching: ContinueWatching? = null, var favorited: Boolean = false) :
    Parcelable