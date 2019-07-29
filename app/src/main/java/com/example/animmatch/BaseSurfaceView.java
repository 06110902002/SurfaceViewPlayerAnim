package com.example.animmatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int DEFAULT_FRAME_DURATION_MILLISECOND = 1000;
    //用于计算帧数据的线程
    private HandlerThread handlerThread;
    private Handler handler;
    //帧刷新频率
    private int frameDuration = DEFAULT_FRAME_DURATION_MILLISECOND;
    //用于绘制帧的画布
    private Canvas canvas;
    private boolean isAlive;
    protected int defaultWidth;
    protected int defaultHeight;


    protected int getDefaultWidth() {
        return defaultWidth;
    }

    protected int getDefaultHeight() {
        return defaultHeight;
    }

    public void setFrameDuration(int frameDuration){
        this.frameDuration = frameDuration;
    }

    public BaseSurfaceView(Context context) {
        super(context);
        init(context);
    }
    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        getHolder().addCallback(this);
        //设置透明背景，否则SurfaceView背景是黑的
        setBackgroundTransparent();
    }

    private void setBackgroundTransparent() {
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isAlive = true;
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
        isAlive = false;
    }

    //停止帧绘制线程
    private void stopDrawThread() {
        handlerThread.quit();
        handler = null;
    }

    //启动帧绘制线程
    private void startDrawThread() {
        handlerThread = new HandlerThread("SurfaceViewThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(new DrawRunnable());
    }

    private class DrawRunnable implements Runnable {

        @Override
        public void run() {
            if (!isAlive) {
                return;
            }
            try {
                //1.获取画布
                canvas = getHolder().lockCanvas();
                //2.绘制一帧
                onFrameDraw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //3.将帧数据提交
                getHolder().unlockCanvasAndPost(canvas);
                //4.一帧绘制结束
                onFrameDrawFinish();
            }
            //不停的将自己推送到绘制线程的消息队列以实现帧刷新
            handler.postDelayed(this, frameDuration);
        }
    }

    protected abstract void onFrameDrawFinish();

    protected abstract void onFrameDraw(Canvas canvas);
}
