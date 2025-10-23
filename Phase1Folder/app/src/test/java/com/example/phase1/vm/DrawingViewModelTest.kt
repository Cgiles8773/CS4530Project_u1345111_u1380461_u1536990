/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains unit tests for the DrawingViewModel class.
 * The tests verify correct behavior for settings toggling, brush updates,
 * and stroke creation logic. These ensure the ViewModel’s drawing state
 * changes properly without relying on the UI layer.
 */

package com.example.phase1.vm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.repository.ImageRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class DrawingViewModelTest {

    private lateinit var viewModel: DrawingViewModel
    private lateinit var mockRepo: ImageRepository
    private lateinit var mockHandler: ImageHandler

    @Before
    fun setup() {
        mockRepo = mock()
        mockHandler = mock()
        viewModel = DrawingViewModel(mockRepo, mockHandler)
    }

    @Test
    fun toggleSettings_changesState() {
        assertFalse(viewModel.showSettings)
        viewModel.toggleSettings()
        assertTrue(viewModel.showSettings)
    }

    @Test
    fun setBrushColor_updatesColor() {
        val color = Color.Red
        viewModel.setBrushColor(color)
        assertEquals(color, viewModel.brushColor)
    }

    @Test
    fun strokeLifecycle_createsStrokeWithPoints() {
        val start = Offset(10f, 20f)
        val mid = Offset(15f, 25f)

        viewModel.startStroke(start)
        viewModel.addPointToStroke(mid)
        viewModel.endStroke()

        assertTrue(viewModel.strokes.isNotEmpty())
        val stroke = viewModel.strokes.first()
        assertEquals(2, stroke.points.size)
    }

    @Test
    fun clearCanvas_resetsState() {
        viewModel.startStroke(Offset(1f, 1f))
        assertTrue(viewModel.strokes.isNotEmpty())

        viewModel.clearCanvas()

        assertTrue(viewModel.strokes.isEmpty())
        assertNull(viewModel.getBitmap())
    }
}
