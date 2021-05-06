package com.ajith.voipcall;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.facebook.react.HeadlessJsTaskService;

import java.util.List;
import java.util.Objects;

public class RNVoipBroadcastReciever extends  BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Application applicationContext = (Application) context.getApplicationContext();

        RNVoipNotificationHelper rnVoipNotificationHelper = new RNVoipNotificationHelper(applicationContext);
        int notificationId = intent.getIntExtra("notificationId",0);

        switch (Objects.requireNonNull(intent.getAction())){
            case "callDismiss":
                RNVoipRingtunePlayer.getInstance(context).stopMusic();
                rnVoipNotificationHelper.clearNotification(notificationId);
               // rnVoipNotificationHelper.showMissCallNotification(intent.getStringExtra("missedCallTitle"), intent.getStringExtra("missedCallBody"), intent.getStringExtra("callerId"));

                Intent serviceIntent = new Intent(applicationContext, RNVoipService.class);
                String action = intent.getAction();
                serviceIntent.putExtra("action", action);
                context.startService(serviceIntent);
                HeadlessJsTaskService.acquireWakeLockNow(context);
                break;
            case "callTimeOut":
                rnVoipNotificationHelper.showMissCallNotification(intent.getStringExtra("missedCallTitle"), intent.getStringExtra("missedCallBody"), intent.getStringExtra("callerId"));
                break;
            default:
                break;
        }

    }

    /**
     We need to check if app is in foreground otherwise the app will crash.
     http://stackoverflow.com/questions/8489993/check-android-application-is-in-foreground-or-not
     **/
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
