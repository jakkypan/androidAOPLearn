/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.pan.learn.internal;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

/**
 * Created by panhongchao on 17/3/14.
 */
public class PermissionUtil {
    static PermissionUtil instance;
    Context context;

    private PermissionUtil(Context context) {
        this.context = context;
    }

    public static PermissionUtil instance(Context context) {
        if (instance == null) {
            instance = new PermissionUtil(context);
        }
        return instance;
    }

    public void check(PermissionItem permissionItem, PermissionListener listener) {
        if (hasSelfPermissions(context, permissionItem.permissions)) {
            if (listener != null) {
                listener.permissionGranted();
            }
        } else {
            ShadowPermissionActivity.start(context
                    , permissionItem.permissions
                    , permissionItem.rationalMessage
                    , permissionItem.rationalButton
                    , permissionItem.needGotoSetting
                    , permissionItem.settingText
                    , permissionItem.deniedMessage
                    , permissionItem.deniedButton
                    , listener);
        }
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        if (!isOverMarshmallow()) {
            return denyPermissions;
        }

        for (String value : permission) {
            if (isOverMarshmallow() && value.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if(!Settings.canDrawOverlays(activity)) {
                    denyPermissions.add(value);
                }
            } else if(isOverMarshmallow() && value.equals(Manifest.permission.WRITE_SETTINGS)) {
                if(!Settings.System.canWrite(activity)) {
                    denyPermissions.add(value);
                }
            } else if(PermissionChecker.checkSelfPermission(activity, value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasSelfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (isOverMarshmallow() && permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!Settings.canDrawOverlays(context)) {
                    return false;
                }
            } else if (isOverMarshmallow() && permission.equals(Manifest.permission.WRITE_SETTINGS)) {
                if (!Settings.System.canWrite(context)) {
                    return false;
                }
            } else if (PermissionChecker.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
