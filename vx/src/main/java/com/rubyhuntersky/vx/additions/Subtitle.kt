package com.rubyhuntersky.vx.additions

import com.rubyhuntersky.vx.*

operator fun TitleDash.plus(@Suppress("UNUSED_PARAMETER") subtitle: Subtitle): Dash<TitleSubtitle, Nothing> =
    TitleAtopSubtileDash + Floor(SubtitleBelowTitleDash) { Pair(it.title, it.subtitle) }

object Subtitle
data class TitleSubtitle(val title: String, val subtitle: String)

private object TitleAtopSubtileDash :
    Dash<String, Nothing> by TextLineDash().transform({ TextLine(it, TextStyle.Highlight6) })

private object SubtitleBelowTitleDash :
    Dash<String, Nothing> by TextLineDash().transform({ TextLine(it, TextStyle.Subtitle1) })
