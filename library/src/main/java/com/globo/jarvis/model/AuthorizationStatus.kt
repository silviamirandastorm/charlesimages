package com.globo.jarvis.model

enum class AuthorizationStatus(val value: Int) {
    UNAUTHORIZED(1010),
    AUTHORIZED(1011),
    TV_EVERYWHERE(1012)
}