package dev.leonp.strata

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform