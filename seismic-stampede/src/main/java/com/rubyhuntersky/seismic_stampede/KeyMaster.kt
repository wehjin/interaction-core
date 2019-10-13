package com.rubyhuntersky.seismic_stampede

import java.security.SecureRandom

object KeyMaster {

    private var password: CharArray? = null
    private var passwordId: Int? = null

    fun setPassword(password: CharArray): Int {
        this.password = password
        this.passwordId = SecureRandom().nextInt()
        return passwordId!!
    }
}