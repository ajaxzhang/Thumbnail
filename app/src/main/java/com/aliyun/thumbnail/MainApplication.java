package com.aliyun.thumbnail;

import android.app.Application;

/**
 * Created by ajax on 2018/4/29.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("live-openh264");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
    }
}
