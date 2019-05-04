package com.rubyhuntersky.interaction.core.app.common

import android.app.Application
import com.rubyhuntersky.interaction.core.app.main.MainInteraction
import com.rubyhuntersky.interaction.core.app.main.MainActivity

@Suppress("unused")
class MyApplication : Application() {

    private val androidEdge = AndroidEdge()

    override fun onCreate() {
        super.onCreate()
        androidEdge.addInteraction(MainInteraction())
        MainActivity.edge = androidEdge
    }
}