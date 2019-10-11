package com.rubyhuntersky.interaction.android

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View

fun AppCompatActivity.updateContentView(@IdRes id: Int, @LayoutRes layout: Int) {
    if (findViewById<View>(id) == null) {
        setContentView(layout)
    }
}

