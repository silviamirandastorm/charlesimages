package com.globo.jarvis.model

import android.os.Parcelable
import com.globo.jarvis.model.Destination.Companion.RESPONSE_CATEGORIES
import com.globo.jarvis.model.Destination.Companion.RESPONSE_CHANNEL
import com.globo.jarvis.model.Destination.Companion.RESPONSE_LOCAL_PROGRAM
import com.globo.jarvis.model.Destination.Companion.RESPONSE_MY_LIST
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Navigation(
    val slug: String? = null,
    val destination: Destination = Destination.UNKNOWN
) :
    Parcelable {

    companion object {
        private const val BAR = "/"

        fun extractSlug(identifier: String?, url: String?) = when {
            !identifier.isNullOrEmpty() -> identifier

            url?.toLowerCase()?.contains(RESPONSE_CATEGORIES) == true -> url.replace(BAR, "")

            url?.toLowerCase()?.contains(RESPONSE_CHANNEL) == true -> url.replace(BAR, "")

            url?.toLowerCase()?.contains(RESPONSE_MY_LIST) == true -> url.replace(BAR, "")

            url?.contains(RESPONSE_LOCAL_PROGRAM) == true -> url

            else -> null
        }
    }
}

