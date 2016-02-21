package com.example.qingunext.app.util;

import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.util_global.QingUNetworkCenter;

import java.io.IOException;

/**
 * Created by Rye on 5/23/2015.
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static GlobalExceptionHandler myCrashHandler = new GlobalExceptionHandler();

    private GlobalExceptionHandler() {
    }

    public static synchronized GlobalExceptionHandler getInstance() {
        return myCrashHandler;
    }

    public void uncaughtException(Thread arg0, final Throwable arg1) {
        new Thread(() -> {
            arg1.printStackTrace();
            String crashReport = "";
            for (StackTraceElement element : arg1.getStackTrace()) {
                crashReport += element.toString();
                crashReport += "\n";
            }
            try {
                QingUNetworkCenter.postNewMail("finix", "new bug discovered", crashReport, false);
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }).start();


//        android.os.Process.killProcess(android.os.Process.myPid());
    }

}