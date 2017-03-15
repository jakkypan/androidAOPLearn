/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.pan.learn;

import com.pan.learn.aspect.ApplicationSDK;

import android.app.Application;

/**
 * Created by panhongchao on 17/3/14.
 */
public class MyApplication extends Application {
    public static MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        ApplicationSDK.init(this);
    }
}
