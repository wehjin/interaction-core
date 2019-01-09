package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.toStatString
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.SharedConstituentSearchInteraction
import com.rubyhuntersky.indexrebellion.views.CorrectionsRecyclerViewAdapter
import com.rubyhuntersky.indexrebellion.views.StatisticView
import com.rubyhuntersky.interaction.InteractionCatalyst
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.interactions.constituentsearch.ConstituentSearchAction
import com.rubyhuntersky.interaction.interactions.main.MainInteraction
import com.rubyhuntersky.interaction.interactions.main.MainVision
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onStart() {
        mainActivity = this
        mainInteraction.visionStream
            .subscribe {
                when (it) {
                    is MainVision.Loading -> {
                        setContentView(
                            R.id.mainLoading,
                            R.layout.activity_main_loading
                        )
                    }
                    is MainVision.Viewing -> {
                        setContentView(
                            R.id.mainViewing,
                            R.layout.activity_main_viewing
                        )
                        setSupportActionBar(toolbar)
                        renderViewing(it)
                    }
                }
            }
            .addTo(disposable)
        super.onStart()
    }

    private fun renderViewing(viewing: MainVision.Viewing) {
        supportActionBar!!.title = getString(R.string.funding)
        val report = viewing.rebellionReport
        renderNewInvestment(report.newInvestment)
        currentInvestmentStatisticView.setText(report.currentInvestment)
        goalInvestmentStatisticView.setText(report.fullInvestment)
        renderCorrectionsView()
    }

    private fun renderCorrectionsView() {
        with(correctionsRecyclerView) {
            if (layoutManager == null) {
                layoutManager = object : LinearLayoutManager(context) {}
            }
            if (adapter == null) {
                adapter = CorrectionsRecyclerViewAdapter(mainInteraction)
            }
        }
    }

    private fun StatisticView.setText(cashEquivalent: CashEquivalent) {
        val invested = cashEquivalent.toDouble()
        text = invested?.toStatString()?.let(this@MainActivity::addDollarToStatString)
                ?: getString(R.string.unknown_quantity)
    }

    private fun renderNewInvestment(newInvestment: CashAmount) {
        val deposit = newInvestment.toDouble()
        with(newInvestmentStatisticView) {
            labelText = if (deposit >= 0) getString(R.string.deposit) else getString(
                R.string.withdrawal
            )
            text = addDollarToStatString(deposit.toStatString())
        }
        with(newInvestmentOperatorTextView) {
            text = if (deposit >= 0) getString(R.string.plus) else getString(R.string.minus)
        }
    }

    private fun addDollarToStatString(statString: String): String = if (statString.length == 1) {
        getString(R.string.dollar_space_format, statString)
    } else {
        getString(R.string.dollar_format, statString)
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
            constituentSearchCatalyst = object : InteractionCatalyst {
                override fun catalyze() {
                    SharedConstituentSearchInteraction.update(ConstituentSearchAction.Clear)
                    mainActivity?.supportFragmentManager?.let {
                        ConstituentSearchDialogFragment.newInstance().show(it, "constituent_search")
                    }
                }
            }
        )
    }
}
