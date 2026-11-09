package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import com.example.model.TaskCategory
import java.io.File
import java.io.FileOutputStream

object ImageStorageManager {

    private const val DIR_NAME = "category_images"

    fun getCategoryDir(context: Context): File {
        val dir = File(context.filesDir, DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getCustomImagePath(context: Context, categoryId: String): String? {
        val file = File(getCategoryDir(context), "${categoryId.lowercase()}.jpg")
        return if (file.exists()) file.absolutePath else null
    }

    fun saveCategoryPhoto(context: Context, categoryId: String, sourceUri: Uri): Boolean {
        return try {
            val destinationFile = File(getCategoryDir(context), "${categoryId.lowercase()}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteCategoryPhoto(context: Context, categoryId: String): Boolean {
        val file = File(getCategoryDir(context), "${categoryId.lowercase()}.jpg")
        return if (file.exists()) file.delete() else false
    }

    fun getCategoryBitmap(context: Context, category: TaskCategory, width: Int = 600, height: Int = 400): Bitmap {
        val customPath = getCustomImagePath(context, category.name)
        if (customPath != null) {
            try {
                val bitmap = BitmapFactory.decodeFile(customPath)
                if (bitmap != null) {
                    return Bitmap.createScaledBitmap(bitmap, width, height, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return createDefaultCategoryBitmap(category, width, height)
    }

    private fun createDefaultCategoryBitmap(category: TaskCategory, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val (c1, c2) = when (category) {
            TaskCategory.THESIS -> Color.parseColor("#4338CA") to Color.parseColor("#6366F1")
            TaskCategory.TOEIC -> Color.parseColor("#B45309") to Color.parseColor("#F59E0B")
            TaskCategory.STRENGTH_TRAINING -> Color.parseColor("#047857") to Color.parseColor("#10B981")
            TaskCategory.FOOTBALL -> Color.parseColor("#BE123C") to Color.parseColor("#F43F5E")
            TaskCategory.BREAKFAST -> Color.parseColor("#C2410C") to Color.parseColor("#FB923C")
            TaskCategory.LUNCH -> Color.parseColor("#D97706") to Color.parseColor("#FBBF24")
            TaskCategory.DINNER -> Color.parseColor("#9A3412") to Color.parseColor("#EA580C")
            TaskCategory.INTERNSHIP -> Color.parseColor("#1D4ED8") to Color.parseColor("#3B82F6")
            TaskCategory.SKINCARE -> Color.parseColor("#0E7490") to Color.parseColor("#06B6D4")
            TaskCategory.COMMUTE -> Color.parseColor("#334155") to Color.parseColor("#64748B")
            TaskCategory.RELAX -> Color.parseColor("#6D28D9") to Color.parseColor("#8B5CF6")
            TaskCategory.SLEEP -> Color.parseColor("#0F172A") to Color.parseColor("#1E293B")
        }

        // Draw lush gradient background
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            c1, c2, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Draw decorative abstract shapes
        paint.shader = null
        paint.color = Color.argb(40, 255, 255, 255)
        canvas.drawCircle(width * 0.85f, height * 0.2f, width * 0.25f, paint)
        canvas.drawCircle(width * 0.15f, height * 0.9f, width * 0.35f, paint)

        // Draw emoji/icon symbol centered
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = height * 0.35f
            textAlign = Paint.Align.CENTER
        }
        val x = width / 2f
        val y = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f) - (height * 0.1f)
        canvas.drawText(category.emoji, x, y, textPaint)

        // Draw Category title text
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = height * 0.11f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            setShadowLayer(8f, 2f, 4f, Color.argb(160, 0, 0, 0))
        }
        canvas.drawText(category.displayName, x, height * 0.82f, titlePaint)

        return bitmap
    }
}
