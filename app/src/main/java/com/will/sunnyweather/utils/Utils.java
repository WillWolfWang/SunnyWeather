package com.will.sunnyweather.utils;

import android.content.Context;

public class Utils {

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

}
