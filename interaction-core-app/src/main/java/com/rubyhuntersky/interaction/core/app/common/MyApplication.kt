package com.rubyhuntersky.interaction.core.app.common

import android.app.Application
import com.rubyhuntersky.interaction.core.app.main.MainStory
import com.rubyhuntersky.interaction.core.app.main.MainActivity

@Suppress("unused")
class MyApplication : Application() {

    private val androidEdge = AndroidEdge()

    override fun onCreate() {
        super.onCreate()
        androidEdge.addStory(MainStory())
        MainActivity.edge = androidEdge
    }
}