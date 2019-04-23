package com.rubyhuntersky.interaction.core

import org.junit.Test

class SubjectQuantumTest {

    @Test
    fun sendReportUpdatesReportStream() {
        data class SendReport(val report: Int)

        val quantum = object : SubjectQuantum<Unit, SendReport, Int>() {
            override fun sendAction(action: SendReport) = sendReport(action.report)
        }

        quantum.reportStream.test()
            .also { quantum.sendAction(SendReport(27)) }
            .assertValue(27)
    }

    @Test
    fun setVisionUpdateVisionStream() {
        data class SetVision(val vision: Int)

        val quantum = object : SubjectQuantum<Int, SetVision, Unit>(startVision = 1) {
            override fun sendAction(action: SetVision) = setVision(action.vision)
        }

        quantum.visionStream.test()
            .also { quantum.sendAction(SetVision(2)) }
            .assertValues(1, 2)
    }
}