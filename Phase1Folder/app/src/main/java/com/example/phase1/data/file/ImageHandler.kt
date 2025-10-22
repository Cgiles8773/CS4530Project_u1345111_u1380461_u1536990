package com.example.phase1.data.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import com.example.phase1.model.BrushShape
import com.example.phase1.model.Stroke
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri

class ImageHandler(private val context: Context) {

    fun saveStrokesToBitmap(
        background: Bitmap?,
        strokes: List<Stroke>,
        width: Int,
        height: Int
    ): Bitmap {
        val result = createBitmap(width, height)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        if (background != null) {
            // Scale and draw the background to fit the output size
            val scaled = background.scale(width, height)
            canvas.drawBitmap(scaled, 0f, 0f, null)
        } else {
            canvas.drawColor(Color.WHITE)
        }

        // Draw strokes on top
        for (stroke in strokes) {
            paint.color = stroke.color.toArgb()
            paint.alpha = (stroke.alpha * 255).toInt()
            paint.strokeWidth = stroke.size
            paint.style = Paint.Style.FILL_AND_STROKE

            val points = stroke.points

            if (points.size == 1) {
                val p = points[0]
                when (stroke.shape) {
                    BrushShape.Circle ->
                        canvas.drawCircle(p.x, p.y, stroke.size / 2, paint)

                    BrushShape.Square ->
                        canvas.drawRect(
                            p.x - stroke.size / 2,
                            p.y - stroke.size / 2,
                            p.x + stroke.size / 2,
                            p.y + stroke.size / 2,
                            paint
                        )

                    BrushShape.Triangle -> {
                        val half = stroke.size / 2
                        val path = Path().apply {
                            moveTo(p.x, p.y - half)
                            lineTo(p.x - half, p.y + half)
                            lineTo(p.x + half, p.y + half)
                            close()
                        }
                        canvas.drawPath(path, paint)
                    }
                }
            } else {
                // Multi-point strokes
                for (i in 0 until points.lastIndex) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    when (stroke.shape) {
                        BrushShape.Circle ->
                            canvas.drawCircle(p1.x, p1.y, stroke.size / 2, paint)

                        BrushShape.Square ->
                            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)

                        BrushShape.Triangle -> {
                            val half = stroke.size / 2
                            val path = Path().apply {
                                moveTo(p1.x, p1.y - half)
                                lineTo(p1.x - half, p1.y + half)
                                lineTo(p1.x + half, p1.y + half)
                                close()
                            }
                            canvas.drawPath(path, paint)
                        }
                    }
                }
            }
        }

        return result
    }

    fun saveBitmapToFile(bitmap: Bitmap, name: String): String? {
        return try {
            val dir = File(context.filesDir, "images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "$name.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun loadBitmapFromFile(filepath: String): Bitmap? {
        return try {
            if (filepath.startsWith("content://")) {
                // Load from content provider
                context.contentResolver.openInputStream(filepath.toUri()).use { input ->
                    BitmapFactory.decodeStream(input)
                }
            } else {
                // Load from filesystem path
                BitmapFactory.decodeFile(filepath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteBitmapFile(filepath: String) {
        val file = File(filepath)
        if (file.exists()) file.delete()
    }
}
