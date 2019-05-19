package com.rubyhuntersky.interaction.app

import android.app.Application
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.app.main.MainStory
import com.rubyhuntersky.interaction.app.select.SelectOptionProjectionSource

@Suppress("unused")
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidEdge += MainStory()
        AndroidEdge += SelectOptionProjectionSource
    }
}


