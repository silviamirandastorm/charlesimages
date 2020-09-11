package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubscriptionServiceFaq(
    val qrCode: PageUrl? = null,
    val url: PageUrl? = null
) : Parcelable