package com.sonal.android.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by User on 11-08-2016.
 */
public class Missile extends GameObject {

        private int speed;
        private int score;
        private Random random = new Random();
        private Animation animation = new Animation();
        private Bitmap spritesheets ;


    public Missile(Bitmap bitmap , int x , int y, int w , int h , int s ,int numFrames){
        super.x= x;
        super.y= y;
        width = w;
         height = h;
        score = s;
        speed = s + (int)(random.nextDouble()*score/30);

        //cap missile speed
        if(speed > 40) speed = 40;
        Bitmap [] image = new Bitmap[numFrames];
        spritesheets = bitmap ;
        for(int i =0 ; i<image.length ;i++){
            image[i] = Bitmap.createBitmap(spritesheets,0, i*height,width,height);
        }
            animation.setFrames(image);
            animation.setDelay(100 -speed);
    }
    public void update(){
        x-=speed;
        animation.update();
    }
    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch (Exception e){

        }
    }

    @Override
    public int getWidth() {
        //for offset effect
        return width - 10;
    }
}
