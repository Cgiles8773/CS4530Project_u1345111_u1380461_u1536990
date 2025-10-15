package com.example.phase1.data.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.phase1.viewmodel.DrawingViewModel.BrushShape
import com.example.phase1.viewmodel.DrawingViewModel.Stroke
import java.io.File
import java.io.FileOutputStream

class ImageHandler(/*private val context: Context*/) {
    fun saveStrokesToBitmap(strokes: List<Stroke>, width: Int, height: Int): Bitmap {
        val bitmap = ImageBitmap(width, height)
        val canvas = Canvas(bitmap)
        strokes.forEach { stroke ->
            val paint = Paint().apply { color = stroke.color }
            val points = stroke.points
            if (points.size == 1) {
                when (stroke.shape) {
                    BrushShape.Circle -> canvas.drawCircle(points[0], stroke.size / 2, paint)
                    BrushShape.Square -> canvas.drawRect(
                        Rect(
                            points[0].x - stroke.size / 2, points[0].y - stroke.size / 2,
                            points[0].x + stroke.size / 2, points[0].y + stroke.size / 2
                        ), paint
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
                for (i in 0 until points.size - 1) {
                    when (stroke.shape) {
                        BrushShape.Circle -> canvas.drawCircle(points[i], stroke.size / 2, paint)
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

    fun saveBitmapToFile(bitmap: Bitmap, filename: String): String {
        /*
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$filename.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
         */
    }

    fun loadBitmapFromFile(filepath: String): Bitmap? = BitmapFactory.decodeFile(filepath)

    fun deleteBitmapFile(filepath: String) {
        val file = File(filepath)
        if (file.exists()) file.delete()
    }
}
