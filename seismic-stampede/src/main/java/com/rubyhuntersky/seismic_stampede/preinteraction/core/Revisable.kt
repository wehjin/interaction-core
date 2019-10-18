package com.rubyhuntersky.seismic_stampede.preinteraction.core

interface Revisable<V : Any, A : Any>

inline fun <reified V : Any, A : Any> Revisable<V, A>.toRevision(isLast: Boolean = false): Revision<V, A> {
    return Revision(this as V, isLast)
}

inline infix fun <reified V : Any, A : Any> Revisable<V, A>.and(wish: Wish2<A>): Revision<V, A> {
    return Revision(this as V, isLast = false, wishes = listOf(wish))
}

class RevisionScope<V : Any, A : Any> {

    fun V.toRevision(isLast: Boolean = false): Revision<V, A> = Revision(this, isLast)

    infix fun V.and(wish: Wish2<A>): Revision<V, A> {
        return Revision(this, isLast = false, wishes = listOf(wish))
    }
}
