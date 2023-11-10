package com.diegusmich.intouch.ui.state

/**
 * Describe all UiEvent that state holders can produce to be consumed by the Views.
 */
sealed interface UiState{
    /**
     * Notify UI is ready to accept users interaction
     */
    data object READY : UiState

    /**
     * Notify that UiState is consumed.
     */
    data object CONSUMED : UiState

    /**
     * Notify that there are some running tasks in ViewModel.
     * It is the only UiState that View can't consume because its lifetime
     * is established by ViewModel.
     */
    data object LOADING : UiState

    /**
     * Notify when the loading task is completed
     */
    data object LOADING_COMPLETED : UiState
    /**
     * Notify that finished task produces error
     */
    data object ERROR : UiState

    /**
     * Notify when viewModel loaded data
     */
    data object CONTENT_LOADED : UiState

    /**
     * Notify when viewModel loaded data
     */
    data object CONTENT_REFRESHED : UiState

    /**
     * Notify users is logged
     */
    data object LOGGED : UiState

    /**
     * Notify that recovery email was sent successfully
     */
    data object RECOVERY_EMAIL_SENT : UiState

    /**
     * Notify when a form validation failed
     */
    data object FORM_VALIDATION_ERROR : UiState

    /**
     * Notify when user's preferences are saved
     */
    data object PREFERENCES_SAVED : UiState


}