package com.will.sunnyweather.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

public class GradientShaderTextView extends AppCompatTextView {

    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private int mViewWidth = 0;
    private int mTranslate = 0;

    private boolean mAnimating = true;
    private int delta = 15;
    public GradientShaderTextView(Context ctx)
    {
        this(ctx,null);
    }

    public GradientShaderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 这个方法在视图的大小发生变化时被调用。它的主要作用是：
    // 获取视图宽度：当视图的宽度第一次被测量时，保存这个宽度。
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.e("WillWolf", "onSizeChanged-->");
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
//            Log.e("WillWolf", "onSizeChanged-->" + mViewWidth);
            if (mViewWidth > 0) {
                mPaint = getPaint();
                String text = getText().toString();
                // float textWidth = mPaint.measureText(text);
                int size;
                if(text.length()>0)
                {
                    size = mViewWidth*2/text.length();
                }else{
                    size = mViewWidth;
                }

                /**
                 * LinearGradient(
                 *     float x0, float y0, // 渐变起始点的坐标
                 *     float x1, float y1, // 渐变结束点的坐标
                 *     int[] colors, // 渐变颜色数组
                 *     float[] positions, // 颜色位置数组（可选）
                 *     Shader.TileMode tile // 瓷砖模式
                 * )
                 */

                mLinearGradient = new LinearGradient(-size, 0, 0, 0,
                        new int[] { 0x33ffffff, 0xffffffff, 0x33ffffff },
                        new float[] { 0, 0.5f, 1 }, Shader.TileMode.CLAMP); //边缘融合
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int length = Math.max(length(), 1);
        if (mAnimating && mGradientMatrix != null) {
            float mTextWidth = getPaint().measureText(getText().toString());
            mTranslate += delta;
            if (mTranslate > mTextWidth+1 || mTranslate<1) {
                delta  = -delta;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);  //自动平移矩阵
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(30);
        }
    }

}