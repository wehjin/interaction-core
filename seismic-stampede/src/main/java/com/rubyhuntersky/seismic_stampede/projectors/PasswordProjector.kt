package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.KeyMaster
import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.display.printLine
import com.rubyhuntersky.seismic_stampede.plots.PasswordPlot.Action
import com.rubyhuntersky.seismic_stampede.plots.PasswordPlot.Vision
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus

object PasswordProjector : Projector<Vision, Action> {
    override fun render(vision: Vision, offer: (Action) -> Boolean) = renderVision(vision, offer)
}

private fun renderVision(vision: Vision, offer: (Action) -> Boolean): RenderStatus {
    return when (vision) {
        is Vision.BuildPassword -> renderBuildPassword(vision, offer)
        is Vision.Ended -> RenderStatus.Stop
    }
}

private fun renderBuildPassword(
    vision: Vision.BuildPassword,
    offer: (Action) -> Boolean
): RenderStatus {
    val password = getConfirmedCheckedPassword(vision.checker)
    if (password == null) {
        offer(Action.Cancel)
    } else {
        val passwordId = KeyMaster.setPassword(password)
        offer(Action.SetPassword(passwordId))
    }
    return RenderStatus.Repeat
}

private fun getConfirmedCheckedPassword(isValid: (CharArray) -> String?): CharArray? {
    var confirmedPassword: CharArray? = null
    var repeat = true
    while (repeat) {
        val password = getCheckedPassword(isValid)
        if (password == null) {
            repeat = false
        } else {
            val confirmation = Display.awaitPassword(prompt = "Confirm  password")
            if (confirmation == null) {
                repeat = false
            } else {
                if (confirmation.contentEquals(password)) {
                    confirmedPassword = password
                    repeat = false
                } else {
                    Display.printLine("Password values differ.")
                }
            }
        }
    }
    return confirmedPassword
}

private fun getCheckedPassword(isValid: (CharArray) -> String?): CharArray? {
    var password: CharArray? = null
    var repeat = true
    while (repeat) {
        val candidate = Display.awaitPassword(prompt = "Enter password")
        if (candidate == null) {
            repeat = false
        } else {
            val errorMessage = isValid(candidate)
            if (errorMessage == null) {
                password = candidate
                repeat = false
            } else {
                Display.printLine(errorMessage)
            }
        }
    }
    return password
}

