package com.jimro.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;


/**
 * 包含下拉刷新的ListView
 * Created by lixichang on 2017/7/26.
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private View mHeaderView;
    private float startY;
    private float moveY;
    public static final int PULL_REFRESH = 0;//下拉刷新
    public static final int RELEASE_REFRESH = 1;//释放刷新
    public static final int REFRESHING = 2;//正在刷新

    private int currentState;
    private View mArrowView;
    private TextView headerTitle;
    private TextView headerDesc;
    private RotateAnimation rotateUp;
    private RotateAnimation rotateDown;
    private int paddingTop;
    private int headerViewHeight;
    private ProgressBar mProgressBar;
    private OnRefreshListener refreshListener;
    private View footerView;
    private int footerViewHeight;

    public static final int MODE_REFRESH = 1;
    public static final int MODE_FOOTER_REFRESH = 2;


    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化头布局脚布局
     * 滚动监听
     */
    private void init() {
        initHeadView();
        initFooterView();
        initAnimation();
        setOnScrollListener(this);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.layout_footer_view, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);

        addFooterView(footerView);
    }

    /**
     * 初始化刷新动画
     */
    private void initAnimation() {
        //向上转，逆时针180
        this.rotateUp = new RotateAnimation(0f, -180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUp.setDuration(300);
        rotateUp.setFillAfter(true);

        //向下转
        this.rotateDown = new RotateAnimation(-180f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDown.setDuration(300);
        rotateDown.setFillAfter(true);


    }

    /**
     * 初始化头布局
     */
    private void initHeadView() {
        mHeaderView = View.inflate(getContext(), R.layout.layout_header_list, null);

        mArrowView = mHeaderView.findViewById(R.id.iv_arrow);
        headerTitle = ((TextView) mHeaderView.findViewById(R.id.tv_title));
        headerDesc = ((TextView) mHeaderView.findViewById(R.id.tv_desc_last_refresh));
        mProgressBar = ((ProgressBar) mHeaderView.findViewById(R.id.pb));

        //提前手动测量控件的宽高
        mHeaderView.measure(0, 0);

        headerViewHeight = mHeaderView.getMeasuredHeight();

        //设置内边距，可以隐藏当前控件
        mHeaderView.setPadding(0, -headerViewHeight, 0, 0);

        //在设置适配器之前执行添加头布局
        addHeaderView(mHeaderView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //判断滑动的距离，并且给Header设置Padding
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                if (currentState == REFRESHING) {
                    return super.onTouchEvent(ev);
                }
                moveY = ev.getY();
                float offset = moveY - startY;
                //只有偏移量大于0，并且当前的可见条目索引为0是才让header显示
                if (offset > 0 && getFirstVisiblePosition() == 0) {
                    this.paddingTop = (int) (-headerViewHeight + offset);
                    mHeaderView.setPadding(0, paddingTop, 0, 0);

                    if (paddingTop >= 0 && currentState != RELEASE_REFRESH) {//完全展示头布局
                        //切换成释放刷新模式
                        currentState = RELEASE_REFRESH;
                        updateHeader();
                    } else if (paddingTop < 0 && currentState != PULL_REFRESH) {
                        //切换成下拉刷新模式
                        currentState = PULL_REFRESH;
                        updateHeader();
                    }
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (currentState == PULL_REFRESH) {
                    mHeaderView.setPadding(0, -headerViewHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {
                    mHeaderView.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;
                    updateHeader();
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    private void updateHeader() {
        switch (currentState) {
            case RELEASE_REFRESH://切换成释放刷新
                mArrowView.startAnimation(rotateUp);
                headerTitle.setText("释放刷新");
                break;
            case PULL_REFRESH://切换回下拉刷新
                mArrowView.startAnimation(rotateDown);
                headerTitle.setText("下拉刷新！");
                break;
            case REFRESHING://刷新中.....
                mArrowView.clearAnimation();
                mArrowView.setVisibility(INVISIBLE);
                mProgressBar.setVisibility(VISIBLE);
                headerTitle.setText("正在刷新.....");
                if (refreshListener != null) {
                    refreshListener.onRefresh(MODE_REFRESH);//通知调用者加载数据
                }
                break;
        }
    }

    /**
     * 刷新结束
     */
    public void onRefreshComplete(int loadMode) {
        if (loadMode == MODE_REFRESH) {
            currentState = PULL_REFRESH;
            headerTitle.setText("下拉刷新");
            mProgressBar.setVisibility(INVISIBLE);
            mArrowView.setVisibility(VISIBLE);
            mHeaderView.setPadding(0, -headerViewHeight, 0, 0);
            String time = getTime();
            headerDesc.setText("最后刷新时间：" + time);
        } else if (loadMode == MODE_FOOTER_REFRESH) {
            footerView.setPadding(0, -footerViewHeight, 0, 0);
        }

    }

    public String getTime() {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        return format.format(currentTimeMillis);
    }

    //
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //状态改变的监听
        if (scrollState == SCROLL_STATE_IDLE && getLastVisiblePosition() >= (getCount() - 1)) {
            footerView.setPadding(0, 0, 0, 0);
            setSelection(getCount());
            if (refreshListener != null) {
                refreshListener.onRefresh(MODE_FOOTER_REFRESH);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //滑动过程中的监听
    }

    public interface OnRefreshListener {
        void onRefresh(int loadMode);
    }

    public void setRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }
}


