package com.rubyhuntersky.interaction.app.alt

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.android.updateContentView
import com.rubyhuntersky.interaction.app.R
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import kotlinx.android.synthetic.main.view_alt_idle.*

class AltActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ActivityInteraction(AltStory.groupId, this, this::renderVision))
    }

    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) = when (vision) {
        is Vision.Idle -> {
            updateContentView(R.id.alt_idle, R.layout.view_alt_idle)
            textView.text = "Alt is Idle"
        }
        is Vision.Viewing -> {
            updateContentView(R.id.alt_idle, R.layout.view_alt_idle)
            textView.text = "Alt: ${vision.text}"
        }
    }

    companion object : ProjectionSource<Vision, Action> {

        override val group: String = AltStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            val intent = Intent(activity, AltActivity::class.java)
                .also { ActivityInteraction.setInteractionSearchKey(it, key) }
            activity.startActivity(intent)
        }
    }
}