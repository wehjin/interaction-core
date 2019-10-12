package com.rubyhuntersky.seismic_stampede

interface Revisable

inline fun <reified T : Any> Revisable.toRevision(isLast: Boolean = false): Revision<T> {
    val value = this as T
    return Revision(value, isLast)
}
