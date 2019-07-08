package com.rubyhuntersky.interaction.app.select

import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.rubyhuntersky.interaction.android.KeyedRendererBottomSheetDialogFragment
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.android.RenderResult
import com.rubyhuntersky.interaction.android.Renderer
import com.rubyhuntersky.interaction.app.R
import com.rubyhuntersky.interaction.core.Interaction
import kotlinx.android.synthetic.main.view_select.view.*
import kotlinx.android.synthetic.main.view_select_item.view.*

class SelectBottomSheetDialogFragment : KeyedRendererBottomSheetDialogFragment<Vision, Action, Unit>(
    closeAction = { Action.SetChoice(null) },
    renderer = object : Renderer<Vision, Action, Unit> {

        override val layoutRes: Int = R.layout.view_select

        override fun start(view: View, sendAction: (Action) -> Unit) = Unit

        override fun update(
            vision: Vision,
            sendAction: (Action) -> Unit,
            view: View,
            data: Unit
        ): RenderResult<Unit> {
            when (vision) {
                is Vision.Options -> updateOptionsVision(vision, view, sendAction)
                is Vision.Choice -> updateChoiceVision(view)
            }
            return super.update(vision, sendAction, view, data)
        }

        private fun updateChoiceVision(view: View) {
            view.selectListView.adapter = null
        }

        private fun updateOptionsVision(vision: Vision.Options, view: View, sendAction: (Action) -> Unit) {
            val options = vision.options
            view.selectListView.adapter = object : BaseAdapter() {
                override fun getCount(): Int = options.size
                override fun getItem(position: Int): String = options[position]
                override fun getItemId(position: Int): Long = position.toLong()
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val outView = convertView ?: LayoutInflater.from(parent.context).inflate(
                        R.layout.view_select_item,
                        parent,
                        false
                    )

                    val item = getItem(position)
                    outView.selectItemTextView.text = item
                    return outView
                }
            }
            view.selectListView.setOnItemClickListener { _, _, position, _ ->
                sendAction(Action.SetChoice(position))
            }
        }
    }
) {
    companion object : ProjectionSource<Vision, Action> {

        override val group: String = SELECT_OPTION

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            val dialog = SelectBottomSheetDialogFragment().also { it.edgeKey = key }
            val tag = "${interaction.group}/key"
            dialog.show(activity.supportFragmentManager, tag)
        }
    }
}