package com.louishildebrand.memo.ui

import java.io.Serializable

data class MemoSettings(
    val allowedChars: Set<Char> = (('A'..'B') + ('D'..'X')).toSet(),
    val len: Int = 8
) : Serializable
