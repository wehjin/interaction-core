package com.rubyhuntersky.interaction.android

abstract class KeyedRendererBottomSheetDialogFragment<V : Any, A : Any, Data : Any>(
    closeAction: (() -> A)?,
    private val renderer: Renderer<V, A, Data>
) : KeyedInteractionBottomSheetDialogFragment<V, A>(renderer.layoutRes, closeAction) {

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