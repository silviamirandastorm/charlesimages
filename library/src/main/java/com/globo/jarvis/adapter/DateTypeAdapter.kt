package com.globo.jarvis.adapter

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import java.text.ParseException

class DateTypeAdapter : CustomTypeAdapter<String> {
    override fun decode(customTypeValue: CustomTypeValue<*>): String {
        return try {
            customTypeValue.value.toString()
        } catch (parseException: ParseException) {
            parseException.printStackTrace()
            return "" //Esse "vazio" está retornando do try { } catch { }
        }
    }

    override fun encode(value: String): CustomTypeValue<*> =
        CustomTypeValue.fromRawValue(value)
}