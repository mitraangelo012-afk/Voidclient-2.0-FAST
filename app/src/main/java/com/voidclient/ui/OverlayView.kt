package com.voidclient.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams

class OverlayView(context: Context) : View(context) {
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 3f
    }

    private val entities = mutableListOf<EntityInfo>()

    data class EntityInfo(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val name: String,
        val health: Float
    )

    fun updateEntities(newEntities: List<EntityInfo>) {
        entities.clear()
        entities.addAll(newEntities)
        postInvalidate() // Trigger redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw entities
        for (entity in entities) {
            drawEntityBox(canvas, entity)
        }
    }

    private fun drawEntityBox(canvas: Canvas, entity: EntityInfo) {
        // Set color based on health
        val healthRatio = entity.health.coerceIn(0f, 1f)
        val red = (255 * (1 - healthRatio)).toInt()
        val green = (255 * healthRatio).toInt()
        paint.color = Color.rgb(red, green, 0)
        paint.style = Paint.Style.STROKE

        // Draw box around entity
        canvas.drawRect(
            entity.x, entity.y,
            entity.x + entity.width, entity.y + entity.height,
            paint
        )

        // Draw health bar
        val barHeight = 3f
        val barWidth = entity.width
        val barX = entity.x
        val barY = entity.y - 10

        // Background (red)
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, paint)

        // Foreground (green based on health)
        paint.color = Color.GREEN
        canvas.drawRect(barX, barY, barX + (barWidth * healthRatio), barY + barHeight, paint)

        // Draw name
        paint.color = Color.WHITE
        paint.textSize = 12f
        paint.style = Paint.Style.FILL
        canvas.drawText(entity.name, entity.x, entity.y - 15, paint)
    }

    companion object {
        fun createLayoutParams(): LayoutParams {
            return LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                LayoutParams.TYPE_APPLICATION_OVERLAY,
                LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCHABLE or LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 0
                y = 0
            }
        }
    }
}