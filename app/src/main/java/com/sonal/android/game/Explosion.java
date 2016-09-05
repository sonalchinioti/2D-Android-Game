package com.sonal.android.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by User on 24-08-2016.
 */
public class Explosion {
   // not extending game object because we just want to show the explosion when the player dies.
    private int x ;
    private int y;
    private int width ;
    private int height;
    private  int row;
     private Animation animation = new Animation();
    private  Bitmap spritesheet;
    private int total_frames;

    public Explosion(Bitmap res , int x , int y  , int w , int h , int numFrames){
        this.x = x;
        this.y= y;
        this.width = w;
        this.height = h;
        this.total_frames = numFrames ;
        Bitmap [] image = new Bitmap[numFrames];

        spritesheet = res ;
        for(int  i = 0 ; i< image.length ; i++){
            if(i%5 == 0 && i> 0) row++;
            image[i] = Bitmap.createBitmap(spritesheet ,  (i-(5*row))*width , row*height , width ,height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
    }

    public void draw(Canvas canvas){


        if(!animation.isPlayed_once()){
            canvas.drawBitmap(animation.getImage(),x, y , null);
        }
    }

    public void update() {

        if (!animation.isPlayed_once()) {

            animation.update();
        }
    }

    public int  getHeight(){
        return height;
    }

    public boolean  updateCompleted(){
     return    animation.is_completed();
    }
}
