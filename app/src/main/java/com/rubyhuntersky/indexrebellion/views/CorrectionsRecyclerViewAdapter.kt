package com.rubyhuntersky.indexrebellion.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.indexrebellion.R

class CorrectionsRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class CorrectionsViewType { HEADER, FOOTER, BODY, CONNECTOR_SHORT, CONNECTOR_TALL }

    private val corrections = listOf(Correction.Hold(AssetSymbol("TSLA"), 100.0))

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val correctionsViewType = CorrectionsViewType.values()[viewType]
        val layoutRes = when (correctionsViewType) {
            CorrectionsViewType.HEADER -> R.layout.view_corrections_header
            CorrectionsViewType.CONNECTOR_SHORT -> R.layout.view_corrections_connector_short
            CorrectionsViewType.BODY -> R.layout.view_corrections_body
            CorrectionsViewType.CONNECTOR_TALL -> R.layout.view_corrections_connector_tall
            CorrectionsViewType.FOOTER -> R.layout.view_corrections_footer
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {}
}