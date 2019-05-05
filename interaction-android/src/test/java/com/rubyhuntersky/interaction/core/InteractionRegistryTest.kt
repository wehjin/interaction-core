package com.rubyhuntersky.interaction.core

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class InteractionRegistryTest {

    @Before
    fun setUp() {
        InteractionRegistry.clearInteractions()
    }

    @Test
    fun addDrop() {
        val interaction = mock<Interaction<Nothing, Nothing>>()
        InteractionRegistry.addInteraction(1, interaction)
        val found = InteractionRegistry.findInteraction<Nothing, Nothing>(1)
        assertEquals(interaction, found)

        InteractionRegistry.dropInteraction(1)
        val afterDrop = InteractionRegistry.findInteraction<Nothing, Nothing>(1)
        assertNull(afterDrop)
    }
}