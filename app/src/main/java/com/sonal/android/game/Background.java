package com.sonal.android.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by User on 01-08-2016.
 */
public class Background {
    private Bitmap image;
    private int x , y , dx;

    public Background (Bitmap res){
       image = res;
        dx = GamePanel.MOVED_SPEED;
    }
    public void update(){
        x += dx;
       if(x< -GamePanel.width ){
           x=0;
       }
    }
    public void draw( Canvas canvas){
          canvas.drawBitmap(image,x,y,null  );
        if(x<0){
            canvas.drawBitmap(image , x+GamePanel.width,y,null);
        }
    }

    public void setVector(int dx){
        this.dx=dx;
    }
}
