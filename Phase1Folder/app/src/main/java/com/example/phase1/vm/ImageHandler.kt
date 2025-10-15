package com.example.phase1.vm

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.room.Dao
import com.example.phase1.vm.DrawingViewModel.BrushShape

class ImageHandler {
    companion object {
        // Saves a given list of strokes to a Bitmap, and returns the result
        fun saveStrokesToBitmap(
            strokes: List<DrawingViewModel.Stroke>,
            width: Int,
            height: Int
        ): Bitmap {
            val bitmap = ImageBitmap(width, height)
            val canvas = Canvas(bitmap)
            strokes.forEach { stroke ->
                val paint = Paint().apply {
                    color = stroke.color
                }
                val points = stroke.points
                if (points.size == 1) {
                    // Single tap
                    when (stroke.shape) {
                        BrushShape.Circle -> canvas.drawCircle(points[0], stroke.size / 2, paint)
                        BrushShape.Square -> canvas.drawRect(
                            Rect(
                                points[0].x - stroke.size / 2, points[0].y - stroke.size / 2,
                                points[0].x + stroke.size / 2, points[0].y + stroke.size / 2
                            ),
                            paint
                        )

                        BrushShape.Triangle -> {
                            val half = stroke.size / 2
                            val path = Path().apply {
                                moveTo(points[0].x, points[0].y - half)
                                lineTo(points[0].x - half, points[0].y + half)
                                lineTo(points[0].x + half, points[0].y + half)
                                close()
                            }
                            canvas.drawPath(path, paint)
                        }
                    }
                } else {
                    // Multi-point strokes
                    for (i in 0 until points.size - 1) {
                        when (stroke.shape) {
                            BrushShape.Circle -> canvas.drawCircle(
                                points[i],
                                stroke.size / 2,
                                paint
                            )

                            BrushShape.Square -> canvas.drawLine(points[i], points[i + 1], paint)
                            BrushShape.Triangle -> {
                                val half = stroke.size / 2
                                val path = Path().apply {
                                    moveTo(points[i].x, points[i].y - half)
                                    lineTo(points[i].x - half, points[i].y + half)
                                    lineTo(points[i].x + half, points[i].y + half)
                                    close()
                                }
                                canvas.drawPath(path, paint)
                            }
                        }
                    }
                }
            }
            return bitmap.asAndroidBitmap()
        }
        // Fetches an image from the database given an ID
        fun loadBitmapFromDatabase(id: Int, dao: Dao): Bitmap {
            val image = dao.getImage(id)
            return bitmap.asAndroidBitmap()
        }
    }
}