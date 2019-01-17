package com.rubyhuntersky.indexrebellion.common

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.interaction.interactions.common.Interaction
import io.reactivex.disposables.Disposable

abstract class InteractionBottomSheetDialogFragment<V, A>(
    @LayoutRes private val layoutRes: Int,
    private val interaction: Interaction<V, A>
) :
    BottomSheetDialogFragment() {

    private var visionDisposable: Disposable? = null

    protected abstract fun render(vision: V)
    protected fun sendAction(action: A) = interaction.onAction(action)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutRes, container, false)

    override fun onStart() {
        super.onStart()
        visionDisposable = interaction.visionStream.subscribe(this::render) { t ->
            Log.e(this::class.java.simpleName, "Vision subscription error", t)
        }
    }

    override fun onStop() {
        visionDisposable?.dispose()
        super.onStop()
    }
}