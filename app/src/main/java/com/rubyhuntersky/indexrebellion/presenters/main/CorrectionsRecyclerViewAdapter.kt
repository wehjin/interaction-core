package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.interaction.interactions.main.MainAction
import com.rubyhuntersky.interaction.interactions.main.MainInteraction
import kotlinx.android.synthetic.main.view_corrections_footer.view.*
import kotlin.math.max

class CorrectionsRecyclerViewAdapter(private val mainInteraction: MainInteraction) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class CorrectionsViewType { HEADER, FOOTER, BODY, CONNECTOR_SHORT, CONNECTOR_TALL }

    private var corrections: List<Correction> = listOf(Correction.Hold(AssetSymbol("TSLA"), 100.0))
    private var correctionsHighWeight: Double = corrections.highWeight

    override fun getItemCount(): Int = 1 + corrections.size * 2 + 2

    override fun getItemViewType(position: Int): Int = getViewType(position).ordinal

    private fun getViewType(position: Int): CorrectionsViewType = if (position % 2 == 1) {
        if ((position + 1) / 2 == 1 + corrections.size) {
            CorrectionsViewType.CONNECTOR_TALL
        } else {
            CorrectionsViewType.CONNECTOR_SHORT
        }
    } else {
        val elementPosition = position / 2
        when {
            elementPosition == 0 -> CorrectionsViewType.HEADER
            elementPosition - 1 < corrections.size -> CorrectionsViewType.BODY
            else -> CorrectionsViewType.FOOTER
        }
    }

    fun setCorrections(value: List<Correction>) {
        corrections = value
        correctionsHighWeight = value.highWeight
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val correctionsViewType = CorrectionsViewType.values()[viewType]
        val layoutRes = when (correctionsViewType) {
            CorrectionsViewType.HEADER -> R.layout.view_corrections_header
            CorrectionsViewType.CONNECTOR_SHORT -> R.layout.view_corrections_connector_short
            CorrectionsViewType.BODY -> R.layout.view_corrections_body
            CorrectionsViewType.CONNECTOR_TALL -> R.layout.view_corrections_connector_tall
            CorrectionsViewType.FOOTER -> R.layout.view_corrections_footer
        }
        val itemView = layoutInflater.inflate(layoutRes, parent, false)
        return when (correctionsViewType) {
            CorrectionsViewType.BODY -> CorrectionBodyViewHolder(itemView)
            else -> object : RecyclerView.ViewHolder(itemView) {}
        }
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getViewType(position)
        when (viewType) {
            CorrectionsViewType.FOOTER -> {
                viewHolder.itemView.plusConstituentButton.setOnClickListener {
                    mainInteraction.onAction(MainAction.FindConstituent)
                }
            }
            CorrectionsViewType.BODY -> {
                val correction = corrections[position / 2 - 1]
                val bodyViewHolder = viewHolder as CorrectionBodyViewHolder
                bodyViewHolder.bindCorrection(correction, correctionsHighWeight)
            }
            CorrectionsViewType.HEADER -> Unit
            CorrectionsViewType.CONNECTOR_SHORT -> Unit
            CorrectionsViewType.CONNECTOR_TALL -> Unit
        }
    }

    private val List<Correction>.highWeight: Double get() = this.map { it.highWeight }.fold(0.0, ::max)
}