package com.rubyhuntersky.seismic_stampede.vibes

import com.rubyhuntersky.seismic_stampede.gather.core.gatherOf
import com.rubyhuntersky.seismic_stampede.gather.core.validWhenNotEmpty
import com.rubyhuntersky.seismic_stampede.stories.GatherStory
import com.rubyhuntersky.seismic_stampede.stories.PasswordStory
import com.rubyhuntersky.seismic_stampede.stories.startPasswordStory
import com.rubyhuntersky.seismic_stampede.stories.startStory
import com.rubyhuntersky.seismic_stampede.preinteraction.core.End
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Storybook
import com.rubyhuntersky.seismic_stampede.preinteraction.core.toEnding
import com.rubyhuntersky.seismic_stampede.preinteraction.core.toWish
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
fun <OutA : Any> wishForNewPassword(
    storybook: Storybook,
    transform: (End<Int>) -> OutA
): Wish2<OutA> = startPasswordStory(storybook)
    .toEnding { (it as? PasswordStory.Vision.Ended)?.end }
    .toWish(transform)

@ExperimentalCoroutinesApi
fun <OutA : Any> wishForLocationUsername(
    storybook: Storybook,
    transform: (End<Pair<String, String>>) -> OutA
): Wish2<OutA> =
    gatherOf("Location", validator = ::validWhenNotEmpty)
        .and("Username", validator = ::validWhenNotEmpty)
        .let { gather ->
            gather.startStory(storybook)
                .toEnding { (it as? GatherStory.Vision.Ended)?.end }
                .toWish {
                    val pairEnd = it.map { gathering ->
                        val location = gather[0](gathering)!!
                        val username = gather[1](gathering)!!
                        Pair(location, username)
                    }
                    transform(pairEnd)
                }
        }
