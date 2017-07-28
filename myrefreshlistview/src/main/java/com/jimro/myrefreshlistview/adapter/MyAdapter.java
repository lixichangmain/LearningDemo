package com.jimro.myrefreshlistview.adapter;

import android.support.annotation.LayoutRes;
import android.widget.TextView;

import com.jimro.myrefreshlistview.R;

import java.util.List;

/**
 * Created by lixichang on 2017/7/27.
 */

public class MyAdapter extends MyBaseAdapter {
    private List<String> list;

    public MyAdapter(List list, @LayoutRes int id) {
        super(list, id);
        this.list = list;
    }

    @Override
    public void fillDate(int position, MyHolder myHolder) {
        ((TextView) myHolder.findChildView(R.id.tv_listview)).setText(list.get(position));
    }
}
