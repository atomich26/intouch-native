package com.diegusmich.intouch.ui.viewmodel

/**
 * Describe all UiEvent that state holders can produce to be consumed by the Views.
 */
sealed interface UiEvent {

    /**
     * Notify UI is ready to accept users interaction
     */
    object READY : UiEvent

    /**
     * Notify that UiEvent is consumed.
     */
    object CONSUMED : UiEvent

    /**
     * Notify that there are some running tasks in ViewModel.
     * It is the only UIEvent that View can't consume because its lifetime
     * is established by ViewModel.
     */
    object LOADING : UiEvent

    /**
     * Notify that finished task produces error
     */
    object ERROR : UiEvent

    /**
     * Notify users is logged
     */
    object LOGGED : UiEvent

    /**
     * Notify that recovery email was sent successfully
     */
    object RECOVERY_EMAIL_SENT : UiEvent

    /**
     * Notify when user profile is created
     */
    object USER_REGISTERED : UiEvent

    /**
     * Notify when a form validation failed
     */
    object FORM_VALIDATION_ERROR : UiEvent


    /**
     *
     */
    object FORM_INPUT_CHANGED : UiEvent
}