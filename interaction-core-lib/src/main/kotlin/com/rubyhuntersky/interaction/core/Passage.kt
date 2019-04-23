package com.rubyhuntersky.interaction.core

interface Passage<Carry, Report, Action> {
    fun open(carry: Carry): Reporter<Report, Action>
}