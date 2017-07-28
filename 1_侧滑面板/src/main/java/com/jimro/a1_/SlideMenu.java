package com.jimro.a1_;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by lixichang on 2017/7/27.
 */

public class SlideMenu extends ViewGroup {
    private float downX;
    private float moveX;
    private int leftMenuWidth;

    private static final int MENU_STATE_ON = 1;
    private static final int MENU_STATE_OFF = 2;
    private int currentMenuState = MENU_STATE_OFF;

    private Scroller scroller;
    private float downY;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext());
    }

    /**
     * 处理子View的测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //测量左面板的宽高
        View leftMenu = getChildAt(0);
        leftMenu.measure(leftMenu.getLayoutParams().width,
                heightMeasureSpec);

        //测量主面板的宽高
        View mainContent = getChildAt(1);
        mainContent.measure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * @param changed 当前控件的尺寸大小，位置是否发生改变
     * @param l       当前控件的左边距
     * @param t       上边距
     * @param r       右边距
     * @param b       下边距
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //摆放内容,左面板
        View leftMenu = getChildAt(0);

        leftMenuWidth = leftMenu.getMeasuredWidth();
        leftMenu.layout(-leftMenuWidth, 0, 0, b);

        //摆放主面板
        getChildAt(1).layout(l, t, r, b);
    }


    /**
     * 拦截触摸事件使得在左面板也可以水平滑动
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float offsetX = Math.abs(ev.getX() - downX);
                float offsetY = Math.abs(ev.getY() - downY);

                if (offsetX > offsetY
                        && offsetX > ViewConfiguration.get(getContext())
                        .getScaledTouchSlop()) {
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 处理触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();

                int scrollX = (int) (downX - moveX);
                //获取即将要滑动的总距离
                int totalScroll = getScrollX() + scrollX;
                //限定左滑的边界
                if (totalScroll < -leftMenuWidth) {
                    scrollTo(-leftMenuWidth, 0);
                    //限定右滑的边界
                } else if (totalScroll > 0) {
                    scrollTo(0, 0);
                } else {
                    scrollBy(scrollX, 0);
                }
                downX = moveX;
                break;

            case MotionEvent.ACTION_UP:
                int leftCenter = (int) (-leftMenuWidth / 2.0f);
                if (getScrollX() < leftCenter) {
                    currentMenuState = MENU_STATE_ON;
                    updateMenuContent();
                } else {
                    currentMenuState = MENU_STATE_OFF;
                    updateMenuContent();
                }
                break;
        }
        return true;
    }

    /**
     * 更新Menu的状态并实现平滑滚动
     */
    private void updateMenuContent() {
        //使用平滑的滚动
        int startX = getScrollX();
        int dx = 0;
        if (currentMenuState == MENU_STATE_ON) {

            dx = -leftMenuWidth - startX;
        } else if (currentMenuState == MENU_STATE_OFF) {
            dx = 0 - startX;
        }
        //1.开始数据模拟
        //定义滑动的时间
        int duration = Math.abs(dx * 2);
        /**
         * startX:开始的x值
         * startY：开始的y值
         * dx：在水平方向上滑动的距离
         * dy：在垂直方向上滑动的距离
         * duration：数据模拟的时间
         * dx = 结束位置 - 开始位置;
         */
        scroller.startScroll(startX, 0, dx, 0, duration);

        invalidate();//重绘界面->onDraw()->computeScroll();
    }

    //2.维持动画的继续
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            //获取当前要滚动到的位置
            int currX = scroller.getCurrX();
            scrollTo(currX, 0);

            invalidate();
        }
    }


    private void menuOpen() {
        currentMenuState = MENU_STATE_ON;
        updateMenuContent();
    }

    private void menuClose() {
        currentMenuState = MENU_STATE_OFF;
        updateMenuContent();
    }

    /**
     * 对外提供改变Menu的状态
     */
    public void switchMenuState() {
        if (currentMenuState == MENU_STATE_OFF) {
            menuOpen();
        } else if (currentMenuState == MENU_STATE_ON) {
            menuClose();
        }
    }

}
