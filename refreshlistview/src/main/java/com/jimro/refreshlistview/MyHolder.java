package com.jimro.refreshlistview;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by lixichang on 2017/7/26.
 */

public class MyHolder {
    private View converView;
    private SparseArray<View> sparseArray = new SparseArray<>();

    public MyHolder(Context context, @LayoutRes int id) {
        converView = LayoutInflater.from(context).inflate(id, null);
        converView.setTag(this);
    }

    public View getConverView() {
        return converView;
    }

    public static MyHolder getHolder(View converView, @LayoutRes int id, Context context) {
        MyHolder myHolder = null;
        if (converView == null) {
            myHolder = new MyHolder(context, id);
        } else {
            myHolder = (MyHolder) converView.getTag();
        }
        return myHolder;
    }

    public View findChildView(@IdRes int id) {
        View view = sparseArray.get(id);
        if (view == null) {
            view = converView.findViewById(id);
            sparseArray.put(id, view);
        }
        return view;
    }
}
