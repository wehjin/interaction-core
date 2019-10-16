package com.rubyhuntersky.seismic_stampede.plots

import com.rubyhuntersky.seismic_stampede.gather.core.Gather
import com.rubyhuntersky.seismic_stampede.gather.core.GatherValidity
import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

object GatherPlot {

    val storyName: String = GatherPlot::class.java.simpleName

    sealed class Vision : Revisable<Vision, Action> {

        data class Gathering(
            val finalGather: Gather,
            val values: List<String>,
            val message: String? = null,
            val changeCount: Int = 0
        ) : Vision() {

            val activeGather: Gather by lazy { finalGather.all[values.size] }

            val isLastGather: Boolean
                get() = ungatheredValues == 1

            private val ungatheredValues: Int
                get() = finalGather.all.size - values.size

            val gatheredValues: Map<String, String> by lazy {
                values
                    .mapIndexed { i, value -> Pair(finalGather.all[i].label, value) }
                    .associate { it }
            }

            fun addValue(validValue: String): Gathering {
                check(ungatheredValues > 1)
                return copy(
                    values = values + validValue,
                    message = null,
                    changeCount = changeCount + 1
                )
            }

            fun setMessage(message: String): Gathering {
                return copy(
                    message = message,
                    changeCount = changeCount + 1
                )
            }
        }

        data class Ended(val end: End<Map<String, String>>) : Vision()
    }

    sealed class Action {
        data class Advance(val value: String) : Action()
    }

    @ExperimentalCoroutinesApi
    fun start(gather: Gather, storybook: Storybook): Story2<Vision, Action> {
        val init = Vision.Gathering(gather, emptyList()) as Vision
        val story = storyOf<Vision, Action>(storyName, init) { action, vision ->
            when (action) {
                is Action.Advance -> {
                    require(vision is Vision.Gathering)
                    val active = vision.activeGather
                    when (val validity = active.validate(action.value, vision.gatheredValues)) {
                        is GatherValidity.Valid -> {
                            if (vision.isLastGather) {
                                val gatheredValues = vision.gatheredValues + Pair(
                                    vision.activeGather.label,
                                    validity.validValue
                                )
                                Vision.Ended(endHigh(gatheredValues)).toRevision(isLast = true)
                            } else {
                                vision.addValue(validity.validValue).toRevision()
                            }
                        }
                        is GatherValidity.Invalid -> {
                            vision.setMessage(validity.reason).toRevision()
                        }
                    }
                }
            }
        }
        return story.also(storybook::startProjector)
    }
}