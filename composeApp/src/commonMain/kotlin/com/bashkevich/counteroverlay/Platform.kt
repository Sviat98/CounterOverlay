package com.bashkevich.counteroverlay

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform