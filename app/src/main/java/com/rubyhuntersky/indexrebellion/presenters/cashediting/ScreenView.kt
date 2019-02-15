package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.additions.toSizeAnchor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject


class ScreenView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), ViewHost {

    private val widthBehavior: BehaviorSubject<Pair<Int, Int>> = BehaviorSubject.create()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val leftDip = toDip(left)
        val rightDip = toDip(left + w)
        widthBehavior.onNext(Pair(leftDip, rightDip))
    }

    val horizontalBound: Observable<Pair<Int, Int>>
        get() = widthBehavior.distinctUntilChanged()

    override fun addTextLine(id: ViewId): DashView<TextLine, Nothing> {
        return object : DashView<TextLine, Nothing> {

            private val textView = (findViewWithTag(id)
                ?: DashViewTextView(context)
                    .also {
                        it.tag = id
                        addView(it, LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT))
                    })

            init {
                val composite = CompositeDisposable()
                textView.onAttached = {
                    Observable.combineLatest(
                        latitudes.map { it.height },
                        anchorBehavior.distinctUntilChanged(),
                        toSizeAnchor
                    ).subscribe {
                        Log.d("DashViewTextView", "onSizeAnchor $it $tag")
                        val limit = it.anchor.toBounds(it.size)
                        Log.d("DashViewTextView", "    limit $limit")
                        val layoutParams = textView.layoutParams as FrameLayout.LayoutParams
                        layoutParams.topMargin = toPixels(limit.first).toInt()
                        textView.layoutParams = layoutParams
                    }.addTo(composite)
                }
                textView.onDetached = {
                    composite.clear()
                }
            }

            override fun setLimit(limit: Dash.Limit) {
                Log.d("DashViewTextView", "Set limit $limit")
                val leftMargin = toPixels(limit.start).toInt()
                val layoutParams = textView.layoutParams as FrameLayout.LayoutParams
                layoutParams.marginStart = leftMargin
                layoutParams.width = toPixels(limit.end - limit.start).toInt()
                textView.layoutParams = layoutParams
            }

            override val latitudes: Observable<Dash.Latitude>
                get() = textView.heights.map { Dash.Latitude(it) }

            private val anchorBehavior = BehaviorSubject.create<Anchor>()
            override fun setAnchor(anchor: Anchor) {
                Log.d("DashViewTextView", "Set anchor $anchor")
                anchorBehavior.onNext(anchor)
            }

            override fun setContent(content: TextLine) {
                Log.d("DashViewTextView", "Set content $content")
                when (content.style) {
                    TextStyle.Highlight5 -> textView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                    TextStyle.Highlight6 -> textView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
                    TextStyle.Subtitle1 -> textView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1)
                }
                textView.text = content.text
                textView.setBackgroundColor(Color.GREEN)
            }

            override val events: Observable<Nothing> get() = Observable.never()
        }
    }
}


private fun View.toPixels(dip: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), resources.displayMetrics)
}

private fun View.toDip(px: Int): Int {
    return Math.round(px / (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

class DashViewTextView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : TextView(context, attrs, defStyleAttr, defStyleRes) {

    val heights: Observable<Int>
        get() = heightBehavior.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())

    private val heightBehavior = BehaviorSubject.create<Int>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(this.javaClass.simpleName, "onSizeChanged $w $h $tag")
        heightBehavior.onNext(toDip(h))
    }

    var onAttached: (() -> Unit)? = null
    var onDetached: (() -> Unit)? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttached?.invoke()
    }

    override fun onDetachedFromWindow() {
        onDetached?.invoke()
        super.onDetachedFromWindow()
    }
}