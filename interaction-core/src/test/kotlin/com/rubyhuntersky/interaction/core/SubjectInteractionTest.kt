package com.rubyhuntersky.interaction.core

import org.junit.Test

class SubjectInteractionTest {

    @Test
    fun setVisionUpdateVisionStream() {
        data class SetVision(val vision: Int)

        val quantum = object : SubjectInteraction<Int, SetVision>(startVision = 1) {
            override fun sendAction(action: SetVision) = setVision(action.vision)
        }

        quantum.visionStream.test()
            .also { quantum.sendAction(SetVision(2)) }
            .assertValues(1, 2)
    }
}