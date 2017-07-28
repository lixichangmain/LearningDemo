package com.jimro.imagemove;


import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
        TouchListener listener = new TouchListener();
        imageView.setOnTouchListener(listener);
    }

    private class TouchListener implements View.OnTouchListener {
        private PointF startPointF = new PointF();
        private Matrix currentMatrix = new Matrix();
        private Matrix matrix = new Matrix();
        private int mode;
        private static final int DRAG = 1;
        private static final int ZOOM = 2;

        //有多点触控开始时两点之间的距离
        private float startDis;
        //手指缩放后两点之间的距离
        private float endDis;
        //两点之间的中心点坐标
        private PointF midPointF;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;
                    //获取手指按下时的位置
                    startPointF.set(event.getX(), event.getY());
                    //获取手指按下时的图片矩阵
                    currentMatrix.set(imageView.getImageMatrix());
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = ZOOM;
                    startDis = distance(event);
                    if (startDis > 10f) {
                        currentMatrix.set(imageView.getImageMatrix());
                        //计算中心点的坐标
                        midPointF = getMidPointF(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        float dx = event.getX() - startPointF.x;
                        float dy = event.getY() - startPointF.y;
                        //在上一次移动的基础上继续移动
                        matrix.set(currentMatrix);
                        //移动需要调用postTranslate(dx,dy)，而不是setTranslate(dx, dy);
                        matrix.postTranslate(dx, dy);
                    } else if (mode == ZOOM) {
                        //缩放
                        /**
                         * 1.在屏幕上有多点触控的时候，首先需要知道开始时两点之间的距离
                         * 2.在手指移动的时候，要知道移动之后两点之间的距离
                         * 3.用之后的距离除以开始的距离就知道了缩放的比例
                         * 4.知道开始两点间中心点的位置，因为要以这个点为中心开始移动
                         */
                        endDis = distance(event);
                        if (endDis > 10f) {
                            float scale = endDis / startDis;
                            matrix.set(currentMatrix);
                            matrix.postScale(scale, scale, midPointF.x, midPointF.y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
            }
            imageView.setImageMatrix(matrix);
            return true;
        }
    }

    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return
     */
    public static float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算中心点的坐标
     *
     * @param event
     * @return
     */
    public static PointF getMidPointF(MotionEvent event) {
        float midx = (event.getX(1) + event.getX(0)) / 2;
        float midy = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midx, midy);
    }
}
