package com.rubyhuntersky.vx.additions

import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.dashes.TextLineSight
import com.rubyhuntersky.vx.dashes.TextLineDash
import com.rubyhuntersky.vx.dashes.TextStyle
import com.rubyhuntersky.vx.dashes.TitleDash

operator fun TitleDash.plus(@Suppress("UNUSED_PARAMETER") subtitle: Subtitle): Dash<TitleSubtitle, Nothing> =
    TitleAtopSubtitleDash + Bottom(SubtitleBelowTitleDash) { Pair(it.title, it.subtitle) }

object Subtitle
data class TitleSubtitle(val title: String, val subtitle: String)

private object TitleAtopSubtitleDash :
    Dash<String, Nothing> by TextLineDash().transform({
        TextLineSight(
            it,
            TextStyle.Highlight6
        )
    })

private object SubtitleBelowTitleDash :
    Dash<String, Nothing> by TextLineDash().transform({
        TextLineSight(
            it,
            TextStyle.Subtitle1
        )
    })
