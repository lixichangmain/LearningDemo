package com.jimro.myrefreshlistview.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by lixichang on 2017/7/27.
 */

public class MyHolder {
    private View convertView;
    private SparseArray<View> sparseArray = new SparseArray<>();

    public MyHolder(Context context, @LayoutRes int id) {
        convertView = LayoutInflater.from(context).inflate(id, null);
        convertView.setTag(this);
    }

    public MyHolder getHolder(View convertView, Context context, @LayoutRes int id) {
        MyHolder myHolder = null;
        if (convertView == null) {
            myHolder = new MyHolder(context, id);
            convertView.setTag(myHolder);
        } else {
            myHolder = (MyHolder) convertView.getTag();
        }
        return myHolder;
    }

    public View getConvertView() {
        return convertView;
    }

    public View findChildView(@IdRes int id) {
        View view = sparseArray.get(id);
        if (view == null) {
            view = convertView.findViewById(id);
            sparseArray.put(id, view);
        }
        return view;
    }
}
