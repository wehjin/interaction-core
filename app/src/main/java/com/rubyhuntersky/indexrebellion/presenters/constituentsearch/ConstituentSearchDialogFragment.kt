package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.interactions.constituentsearch.ConstituentSearchAction
import com.rubyhuntersky.interaction.interactions.constituentsearch.ConstituentSearchVision
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_constituent_search.*

class ConstituentSearchDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_constituent_search, container, false)

    private val disposable = CompositeDisposable()

    override fun onStart() {
        super.onStart()

        SharedConstituentSearchInteraction.visionStream.subscribe({ vision ->
            when (vision) {
                is ConstituentSearchVision.Idle -> renderIdle()
                is ConstituentSearchVision.Searching -> renderSearch()
                is ConstituentSearchVision.Done -> renderDone()
            }
        }, { t ->
            Log.e(this.javaClass.simpleName, "onStart", t)
        }).addTo(disposable)
    }

    private fun renderDone() {
        dismiss()
    }

    private fun renderSearch() {}

    private fun renderIdle() {
        saveButton.setOnClickListener {
            SharedConstituentSearchInteraction.update(ConstituentSearchAction.Search("TSLA"))
            SharedConstituentSearchInteraction.update(
                ConstituentSearchAction.Save(AssetSymbol("TSLA"), MarketWeight(50000000000))
            )
        }
    }

    override fun onStop() {
        disposable.clear()
        super.onStop()
    }

    companion object {
        fun newInstance(): ConstituentSearchDialogFragment {
            return ConstituentSearchDialogFragment()
        }
    }
}