package com.will.sunnyweather.weight;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.will.sunnyweather.R;
import com.will.sunnyweather.utils.Utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WeightTestActivity extends AppCompatActivity {
    private LinearLayout llParent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_test);

        llParent = findViewById(R.id.ll_parent);
//        GradientView gradientView = new GradientView(this);
//        llParent.addView(gradientView);
        Log.e("WillWolf", "200->" + Utils.dp2px(this, 200));
    }
}
