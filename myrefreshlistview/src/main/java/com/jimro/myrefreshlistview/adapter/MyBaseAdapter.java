package com.jimro.myrefreshlistview.adapter;


import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by lixichang on 2017/7/27.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private List<T> list;
    private
    @LayoutRes
    int id;

    public MyBaseAdapter(List<T> list, @LayoutRes int id) {
        this.list = list;
        this.id = id;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder = new MyHolder(parent.getContext(), id);
        fillDate(position, myHolder);
        return myHolder.getConvertView();
    }

    public abstract void fillDate(int position, MyHolder myHolder);
}
