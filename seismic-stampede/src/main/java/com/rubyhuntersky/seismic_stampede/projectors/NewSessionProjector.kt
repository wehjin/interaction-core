package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.display.printLine
import com.rubyhuntersky.seismic_stampede.plots.NewSessionPlot.Action
import com.rubyhuntersky.seismic_stampede.plots.NewSessionPlot.Vision
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus

object NewSessionProjector : Projector<Vision, Action> {
    override fun render(vision: Vision, offer: (Action) -> Boolean) = renderVision(vision, offer)
}

private fun renderVision(vision: Vision, offer: (Action) -> Boolean): RenderStatus {
    Display.printLine(vision.toString())
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
        offer(Action.SetPassword(password, vision.target))
    }
    return RenderStatus.Repeat
}

private fun getConfirmedCheckedPassword(checker: (CharArray) -> String?): CharArray? {
    var confirmedPassword: CharArray? = null
    var repeat = true
    while (repeat) {
        val password = getCheckedPassword(checker)
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

private fun getCheckedPassword(checker: (CharArray) -> String?): CharArray? {
    var password: CharArray? = null
    var repeat = true
    while (repeat) {
        val candidate = Display.awaitPassword(prompt = "Enter password")
        if (candidate == null) {
            repeat = false
        } else {
            val errorMessage = checker(candidate)
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

