package com.souradip.facebookgametrainer;/*
 *  Copyright &copy; 2016, TopCoder, Inc. All rights reserved
 *
 *  /// <summary>
 *  ///     This class 
 *  /// </summary>
 *  /// <author>TCSCODER</author>
 *  /// <threadsafety>This class is thread-safe</threadsafety>
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.ImageView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageManager {

    public static Bitmap getBitmapFromImage(String name){
        Bitmap screen = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+
                File.separator + name);
        return screen;
    }

    public static Bitmap sliceWidth(Bitmap source, int start_Y, int end_y) {
        return Bitmap.createBitmap(source,0,start_Y,source.getWidth(),end_y-start_Y);
    }

    public static void saveBitmap(Bitmap bitmap, String name){
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, name); // the File to save to
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void swipe(int x1, int y1, int x2, int y2){
        RootManager.runRootCommands(new String[]{"/system/bin/input swipe "+x1+" "+y1+" "+x2+" "+y2}, false);
    }

    public static void play(Bitmap ball, int ball_y, Bitmap basket, int basket_y){
        int startX = ball.getWidth();
        int endX = 0;
        for (int i=0;i<ball.getWidth();i++){
            if(!(Color.blue(ball.getPixel(i, 0))>240)){
                startX = Math.min(startX,i);
                endX = Math.max(endX,i);
            }
        }

        int startX2 = basket.getWidth();
        int endX2 = 0;
        for (int i=0;i<basket.getWidth();i++){
            if(!(Color.blue(basket.getPixel(i, 0))>240)){
                startX2 = Math.min(startX2,i);
                endX2 = Math.max(endX2,i);
            }
        }
        swipe((startX + endX) / 2, ball_y, (startX2 + endX2) / 2, basket_y);
    }
}
