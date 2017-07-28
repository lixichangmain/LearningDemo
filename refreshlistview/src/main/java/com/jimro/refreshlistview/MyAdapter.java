package com.jimro.refreshlistview;

import android.support.annotation.LayoutRes;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lixichang on 2017/7/26.
 */

public class MyAdapter extends MyBaseAdapter {
    private List<String> list;

    public MyAdapter(List list, @LayoutRes int id) {
        super(list, id);
        this.list = list;
    }

    @Override
    public void fillData(int position, MyHolder myHolder) {
        ((TextView) myHolder.findChildView(R.id.list_text)).setText(list.get(position));
    }
}
