package com.sonal.android.game;

import android.graphics.Bitmap;

/**
 * Created by User on 06-08-2016.
 */
public class Animation {

    private Bitmap[] frames;
    private int current_frame;
    private long start_time;
    private long delay;
    private boolean played_once;




    public void setFrames(Bitmap[] frames) {
        this.frames = frames;
        current_frame = 0;
        start_time = System.nanoTime();
    }

    public void setDelay(long d){
        this.delay = d;
    }


    public void setFrames(int frame){
        current_frame=frame;

    }


    public void update(){
        long elapsed =  (System.nanoTime() - start_time);
                if(elapsed > delay){
                    current_frame ++;
                    start_time = System.nanoTime();
                }
        if(current_frame == frames.length){
            current_frame =0 ;
            played_once = true;
        }
    }

    public Bitmap getImage(){
        return frames[current_frame];

    }
    public int getFrame (){
        return current_frame;
    }

    public boolean isPlayed_once(){
        return played_once;
    }
    public boolean is_completed (){
        if(getFrame() == frames.length){
            return true ;
        }else {
            return false ;
        }
    }
}
