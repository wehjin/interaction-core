package com.rubyhuntersky.vx

object TitleDash : Dash<String, Nothing> by TextLineDash().transform({ TextLine(it, TextStyle.Highlight5) })
