package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.dashes.Input
import com.rubyhuntersky.vx.dashes.InputEvent
import com.rubyhuntersky.vx.dashes.TextLine
import com.rubyhuntersky.vx.dashes.TextStyle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject


fun View.toPixels(dip: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), resources.displayMetrics)
}

fun View.toDip(px: Int): Int {
    return Math.round(px / (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}


class ScreenView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), ViewHost {

    fun <C : Any, E : Any> setContentView(dashView: Dash.View<C, E>) {
        this.contentDashView = dashView
        beginHBoundUpdatesIfAttachedToWindow()
    }

    private var contentDashView: DashView<*, *>? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        beginHBoundUpdatesIfAttachedToWindow()
    }

    private fun beginHBoundUpdatesIfAttachedToWindow() {
        composite.clear()
        if (isAttachedToWindow) {
            contentDashView?.let { dashView ->
                composite.clear()
                dashView.setAnchor(Anchor(0, 0f))
                hboundBehavior.distinctUntilChanged().subscribe {
                    dashView.setHBound(it.startZero())
                }.addTo(composite)
            }
        }
    }

    private val composite = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()

    override fun onDetachedFromWindow() {
        composite.clear()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hboundBehavior.onNext(HBound(toDip(left), toDip(left + w)))
    }

    override fun addInput(id: ViewId): Dash.View<Input, InputEvent> = ViewBackedDashView(
        frameLayout = this@ScreenView,
        id = id,
        adapter = object : ViewBackedDashView.Adapter<BackingViewInputLayout, Input, InputEvent> {
            override fun buildView(context: Context): BackingViewInputLayout = BackingViewInputLayout(context, null)
            override fun renderView(view: BackingViewInputLayout, content: Input) {
                view.render(content)
            }
        }
    )

    override fun addTextLine(id: ViewId): DashView<TextLine, Nothing> = ViewBackedDashView(
        frameLayout = this@ScreenView,
        id = id,
        adapter = object : ViewBackedDashView.Adapter<BackingViewTextView, TextLine, Nothing> {
            override fun buildView(context: Context): BackingViewTextView = BackingViewTextView(context)
            override fun renderView(view: BackingViewTextView, content: TextLine) {
                when (content.style) {
                    TextStyle.Highlight5 -> view.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                    TextStyle.Highlight6 -> view.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
                    TextStyle.Subtitle1 -> view.setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1)
                }
                view.text = content.text
            }
        })
}

