package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.Icon
import com.rubyhuntersky.vx.Input
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.view_input.view.*

class BackingViewInputLayout
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), ViewBackedDashView.BackingView {

    private var layout: TextInputLayout

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_input, this, false)
        addView(
            view,
            ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        )
        layout = view as TextInputLayout
    }

    fun render(content: Input) {
        Log.d(this.tag.toString(), "render $content")
        layout.hint = content.label

        renderEditTextHint(editText.hasFocus(), content)
        editText.setOnFocusChangeListener { _, hasFocus ->
            renderEditTextHint(hasFocus, content)
        }
        if (editText.text.toString() != content.text) {
            editText.setText(content.text)
        }
        val drawable = (content.icon as? Icon.ResId)?.let {
            resources.getDrawable(it.resId, context.theme)
        }
        editText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    private fun renderEditTextHint(hasFocus: Boolean, content: Input) {
        if (hasFocus) {
            editText.hint = content.originalText
        } else {
            editText.hint = null
        }
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

    private val editText by lazy { layout.inputEditText }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttached?.invoke()
    }

    override fun onDetachedFromWindow() {
        onDetached?.invoke()
        super.onDetachedFromWindow()
    }
}