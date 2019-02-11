package com.rubyhuntersky.vx

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Test

class TextLineDashTest {

    private val id = ViewId()
    private val viewMock = mock<DashView<TextLine, Nothing>>()
    private val hostMock = mock<ViewHost> {
        on { addTextLine(id) } doReturn viewMock
    }

    @Test
    fun envisionPassesIdAndReturnsHostView() {
        val dash = TextLineDash()
        val view = dash.enview(hostMock, id)
        verify(hostMock).addTextLine(id)
        Assert.assertEquals(viewMock, view)
    }
}