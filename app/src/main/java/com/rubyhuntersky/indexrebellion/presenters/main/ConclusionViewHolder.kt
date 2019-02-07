package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.rubyhuntersky.data.report.RebellionReport

class ConclusionViewHolder(private val conclusionView: RecyclerView) : RecyclerView.ViewHolder(conclusionView) {

    fun render(
        conclusion: RebellionReport.Conclusion,
        onAddConstituentClick: () -> Unit,
        onCorrectionDetailsClick: () -> Unit
    ) {
        with(conclusionView) {
            if (layoutManager == null) {
                layoutManager = object : LinearLayoutManager(context) {}
            }

            val adapter: CorrectionsRecyclerViewAdapter = adapter as? CorrectionsRecyclerViewAdapter
                ?: CorrectionsRecyclerViewAdapter(onAddConstituentClick, onCorrectionDetailsClick)
                    .also { adapter = it }

            Log.d(this.javaClass.simpleName, "conclusion: $conclusion")
            when (conclusion) {
                is RebellionReport.Conclusion.AddConstituent -> adapter.setCorrections(emptyList())
                is RebellionReport.Conclusion.RefreshPrices -> Unit
                is RebellionReport.Conclusion.Divest -> adapter.setCorrections(conclusion.corrections)
                is RebellionReport.Conclusion.Maintain -> adapter.setCorrections(conclusion.corrections)
            }
        }
    }
}