package com.rubyhuntersky.seismic_stampede

import java.io.File
import java.util.*

fun vaultOf(folder: File) = Vault(folder)

class Vault(initFolder: File) {

    private val folder: File = initFolder.apply { mkdir() }

    fun addNote(title: String, text: String, passwordId: Int) {
        val metaBytes = makeMeta("note", listOf(title))
        val bodyBytes = text.toByteArray(Charsets.UTF_8)
        val recordBytes = makeRecord(metaBytes, bodyBytes).also { bodyBytes.zero() }
        writeCipherItem(KeyMaster.encryptAndZeroPlain(recordBytes, passwordId))
    }

    fun addPassword(location: String, user: String, password: String, passwordId: Int) {
        val metaBytes = makeMeta("password", listOf(location, user))
        val bodyBytes = password.toByteArray(Charsets.UTF_8)
        val recordBytes = makeRecord(metaBytes, bodyBytes).also { bodyBytes.zero() }
        writeCipherItem(KeyMaster.encryptAndZeroPlain(recordBytes, passwordId))
    }

    fun findGems(passwordId: Int): List<Gem> {
        return readCipherItems(passwordId).map { cipherItem ->
            val recordBytes = KeyMaster.decrypt(cipherItem, passwordId)
            val metaBytes = breakRecordToMeta(recordBytes).also { recordBytes.zero() }
            val fields = breakMeta(metaBytes)
            when (val type = fields[0]) {
                "note" -> Gem(type, fields[1])
                "password" -> Gem(type, "${fields[1]} - ${fields[2]}")
                else -> error("Invalid type $type")
            }
        }
    }

    private fun readCipherItems(passwordId: Int): List<CipherItem> {
        return folder.listFiles()!!.mapNotNull {
            val split = it.nameWithoutExtension.split(":")
            if (it.isFile && split.size == 2) {
                readCipherItem(it)
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
