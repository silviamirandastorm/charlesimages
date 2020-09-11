package com.globo.jarvis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesProduct(
    val productId: String? = null,
    val name: String? = null,
    val offerText: String? = null,
    val paymentInfo: SalesPaymentInfo? = null,
    val benefits: List<String>? = null,
    val productFaq: SalesFaq? = null,
    val channels: List<Broadcast>? = null,
    val androidSku: String? = null,
    val legalText: SalesLegalText? = null,
    val productError: SalesError? = null,
    val userConditions: SalesUserConditions? = null
): Parcelable