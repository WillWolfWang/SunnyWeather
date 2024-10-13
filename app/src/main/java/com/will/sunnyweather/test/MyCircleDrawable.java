package com.will.sunnyweather.test;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Property;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyCircleDrawable extends Drawable implements Animatable {

    private Paint paint;
    // 动画控制
    private ValueAnimator valueAnimator;
    // 扩散半径
    private int radius;
    // 绘制的矩形框
    private RectF rectF = new RectF();
    // 动画启动延迟时间
    private int startDelay;
    // 自定义一个扩散半径属性
    Property<MyCircleDrawable, Integer> radiusProperty = new Property<MyCircleDrawable, Integer>(Integer.class, "radius") {

        @Override
        public void set(MyCircleDrawable object, Integer value) {
            object.setRadius(value);
        }

        @Override
        public Integer get(MyCircleDrawable object) {
            return object.getRadius();
        }
    };

    public MyCircleDrawable() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    // 提供一个 radius 的 set get 方法
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    // Drawable 重写方法
    @Override
    public void draw(@NonNull Canvas canvas) {
        // 绘图
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // 设置透明度
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // 设置颜色过滤
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);

    }

    private Rect clipSquare(Rect rect) {
        int w = rect.width();
        int h = rect.height();
        int min = Math.min(w, h);
        int cx = rect.centerX();
        int cy = rect.centerY();
        int r = min / 2;
        return new Rect(
                cx - r,
                cy - r,
                cx + r,
                cy + r
        );
    }

    // Animatable 重新方法
    @Override
    public void start() {
        // 启动动画
    }

    @Override
    public void stop() {
        // 停止动画
    }

    @Override
    public boolean isRunning() {
        //　判断动画是否运行
        return false;
    }
}
