package com.example.animmatch;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    private static LruCache<String, Bitmap> mMemoryCache;
    public static  int pageTotal = -1;
    public static  int tail = 0;


   public static Bitmap getBitmapFromAssets(Context context,String assetDir,String fileName){
       Bitmap bitmap = null;
       AssetManager assetManager = context.getAssets();
       try {
           InputStream inputStream = assetManager.open(assetDir + "/"+fileName);
           bitmap = BitmapFactory.decodeStream(inputStream);
       } catch (IOException e) {
           e.printStackTrace();
       }
       return bitmap;

   }

    /**
     * 从assets加载资源不宜过多，如果太多建议使用压缩包的方式
     * @param context
     * @param assetDir
     * @return
     */
   public static void getBitmapListFromAssets(Context context, String assetDir){
       //List<Bitmap> bitmaps = new ArrayList<>();
       String[] files;
       try {
           files = context.getResources().getAssets().list(assetDir);
           initCache();

           for(int i = 0; i < files.length; i ++){
               //bitmaps.add(getBitmapFromAssets(context,assetDir,files[i]));
               addBitmapToMemoryCache(i+"",getBitmapFromAssets(context,assetDir,files[i]));
           }
       } catch (IOException e1) {
           e1.printStackTrace();
       }



      // return bitmaps;
   }

   public static List<Bitmap> getBitmapsByIndexFromAssets(Context context,String assetDir,int pageIndex,int pageLimit){

       List<Bitmap> bitmaps = new ArrayList<>();
       String[] files;
       try {
           files = context.getResources().getAssets().list(assetDir);

            pageTotal = files.length / pageLimit;
            tail = files.length % pageLimit;
           if(pageIndex <= pageTotal){

               if(pageIndex * pageLimit <= files.length){
                   for(int i = (pageIndex - 1) * pageLimit; i < pageIndex * pageLimit; i ++){
                       bitmaps.add(getBitmapFromAssets(context,assetDir,files[i]));
                   }
               }

           }else{
               if(tail == 0){
                   for(int i = 0; i < pageLimit; i ++){
                       bitmaps.add(getBitmapFromAssets(context,assetDir,files[i]));
                   }
               }else{
                   for(int i = (pageIndex - 1) * pageLimit; i < (pageIndex - 1) * pageLimit + tail; i ++){
                       bitmaps.add(getBitmapFromAssets(context,assetDir,files[i]));
                   }
               }

           }


       } catch (IOException e1) {
           e1.printStackTrace();
       }



       return bitmaps;
   }

    /**
     * 获取assets指定目录下文件个数
     * @param context
     * @param dir
     * @return
     */
   public int getAssetsDirFileCount(Context context,String dir){
       int count = -1;
       if(!TextUtils.isEmpty(dir)) return count;
       try {
           String[] files = context.getResources().getAssets().list(dir);
           count = files == null? -1: files.length;

       } catch (IOException e) {
           e.printStackTrace();
           count = -1;
       }
       return count;
   }

    /**
     * 缩放bitmap
     * @param oriBitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
   public static Bitmap scaleBitmap(Bitmap oriBitmap,int newWidth,int newHeight){
       if(oriBitmap == null) return null;

       // 获得图片的宽高
       int width = oriBitmap.getWidth();
       int height = oriBitmap.getHeight();


       float scaleWidth = ((float) newWidth) / width;
       float scaleHeight = ((float) newHeight) / height;

       Matrix matrix = new Matrix();
       matrix.postScale(scaleWidth, scaleHeight);
       Bitmap newbm = Bitmap.createBitmap(oriBitmap, 0, 0, width, height, matrix,
               true);
       return newbm;

   }

   public static void initCache(){
       int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
       // 使用最大可用内存值的1/8作为缓存的大小。
       int cacheSize = maxMemory / 8;
       mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
           protected int sizeOf(Integer key, Bitmap bitmap) {
               // 重写此方法来衡量每张图片的大小，默认返回图片数量。
               return bitmap.getByteCount() / 1024;
           }
       };
   }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }







}







