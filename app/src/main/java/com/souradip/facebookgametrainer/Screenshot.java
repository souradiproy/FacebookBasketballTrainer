package com.souradip.facebookgametrainer;/*
 *  Copyright &copy; 2016, TopCoder, Inc. All rights reserved
 *
 *  /// <summary>
 *  ///     This class
 *  /// </summary>
 *  /// <author>TCSCODER</author>
 *  /// <threadsafety>This class is thread-safe</threadsafety>
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class Screenshot {
    public static void takeScreenshot(String path) {
        RootManager.runRootCommands(new String[]{"/system/bin/screencap -p " + path}, true);
    }
}
