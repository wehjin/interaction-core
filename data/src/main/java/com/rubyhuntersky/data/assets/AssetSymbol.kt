package com.rubyhuntersky.data.assets

data class AssetSymbol(private val string: String) {

    override fun toString(): String = string.trim().toUpperCase()
}