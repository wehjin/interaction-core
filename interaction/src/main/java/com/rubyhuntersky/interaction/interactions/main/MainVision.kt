package com.rubyhuntersky.interaction.interactions.main

import com.rubyhuntersky.data.report.RebellionReport

sealed class MainVision {
    object Loading : MainVision()
    data class Viewing(val rebellionReport: RebellionReport) : MainVision()
}