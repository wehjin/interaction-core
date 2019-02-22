package com.rubyhuntersky.interaction.core

import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectorTest {
    @Test
    fun fallback() {
        val interaction = object : BehaviorInteraction<Int, Nothing>(17) {
            override fun sendAction(action: Nothing) {
            }
        }

        var currentVision = 0
        val projector = Projector(interaction, Schedulers.trampoline())
            .fallback { vision, _ ->
                currentVision = vision
            }
        projector.start()
        assertEquals(17, currentVision)
    }
}