package com.rubyhuntersky.interaction.android

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class KeyedInteractionBottomSheetDialogFragment<V : Any, A : Any>(
    @LayoutRes private val layoutRes: Int,
    private val closeAction: (() -> A)?
) : BottomSheetDialogFragment() {

    var edgeKey: Long
        get() = arguments!!.getLong(EDGE_INTERACTION_ARGS_KEY)
        set(value) {
            arguments =
                (arguments ?: Bundle()).also { it.putLong(EDGE_INTERACTION_ARGS_KEY, value) }
        }

    private var visionDisposable: Disposable? = null

    private var _vision: V? = null
    protected val renderedVision get() = _vision
    protected abstract fun render(vision: V)

    protected open fun erase() = Unit
    protected fun sendAction(action: A) {
        Log.d(this.javaClass.simpleName, "ACTION: $action")
        interaction.sendAction(action)
    }

    private val interaction: Interaction<V, A> by lazy {
        AndroidEdge.findInteraction<V, A>(InteractionSearch.ByKey(edgeKey))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(layoutRes, container, false)

    override fun onStart() {
        super.onStart()
        visionDisposable = interaction.visions
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::renderVision, this::renderError, this::renderComplete)
    }

    private fun renderVision(v: V) {
        Log.d(this.javaClass.simpleName, "VISION: $v")
        if (interaction.isEnding(v)) {
            renderComplete()
        } else {
            this.render(v)
            this._vision = v
        }
    }

    private fun renderError(t: Throwable) {
        Log.e(this::class.java.simpleName, "Vision subscription error", t)
    }

    private fun renderComplete() {
        dismiss()
    }

    override fun onStop() {
        visionDisposable?.dispose()
        erase()
        super.onStop()
    }

    override fun onDismiss(dialog: DialogInterface) {
        closeAction?.let {
            interaction.sendAction(it.invoke())
        }
        super.onDismiss(dialog)
    }

    companion object {
        private const val EDGE_INTERACTION_ARGS_KEY = "edge-interaction"
    }
}