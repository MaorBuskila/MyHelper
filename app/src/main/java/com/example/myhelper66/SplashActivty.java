package com.example.myhelper66;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class SplashActivty extends AppCompatActivity {
    //Initilaize Varibales
    ImageView ivTop, ivAndroidGif , ivGear , ivBottom;
    TextView readyToHelp;
    CharSequence charSequence;
    int index;
    long delay = 200;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activty);

        //Assign variable
        ivTop = findViewById(R.id.iv_top);
        ivAndroidGif = findViewById(R.id.android_gif);
        ivGear = findViewById(R.id.gear_gif);
        readyToHelp = findViewById(R.id.tv_ready_to_help);
        ivBottom = findViewById(R.id.iv_bottom);



        //Set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Initialize top animation
        Animation top_animation = AnimationUtils.loadAnimation(this,R.anim.top_wave);

        //Start top animtion
        ivTop.setAnimation(top_animation);

        //Set animation text
        animatText("My Helper");

        //Load GIF
        ImageView imageView = (ImageView) findViewById(R.id.android_gif);
        Glide.with(this).asGif().load("https://media.giphy.com/media/llarwdtFqG63IlqUR1/giphy.gif").into(ivAndroidGif);


        //Initalize bottom animation
        Animation animation_bottom  = AnimationUtils.loadAnimation(this,R.anim.bottom_wave);

        //Start bottom animtion
        ivBottom.setAnimation(animation_bottom);

        //Initalize Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivty.this
                        ,MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                //Finish activty
                finish();
            }
        }, 4000);


    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //When runnable is run
            //Set text
            readyToHelp.setText(charSequence.subSequence(0, index++));
            //Check condtion
            if (index <= charSequence.length()){
                //When index is equal to text length
                //run hanlder
                handler.postDelayed(runnable,delay);

            }

        }
    };

    //Create animated text method
    public void animatText (CharSequence cs){
        //Set text
        charSequence = cs;
        //Clear index
        index = 0;
        //Clear Text
        readyToHelp.setText("");
        //Remove call back
        handler.removeCallbacks(runnable);
        //Run hanlder
        handler.postDelayed(runnable,delay);

    }

}