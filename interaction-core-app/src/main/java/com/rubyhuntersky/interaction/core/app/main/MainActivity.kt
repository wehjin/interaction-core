package com.rubyhuntersky.interaction.core.app.main

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rubyhuntersky.interaction.core.app.R
import com.rubyhuntersky.interaction.core.app.common.AndroidEdge
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_main_idle.*

class MainActivity : AppCompatActivity() {

    private fun render(vision: MainVision) {
        when (vision) {
            is MainVision.Idle -> {
                updateContentView(R.id.main_idle, R.layout.view_main_idle)
                this.selectButton.setOnClickListener {
                    story.update(vision.toSelectAction())
                }
            }
        }
    }

    private lateinit var story: MainStory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        story = MainStory.locateInEdge(edge)
    }

    override fun onStart() {
        super.onStart()
        visions = story.visions
            .doOnNext(this::render)
            .doOnComplete {
                visions?.dispose()
                finish()
            }
            .subscribe()
    }

    private fun updateContentView(@IdRes id: Int, @LayoutRes layout: Int) {
        if (findViewById<View>(id) == null) {
            setContentView(layout)
        }
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
