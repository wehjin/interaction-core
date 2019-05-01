package com.rubyhuntersky.interaction.core.app.main

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rubyhuntersky.interaction.core.app.R
import com.rubyhuntersky.interaction.core.app.common.AndroidEdge
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    private fun render(vision: MainVision) {
        when (vision) {
            is MainVision.Idle -> {
                updateContentView(R.id.main_idle, R.layout.view_main_idle)
            }
        }
    }

    private fun updateContentView(@IdRes id: Int, @LayoutRes layout: Int) {
        if (findViewById<View>(id) == null) {
            setContentView(layout)
        }
    }

    override fun onStart() {
        super.onStart()
        visions = MainStory.locateInEdge(edge).visions
            .doOnNext(this::render)
            .doOnComplete {
                visions?.dispose()
                finish()
            }
            .subscribe()
    }

    private var visions: Disposable? = null

    override fun onStop() {
        visions?.dispose()
        super.onStop()
    }

    companion object {
        lateinit var edge: AndroidEdge
    }
}
