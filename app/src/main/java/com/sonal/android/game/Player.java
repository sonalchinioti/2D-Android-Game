package com.sonal.android.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;


/**
 * Created by User on 06-08-2016.
 */
public class Player  extends GameObject{

    private Bitmap spritesheet;
    private int score;
    //variable for accelaration
    private boolean up;
    private boolean playing ;
    private Animation animation = new Animation();
    private long start_time;

    public Player (Bitmap bitmap , int w , int h , int nunofFrames ){
        x= 100;
        y=GamePanel.height/2;
        dy =0;
        score =0;
        height =h;
        width =w;
        spritesheet = bitmap;
        Bitmap [] image = new Bitmap [nunofFrames];

        for(int i = 0 ; i < image.length ; i++){

            image[i] =  Bitmap.createBitmap(spritesheet, i*width,0,width,height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
        start_time =System.nanoTime();
    }


    public void setUp(boolean b){
        up=b;
    }
    public void update(){
        long elapsed = (System.nanoTime()-start_time)/1000000;

        if(elapsed >100 ){
            score++;
            start_time = System.nanoTime();

        }
        animation.update();
        if(up){
            dy -=1;
        }else{
            dy+=1;
        }

        if(dy>14) dy =14;

        if(dy< -14) dy =-14;

        y += dy*2;

    }
     public void draw(Canvas canvas){
         canvas.drawBitmap(animation.getImage(), x, y, null);
     }

    public int getScore(){
        return score;
    }
    public boolean getPlaying (){
        return playing ;
    }
    public void setPlaying(boolean b){
        playing = b;
    }
    public void resetDY(){
      dy= 0;
    }
    public void resetScore(){
        score=0;
    }
    }


