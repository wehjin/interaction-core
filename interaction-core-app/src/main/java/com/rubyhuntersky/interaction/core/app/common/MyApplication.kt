package com.rubyhuntersky.interaction.core.app.common

import android.app.Application
import com.rubyhuntersky.interaction.core.SwitchWell
import com.rubyhuntersky.interaction.core.app.main.MainActivity
import com.rubyhuntersky.interaction.core.app.main.MainInteraction

@Suppress("unused")
class MyApplication : Application() {

    private val edge = AndroidEdge()
    private val well = SwitchWell()

    override fun onCreate() {
        super.onCreate()
        edge.addInteraction(MainInteraction(well, edge))
        MainActivity.edge = edge
    }
}