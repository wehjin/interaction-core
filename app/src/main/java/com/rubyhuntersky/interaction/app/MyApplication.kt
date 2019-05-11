package com.rubyhuntersky.interaction.app

import android.app.Application
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.app.main.MainStory
import com.rubyhuntersky.interaction.app.select.SelectOptionProjectionSource
import com.rubyhuntersky.interaction.app.select.SelectOptionStory
import com.rubyhuntersky.interaction.core.SwitchWell

@Suppress("unused")
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidEdge += SelectOptionProjectionSource
        AndroidEdge += MainStory(well)
    }

    companion object {

        private val well = SwitchWell()

        fun selectionInteraction(vararg options: String) =
            SelectOptionStory(well, *options).also { AndroidEdge.presentInteraction(it) }
    }
}


