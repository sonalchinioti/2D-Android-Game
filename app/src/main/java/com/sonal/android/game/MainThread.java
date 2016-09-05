package com.sonal.android.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by User on 27-07-2016.
 */
public class MainThread extends Thread {
    private int fps = 30;
    private double average_fps;
    private SurfaceHolder surface_holder;
    private GamePanel game_panel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surface_holder , GamePanel game_panel){
        super();
        this.surface_holder=surface_holder;
        this.game_panel=game_panel;

    }
    @Override
    public void run(){
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/fps;
        while(running){
            startTime = System.nanoTime();
            canvas = null;
            try{
                canvas =  this.surface_holder.lockCanvas();
                synchronized (surface_holder){
                    this.game_panel.update();
                    this.game_panel.draw(canvas);
                }
            }catch (Exception e){}
            finally {

                if(canvas != null){
                    surface_holder.unlockCanvasAndPost(canvas);
                }
            }
            timeMillis = (System.nanoTime()-startTime)/1000000;
            waitTime = targetTime - timeMillis;
            if(waitTime > 0) {
                try {
                    this.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            totalTime += System.nanoTime() -startTime;
            frameCount++;
            if(frameCount == fps){
                average_fps = 1000/((totalTime/frameCount)/1000000);
                frameCount =0 ;
                totalTime =0;
                System.out.println(average_fps);
            }
        }


}
    public void setRunning(Boolean b){
       running=b;
    }
}
