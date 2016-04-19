package com.souradip.facebookgametrainer;/*
 *  Copyright &copy; 2016, TopCoder, Inc. All rights reserved
 *
 *  /// <summary>
 *  ///     This class 
 *  /// </summary>
 *  /// <author>TCSCODER</author>
 *  /// <threadsafety>This class is thread-safe</threadsafety>
 */

import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class RootManager {

    public static void runRootCommands(String[] commands, boolean shouldWait) {
        for (String tmpCmd : commands) {
            try {
                Process process = Runtime.getRuntime().exec("su", null,null);
                OutputStream os = process.getOutputStream();
                os.write((tmpCmd+"\n").getBytes());
                os.flush();
                os.close();
                if(shouldWait) {
                    process.waitFor();
                }
            } catch (Exception e) {
                Log.e("Root","ERROR",e);
            }
        }
    }

    public static void pause(int pid){
        runRootCommands(new String[]{"kill -STOP "+pid}, true);
    }

    public static void play(int pid){
        runRootCommands(new String[]{"kill -CONT "+pid}, true);
    }
}
