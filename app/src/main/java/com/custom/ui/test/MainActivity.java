package com.custom.ui.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.custom.ui.textview.FadeTextView;

/**
 * @author lijia
 */
public class MainActivity extends Activity {

    FadeTextView fadeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fadeTextView = findViewById(R.id.fade_tv);

    }

    public void clickFade(View view) {
        fadeTextView.setTextFade("永远保持马克思主义执政党本色，永远走在时代前列，永远做中国人民和中华民族的主心骨！",null);
    }
}
