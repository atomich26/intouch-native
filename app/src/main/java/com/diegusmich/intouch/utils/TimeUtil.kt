package com.diegusmich.intouch.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtil {
    val DAY_OF_YEAR = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
    val DAY_OF_YEAR_HH_MM = SimpleDateFormat("dd/MM/yyyy 'alle ore' HH:mm", Locale.ITALY)
    val HH_MM = SimpleDateFormat("HH:mm", Locale.ITALY)

    fun toLocaleString(date: Date, dateFormat : SimpleDateFormat): String = dateFormat.format(date)
}