package com.diegusmich.intouch.data.model

class Friendship(val status: Status){

    sealed interface Status{

        data object AUTH: Status

        data object FRIEND: Status

        data object NONE: Status

        data class PENDING(val requestId: String, val isActor: Boolean): Status
    }
}