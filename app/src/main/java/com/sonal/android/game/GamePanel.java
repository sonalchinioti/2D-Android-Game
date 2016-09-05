package com.sonal.android.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by User on 26-07-2016.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback  {
    public static final int width = 856;
    public static final int height =480;
    private long SmokeStartTime;
    private long MissilesStartTime;

    private MainThread thread;
    public static final int MOVED_SPEED= -5;
    private Background background;
    private Player player;
    private ArrayList<SmokePuff> smokePuff;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topBorders;
    private ArrayList<BottomBorder> bottomBorders;
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;
    // increase to slow down difficulty progression  decrease to speed up difficulty progression.
    private int progressDenom = 20;
    private Random rand = new Random();
    private Explosion explosion ;
    private long  startReset;
    private boolean reset;
    private boolean disappear;
    private boolean started;
    private int best ;
   private MediaPlayer sound_collision;


    public GamePanel(Context context){
        super(context);
        //add call back to the surface view.
        getHolder().addCallback(this);

        // making  focusable
        setFocusable(true);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        background = new Background (BitmapFactory.decodeResource(getResources(),R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.helicopter ) ,65,25,3);
        smokePuff = new ArrayList<SmokePuff>();
        missiles =  new ArrayList<Missile>();
        topBorders = new ArrayList<TopBorder>();
        bottomBorders = new ArrayList<BottomBorder>();
        sound_collision =  MediaPlayer.create(getContext(),R.raw.helicopter_crash);
        SmokeStartTime = System.nanoTime();
        MissilesStartTime = System.nanoTime();
        background.setVector(-5);

        thread=  new MainThread(getHolder(),this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int count = 0;
        while(retry && count < 1000){
            count++;
            try {
                thread.setRunning(false);
                thread.join();
                retry =false;
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if(!player.getPlaying()&& newGameCreated || reset){
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying()){

                if(!started)started= true;
                reset = false;
                player.setUp(true);

            }

            return true;
        }
        if (event.getAction()== MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }
    public void update(){
        if(player.getPlaying()){

            if(bottomBorders.isEmpty() || topBorders.isEmpty()){
                player.setPlaying(false);
                return  ;
            }

            background.update();
            player.update();

            //calculate the threshold height border can have based on score.
            maxBorderHeight =  30 + player.getScore()/progressDenom;

            //cap max border height so  border can only take up of total of half a screen.
            if(maxBorderHeight > height/4)  maxBorderHeight=height/4;

            minBorderHeight =  5 + player.getScore()/progressDenom;

                // creating collision for bottom borders
            for(int i =0 ; i < bottomBorders.size();i++ ){
                if(collison(bottomBorders.get(i),player)){
                    player.setPlaying(false);
                }
            }

            // checking collision for top borders
            for(int  i =0 ; i<topBorders.size(); i++){
                if(collison(topBorders.get(i),player)){
                    player.setPlaying(false);
                }
            }


            //create top border method.
           this.updateTopBorder();
            //create Bottom border method
            this.updateBottomBorder();

            //add Missiles on timer
            long MissilesElapsed = (System.nanoTime()-MissilesStartTime)/1000000;

            if(MissilesElapsed >(2000-player.getScore()/4)){
                //first missiles goes down the  middle
                if(missiles.size() == 0){
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            width+10,height/2,45,15,player.getScore()  ,13));
                }else{
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            width+10,(int)(rand.nextDouble()*(height-(maxBorderHeight*2)))+maxBorderHeight,45,15,player.getScore(),13));
                }
                //Reset the timer
                MissilesStartTime=System.nanoTime();
            }

            // updates the Missiles
            for (int i =0 ; i<missiles.size();i++){
                missiles.get(i).update();
                if(collison(missiles.get(i),player)){
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                if(missiles.get(i).getX() < -100){
                    missiles.remove(i);
                }
            }
            long elapsed = (System.nanoTime() - SmokeStartTime) /1000000;
            if(elapsed > 120 ){
                smokePuff.add(new SmokePuff(player.getX(),player.getY()+10));
                SmokeStartTime = System.nanoTime();
            }

            for(int  i =0 ; i<smokePuff.size() ; i++){
                smokePuff.get(i).update();
                if(smokePuff.get(i).getX() < -10){
                    smokePuff.remove(i);
                }
            }
        }else{
              player.resetDY();

            if(!reset){

                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                //it will decide wether we have to show the helicopter or not .
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources() ,R.drawable.explosion), player.getX(),player.getY() -30
                 ,100,100 ,25);
            }
            explosion.update();
            long elapsed_time = (System.nanoTime() - startReset)/1000000;
            if(elapsed_time > 2500 && !newGameCreated){
                newGame();
            }
            newGameCreated = false;
            if(!newGameCreated){
                newGame();
            }
        }

    }
     public boolean collison(GameObject a,GameObject b){

         if(Rect.intersects(a.getRectangle(), b.getRectangle())){
             sound_collision.start();
             return true;
         }

            return false;
     }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int height2 = getHeight();
        final float scaleFactorX =(float) getWidth()/(width*1.f);
        final float scaleFactorY =(float)getHeight()/(height*1.f) ;
        if(canvas != null) {
            final int saved_state = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);
            if(!disappear){
                player.draw(canvas);
            }


            for(SmokePuff sp :smokePuff ){
                sp.draw(canvas);
            }

            for(Missile m: missiles){
                m.draw(canvas);
            }

            for(TopBorder tb : topBorders){
                tb.draw(canvas);
            }
            for(BottomBorder bb :bottomBorders){
                bb.draw(canvas);
            }
            // draw explosion

            if(started && !player.getPlaying()){
                explosion.draw(canvas);
            }

            drawText(canvas);
            canvas.restoreToCount(saved_state);

        }
    }

    public void updateTopBorder(){
     // every 50 points insert randomly placed top blocks that break the pattern

        if(player.getScore()%50 == 0){
            topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    topBorders.get(topBorders.size() - 1).getX() + 20, 0, (int) rand.nextDouble() * maxBorderHeight));
        }
        for(int i =0 ; i<topBorders.size(); i++){

            topBorders.get(i).update();

            if(topBorders.get(i).getX()<-20){
                topBorders.remove(i);
                //replace this element from the array list by adding the new one.

                // calculate top down which determines the  direction  in which border is moving (top or down.)

                if(topBorders.get(topBorders.size()-1).getHeight() >=maxBorderHeight){
                    topDown = false;
                }

                if (topBorders.get(topBorders.size()-1).getHeight() <= minBorderHeight){
                    topDown = true;
                }
                // new border will have larger height
                if(topDown){
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            topBorders.get(topBorders.size()-1).getX()+20,0 ,topBorders.get(topBorders.size() -1).getHeight()+1));
                }
                //new border will have smaller height
                else{
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            topBorders.get(topBorders.size()-1).getX()+20,0 ,topBorders.get(topBorders.size() -1).getHeight() - 1));
                }
            }
        }


    }

    public void updateBottomBorder(){
        // every 40 ponts

        if(player.getScore() % 40 ==0 ){

            bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                    bottomBorders.get(bottomBorders.size()-1).getX()-20,(int)rand.nextDouble()*maxBorderHeight +(height-maxBorderHeight)));

        }

      // update bottom border
        for(int i =0 ; i<bottomBorders.size(); i++){
            bottomBorders.get(i).update();


            // if the border is moving off the screen remove it and add a new one.
            if(bottomBorders.get(i).getX() < -20){
                bottomBorders.remove(i);


                // calculate top down which determines the  direction  in which border is moving (top or down.)

                if(bottomBorders.get(bottomBorders.size()-1).getY() <= height - maxBorderHeight){
                    botDown = true;
                }

                if (bottomBorders.get(bottomBorders.size()-1).getY()  >= height-  minBorderHeight){
                    botDown = false;
                }

                // new border will have larger height
                if(botDown){
                    bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            bottomBorders.get(bottomBorders.size()-1).getX()+20,bottomBorders.get(bottomBorders.size() -1).getY()+1));
                }
                //new border will have smaller height
                else{
                    bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            bottomBorders.get(bottomBorders.size()-1).getX()+20 ,bottomBorders.get(bottomBorders.size() -1).getY() - 1));
                }
            }
        }

    }

    public void newGame(){

        // new game will be called after 2500 mili  sec.
        disappear = false;
        topBorders.clear();
        bottomBorders.clear();
        smokePuff.clear();
        missiles.clear();
//        explosion=null;
        sound_collision.stop();
        minBorderHeight =5 ;
        maxBorderHeight =30;

//        player.resetDY();
        player.setY(height/2);

        // get the best score
        if((player.getScore() *3 )> best ){
            best = (player.getScore() * 3);
        }

        player.resetScore();
        // creating intial borders

        for(int i =0; i*20<width+40 ; i++){

            // first border ever created .
            if(i ==0 ){
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick), i*20 , 0 ,10));
            }
            else{
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources() ,R.drawable.brick),i*20,0
                        ,topBorders.get(i-1).getHeight()+1));
            }
        }


        // intial bottom border
        for(int i =0 ; i*20<width+40 ;i++){


        // first border ever created
            if(i==0){

                bottomBorders.add(new BottomBorder(BitmapFactory.
                        decodeResource(getResources(), R.drawable.brick),
                        i * 20,  height-minBorderHeight));
            }
            //adding borders until the screen is full.
            else{
                bottomBorders.add(new BottomBorder(BitmapFactory
                        .decodeResource(getResources() ,R.drawable.brick),i*20
                        ,bottomBorders.get(i-1).getY()-1));
            }
        }


        newGameCreated = true;
    }

    public void drawText(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore() * 3), 10, height - 10, paint);
        canvas.drawText("BEST: "+best ,width-215,height -10,paint);

        if(!player.getPlaying() && newGameCreated && reset){
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", width / 2 - 50, height / 2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP" , width/2 -50 ,height/2+20 ,paint1);
            canvas.drawText("RELEASE TO GO DOWN " , width/2 -50 ,height/2+40 ,paint1);

        }
    }
}
