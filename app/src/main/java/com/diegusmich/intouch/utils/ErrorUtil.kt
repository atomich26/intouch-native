package com.diegusmich.intouch.utils

import com.diegusmich.intouch.R

object ErrorUtil {

    private val messages = hashMapOf(
        "INVALID_ARGUMENT" to R.string.invalid_argument,
        "PERMISSION_DENIED" to R.string.permission_denied,
        "USER_NOT_EXISTS" to R.string.user_not_exists,
        "INVALID_USERNAME" to R.string.invalid_username,
        "USERNAME_ALREADY_EXISTS" to R.string.username_already_exists,
        "EMPTY_USERNAME" to R.string.username_already_exists,
        "INVALID_NAME" to R.string.invalid_name,
        "INVALID_SPECIAL_CHARS_NAME" to R.string.invalid_special_chars_name,
        "EMPTY_NAME" to R.string.empty_name,
        "INVALID_EMAIL_LENGTH" to R.string.invalid_email_length,
        "INVALID_EMAIL" to R.string.invalid_email,
        "EMAIL_ALREADY_EXISTS" to R.string.email_already_exists,
        "EMPTY_EMAIL" to R.string.empty_email,
        "INVALID_PASSWORD" to R.string.invalid_password,
        "EMPTY_PASSWORD" to R.string.invalid_password,
        "INVALID_DATE" to R.string.invalid_date,
        "DATE_AFTER_NOW" to R.string.date_after_now,
        "EMPTY_BIRTHDATE" to R.string.empty_birthdate
    )

    fun getMessage(key : String) = messages[key] ?: R.string.default_invalid_error
}