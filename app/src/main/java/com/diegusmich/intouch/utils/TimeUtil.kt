package com.diegusmich.intouch.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtil {
    private val _dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)

    fun toLocaleString(date: Date): String = _dateFormat.format(date)
}