package com.jimro.a1_;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private SlideMenu mSlideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //继承AppCompatActivity时隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //继承Activity时隐藏标题栏有效
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mSlideMenu = (SlideMenu) findViewById(R.id.slide_menu);

    }

    public void onTabClick(View view) {
        if (view.getId() == R.id.ib_back) {
            mSlideMenu.switchMenuState();
        }
    }
}
