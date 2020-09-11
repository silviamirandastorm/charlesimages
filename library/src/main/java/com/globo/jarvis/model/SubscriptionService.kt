package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubscriptionService(
    val name: String? = null,
    val salesPage: SalesPage? = null,
    val faq: SubscriptionServiceFaq? = null
) : Parcelable