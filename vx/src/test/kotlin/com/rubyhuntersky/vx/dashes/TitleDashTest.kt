package com.rubyhuntersky.vx.dashes

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class TitleDashTest {

    private val viewId = ViewId()
    private val latitudeSubject = PublishSubject.create<DashLatitude>()
    private val eventSubject = PublishSubject.create<Nothing>()
    private val viewMock = mock<Dash.View<TextLine, Nothing>> {
        on { latitudes } doReturn latitudeSubject
        on { events } doReturn eventSubject
    }
    private val hostMock = mock<ViewHost> {
        on { addTextLine(viewId) } doReturn viewMock
    }
    private val view = TitleDash.enview(hostMock, viewId)

    @Test
    fun setContent() {
        view.setContent("Hello")
        verify(viewMock).setContent(
            TextLine(
                "Hello",
                TextStyle.Highlight5
            )
        )
    }

    @Test
    fun setLimit() {
        view.setHBound(HBound(0, 20))
        verify(viewMock).setHBound(HBound(0, 20))
    }

    @Test
    fun setAnchor() {
        view.setAnchor(Anchor(0, 0f))
        verify(viewMock).setAnchor(Anchor(0, 0f))
    }


    @Test
    fun latitudes() {
        val test = view.latitudes.test()
        latitudeSubject.onNext(DashLatitude(100))
        test.assertValue(DashLatitude(100))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}