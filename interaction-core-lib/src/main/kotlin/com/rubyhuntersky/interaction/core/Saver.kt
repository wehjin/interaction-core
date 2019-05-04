package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

object Saver {

    sealed class Vision<T : Any> {
        class Reading<T : Any> : Vision<T>()
        data class Ready<T : Any>(val value: T) : Vision<T>()
    }

    sealed class Action<T : Any> {
        data class Write<T : Any>(val value: T) : Action<T>()
    }

    class InteractionImpl<T : Any>(private val book: Book<T>) :
        SubjectInteraction<Vision<T>, Action<T>>(startVision = Saver.Vision.Reading<T>()) {

        private val disposable = CompositeDisposable()

        init {
            book.reader
                .subscribe { setVision(Saver.Vision.Ready(it)) }
                .addTo(disposable)
        }

        override fun sendAction(action: Action<T>) {
            val newValue = (action as Action.Write).value
            if (newValue != value) {
                book.write(newValue)
            }
        }

        private val value: T?
            get() {
                val vision = vision
                return when (vision) {
                    is Vision.Reading -> null
                    is Vision.Ready -> vision.value
                }
            }
    }
}