package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.content.Context
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

    override fun addTextLine(id: ViewId): DashView<TextLine, Nothing> = findViewWithTag<DashViewTextView>(id)
        ?: DashViewTextView(context)
            .also {
                it.tag = id
                addView(it, LayoutParams(320, ViewGroup.LayoutParams.WRAP_CONTENT))
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
) : TextView(context, attrs, defStyleAttr, defStyleRes), Dash.View<TextLine, Nothing> {

    override fun setLimit(limit: Dash.Limit) {
        Log.d(this.javaClass.simpleName, "Set limit $limit")
//        left = toPixels(limit.start).toInt()
//        right = toPixels(limit.end).toInt()
    }

    private val latitudeBehavior = BehaviorSubject.create<Dash.Latitude>()
    override val latitudes: Observable<Dash.Latitude>
        get() {
            return latitudeBehavior.distinctUntilChanged()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(this.javaClass.simpleName, "onSizeChanged $w $h $tag")
        latitudeBehavior.onNext(Dash.Latitude(toDip(h)))
    }

    private val anchorBehavior = BehaviorSubject.create<Anchor>()
    override fun setAnchor(anchor: Anchor) {
        Log.d(this.javaClass.simpleName, "Set anchor $anchor")
        anchorBehavior.onNext(anchor)
    }

    private val composite = CompositeDisposable()
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Observable.combineLatest(latitudes.map { it.height }, anchorBehavior.distinctUntilChanged(), toSizeAnchor)
            .subscribe {
                Log.d(this.javaClass.simpleName, "onSizeAnchor $it $tag")
                val limit = it.anchor.toBounds(it.size)
                Log.d(this.javaClass.simpleName, "    limit $limit")
                val pxTop = toPixels(limit.first)
                (this.layoutParams as FrameLayout.LayoutParams).setMargins(left, pxTop.toInt(), 0, 0)
            }
            .addTo(composite)
    }

    override fun onDetachedFromWindow() {
        composite.clear()
        super.onDetachedFromWindow()
    }

    override fun setContent(content: TextLine) {
        Log.d(this.javaClass.simpleName, "Set content $content")
        when (content.style) {
            TextStyle.Highlight5 -> setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
            TextStyle.Highlight6 -> setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
            TextStyle.Subtitle1 -> setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1)
        }
        text = content.text
        requestLayout()
    }

    override val events: Observable<Nothing> get() = Observable.never()
}