package com.rubyhuntersky.interaction.core.app.main

import com.rubyhuntersky.interaction.core.app.R
import com.rubyhuntersky.interaction.core.app.common.NamedInteractionActivity
import kotlinx.android.synthetic.main.view_main_idle.*

class MainActivity : NamedInteractionActivity<MainVision, MainAction>() {

    override val name: String = MainInteraction.TAG

    override fun renderVision(vision: MainVision) {
        when (vision) {
            is MainVision.Message -> {
                updateContentView(R.id.main_idle, R.layout.view_main_idle)
                selectionTextView.text = vision.message
                selectButton.setOnClickListener { interaction.sendAction(MainAction.Select) }
            }
        }
    }
}
