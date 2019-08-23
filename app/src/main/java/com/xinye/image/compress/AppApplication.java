package com.xinye.image.compress;

import android.app.Application;

import com.zxy.tiny.Tiny;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        App.getInstance().setContext(this);
        Tiny.getInstance().init(this);
    }
}
