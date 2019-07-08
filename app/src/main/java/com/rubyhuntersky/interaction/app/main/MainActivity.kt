package com.rubyhuntersky.interaction.app.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.updateContentView
import com.rubyhuntersky.interaction.app.R
import com.rubyhuntersky.interaction.core.Edge
import kotlinx.android.synthetic.main.view_main_idle.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ActivityInteraction(MainStory.groupId, this, this::renderVision))
    }

    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) = when (vision) {
        is Vision.Message -> {
            updateContentView(R.id.main_idle, R.layout.view_main_idle)
            selectionTextView.text = vision.message
            selectButton.setOnClickListener { sendAction(Action.Select) }
        }
    }
}

