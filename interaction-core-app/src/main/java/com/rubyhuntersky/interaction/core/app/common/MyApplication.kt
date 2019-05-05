package com.rubyhuntersky.interaction.core.app.common

import android.app.Application
import com.rubyhuntersky.interaction.core.SwitchWell
import com.rubyhuntersky.interaction.core.app.main.MainInteraction
import com.rubyhuntersky.interaction.core.app.select.SelectProjectionBuilder

@Suppress("unused")
class MyApplication : Application() {

    private val well = SwitchWell()

    override fun onCreate() {
        super.onCreate()
        AndroidEdge.addProjectionBuilder(
            SelectProjectionBuilder()
        )
        AndroidEdge.addInteraction(
            MainInteraction(well, AndroidEdge)
        )
    }
}


