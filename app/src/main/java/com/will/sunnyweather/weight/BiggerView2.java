package com.will.sunnyweather.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.will.sunnyweather.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BiggerView2 extends View {

   private int mBvRadius = dp(30);//半径
   private int mBvOutlineWidth = 2;//边线宽

   private float rate = 4f;//默认放大的倍数
   private int mBvOutlineColor = 0xffCCDCE4;//边线颜色

   private Paint mPaint;//主画笔
   private Bitmap mBiggerBitmap;//放大的图片

   private Bitmap mBitmapSrc;//源图片

   private Path mPath;//剪切路径

   public BiggerView2(Context context) {
      this(context, null);
   }

   public BiggerView2(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BiggerView);
      mBvRadius = (int) a.getDimension(R.styleable.BiggerView_z_bv_radius, mBvRadius);
      mBvOutlineWidth = (int) a.getDimension(R.styleable.BiggerView_z_bv_outline_width, mBvOutlineWidth);
      mBvOutlineColor = a.getColor(R.styleable.BiggerView_z_bv_outline_color, mBvOutlineColor);
      rate = (int) a.getFloat(R.styleable.BiggerView_z_bv_rate, rate);
      a.recycle();
      init();
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
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);

   }

   @Override
   protected void onDraw(@NonNull Canvas canvas) {
      super.onDraw(canvas);

      Log.e("WillWolf", "BiggerView2 onDraw-->" + mBitmapSrc);
      if (mBitmapSrc != null)
         canvas.drawBitmap(mBitmapSrc, 0, 0, mPaint);

      if (isDown) {
         mPath.reset();
         Bitmap tmp = createBigBitmap(rate, mBitmapSrc, (int) (mCurX - mBvRadius), (int) (mCurY - mBvRadius), mBvRadius * 2, mBvRadius * 2);

         Log.e("WillWolf", "tmp-->" + tmp.getWidth());
         // 判断触摸位置离图片顶部距离，离的比较远，就把圆圈画在上方，离得比较近，就画在下方
         float rY = mCurY > 2 * mBvRadius ? mCurY - 2 * mBvRadius : mCurY + mBvRadius;
         int count = canvas.save();
         mPath.addCircle(mCurX, rY, mBvRadius, Path.Direction.CCW);
         canvas.clipPath(mPath);
         canvas.drawBitmap(tmp, mCurX - tmp.getWidth() / 2f, rY - tmp.getHeight() / 2f, mPaint);
         canvas.restoreToCount(count);
         canvas.drawCircle(mCurX, rY, mBvRadius, mPaint);
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
            isDown = judgeRectArea(getWidth()/ 2, getHeight() / 2, mCurX, mCurY, getWidth(), getHeight());
//                Log.e("WillWolf", "isDown-->" + isDown + ", " + mCurX + ", " + mCurY);
            break;
         case MotionEvent.ACTION_UP:
            float x = event.getX();
            float y = event.getY();
            isDown = false;
            if (callback != null) {
               callback.onActionUp(x,y);
            }
      }
      invalidate();//记得刷新
      return true;
   }

   private BiggerView.Callback callback;

   public void setCallback(BiggerView.Callback callback) {
      this.callback = callback;
   }

   public interface Callback {
      void onActionUp(float x, float y);
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if (mBitmapSrc != null) {
         mBitmapSrc.recycle();
      }
   }

   /**
    * 判断落点是否在矩形区域
    */
   public static boolean judgeRectArea(float srcX, float srcY, float dstX, float dstY, float w, float h) {
      return dstX > 0 && dstX < w && dstY > 0 && dstY < h;
   }

   public void setBitmapSrc(Bitmap bitmapSrc) {
      Log.e("WillWolf", "BiggerView2 setBitmapSrc-->" + bitmapSrc.getWidth() + ", " + bitmapSrc.getHeight());
      // 做一个放大
      // 设置采样率后，bitmap 尺寸会比 view 小
      float radio = getWidth() * 1.f / bitmapSrc.getWidth();
      this.mBitmapSrc = createBigBitmap(radio, bitmapSrc);
   }

   protected Bitmap createBigBitmap(float rate, Bitmap src) {
      Matrix matrix = new Matrix();
      //设置变换矩阵:
      matrix.setValues(new float[]{
              rate, 0, 0,
              0, rate, 0,
              0, 0, 1
      });

      return Bitmap.createBitmap(src, 0, 0,
              src.getWidth(), src.getHeight(), matrix, true);
   }

   protected Bitmap createBigBitmap(float rate, Bitmap src, int x, int y, int width, int height) {
      Matrix matrix = new Matrix();
      //设置变换矩阵:
      matrix.postScale(rate, rate);
      if (x < 0) {
         x = 0;
      }

      if (y < 0) {
         y = 0;
      }

      if (x + width >= src.getWidth()) {
         x = src.getWidth() - width;
      }
      if (y + height >= src.getHeight()) {
         y = src.getHeight() - height;
      }

      return Bitmap.createBitmap(src, x, y,
              width, height, matrix, true);
   }

   protected int dp(int dp) {
      return (int) TypedValue.applyDimension(
              TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
   }
}
