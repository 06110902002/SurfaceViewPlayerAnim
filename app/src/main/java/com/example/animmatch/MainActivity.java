package com.example.animmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private List<Bitmap> test;
    private Bitmap bitmap;
    private int index = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img_test);
        test = FileUtil.getBitmapsByIndexFromAssets(this,"test",1,10);
        bitmap = test.get(0);

        findViewById(R.id.btn_pkm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPkmAnim();
            }
        });

        findViewById(R.id.btn_png).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPngAnim();
            }
        });


        findViewById(R.id.btn_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index > 1){
                    index --;
                }else{
                    index = 1;
                }
                test.clear();
                test = FileUtil.getBitmapsByIndexFromAssets(MainActivity.this,"test",index,10);

                System.out.println("61-----------:size:"+test.size() + " page:"+index);
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(index < 20){
                    test.clear();
                    test = FileUtil.getBitmapsByIndexFromAssets(MainActivity.this,"test",index,10);
                    System.out.println("65-----------:size:"+test.size()+ " page:"+index);
                    index ++;
                }else{
                    index = 1;
                }
            }
        });

    }


    private void startPkmAnim(){
//        List<Bitmap> mm= FileUtil.getBitmapListFromAssets(this,"test");
//        FileUtil.scaleBitmap(mm.get(0),320,480);

        test.clear();
        System.out.println("48-----------:size:"+(13 / 3) + " :"+ (13 % 3));
        System.out.println("49-------------:"+bitmap);
        img.setImageBitmap(bitmap);


    }

    private void startPngAnim(){

        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation_set);
        img.startAnimation(animationSet);

    }
}
