package com.will.sunnyweather.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.view.View

class GradientView(context: Context) : View(context) {

    private val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 定义渐变颜色
        val colors = intArrayOf(Color.RED, Color.BLUE)
        // 创建 LinearGradient
        val linearGradient = LinearGradient(
            0f, 0f, // 起始点
            width.toFloat(), height.toFloat(), // 结束点
            colors, // 渐变颜色
            null, // 颜色位置（可选）
            Shader.TileMode.CLAMP // 瓷砖模式
        )

        // 设置画笔的着色器为线性渐变
        paint.shader = linearGradient

        // 绘制矩形
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
}