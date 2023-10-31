package com.diegusmich.intouch.utils

import java.text.SimpleDateFormat
import java.util.*


class TimeUtil {
    companion object{
        private val _dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)

        fun toLocaleString(date : Date): String = _dateFormat.format(date)
    }
}