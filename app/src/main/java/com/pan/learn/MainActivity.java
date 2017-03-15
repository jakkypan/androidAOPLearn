/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.pan.learn;

import java.util.HashMap;
import java.util.Map;

import com.pan.learn.annotation.MLog;
import com.pan.learn.annotation.Permission;
import com.pan.learn.annotation.Time;
import com.pan.learn.internal.AopMap;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by panhongchao on 17/3/14.
 */
public class MainActivity extends AppCompatActivity {
    String[] arrys = new String[100];

    @Time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = (ListView) findViewById(R.id.list);
        View view = getLayoutInflater().inflate(R.layout.header, null);
        for (int i = 0; i < 100; i++) {
            arrys[i] = "innerList" + i;
        }
//        list.setAdapter(new ArrayAdapter(this, 0));
        list.setAdapter(new MyAdapter());
        list.addHeaderView(view);

        TextView v = (TextView) findViewById(R.id.open_camera);
        v.setOnClickListener(new View.OnClickListener() {
            @Permission(permissions = {Manifest.permission.CAMERA}, rationalMessage = "请打开相机权限")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        });
    }

    @Time
    @Override
    protected void onResume() {
        super.onResume();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrys.length;
        }

        @Override
        public Object getItem(int position) {
            return arrys[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Time
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                holder.textView = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setBackgroundResource(R.drawable.logo);
            holder.textView.setText(getItem(position) + "");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AopMap<String, Object> map = new AopMap<String, Object>();
                    map.put("position", position);
                    doRealClick(v, map);
                }
            });
            return convertView;
        }

        @MLog(logId = "list_item_click_id", logTxt = "列表点击埋点")
        private void doRealClick(View v, AopMap<String, Object> runnableData) {
            Toast.makeText(MainActivity.this, "you clicked postion: " + runnableData.get("position") + ", and has recorded into "
                    + "log file", Toast
                    .LENGTH_LONG)
                    .show();
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }
}