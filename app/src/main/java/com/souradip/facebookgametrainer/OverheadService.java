package com.souradip.facebookgametrainer;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class OverheadService extends Service {

    public static final int WAIT = 100;
    private WindowManager windowManager;
    private ImageView chatHead;
    private ImageView close;
    WindowManager.LayoutParams params;

    private static boolean isTouched;
    private static long time;

    private static final String path = "/sdcard/img.png";
    private static final String name = "img.png";

    public OverheadService() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.mipmap.ic_launcher);

        close = new ImageView(this);
        close.setImageResource(R.mipmap.ic_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverheadService.super.onDestroy();
                if (chatHead != null) windowManager.removeView(chatHead);
                if (close != null) windowManager.removeView(close);
                stopSelf();
            }
        });

        chatHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pid = getProcessId("com.facebook.orca");
                if (pid == -1) {
                    showFacebookNotOpenDialog();
                    return;
                } else {
                    //Optimize
                    RootManager.runRootCommands(new String[]{"renice -10 "+android.os.Process.myPid()}, true);

                    play(pid);
//                    if(!isTouched) {
//                        RootManager.runRootCommands(new String[]{"/system/bin/input touchscreen swipe "+356+" "+1146+" "+356+" "+488}, true);
//                        time = System.nanoTime();
//                    }
//                    else{
//                        Log.d("TIME","Time: "+(System.nanoTime()-time));
//                    }
//                    isTouched=!isTouched;
                }
            }
        });


        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 50;
        params.y = 550;
        windowManager.addView(chatHead, params);

        params.x = 650;
        params.y = 550;
        windowManager.addView(close, params);
    }

    private void play(int pid) {

        RootManager.pause(pid);
        Screenshot.takeScreenshot(path);
        Bitmap bitmap = ImageManager.getBitmapFromImage(name);
        Bitmap ball = ImageManager.sliceWidth(bitmap, 1151, 1152);
        Bitmap basketInitial = ImageManager.sliceWidth(bitmap, 485, 486);
        long startTime = System.currentTimeMillis();
        RootManager.play(pid);

        try {
            Thread.sleep(WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RootManager.pause(pid);
        long endTime = System.currentTimeMillis();
        Screenshot.takeScreenshot(path);
        bitmap = ImageManager.getBitmapFromImage(name);
        Bitmap basketFinal = ImageManager.sliceWidth(bitmap, 485, 486);

        ImageManager.play(ball, 1151, basketInitial, basketFinal, 485, 100, 600);
        RootManager.play(pid);
    }

    private void showFacebookNotOpenDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This is to cheat Facebook Messenger").setTitle("Please open messenger!")
            .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public int getProcessId(String name){
        ActivityManager activityManager = (ActivityManager) this.getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager.getRunningAppProcesses();

        for(int i = 0; i < pidsTask.size(); i++) {
           if(name.equals(pidsTask.get(i).processName))
                return pidsTask.get(i).pid;
        }
        return -1;

    }

    @Override
    public void onDestroy() {

    }
}
