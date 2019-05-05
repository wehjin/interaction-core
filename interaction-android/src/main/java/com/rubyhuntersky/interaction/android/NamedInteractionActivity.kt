package com.rubyhuntersky.interaction.android

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionSearch
import io.reactivex.disposables.Disposable

abstract class NamedInteractionActivity<V : Any, A : Any> : AppCompatActivity() {

    protected abstract val name: String

    protected abstract fun renderVision(vision: V)

    protected val interaction: Interaction<V, A> by lazy {
        AndroidEdge.findInteraction<V, A>(InteractionSearch.ByName(name))
    }

    override fun onStart() {
        super.onStart()
        AndroidEdge.setActivity(this)
        visions = interaction.visionStream
            .doOnNext {
                if (interaction.isTailVision(it)) {
                    finish()
                } else {
                    renderVision(it)
                }
            }
            .doOnComplete(this::finish)
            .subscribe()
    }

    private var visions: Disposable? = null

    override fun onStop() {
        visions?.dispose()
        AndroidEdge.unsetActivity(this)
        super.onStop()
    }

    protected fun updateContentView(@IdRes id: Int, @LayoutRes layout: Int) {
        if (findViewById<View>(id) == null) {
            setContentView(layout)
        }
    }
}