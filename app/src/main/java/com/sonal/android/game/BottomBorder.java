package com.sonal.android.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by User on 20-08-2016.
 */
public class BottomBorder extends GameObject {

    private Bitmap image ;

    public BottomBorder(Bitmap res , int x  , int y  ){
        height = 200 ;
        width =  20 ;
        this.x = x;
        this.y = y;
        dx = GamePanel.MOVED_SPEED ;

        image = Bitmap.createBitmap(res,  0 , 0 ,width ,height);

    }

    public void update(){
        x += dx;
    }

    public void draw (Canvas canvas){


        canvas.drawBitmap(image,x,y, null);
    }
}
