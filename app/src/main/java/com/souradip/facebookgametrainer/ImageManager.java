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

    private static boolean isNegative = false;

    private static long verticalTime = 50;

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
        double actualDistance = Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
        double neededDistance = Math.abs(y2 - y1);
        double t = neededDistance/actualDistance;

        int xNeeded = (int)((1-t)*x1+t*x2);
        int yNeeded = (int)((1-t)*y1+t*y2);

        RootManager.runRootCommands(new String[]{"/system/bin/input touchscreen swipe "+x1+" "+y1+" "+xNeeded+" "+yNeeded}, false);
    }

    public static void play(Bitmap ball, int ball_y, Bitmap basketIntitial, Bitmap basketFinal, int basket_y, double wait, int delay){

        int width = ball.getWidth();
        //Calculate ball
        int ballStartX = width;
        int ballEndX = 0;
        for (int i=0;i<width;i++){
            if(!(Color.blue(ball.getPixel(i, 0))>240)){
                ballStartX = Math.min(ballStartX,i);
                ballEndX = Math.max(ballEndX,i);
            }
        }

        int ballMidpoint = (ballStartX + ballEndX) / 2;

        //Calculate frame1
        int frameInitialStartX = width;
        int frameInitialEndX = 0;
        for (int i=0;i<width;i++){
            if((Color.blue(basketIntitial.getPixel(i, 0)))<100){
                frameInitialStartX = Math.min(frameInitialStartX,i);
                frameInitialEndX = Math.max(frameInitialEndX,i);
            }
        }

        //Calculate frame2
        int frameFinalStartX = width;
        int frameFinalEndX = 0;
        for (int i=0;i<width;i++){
            if((Color.blue(basketFinal.getPixel(i, 0)))<100){
                frameFinalStartX = Math.min(frameFinalStartX,i);
                frameFinalEndX = Math.max(frameFinalEndX,i);
            }
        }

        Log.d("PLAY","Wait: "+ wait);

        double speed =(frameFinalEndX-frameInitialEndX)/wait;
        if(speed!=0) {
            Log.d("PLAY", "Separation: " + (frameFinalEndX-frameInitialEndX));
            Log.d("PLAY", "Speed: " + speed);
        }

        double distanceTravelled = speed * (delay+verticalTime);
        
        int basketMidpoint = getPoint(frameFinalStartX, frameFinalEndX, (int) distanceTravelled, width);

        //Delay adjustment
        if(speed!=0) {
            double additionalDistance = speed*verticalTime;
            if(isNegative){
                //basketMidpoint-=additionalDistance;
            } else{
                //basketMidpoint+=additionalDistance;
            }
            Log.d("PLAY","Additional: "+ additionalDistance);
            if(basketMidpoint>width || basketMidpoint<0)
                Log.e("ERROR","ZERO");
        }

        swipe(ballMidpoint, ball_y, basketMidpoint, basket_y);
    }

    private static int getPoint(int frameFinalStartX, int frameFinalEndX, int distanceTravelled, int width) {

        while(distanceTravelled!=0) {
            if (distanceTravelled < 0) {
                if (frameFinalStartX + distanceTravelled < 0) {
                    frameFinalEndX -= frameFinalStartX;
                    distanceTravelled += frameFinalStartX;
                    distanceTravelled = - distanceTravelled;
                    frameFinalStartX -= frameFinalStartX;
                }
                else {
                    frameFinalStartX += distanceTravelled;
                    frameFinalEndX += distanceTravelled;
                    distanceTravelled = 0;
                    isNegative = true;
                }
            }
            else{
                if (frameFinalEndX + distanceTravelled > width) {
                    frameFinalStartX += width-frameFinalEndX;
                    distanceTravelled -= width-frameFinalEndX;
                    frameFinalEndX = width;
                    distanceTravelled = - distanceTravelled;
                }
                else {
                    frameFinalStartX += distanceTravelled;
                    frameFinalEndX += distanceTravelled;
                    distanceTravelled = 0;
                    isNegative = false;
                }
            }
        }

        return (frameFinalStartX + frameFinalEndX) / 2;
    }
}
