package com.rubyhuntersky.vx

object TitleDash : Dash<String, Nothing> by TextLineDash().transform({ TextLine(it, TextStyle.Highlight5) })

data class TitleSubtitle(val title: String, val subtitle: String)
