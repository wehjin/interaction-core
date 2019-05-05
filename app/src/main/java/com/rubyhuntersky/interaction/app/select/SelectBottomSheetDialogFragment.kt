package com.rubyhuntersky.interaction.app.select

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.rubyhuntersky.interaction.app.R
import com.rubyhuntersky.interaction.android.KeyedRendererBottomSheetDialogFragment
import com.rubyhuntersky.interaction.android.RenderResult
import com.rubyhuntersky.interaction.android.Renderer
import kotlinx.android.synthetic.main.view_select.view.*
import kotlinx.android.synthetic.main.view_select_item.view.*

class SelectBottomSheetDialogFragment : KeyedRendererBottomSheetDialogFragment<SelectVision, SelectAction, Unit>(
    closeAction = { SelectAction.SetChoice(null) },
    renderer = object : Renderer<SelectVision, SelectAction, Unit> {

        override val layoutRes: Int = R.layout.view_select

        override fun start(view: View, sendAction: (SelectAction) -> Unit) = Unit

        override fun update(
            vision: SelectVision,
            sendAction: (SelectAction) -> Unit,
            view: View,
            data: Unit
        ): RenderResult<Unit> {
            when (vision) {
                is SelectVision.Options -> updateOptionsVision(vision, view, sendAction)
                is SelectVision.Choice -> updateChoiceVision(view)
            }
            return super.update(vision, sendAction, view, data)
        }

        private fun updateChoiceVision(view: View) {
            view.selectListView.adapter = null
        }

        private fun updateOptionsVision(vision: SelectVision.Options, view: View, sendAction: (SelectAction) -> Unit) {
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
                sendAction(SelectAction.SetChoice(position))
            }
        }
    }
)