package com.rubyhuntersky.indexrebellion.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Space
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.indexrebellion.R

class CorrectionsRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class CorrectionsViewType { HEADER, FOOTER, CORRECTION, CONNECTOR }

    private val corrections = listOf(Correction.Hold(AssetSymbol("TSLA"), 100.0))

    override fun getItemCount(): Int = 1 + corrections.size * 2 + 2

    override fun getItemViewType(position: Int): Int = getViewType(position).ordinal

    private fun getViewType(position: Int): CorrectionsViewType = if (position % 2 == 1) {
        CorrectionsViewType.CONNECTOR
    } else {
        val elementPosition = position / 2
        when {
            elementPosition == 0 -> CorrectionsViewType.HEADER
            elementPosition - 1 < corrections.size -> CorrectionsViewType.CORRECTION
            else -> CorrectionsViewType.FOOTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val correctionsViewType = CorrectionsViewType.values()[viewType]
        val view = when (correctionsViewType) {

            CorrectionsViewType.HEADER -> LayoutInflater.from(parent.context).inflate(
                R.layout.view_corrections_header,
                parent,
                false
            )

            CorrectionsViewType.CONNECTOR -> LayoutInflater.from(parent.context).inflate(
                R.layout.view_corrections_connector,
                parent,
                false
            )

            CorrectionsViewType.FOOTER -> LayoutInflater.from(parent.context).inflate(
                R.layout.view_corrections_footer,
                parent,
                false
            )

            CorrectionsViewType.CORRECTION -> Space(parent.context)

        }
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {}
}