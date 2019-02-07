package com.rubyhuntersky.interaction.interactions.common

import com.rubyhuntersky.interaction.BehaviorInteraction
import com.rubyhuntersky.interaction.books.Book
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

object ReadWrite {

    sealed class Vision<T : Any> {
        class Reading<T : Any> : Vision<T>()
        data class Ready<T : Any>(val value: T) : Vision<T>()
    }

    sealed class Action<T : Any> {
        data class Write<T : Any>(val value: T) : Action<T>()
    }

    class Interaction<T : Any>(private val book: Book<T>) :
        BehaviorInteraction<Vision<T>, Action<T>>(startingVision = Vision.Reading<T>()) {

        private val disposable = CompositeDisposable()

        init {
            book.reader
                .subscribe { setVision(Vision.Ready(it)) }
                .addTo(disposable)
        }

        override fun onAction(action: Action<T>) {
            val newStory = (action as Action.Write).value
            if (newStory != value) {
                book.write(newStory)
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