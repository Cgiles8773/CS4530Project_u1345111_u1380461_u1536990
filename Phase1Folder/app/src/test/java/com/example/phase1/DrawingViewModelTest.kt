/**
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains unit tests for the DrawingViewModel class.
 * The tests verify correct behavior for toggling settings,
 * updating brush color, and managing the stroke lifecycle.
 * These tests ensure the ViewModel logic functions as expected
 * independently of the UI layer.
 */
package com.example.phase1.vm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DrawingViewModelTest
{

    private lateinit var viewModel: DrawingViewModel

    @Before
    fun setup()
    {
        viewModel = DrawingViewModel()
    }

    @Test
    fun toggleSettings_changesState()
    {
        assertFalse(viewModel.showSettings)
        viewModel.toggleSettings()
        assertTrue(viewModel.showSettings)
    }

    @Test
    fun setBrushColor_updatesColor()
    {
        val color = Color.Red
        viewModel.setBrushColor(color)
        assertEquals(color, viewModel.brushColor)
    }

    @Test
    fun strokeLifecycle_createsStrokeWithPoints()
    {
        val start = Offset(10f, 20f)
        val mid = Offset(15f, 25f)

        viewModel.startStroke(start)
        viewModel.addPointToStroke(mid)
        viewModel.endStroke()

        assertTrue(viewModel.strokes.isNotEmpty())
        val stroke = viewModel.strokes.first()
        assertEquals(2, stroke.points.size)
        assertEquals(start, stroke.points.first())
        assertEquals(mid, stroke.points.last())
    }
}
