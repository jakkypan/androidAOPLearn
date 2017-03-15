/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.pan.learn.aspect;

import android.app.Application;

/**
 * Created by panhongchao on 17/3/15.
 */
public class ApplicationSDK {
    public static Application application;

    public static void init(Application app) {
        if (app == null) {
            throw new IllegalArgumentException("application must not be null");
        }
        application = app;
    }
}
