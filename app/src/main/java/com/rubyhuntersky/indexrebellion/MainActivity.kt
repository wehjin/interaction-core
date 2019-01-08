package com.rubyhuntersky.indexrebellion

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.toStatString
import com.rubyhuntersky.indexrebellion.views.StatisticView
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.interactions.main.MainInteraction
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*

class MainActivity : AppCompatActivity() {

    object MemoryRebellionBook : RebellionBook {
        private val rebellionSubject: BehaviorSubject<Rebellion> = BehaviorSubject.createDefault(Rebellion.SEED)
        private val rebellionWriter = rebellionSubject.toSerialized()

        override val reader: Observable<Rebellion> get() = rebellionSubject.distinctUntilChanged()
        override fun write(rebellion: Rebellion) = rebellionWriter.onNext(rebellion)
    }

    private val disposable = CompositeDisposable()

    override fun onStart() {
        interaction.state
            .subscribe {
                when (it) {
                    is MainInteraction.State.Loading -> {
                        setContentView(R.id.mainLoading, R.layout.activity_main_loading)
                    }
                    is MainInteraction.State.Viewing -> {
                        setContentView(R.id.mainViewing, R.layout.activity_main_viewing)
                        setSupportActionBar(toolbar)
                        renderViewing(it)
                    }
                }
            }
            .addTo(disposable)
        super.onStart()
    }

    private fun renderViewing(viewing: MainInteraction.State.Viewing) {
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
                adapter = CorrectionsRecyclerViewAdapter()
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
            labelText = if (deposit >= 0) getString(R.string.deposit) else getString(R.string.withdrawal)
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
        super.onStop()
    }

    private fun setContentView(@IdRes viewId: Int, @LayoutRes layoutId: Int) {
        if (findViewById<View>(viewId) == null) {
            setContentView(layoutId)
        }
    }

    companion object {

        private val interaction = MainInteraction(MemoryRebellionBook)
    }
}
