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
        "EMPTY_BIRTHDATE" to R.string.empty_birthdate,
        "EVENT_NOT_EXISTS" to R.string.event_not_exists_error,
        "NOT_EVENT_AUTHOR" to R.string.event_not_author,
        "EVENT_CLOSED" to R.string.event_closed_error,
        "EVENT_NAME_INVALID" to R.string.event_name_invalid,
        "EVENT_NAME_EMPTY" to R.string.event_name_empty,
        "EVENT_DESCRIPTION_INVALID" to R.string.event_description_invalid,
        "EVENT_DESCRIPTION_EMPTY" to R.string.event_description_empty,
        "EVENT_CITY_INVALID" to R.string.event_city_invalid,
        "EVENT_CITY_EMPTY" to R.string.event_city_empty,
        "EVENT_ADDRESS_INVALID" to R.string.event_address_invalid,
        "EVENT_ADDRESS_EMPTY" to R.string.event_address_empty,
        "EVENT_RESTRICTED_INVALID" to R.string.event_restricted_invalid,
        "EVENT_RESTRICTED_EMPTY" to R.string.event_restricted_empty,
        "EVENT_COVER_INVALID" to R.string.event_cover_invalid,
        "EVENT_COVER_EMPTY" to R.string.event_cover_empty,
        "INVALID_START_AT_FIELD" to R.string.event_start_at_invalid,
        "START_AT_BEFORE_NOW" to R.string.event_start_at_before_now,
        "START_AT_EMPTY" to R.string.event_start_at_empty,
        "INVALID_END_AT_FIELD" to R.string.event_end_at_invalid,
        "END_AT_BEFORE_NOW" to R.string.event_end_before_now,
        "END_AT_BEFORE_START" to R.string.event_end_before_start,
        "EVENT_CATEGORY_NOT_ESISTS" to R.string.event_category_not_exists,
        "EVENT_CATEGORY_INVALID" to R.string.event_category_invalid,
        "EVENT_CATEGORY_EMPTY" to R.string.event_category_empty,
        "EVENT_AVAILABLE_INVALID" to R.string.event_available_invalid,
        "EVENT_AVAILABLE_EMPTY" to R.string.event_available_empty,
        "AVAILABILITY_LESS_THAN_ATTENDEES" to R.string.event_availability_less_than_attendees,
        "EVENT_GEO_INVALID" to R.string.event_geo_invalid,
        "EVENT_GEO_EMPTY" to R.string.event_geo_empty,
        "INVALID_FIELDS_VALUE" to R.string.invalid_fields_value,
        "TERMINATED_JOINED" to R.string.event_terminated,
        "TERMINATED_NOT_JOINED" to R.string.event_terminated,
        "EVENT_UNAVAILABLE" to R.string.event_unavailable
    )

    fun getMessage(key : String) = messages[key] ?: R.string.default_invalid_error
}