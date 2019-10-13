package com.rubyhuntersky.seismic_stampede

data class Session(
    val keyStack: KeyStack,
    val vault: Vault,
    val refreshCount: Int = 0
) {
    fun refresh() = copy(refreshCount = refreshCount + 1)
    fun setKeyStack(newKeyStack: KeyStack) = copy(keyStack = newKeyStack)
}