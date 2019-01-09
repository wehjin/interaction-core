package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.indexrebellion.R
import kotlinx.android.synthetic.main.view_corrections_body.view.*

class CorrectionBodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCorrection(correction: Correction) {
        with(itemView) {
            when (correction) {
                is Correction.Hold -> {
                    correctionHeadingTextView.text = correction.assetSymbol.toString()
                    correctionCircleView.setBackgroundResource(R.drawable.ic_check_circle_black_24dp)
                    correctionActionButton.text = context.getString(R.string.hold)
                    if (correction.weight == 0.0) {
                        leftSpace.setWeight(1f)
                        leftWing.setWeight(0f)
                        rightWing.setWeight(0f)
                        rightSpecial.setWeight(0f)
                        rightSpace.setWeight(1f)
                    } else {

                    }
                }
                is Correction.Buy -> {
                    correctionHeadingTextView.text = correction.assetSymbol.toString()
                    correctionCircleView.setBackgroundResource(R.drawable.ic_add_circle_black_24dp)
                    correctionActionButton.text = context.getString(R.string.buy)
                }
                is Correction.Sell -> {
                    correctionHeadingTextView.text = correction.assetSymbol.toString()
                    correctionCircleView.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp)
                    correctionActionButton.text = context.getString(R.string.sell)
                }
            }
        }
    }

    private fun View.setWeight(weight: Float) {
        val params = layoutParams as LinearLayout.LayoutParams
        params.weight = weight
        layoutParams = params
    }
}