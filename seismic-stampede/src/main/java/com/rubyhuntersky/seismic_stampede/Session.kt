package com.rubyhuntersky.seismic_stampede

data class Session(
    val keyStack: KeyStack,
    val vault: Vault,
    val refreshCount: Int = 0
) {
    fun refresh() = copy(refreshCount = refreshCount + 1)
    fun setKeyStack(newKeyStack: KeyStack) = copy(keyStack = newKeyStack)

    fun addNote(text: String): Session {
        require(keyStack is KeyStack.Shallow)
        vault.addNote(text, "empty", keyStack.passwordId)
        return refresh()
    }

    fun addPassword(location: String, user: String): Session {
        require(keyStack is KeyStack.Shallow)
        vault.addPassword(location, user, "12345", keyStack.passwordId)
        return refresh()
    }

    val activeGems: List<Gem>
        get() {
            return when (keyStack) {
                is KeyStack.Empty -> emptyList()
                is KeyStack.Shallow -> {
                    vault.findGems(keyStack.passwordId)
                }
            }
        }

}
