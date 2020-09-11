package com.globo.jarvis.common

import java.lang.String.format
import java.util.*

// ################################ Double ################################ //
internal fun Double.formatCoordinate() = format(Locale.ENGLISH, "%.3f", this)

