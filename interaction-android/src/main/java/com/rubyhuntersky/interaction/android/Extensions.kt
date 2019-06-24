package com.rubyhuntersky.interaction.android

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View

fun AppCompatActivity.updateContentView(@IdRes id: Int, @LayoutRes layout: Int) {
    if (findViewById<View>(id) == null) {
        setContentView(layout)
    }
}

