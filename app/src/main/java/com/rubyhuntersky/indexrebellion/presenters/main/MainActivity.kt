package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchCatalyst
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsCatalyst
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.main.Action
import com.rubyhuntersky.interaction.main.MainInteraction
import com.rubyhuntersky.interaction.main.Vision
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onStart() {
        mainActivity = this
        mainInteraction.visionStream
            .subscribe {
                Log.d(this.javaClass.simpleName, "VISION: $it")
                when (it) {
                    is Vision.Loading -> {
                        setContentView(R.id.mainLoading, R.layout.activity_main_loading)
                    }
                    is Vision.Viewing -> {
                        setContentView(R.id.mainViewing, R.layout.activity_main_viewing)
                        setSupportActionBar(toolbar)
                        renderViewing(it)
                    }
                }
            }
            .addTo(disposable)
        super.onStart()
    }

    private fun renderViewing(viewing: Vision.Viewing) {
        val report = viewing.rebellionReport
        supportActionBar!!.title = getString(R.string.funding)
        FundingViewHolder(fundingView)
            .render(report.funding, onNewInvestmentClick = {
                mainInteraction.sendAction(Action.OpenCashEditor)
            })

        ConclusionViewHolder(correctionsRecyclerView)
            .render(
                conclusion = report.conclusion,
                onAddConstituentClick = { mainInteraction.sendAction(Action.FindConstituent) },
                onCorrectionDetailsClick = { mainInteraction.sendAction(Action.OpenCorrectionDetails(it)) }
            )
    }

    override fun onStop() {
        disposable.clear()
        if (mainActivity == this) {
            mainActivity = null
        }
        super.onStop()
    }

    private fun setContentView(@IdRes viewId: Int, @LayoutRes layoutId: Int) {
        if (findViewById<View>(viewId) == null) {
            setContentView(layoutId)
        }
    }

    companion object {
        private var mainActivity: MainActivity? = null

        private val mainInteraction = MainInteraction(
            rebellionBook = SharedRebellionBook,
            constituentSearchCatalyst = ConstituentSearchCatalyst { mainActivity!! },
            cashEditingCatalyst = object : Catalyst<Unit> {
                override fun catalyze(seed: Unit) {
                    SharedCashEditingInteraction.reset()
                    mainActivity?.supportFragmentManager?.let {
                        CashEditingDialogFragment.newInstance().show(it, "cash_editing")
                    }
                }
            },
            correctionDetailCatalyst = CorrectionDetailsCatalyst { mainActivity!! }
        )
    }
}
