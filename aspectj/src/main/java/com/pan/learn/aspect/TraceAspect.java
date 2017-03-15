/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.pan.learn.aspect;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.pan.learn.annotation.MLog;
import com.pan.learn.annotation.Permission;
import com.pan.learn.internal.AopMap;
import com.pan.learn.internal.PermissionItem;
import com.pan.learn.internal.PermissionListener;
import com.pan.learn.internal.PermissionUtil;
import com.pan.learn.internal.StopWatch;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

@Aspect
public class TraceAspect {

    private static final String TIME_POINTCUT =
            "execution(@ com.pan.learn.annotation.Time * *(..))";

    private static final String LOG_POINTCUT =
            "execution(@ com.pan.learn.annotation.MLog * *(..)) && @annotation(mLog)";

    private static final String PERMISSION_POINTCUT =
            "execution(@ com.pan.learn.annotation.Permission * *(..)) && @annotation(permission)";

    @Pointcut(TIME_POINTCUT)
    public void timePoint() {

    }

    @Pointcut(LOG_POINTCUT)
    public void logPoint(MLog mLog) {

    }

    @Pointcut(PERMISSION_POINTCUT)
    public void permissionPoint(Permission permission) {

    }

    @Around("timePoint()")
    public Object timeRecord(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 实际方法的执行
        Object o = joinPoint.proceed();

        stopWatch.stop();
        Log.e("aspectj", buildLogMessage(className, methodName, stopWatch.getTotalTimeMillis()));

        return o;
    }

    @Around("logPoint(mLog)")
    public Object logRecord(ProceedingJoinPoint joinPoint, MLog mLog) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        Object object = null;
        for (Object o : objects) {
            if (o instanceof AopMap) {
                object = o;
                break;
            }
        }

        String params = "";
        if (object != null) {
            AopMap map = (AopMap) object;

            for (Object key : map.keySet()) {
                params += key + ":" + map.get(key);
            }
        }
        String ss = mLog.logId() + ", " + mLog.logTxt() + ", " + params;
        Log.e("aspectj", "埋点：" + ss);
        return joinPoint.proceed();
    }

    @Around("permissionPoint(permission)")
    public Object permissionRecord(final ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {
        final Object[] object = {null};
        try {
            if (permission == null) {
                object[0] = joinPoint.proceed();
                return object[0];
            }

            if (permission.runIgnorePermission()) {
                object[0] = joinPoint.proceed();
                return object[0];
            }

            String[] permissions = permission.permissions();
            PermissionItem permissionItem = new PermissionItem(permissions);
            if (permissions != null && permissions.length > 0) {
                String rationalMsg = chooseContent(ApplicationSDK.application, permission.rationalMessage(),
                        permission.rationalMsgResId());
                permissionItem.rationalMessage(rationalMsg);

                PermissionUtil.instance(ApplicationSDK.application).check(permissionItem, new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        try {
                            object[0] = joinPoint.proceed();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void permissionDenied() {

                    }
                });
            }
        } catch (Exception e) {
            object[0] = joinPoint.proceed();
        }
        return object[0];
    }

    private static String chooseContent(Context context, String strContent, int resId) {
        if (context == null) {
            return null;
        }

        if (TextUtils.isEmpty(strContent)) {
            if (resId <= 0) {
                return strContent;
            }

            try {
                return context.getString(resId);
            } catch (Resources.NotFoundException e) {
                return strContent;
            }
        }

        return strContent;
    }

    private static String buildLogMessage(String className, String methodName, long methodDuration) {
        StringBuilder message = new StringBuilder();
        message.append(className + " --> ");
        message.append(methodName);
        message.append(" --> ");
        message.append("[");
        message.append(methodDuration);
        message.append("ms");
        message.append("]");
        return message.toString();
    }
}
