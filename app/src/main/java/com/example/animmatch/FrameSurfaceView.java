package com.example.animmatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class FrameSurfaceView extends BaseSurfaceView {
    public static final int INVALID_BITMAP_INDEX = 0;
    private List<Integer> bitmaps = new ArrayList<>();
    private Bitmap frameBitmap;
    private int bitmapIndex = 0;
    private Paint paint = new Paint();
    private BitmapFactory.Options options;

    private Rect srcRect;
    private Rect dstRect = new Rect();
    private List<Bitmap> bitmapList;
    private Context mContext;
    private final int pageLimit = 10;
    private final int pageIndex = 1;
    private int index = 1;



    public void setDuration(int duration) {
        int frameDuration = duration / bitmaps.size();
        setFrameDuration(frameDuration);
    }

    public void setBitmaps(List<Integer> bitmaps) {
        if (bitmaps == null || bitmaps.size() == 0) {
            return;
        }
        this.bitmaps = bitmaps;
        getBitmapDimension(bitmaps.get(0));
    }

    private void getBitmapDimension(Integer integer) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), integer, options);
        defaultWidth = options.outWidth;
        defaultHeight = options.outHeight;
        srcRect = new Rect(0, 0, defaultWidth, defaultHeight);
    }

    public FrameSurfaceView(Context context) {
        super(context);
    }
    public FrameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mContext = context;
        //定义解析Bitmap参数为可变类型，这样才能复用Bitmap
        options = new BitmapFactory.Options();
        options.inMutable = true;
        bitmapList = FileUtil.getBitmapsByIndexFromAssets(context,"test",1,pageLimit);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        dstRect.set(0, 0, getWidth(), getHeight());
    }



    @Override
    protected void onFrameDrawFinish() {
        //每帧绘制完毕后不再回收
        //recycle();
    }

    public void recycle() {
        if (frameBitmap != null) {
            frameBitmap.recycle();
            frameBitmap = null;
        }
    }

    @Override
    protected void onFrameDraw(Canvas canvas) {
        clearCanvas(canvas);
//        if (!isStart()) {
//            return;
//        }
        if (!isFinish()) {
            drawOneFrame(canvas);
        } else {
            onFrameAnimationEnd();
        }
    }

    private void drawOneFrame(Canvas canvas) {
        if(bitmapIndex < pageLimit ){
            System.out.println("114-----------:query bitmapList index:"+ bitmapIndex);
            frameBitmap = bitmapList.get(bitmapIndex);
            frameBitmap = FileUtil.scaleBitmap(frameBitmap,380,480);
            bitmapIndex ++;

        }else{
            bitmapIndex = 0;
        }

        if(bitmapIndex >= FileUtil.pageTotal){
            bitmapIndex = 0;
        }


        if(bitmapIndex == bitmapList.size() - 1){
            bitmapIndex = 0;
            loadNewBitmap();
        }

        //复用上一帧Bitmap的内存
        options.inBitmap = frameBitmap;
        canvas.drawBitmap(frameBitmap, srcRect, dstRect, paint);

    }

    private void onFrameAnimationEnd() {
        reset();
    }

    private void reset() {
        bitmapIndex = INVALID_BITMAP_INDEX;
    }

    private boolean isFinish() {
        //return bitmapIndex >= bitmaps.size();
        return false;
    }

    private boolean isStart() {
        return bitmapIndex != INVALID_BITMAP_INDEX;
    }

    public void start() {
        bitmapIndex = 0;
    }

    private void clearCanvas(Canvas canvas) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    /**
     * 倒数第二张的时候预加载图片资源，并将之前的图片释放
     */
    private void loadNewBitmap(){
        if(bitmapList != null){
            bitmapList.clear();
        }
        if(index < 20){
            index ++;
        }else{
            index = 1;
        }
        bitmapList.addAll(FileUtil.getBitmapsByIndexFromAssets(mContext,"test",index,pageLimit));
        System.out.println("171-----------loadNewBitmap index:"+index + "  bitmapList_size:"+bitmapList.size());

    }
}