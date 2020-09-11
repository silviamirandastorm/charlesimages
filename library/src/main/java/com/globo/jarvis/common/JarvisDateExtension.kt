package com.globo.jarvis.common

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// ################################ Date ################################ //
internal const val JARVIS_PATTERN_YYYY_MM_DD = "yyyy-MM-dd"
internal val JARVIS_LOCALE_BR: Locale = Locale("pt", "BR")

internal fun Date.formatByPattern(pattern: String) =
    try {
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        simpleDateFormat.format(this)
    } catch (parseException: ParseException) {
        ""
    }

internal fun Calendar.lastDayOfMonth(): Calendar = Calendar.getInstance().apply {
    set(Calendar.YEAR, this@lastDayOfMonth.get(Calendar.YEAR))
    set(Calendar.MONTH, this@lastDayOfMonth.get(Calendar.MONTH))
    set(Calendar.DAY_OF_MONTH, this@lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
}

internal fun String.formatByPattern(pattern: String, locale: Locale = JARVIS_LOCALE_BR) =
    try {
        val simpleDateFormat = SimpleDateFormat(pattern, locale)
        simpleDateFormat.parse(this) ?: Date()
    } catch (parseException: ParseException) {
        Date()
    }

