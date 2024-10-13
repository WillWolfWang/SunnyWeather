package com.will.sunnyweather.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.will.sunnyweather.R;


public class BiggerView extends FitImageView {
    private int mBvRadius = dp(30);//半径
    private int mBvOutlineWidth = 2;//边线宽

    private float rate = 4;//默认放大的倍数
    private int mBvOutlineColor = 0xffCCDCE4;//边线颜色

    private Paint mPaint;//主画笔
    private Bitmap mBiggerBitmap;//放大的图片
    private Path mPath;//剪切路径

    private boolean firstClick = true;//TODO 让大图片延迟到第一次点击时生成，flag

    private Style mStyle = Style.CLIP_CIRCLE;

    public BiggerView(Context context) {
        this(context, null);
    }

    public BiggerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BiggerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BiggerView);
        mBvRadius = (int) a.getDimension(R.styleable.BiggerView_z_bv_radius, mBvRadius);
        mBvOutlineWidth = (int) a.getDimension(R.styleable.BiggerView_z_bv_outline_width, mBvOutlineWidth);
        mBvOutlineColor = a.getColor(R.styleable.BiggerView_z_bv_outline_color, mBvOutlineColor);
        rate = (int) a.getFloat(R.styleable.BiggerView_z_bv_rate, rate);
        a.recycle();
        init();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBiggerBitmap = createBigBitmap(rate, mFitBitmap);
    }

    private void init() {
        //初始化主画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBvOutlineColor);
        mPaint.setStrokeWidth(mBvOutlineWidth * 2);

        mPath = new Path();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDown) {
            switch (mStyle) {
                case NO_CLIP://无裁剪，直接放大
                    // 这里是将放大后的图片位置往后移动，因为放大后，当前点的位置会往 x 正方向和 y 正方向平移
                    // ----x 原始位置
                    // ------------x 放大 3 倍后，会移动到远处
                    // 将 x 移回来，需要移动的位置是 - (3x - x)

                    float showY = -mCurY * (rate - 1);
                    // 这里相当于又画了一层放大的 bitmap 覆盖在原来的 canvas 上
                    canvas.drawBitmap(mBiggerBitmap, -mCurX * (rate - 1), showY, mPaint);
                    break;
                case CLIP_CIRCLE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mPath.reset();
                        showY = -mCurY * (rate - 1) - 2 * mBvRadius;
                        canvas.drawBitmap(mBiggerBitmap, -mCurX * (rate - 1), showY, mPaint);
                        // 判断触摸位置离图片顶部距离，离的比较远，就把圆圈画在上方，离得比较近，就画在下方
                        float rY = mCurY > 2 * mBvRadius ? mCurY - 2 * mBvRadius : mCurY + mBvRadius;
                        mPath.addCircle(mCurX, rY, mBvRadius, Path.Direction.CCW);
                        canvas.clipOutPath(mPath);
                        super.onDraw(canvas);
                        canvas.drawCircle(mCurX, rY, mBvRadius, mPaint);
                    } else {
                        mStyle = Style.NO_CLIP;//如果版本过低,无裁剪，直接放大
                        invalidate();
                    }
                    //可拓展更多模式....
            }
        }
    }

    private float mCurX;//当前触点X
    private float mCurY;//当前触点Y
    private boolean isDown;//是否触摸

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mCurX = event.getX();
                mCurY = event.getY();
                //校验矩形区域
                isDown = judgeRectArea(mImageW / 2, mImageH / 2, mCurX, mCurY, mImageW, mImageH);
                Log.e("WillWolf", "isDown-->" + isDown + ", " + mCurX + ", " + mCurY);
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                Log.e("WillWolf", "x-->" + x + ", " + y);
                isDown = false;
        }
        invalidate();//记得刷新
        return true;
    }

    protected int sp(int sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    protected int dp(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 判断落点是否在矩形区域
     */
    public static boolean judgeRectArea(float srcX, float srcY, float dstX, float dstY, float w, float h) {
        return Math.abs(dstX - srcX) < w / 2 && Math.abs(dstY - srcY) < h / 2;
    }

    enum Style {
        NO_CLIP,//无裁剪，直接放大
        CLIP_CIRCLE,//圆形裁剪
    }
}

