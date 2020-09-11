package com.globo.jarvis

enum class Environment(val value: String) {
    PRODUCTION("https://jarvis.globo.com/graphql/"),
    BETA("https://beta.jarvis.globo.com/graphql/")
}