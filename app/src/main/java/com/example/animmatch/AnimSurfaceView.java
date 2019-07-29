package com.example.animmatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap mBitmap;
    private int leftMargin = 0;
    private MoveThread moveThread;
    private SurfaceHolder mHolder;
    private Canvas canvas;
    private int bitmapIndex = 0;

    public AnimSurfaceView(Context context) {
        super(context);
        init(context, null);
    }

    public AnimSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        getHolder().addCallback(this);
        moveThread = new MoveThread();
        moveThread.start();

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //设置透明背景
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if(mBitmap != null){
            canvas.drawBitmap(mBitmap, leftMargin, 0, null);

        }


    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //设置透明背景
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }



    /**
     * 移动线程
     */
    private class MoveThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(100);
                    leftMargin += 1;


                    if(bitmapIndex < 200 ){
                        mBitmap = FileUtil.getBitmapFromMemCache(bitmapIndex+"");
                        System.out.println("89-----------:"+mBitmap);
                        mBitmap = FileUtil.scaleBitmap(mBitmap,380,480);
                        bitmapIndex ++;
                    }
                    if(bitmapIndex >= 50){
                        bitmapIndex = 2;
                    }


                    if(mHolder != null){
                        try {
                            canvas = mHolder.lockCanvas();
                            draw(canvas);
                        } finally {
                            if (canvas != null) {
                                mHolder.unlockCanvasAndPost(canvas);
                            }
                            if (mBitmap != null) {
                                mBitmap.recycle();
                                mBitmap = null;
                            }

                        }
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}

