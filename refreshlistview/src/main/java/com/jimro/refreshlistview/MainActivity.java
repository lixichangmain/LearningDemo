package com.jimro.refreshlistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RefreshListView listView;
    private ArrayList<String> list;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (RefreshListView) findViewById(R.id.list_view);
        list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add("这是第" + i + "条信息");
        }

        listView.setRefreshListener(new RefreshListView.OnRefreshListener() {
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

                        if (loadMode==RefreshListView.MODE_REFRESH){
                            list.add(0, "我是下拉刷新出来的数据");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myAdapter.notifyDataSetChanged();
                                    listView.onRefreshComplete(RefreshListView.MODE_REFRESH);
                                }
                            });
                        }else if (loadMode == RefreshListView.MODE_FOOTER_REFRESH){
                            list.add("上拉加载出来的数据");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myAdapter.notifyDataSetChanged();
                                    listView.onRefreshComplete(RefreshListView.MODE_FOOTER_REFRESH);
                                }
                            });
                        }

                    }
                }.start();
            }
        });


        myAdapter = new MyAdapter(list, R.layout.list_item_layout);
        listView.setAdapter(myAdapter);
    }
}
