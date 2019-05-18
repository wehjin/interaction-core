package com.rubyhuntersky.interaction.core

class Story<V, A : Any>(
    well: Well,
    start: () -> V,
    private val isEnding: (Any?) -> Boolean,
    private val revise: (V, A, Edge) -> Revision<V, A>,
    private val customGroup: String? = null
) : Interaction<V, A>
by object : SubjectInteraction<V, A>(startVision = start()) {

    override val group: String get() = customGroup ?: super.group
    override fun isEnding(someVision: Any?): Boolean = isEnding(someVision)
    override fun sendAction(action: A) {
        val result = revise(vision, action, edge)
        setVision(result.newVision)
        well.addWishes(result.wishes, this)
    }

} {
    constructor(
        well: Well,
        start: () -> V,
        isEnding: (Any?) -> Boolean,
        revise: (V, A) -> Revision<V, A>,
        customGroup: String? = null
    ) : this(well, start, isEnding, { v, a, _ -> revise(v, a) }, customGroup)
}

