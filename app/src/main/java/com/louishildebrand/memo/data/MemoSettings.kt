package com.louishildebrand.memo.data

data class MemoSettings(
    val allowedChars: Set<Char> = (('A'..'B') + ('D'..'X')).toSet(),
    val len: Int = 8
)
