package com.will.sunnyweather.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.will.sunnyweather.R;
import com.will.sunnyweather.test.CircleDrawable;
import com.will.sunnyweather.utils.Utils;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    private ImageView iv,
            // 指示图标 imageView
            ivPosition,
            // 放大镜的 imageView
           ivMagnifier;
    private int bmWidth, bmHeight;
    private int ivWidth, ivHeight;
    private FrameLayout fl;

    // 触摸点的坐标
    private float mX, mY;
    // 按下的点的坐标
    private float centerX, centerY;
    // 加载显示的 bitmap
    private Bitmap bm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        fl = findViewById(R.id.fl);
        iv = findViewById(R.id.imageView);
        ivPosition = findViewById(R.id.iv_location);
        ivMagnifier = findViewById(R.id.iv_magnifier);

        matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);

//        iv.setImageDrawable(new CircleDrawable());

        // 计算 imageView 的父布局的宽高信息，让 imageView 和 父布局一样大
        // 这样，在同一个父布局内的 view 才可以使用 imageView 的触摸坐标
        fl.post(new Runnable() {
            @Override
            public void run() {
                // iv 的最大尺寸
                int maxWidth = fl.getWidth();
                int maxHeight = fl.getHeight();
                Log.e("WillWolf", "width-->" + maxWidth);
                Log.e("WillWolf", "height-->" + maxHeight);

                bm = getImage();
                bmWidth = bm.getWidth();
                bmHeight = bm.getHeight();
                Log.e("WillWolf", "bm-->" + bm.getWidth() + ", " + bm.getHeight());
                // 调整 imageView 的宽高尺寸
                float radio = bm.getWidth() * 1.0f / bm.getHeight();

                int viewWidth = maxWidth;
                int viewHeight = (int) (viewWidth / radio);
                Log.e("WillWolf", "viewHeight-->" + viewHeight);
                if (viewHeight > maxHeight) {
                    viewHeight = maxHeight;
                    viewWidth = (int) (viewHeight * radio);
                    Log.e("WillWolf", "viewWidth-->" + viewWidth);
                }

                ivWidth = viewWidth;
                ivHeight = viewHeight;

                // 设置 imageView 的宽高
                ViewGroup.LayoutParams lp = fl.getLayoutParams();
                lp.width = viewWidth;
                lp.height = viewHeight;
                fl.setLayoutParams(lp);


                iv.setImageBitmap(bm);
            }
        });

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mX = event.getX();
                        mY = event.getY();
                        centerX = mX;
                        centerY = mY;
                        Log.e("WillWolf", "down mX-->" + mX + ", " + mY);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 计算离按下的坐标移动的距离
                        float dx = event.getX() - centerX;
                        float dy = event.getY() - centerY;

                        // 实时的移动坐标位置，是按下的坐标位置 + 移动的距离的 1/10
                        mX = centerX + dx / 10;
                        mY = centerY + dy / 10;

                        showMagnifierImage();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 这里的 x 和 y 的坐标是相对于触摸的 view 的坐标
//                        float x = event.getX();
//                        float y = event.getY();
                        Log.e("WillWolf", "x-->" + mX + ", " + mY);

                        // 计算对应的 bitmap 中的位置
                        float bmX = mX / ivWidth * bmWidth;
                        float bmY = mY / ivHeight * bmHeight;
                        Log.e("WillWolf", "bmX-->" + bmX + ", " + bmY);
                        ivPosition.setX(mX - Utils.dp2px(TestActivity.this, 25));
                        ivPosition.setY(mY - Utils.dp2px(TestActivity.this, 50));
                        break;
                }

                return true;
            }
        });
    }

    // 显示放大图片
    // 放大镜显示图片的宽高
    private int bmMagnifierW = 50, bmMagnifierH = 50;
    // 放大用的 matrix
    private Matrix matrix;
    // 放大的倍数
    private float scaleX = 10f, scaleY = 10f;
    private Bitmap bmMagnifier;// 局部放大的图片
    private void showMagnifierImage() {
        int width = bmMagnifierW;
        int height = bmMagnifierH;

        // 根据触摸点，获取 bitmap 上的坐标位置
        float dstX = mX / ivWidth * bmWidth;
        float dstY = mY / ivHeight * bmHeight;

        // 这里是表示最右边
        if (dstX + bmMagnifierW / 2 >= bm.getWidth()) {
            width = (int) (bmMagnifierW - (dstX + bmMagnifierW / 2 - bm.getWidth()));
            if (width <= 0) {
                width = 1;
            }
        } else {
            width = bmMagnifierW;
        }
        // 这里是最下边
        if (dstY + bmMagnifierH / 2 >= bm.getHeight()) {
            height = (int) (bmMagnifierH - (dstY + bmMagnifierH / 2 - bm.getHeight()));
            if (height <= 0) {
                height = 1;
            }
        } else {
            height = bmMagnifierH;
        }

        // 如果从屏幕边缘处下滑，会出现值 < 0 的情况
        if (dstX - bmMagnifierW / 2 >= 0 && dstY - bmMagnifierH / 2 >= 0) {
            // 做了边界处理，有可能会导致左边的位置 + 显示的宽度 > 图片的宽度而崩溃。
            int x = (int) dstX - bmMagnifierW / 2;
            if (x + width >= bm.getWidth()) {
                x = bm.getWidth() - width;
            }
            int y = (int) dstY - bmMagnifierH / 2;
            if (y + height >= bm.getHeight()) {
                y = bm.getHeight() - height;
            }
            bmMagnifier = Bitmap.createBitmap(bm, x, y, width, height, matrix, true);
            ivMagnifier.setImageBitmap(bmMagnifier);
        } else {
            // 如果 dstX 大于 0， 表示是在最左边
            if (dstX > 0 && dstY - bmMagnifierH / 2 >= 0) {
                bmMagnifier = Bitmap.createBitmap(bm, (int) dstX, (int) dstY - bmMagnifierH / 2, (int) (dstX * 2), height, matrix, true);
                ivMagnifier.setImageBitmap(bmMagnifier);
            } else if (dstX - bmMagnifierW / 2 >= 0 && dstY > 0) {
                // 如果 dstY 大于 0，表示是在最上边
                bmMagnifier = Bitmap.createBitmap(bm, (int) dstX - bmMagnifierW / 2, (int) dstY, width, (int) (dstY * 2), matrix, true);
                ivMagnifier.setImageBitmap(bmMagnifier);
            }
        }

//        // 显示位置是以触摸点为中心，左边 bmMagnifierW / 2，右边 bmMagnifierW / 2
//        int x = (int) (dstX - bmMagnifierW / 2);
//        int y = (int) (dstY - bmMagnifierH / 2);
//        bmMagnifier = Bitmap.createBitmap(bm, x, y, width, height, matrix, true);
//        ivMagnifier.setImageBitmap(bmMagnifier);
    }


    private Bitmap getImage() {
        File file = new File(getExternalFilesDir("").getPath() + "/" + "MyPic", "Test.jpg");
        Bitmap bm = BitmapFactory.decodeFile(file.getPath());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                ExifInterface exifInterface = new ExifInterface(file);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Matrix matrix = new Matrix();
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    matrix.postRotate(90);
                }
                return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bm;
    }
}
