package com.rubyhuntersky.interaction.interactions.main

import com.rubyhuntersky.data.report.Correction

sealed class MainAction {
    object FindConstituent : MainAction()
    object OpenCashEditor : MainAction()
    data class OpenCorrectionDetails(val correction: Correction) : MainAction()
}