/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.pan.learn.internal;

/**
 * Created by panhongchao on 17/3/15.
 */
public interface PermissionListener {
    public void permissionGranted();
    public void permissionDenied();
}
