package com.jimro.myrefreshlistview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jimro.myrefreshlistview.R;

import java.text.SimpleDateFormat;

/**
 * Created by lixichang on 2017/7/27.
 */

public class MyRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private View mHeaderView;
    private int mHeaderViewHeight;
    private float startY;
    private float moveY;

    private int currentRefreshState;
    private static final int PULL_TO_REFRESH = 0;
    private static final int RELSEASE_REFRESH = 1;
    private static final int REFRESHING = 2;
    private RotateAnimation rotateUp;
    private RotateAnimation rotateDown;
    private View image_arrow;
    private ProgressBar mProgressBar;
    private TextView mHeaderViewTitle;
    private TextView mHeaderViewLastRefreshTime;

    private OnRrefreshListViewListener refreshListViewListener;
    private View mFooterView;
    private int mFooterViewHeight;

    public static final int LOAD_MODE_ARROW = 1;
    public static final int LOAD_MODE_FOOTER = 2;

    public MyRefreshListView(Context context) {
        super(context);
        init();
    }

    public MyRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initHeaderView();
        initAnimator();
        initFooterView();
        setOnScrollListener(this);
    }

    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.layout_footer_view, null);
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        addFooterView(mFooterView);
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
    }

    private void initAnimator() {

        rotateUp = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateUp.setDuration(300);
        rotateUp.setFillAfter(true);

        rotateDown = new RotateAnimation(-180, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateDown.setDuration(300);
        rotateDown.setFillAfter(true);

    }

    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.layout_listview_headerview, null);
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();

        image_arrow = mHeaderView.findViewById(R.id.iv_arrow);
        mProgressBar = ((ProgressBar) mHeaderView.findViewById(R.id.refresh_progress));
        mHeaderViewTitle = ((TextView) mHeaderView.findViewById(R.id.header_view_title));
        mHeaderViewLastRefreshTime = ((TextView) mHeaderView.findViewById(R.id.header_view_last_refresh_time));


        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
        addHeaderView(mHeaderView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;


            case MotionEvent.ACTION_MOVE:

                // 如果当前ListView正在刷新，则不触发再次刷新
                if (currentRefreshState == REFRESHING) {
                    return super.onTouchEvent(ev);
                }
                moveY = ev.getY();
                float offset = moveY - startY;
                int paddingTop = (int) (-mHeaderViewHeight + offset);


                /**
                 * 如果下拉的时候当前的可见item的索引为0，则触摸时间交由我们处理.
                 * 当下拉的距离大于一定的值，并且当前可见item的索引为0，
                 * 则设置HeaderView的PaddingTop让其显示
                 *
                 * ViewConfiguration.get(getContext()).getScaledTouchSlop()
                 * 获取系统认为滑动的最小距离（系统认为是8）
                 */
                if (offset > ViewConfiguration.get(getContext()).getScaledTouchSlop()
                        && getFirstVisiblePosition() == 0) {
                    mHeaderView.setPadding(0, paddingTop, 0, 0);


                    //paddingTop>=0,将刷新状态改为释放刷新，并更新HeaderView

                    if (paddingTop >= 0 && currentRefreshState != RELSEASE_REFRESH) {

                        currentRefreshState = RELSEASE_REFRESH;
                        updateHeader();

                    } else if (paddingTop < 0 && currentRefreshState != PULL_TO_REFRESH) {
                        /**
                         * 当PaddingTop<0并且当前的刷新状态不是PULL_TO_REFRESH，
                         * 则将刷新状态改为下拉刷新，并更新HeaderView
                         */
                        currentRefreshState = PULL_TO_REFRESH;
                        updateHeader();
                    }
                    return true;
                }
                break;

            /**
             * 下拉松手的时候currentRefreshState只可能是RELSEASE_REFRESH或PULL_TO_REFRESH
             * 对这两种状态最相应的处理
             */
            case MotionEvent.ACTION_UP:

                if (currentRefreshState == RELSEASE_REFRESH) {
                    //下拉松手之后，让ListView回弹完全显示HeaderView,并将状态改为正在刷新，随后更新HeaderView
                    mHeaderView.setPadding(0, 0, 0, 0);
                    currentRefreshState = REFRESHING;
                    updateHeader();
                } else if (currentRefreshState == PULL_TO_REFRESH) {
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void updateHeader() {
        //更新headerView的显示状态和动画的执行
        switch (currentRefreshState) {
            case PULL_TO_REFRESH:
                /**
                 * 下拉刷新状态让箭头向下旋转
                 */
                image_arrow.startAnimation(rotateDown);
                currentRefreshState = PULL_TO_REFRESH;
                mHeaderViewTitle.setText("下拉刷新");

                break;
            case RELSEASE_REFRESH:
                /**
                 * 释放刷新，让箭头向上旋转
                 */
                image_arrow.startAnimation(rotateUp);
                currentRefreshState = RELSEASE_REFRESH;
                mHeaderViewTitle.setText("释放刷新");
                break;
            case REFRESHING:
                /**
                 * 正在刷新，让箭头隐藏，并显示ProgressBar，然后调用刷新接口
                 */
                image_arrow.clearAnimation();
                mHeaderViewTitle.setText("正在刷新...");
                mProgressBar.setVisibility(VISIBLE);
                image_arrow.setVisibility(INVISIBLE);
                if (refreshListViewListener != null) {
                    refreshListViewListener.onRefresh(LOAD_MODE_ARROW);
                }
                break;
        }
    }

    public void onCompleteRefresh(int loadMode) {
        /**
         * 刷新完成后，更新最后刷新时间，并让ProgressBar隐藏，让箭头显示
         * 并将刷新状态重置
         */
        if (loadMode == LOAD_MODE_ARROW) {
            mProgressBar.setVisibility(INVISIBLE);
            image_arrow.setVisibility(VISIBLE);
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
            mHeaderViewLastRefreshTime.setText("最后刷新时间：" + getTime());
            currentRefreshState = PULL_TO_REFRESH;
        } else if (loadMode == LOAD_MODE_FOOTER) {
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
        }

    }

    public String getTime() {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(currentTimeMillis);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE && getLastVisiblePosition() >= getCount() - 1) {
            //显示FooterView
            mFooterView.setPadding(0, 0, 0, 0);
            setSelection(getCount());
            if (refreshListViewListener != null) {
                refreshListViewListener.onRefresh(LOAD_MODE_FOOTER);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public interface OnRrefreshListViewListener {
        void onRefresh(int loadMode);
    }

    public void setRefreshListViewListener(OnRrefreshListViewListener refreshListViewListener) {
        this.refreshListViewListener = refreshListViewListener;
    }
}
