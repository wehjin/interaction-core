package com.rubyhuntersky.vx

data class Anchor(val position: Int, val placement: Float) {

    fun toBounds(size: Int): Pair<Int, Int> {
        val a = position - (placement * size).toInt()
        val b = a + size
        return Pair(a, b)
    }
}