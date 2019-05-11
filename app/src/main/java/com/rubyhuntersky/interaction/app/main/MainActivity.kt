package com.rubyhuntersky.interaction.app.main

import com.rubyhuntersky.interaction.android.NamedInteractionActivity
import com.rubyhuntersky.interaction.app.R
import kotlinx.android.synthetic.main.view_main_idle.*

class MainActivity : NamedInteractionActivity<Vision, Action>() {

    override val name: String = MainStory.TAG

    override fun renderVision(vision: Vision) {
        when (vision) {
            is Vision.Message -> {
                updateContentView(R.id.main_idle, R.layout.view_main_idle)
                selectionTextView.text = vision.message
                selectButton.setOnClickListener { interaction.sendAction(Action.Select) }
            }
        }
    }
}
