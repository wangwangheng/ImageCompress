package com.xinye.image.compress;

import android.content.Context;

public class App {

    private App() {

    }

    private static class Generator{
        private static final App INSTANCE = new App();
    }

    public static App getInstance(){
        return Generator.INSTANCE;
    }

    private Context mContext;

    public void setContext(Context context){
        mContext = context;
    }

    public Context getContext(){
        return mContext;
    }

}
