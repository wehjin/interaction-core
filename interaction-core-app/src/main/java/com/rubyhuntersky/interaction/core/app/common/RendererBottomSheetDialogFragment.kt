package com.rubyhuntersky.interaction.core.app.common

import com.rubyhuntersky.interaction.core.Edge

abstract class RendererBottomSheetDialogFragment<V : Any, A : Any, Data : Any>(
    private val renderer: Renderer<V, A, Data>,
    edge: Edge,
    closeAction: (() -> A)?
) : EdgeBottomSheetDialogFragment<V, A>(renderer.layoutRes, edge, closeAction) {

    private lateinit var data: Data

    override fun render(vision: V) {
        val oldData = if (::data.isInitialized) data else renderer.start(this.view!!, this::sendAction)
        val result = renderer.update(vision, this::sendAction, this.view!!, oldData)
        data = result.data
        if (result is RenderResult.Finish) {
            dismiss()
        }
    }

    override fun erase() {
        renderer.end(this.view!!, data)
    }
}