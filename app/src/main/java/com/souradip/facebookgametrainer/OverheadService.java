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

import java.util.List;

public class OverheadService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;
    private ImageView close;
    WindowManager.LayoutParams params;

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
                    //RootManager.runRootCommands(new String[]{"kill -STOP "+pid}, true);
                    Screenshot.takeScreenshot(path);
                    Bitmap bitmap = ImageManager.getBitmapFromImage(name);
                    Bitmap ball = ImageManager.sliceWidth(bitmap, 1151, 1152);
                    Bitmap basket = ImageManager.sliceWidth(bitmap, 485, 486);
                    ImageManager.play(ball, 1151, basket, 485);
                    //RootManager.runRootCommands(new String[]{"kill -CONT " + pid}, true);
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
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
