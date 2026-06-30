package com.mobinjam.tempo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform