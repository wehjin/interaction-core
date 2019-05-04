package com.rubyhuntersky.interaction.core

class WellInteraction<V, A>(
    well: Well,
    start: () -> V,
    private val update: (V, A) -> WellResult<V, A>,
    private val customName: String? = null
) : Interaction<V, A>
by object : SubjectInteraction<V, A>(startVision = start()) {

    override val name: String get() = customName ?: super.name
    override fun sendAction(action: A) {
        val result = update(vision, action)
        setVision(result.newVision)
        well.addWishes(result.wishes, this)
    }
}

