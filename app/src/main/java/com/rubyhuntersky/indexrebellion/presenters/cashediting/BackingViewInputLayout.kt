package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import com.rubyhuntersky.indexrebellion.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject

class BackingViewInputLayout
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextInputLayout(context, attrs, R.style.Widget_MaterialComponents_TextInputLayout_FilledBox),
    ViewBackedDashView.BackingView {

    init {
        val editText = TextInputEditText(context, null, R.style.Widget_MaterialComponents_TextInputEditText_FilledBox)
        editText.setText("Hey")
        addView(
            editText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    override val heights: Observable<Int>
        get() = heightBehavior.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())

    private val heightBehavior = BehaviorSubject.create<Int>()


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val w = right - left
        val h = bottom - top
        Log.d(this.tag.toString(), "onSizeChanged w x h: $w x $h")
        heightBehavior.onNext(toDip(h))
    }

    override var onAttached: (() -> Unit)? = null
    override var onDetached: (() -> Unit)? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttached?.invoke()
    }

    override fun onDetachedFromWindow() {
        onDetached?.invoke()
        super.onDetachedFromWindow()
    }
}