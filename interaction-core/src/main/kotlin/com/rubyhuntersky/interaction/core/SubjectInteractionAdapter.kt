package com.rubyhuntersky.interaction.core

interface SubjectInteractionAdapter<CoreV, CoreA, EdgeV, EdgeA> {

    fun onVision(vision: CoreV, controller: Controller<EdgeV, CoreA>)
    fun onAction(action: EdgeA, controller: Controller<EdgeV, CoreA>)

    interface Controller<EdgeV, CoreA> {
        val vision: EdgeV
        fun setVision(vision: EdgeV)
        fun sendUpstreamAction(action: CoreA)
    }
}