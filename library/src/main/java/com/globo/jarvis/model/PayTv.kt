package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PayTv(
    val serviceId: Int = 0,
    val usersMessage: String? = null,
    val internalLink: String? = null,
    val externalLink: String? = null,
    val externalLinkLabel: String? = null,
    val requireUserTeam: Boolean = false
): Parcelable