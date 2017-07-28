package com.jimro.myrefreshlistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.jimro.myrefreshlistview.adapter.MyAdapter;
import com.jimro.myrefreshlistview.view.MyRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyRefreshListView listView;
    private MyAdapter myAdapter;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (MyRefreshListView) findViewById(R.id.list_view);
        list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("这是第" + i + "条数据");
        }

        listView.setRefreshListViewListener(new MyRefreshListView.OnRrefreshListViewListener() {
            @Override
            public void onRefresh(final int loadMode) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (loadMode == MyRefreshListView.LOAD_MODE_ARROW) {
                            list.add(0, "我是下拉刷新出来的数据");
                        } else if (loadMode == MyRefreshListView.LOAD_MODE_FOOTER) {
                            list.add("我是上拉加载更多的数据");
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                                listView.onCompleteRefresh(loadMode);
                            }
                        });

                    }
                }.start();
            }
        });

        myAdapter = new MyAdapter(list, R.layout.layout_listview_item);
        listView.setAdapter(myAdapter);

    }
}
