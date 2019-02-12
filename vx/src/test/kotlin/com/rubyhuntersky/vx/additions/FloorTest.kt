package com.rubyhuntersky.vx.additions

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class FloorTest {
    private val viewId = ViewId()

    private val latitudeSubjectA = PublishSubject.create<DashLatitude>()
    private val eventSubjectA = PublishSubject.create<Nothing>()
    private val viewMockA = mock<Dash.View<TextLine, Nothing>> {
        on { latitudes } doReturn latitudeSubjectA
        on { events } doReturn eventSubjectA
    }

    private val latitudeSubjectB = PublishSubject.create<DashLatitude>()
    private val eventSubjectB = PublishSubject.create<Nothing>()
    private val viewMockB = mock<Dash.View<TextLine, Nothing>> {
        on { latitudes } doReturn latitudeSubjectB
        on { events } doReturn eventSubjectB
    }

    private val hostMock = mock<ViewHost> {
        on { addTextLine(ViewId().extend(0)) } doReturn viewMockA
        on { addTextLine(ViewId().extend(1)) } doReturn viewMockB
    }
    private val dash = TitleDash + Floor(TitleDash) { content: Pair<String, String> -> content }
    private val view = dash.enview(hostMock, viewId)

    @Test
    fun setContent() {
        view.setContent(Pair("Hello", "World"))
        verify(viewMockA).setContent(TextLine("Hello", TextStyle.Highlight5))
        verify(viewMockB).setContent(TextLine("World", TextStyle.Highlight5))
    }

    @Test
    fun setLimit() {
        val limit = DashLimit(0, 20)
        view.setLimit(limit)
        verify(viewMockA).setLimit(limit)
        verify(viewMockB).setLimit(limit)
    }

    @Test
    fun setAnchor() {
        latitudeSubjectA.onNext(Dash.Latitude(75))
        latitudeSubjectB.onNext(Dash.Latitude(25))
        view.setAnchor(Anchor(0, 0f))
        verify(viewMockA).setAnchor(Anchor(0, 0f))
        verify(viewMockB).setAnchor(Anchor(100, 1f))
    }


    @Test
    fun latitudes() {
        val test = view.latitudes.test()
        latitudeSubjectA.onNext(DashLatitude(100))
        latitudeSubjectB.onNext(DashLatitude(5))
        test.assertValue(DashLatitude(105))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}