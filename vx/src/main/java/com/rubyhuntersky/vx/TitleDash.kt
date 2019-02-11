package com.rubyhuntersky.vx

class TitleDash : Dash<String, Nothing> by TextLineDash().transform({ TextLine(it, TextStyle.Highlight5) })
