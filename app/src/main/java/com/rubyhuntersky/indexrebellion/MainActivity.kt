package com.rubyhuntersky.indexrebellion

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.interactions.main.MainInteraction
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

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
                    }
                }
            }
            .addTo(disposable)
        super.onStart()
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
