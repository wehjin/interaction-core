package com.rubyhuntersky.interaction.core.app.common

import android.app.Application
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.SwitchWell
import com.rubyhuntersky.interaction.core.app.R
import com.rubyhuntersky.interaction.core.app.main.*
import kotlinx.android.synthetic.main.view_select.view.*
import kotlinx.android.synthetic.main.view_select_item.view.*

@Suppress("unused")
class MyApplication : Application() {

    private val well = SwitchWell()

    override fun onCreate() {
        super.onCreate()
        androidEdge.addProjectionBuilder(SelectProjectionBuilder())
        androidEdge.addInteraction(MainInteraction(well, androidEdge))
        MainActivity.edge = androidEdge
    }

    companion object {
        val androidEdge = AndroidEdge()
    }
}


class SelectProjectionBuilder : ProjectionBuilder {
    override val name: String = SelectInteraction.TAG

    override fun <V, A> startProjection(fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long) {
        val dialog = SelectBottomSheetDialogFragment().also { it.edgeKey = key }
        val tag = "${interaction.name}/key"
        dialog.show(fragmentActivity.supportFragmentManager, tag)
    }
}

class SelectBottomSheetDialogFragment : RendererBottomSheetDialogFragment<SelectVision, SelectAction, Unit>(

    object : Renderer<SelectVision, SelectAction, Unit> {
        override val layoutRes: Int = R.layout.view_select
        override fun start(view: View, sendAction: (SelectAction) -> Unit) = Unit
        override fun update(
            vision: SelectVision,
            sendAction: (SelectAction) -> Unit,
            view: View,
            data: Unit
        ): RenderResult<Unit> {
            when (vision) {
                is SelectVision.Options -> {
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
                    view.selectListView.setOnItemClickListener { parent, view, position, id ->
                        sendAction(SelectAction.SetChoice(position))
                    }
                }
                is SelectVision.Choice -> {
                    view.selectListView.adapter = null
                }
            }
            return super.update(vision, sendAction, view, data)
        }
    },
    MyApplication.androidEdge,
    closeAction = { SelectAction.SetChoice(null) }
)
