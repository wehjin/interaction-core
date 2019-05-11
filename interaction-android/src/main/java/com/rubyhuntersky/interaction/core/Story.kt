package com.rubyhuntersky.interaction.core

class Story<V, A>(
    well: Well,
    start: () -> V,
    private val isEnding: (Any?) -> Boolean,
    private val revise: (V, A) -> Revision<V, A>,
    private val customGroup: String? = null
) : Interaction<V, A>
by object : SubjectInteraction<V, A>(startVision = start()) {

    override val group: String get() = customGroup ?: super.group
    override fun isEnding(someVision: Any?): Boolean = isEnding(someVision)
    override fun sendAction(action: A) {
        val result = revise(vision, action)
        setVision(result.newVision)
        well.addWishes(result.wishes, this)
    }
}

