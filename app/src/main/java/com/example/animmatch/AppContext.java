package com.example.animmatch;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.List;

public class AppContext extends Application {

//    public static List<Bitmap> mBitmaps;
//    public synchronized static List<Bitmap> getBitmaps(){
//        return mBitmaps;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        new LoadBitmapResThread().start();
    }

    private class LoadBitmapResThread extends Thread{

        @Override
        public void run() {
            super.run();
            //FileUtil.getBitmapListFromAssets(AppContext.this,"test");
        }
    }

}
