package com.rubyhuntersky.indexrebellion

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Space

class CorrectionsRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class CorrectionsViewType { HEADER, FOOTER, CORRECTION }

    private val headerPosition = 0
    private val footerPosition = 1

    override fun getItemCount(): Int = footerPosition + 1

    override fun getItemViewType(position: Int): Int {
        val viewType = when (position) {
            headerPosition -> CorrectionsViewType.HEADER
            footerPosition -> CorrectionsViewType.FOOTER
            else -> CorrectionsViewType.CORRECTION
        }
        return viewType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val correctionsViewType = CorrectionsViewType.values()[viewType]
        val view = when (correctionsViewType) {
            CorrectionsViewType.HEADER -> LayoutInflater.from(parent.context).inflate(
                R.layout.view_corrections_header,
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