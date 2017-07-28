package com.jimro.drawabledemo;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button changeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
        changeButton = (Button) findViewById(R.id.change_button);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.change_button) {
            LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.layout_drawable);
            Drawable drawable = getResources().getDrawable(R.drawable.tupian);
            layerDrawable.setDrawableByLayerId(R.id.tupian, drawable);
            //换完图片重新给ImageView设置Drawable
            imageView.setImageDrawable(layerDrawable);

        }
    }
}
