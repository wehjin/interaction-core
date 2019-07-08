package com.rubyhuntersky.interaction.core

interface InteractionCompanion<Vision : Any, Action : Any> {

    val groupId: String

    val searchByGroup: InteractionSearch
        get() = InteractionSearch.ByName(groupId)

    fun findInEdge(edge: Edge): Interaction<Vision, Action> = edge.findInteraction<Vision, Action>(searchByGroup)
}
