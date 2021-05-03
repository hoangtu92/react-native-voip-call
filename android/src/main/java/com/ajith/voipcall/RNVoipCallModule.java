
package com.ajith.voipcall;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;


public class RNVoipCallModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  private final ReactApplicationContext reactContext;

  private  RNVoipNotificationHelper rnVoipNotificationHelper;
  private RNVoipSendData sendjsData;
  public static final String LogTag = "RNVoipCall";

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
    sendjsData.sentEventToJsModule(intent);
  }

  @Override
  public void onNewIntent(Intent intent){
    sendjsData.sentEventToJsModule(intent);
  }


  RNVoipCallModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addActivityEventListener(this);
    Application applicationContext = (Application) reactContext.getApplicationContext();
    rnVoipNotificationHelper = new RNVoipNotificationHelper(applicationContext);
    sendjsData = new RNVoipSendData(reactContext);
  }

  @Override
  public String getName() {
    return "RNVoipCall";
  }

  @ReactMethod
  public void displayIncomingCall(ReadableMap jsonObject){
    ReadableMap data = RNVoipConfig.callNotificationConfig(jsonObject);
    rnVoipNotificationHelper.sendCallNotification(data);

    Activity activity = getCurrentActivity();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      activity.setShowWhenLocked(true);
      activity.setTurnScreenOn(true);
    } else {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
              | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }
  }

  @ReactMethod
  public  void clearNotificationById(int id){
    rnVoipNotificationHelper.clearNotification(id);
  }

  @ReactMethod
  public void clearAllNotifications(){
    rnVoipNotificationHelper.clearAllNorifications();
  }


  @ReactMethod
  public void getInitialNotificationActions(Promise promise) {
    Activity activity = getCurrentActivity();
    Intent intent = activity.getIntent();
    sendjsData.sendIntilialData(promise,intent);
  }


  @ReactMethod
  public void playRingtune(String fileName, Boolean isLooping){
    RNVoipRingtunePlayer.getInstance(reactContext).playRingtune(fileName, isLooping);
  }

  @ReactMethod
  public void  stopRingtune(){
    RNVoipRingtunePlayer.getInstance(reactContext).stopMusic();
  }



  @ReactMethod
  public  void  showMissedCallNotification(String title, String body, String callerId){
    rnVoipNotificationHelper.showMissCallNotification(title,body, callerId);
  }
}
