package com.rubyhuntersky.indexrebellion.common

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.interaction.interactions.common.Interaction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class InteractionBottomSheetDialogFragment<V : Any, A : Any>(
    @LayoutRes private val layoutRes: Int,
    private val interaction: Interaction<V, A>
) : BottomSheetDialogFragment() {

    private var visionDisposable: Disposable? = null
    private var _vision: V? = null
    protected val renderedVision get() = _vision

    protected abstract fun render(vision: V)

    protected fun sendAction(action: A) {
        Log.d(this.javaClass.simpleName, "ACTION: $action")
        interaction.onAction(action)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutRes, container, false)

    override fun onStart() {
        super.onStart()
        visionDisposable = interaction.visionStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { v ->
                    Log.d(this.javaClass.simpleName, "VISION: $v")
                    this.render(v)
                    this._vision = v
                },
                { t ->
                    Log.e(this::class.java.simpleName, "Vision subscription error", t)
                }
            )
    }

    override fun onStop() {
        visionDisposable?.dispose()
        super.onStop()
    }
}