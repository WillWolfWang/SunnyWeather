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


   float oldDistance = 0f;// 刚按下时双指之间的距离
   float newDistance = 0f;// 在屏幕上滑动后双指之间的距离
   float scalePoint = 0f; // 缩放中心点
   float scale = 1f; // 缩放比
   float translationX = 0f;// x 轴移动量
   float translationY = 0f;// y 轴移动量
   float oldCenterX = 0f;// 刚按下时双指之间的 x 坐标
   float oldCenterY = 0f;// 刚按下时双指之间的 y 坐标
   float newCenterX = 0f;// 屏幕上滑动后双指之间的点的 x 坐标
   float newCenterY = 0f;// 屏幕上滑动后双指之间的点的 y 坐标

   private int count = 0;//点击次数
   private long firstClick = 0;//第一次点击时间
   private long secondClick = 0;//第二次点击时间
   private final int totalTime = 2000;
   @Override
   public boolean onTouchEvent(MotionEvent event) {
      switch (event.getActionMasked()) {
        /* case MotionEvent.ACTION_DOWN:
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
            }*/
         case MotionEvent.ACTION_DOWN:
            if (MotionEvent.ACTION_DOWN == event.getAction()) {//按下
               count++;
               if (1 == count) {
                  firstClick = System.currentTimeMillis();//记录第一次点击时间
               } else if (2 == count) {
                  secondClick = System.currentTimeMillis();//记录第二次点击时间
                  if (secondClick - firstClick < totalTime) {//判断二次点击时间间隔是否在设定的间隔时间之内
                     count = 0;
                     firstClick = 0;
                     scale = 1;
                     translationX = 0;
                     translationY = 0;
                     setScaleX(scale);
                     setScaleY(scale);
                     setTranslationX(translationX);
                     setTranslationY(translationY);
                  } else {
                     firstClick = secondClick;
                     count = 1;
                  }
                  secondClick = 0;
               }
            }
            break;

         case MotionEvent.ACTION_POINTER_DOWN:
               if (event.getPointerCount() == 2) {
                  oldDistance = getDistance(event);
                  oldCenterX = calculateCenterX(event);
                  oldCenterY = calculateCenterY(event);
               }
            break;
         case MotionEvent.ACTION_MOVE:
            if (event.getPointerCount() == 2) {
               newDistance = getDistance(event);
               scale += (newDistance - oldDistance) / oldDistance;
               if (scale > 10) {
                  scale = 10f;
               }
               if (scale < 1) {
                  scale = 1;
               }
               newCenterX = calculateCenterX(event);
               newCenterY = calculateCenterY(event);
               // 缩放
//               imageView.scaleX = scale
//               imageView.scaleY = scale
               setScaleX(scale);
               setScaleY(scale);
               // 位移
               translationX += newCenterX - oldCenterX;
               translationY += newCenterY - oldCenterY;
//
               setTranslationX(translationX);
               setTranslationY(translationY);
//               imageView.translationX = translationX;
//               imageView.translationY = translationY;
            }
            break;

      }
      invalidate();//记得刷新
      return true;
   }

   private float calculateCenterX(MotionEvent motionEvent) {
      float x1 = motionEvent.getX(0);
      float x2 = motionEvent.getX(1);
      return (x1 + x2) / 2;
   }

   private float calculateCenterY(MotionEvent motionEvent) {
      float y1 = motionEvent.getY(0);
      float y2 = motionEvent.getY(1);
      return (y1 + y2) / 2;
   }

   private float getDistance(MotionEvent event) {
      float dx = event.getX(0) - event.getX(1);
      float dy = event.getY(0) - event.getY(1);
      return (float) Math.sqrt(dx * dx + dy * dy);
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
