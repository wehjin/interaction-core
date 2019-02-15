package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.additions.toSizeAnchor
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class ViewBackedDashView<V, C : Any>(
    frameLayout: FrameLayout,
    id: ViewId,
    private val adapter: Adapter<V, C>
) : DashView<C, Nothing> where V : View, V : ViewBackedDashView.BackingView {

    interface BackingView {
        var onAttached: (() -> Unit)?
        var onDetached: (() -> Unit)?
        val heights: Observable<Int>
    }

    interface Adapter<V, C : Any> where V : View, V : BackingView {
        fun buildView(context: Context): V
        fun renderView(view: V, content: C)
    }

    private val view = (frameLayout.findViewWithTag(id)
        ?: adapter.buildView(frameLayout.context)
            .also {
                it.tag = id
                frameLayout.addView(
                    it,
                    FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                )
            })

    init {
        val composite = CompositeDisposable()
        view.onAttached = {
            Observable.combineLatest(
                latitudes.map { it.height },
                anchorBehavior.distinctUntilChanged(),
                toSizeAnchor
            ).subscribe { sizeAnchor ->
                Log.d(view.tag.toString(), "onSizeAnchor $sizeAnchor")
                setVBound(sizeAnchor.anchor.toVBound(sizeAnchor.size))
            }.addTo(composite)
        }
        view.onDetached = {
            composite.clear()
        }
    }

    private val anchorBehavior = BehaviorSubject.create<Anchor>()

    private fun setVBound(vbound: VBound) {
        Log.d(view.tag.toString(), "Set vbound $vbound")
        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
            .apply {
                topMargin = view.toPixels(vbound.ceiling).toInt()
            }
    }

    override fun setHBound(hbound: HBound) {
        Log.d(view.tag.toString(), "Set hbound $hbound")
        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
            .apply {
                marginStart = view.toPixels(hbound.start).toInt()
                width = view.toPixels(hbound.end - hbound.start).toInt()
            }
    }

    override val latitudes: Observable<Dash.Latitude>
        get() = view.heights.map { Dash.Latitude(it) }

    override fun setAnchor(anchor: Anchor) {
        Log.d(view.tag.toString(), "Set anchor $anchor")
        anchorBehavior.onNext(anchor)
    }

    override fun setContent(content: C) {
        Log.d(view.tag.toString(), "Set content $content")
        adapter.renderView(view, content)
    }

    override val events: Observable<Nothing> get() = Observable.never()
}