package com.rubyhuntersky.seismic_stampede

import java.io.File
import java.util.*

fun vaultOf(folder: File) = Vault(folder)

class Vault(initFolder: File) {

    private val folder: File = initFolder.apply { mkdir() }

    fun addNote(text: String, passwordId: Int) {
        val bytes = "$NOTE_GEM_TYPE\n$text".toByteArray(Charsets.UTF_8)
        val cipherItem = KeyMaster.encryptAndZeroPlain(bytes, passwordId)
        writeCipherItem(cipherItem)
    }

    fun findGems(passwordId: Int): List<Gem> {
        return folder.listFiles()!!.mapNotNull {
            val split = it.nameWithoutExtension.split(":")
            if (it.isFile && split.size == 2) {
                val cipherItem = readCipherItem(it)
                val plainBytes = KeyMaster.decrypt(
                    cipherItem = cipherItem,
                    passwordId = passwordId
                )
                val plain = String(plainBytes)
                val division = plain.indexOf('\n')
                val type = plain.substring(0, division)
                val title = plain.substring(division + 1)
                Gem(type, title)
            } else {
                null
            }
        }
    }

    private fun readCipherItem(file: File): CipherItem {
        val name = file.nameWithoutExtension
        val cipherBytes = Base64.getDecoder().decode(file.readText(Charsets.UTF_8))
        return CipherItem(name, cipherBytes)
    }

    private fun writeCipherItem(cipherItem: CipherItem) {
        val fileBytes = Base64.getEncoder().encode(cipherItem.cipherBytes)
        val fileNameWithoutExtension = cipherItem.name
        File(folder, "$fileNameWithoutExtension.item").writeBytes(fileBytes)
    }
}
