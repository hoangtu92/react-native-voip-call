package com.ajith.voipcall;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Objects;


public class RNVoipSendData {
    private ReactApplicationContext mReactContext;
    public RNVoipSendData(ReactApplicationContext reactContext) {
        mReactContext = reactContext;
    }

    public void sendIntilialData(Promise promise, Intent intent){
        WritableMap params = Arguments.createMap();
        String action = intent.getAction();
        if(action == null){
            promise.reject("error","no new Call Notications");
            return;
        }
        try {
            params.putString("action", action);
            params.putInt("notificationId", intent.getIntExtra("notificationId", 0));
            params.putString("callerId", intent.getStringExtra("callerId"));
            params.putString("callerName", intent.getStringExtra("callerName"));
            params.putBoolean("isVideo", intent.getBooleanExtra("isVideo", false));
            params.putString("callerPic", intent.getStringExtra("callerPic"));
            params.putString("message","success");

            switch (action) {
                case "callAnswer":
                    promise.resolve(params);
                    break;
                case "fullScreenIntent":
                    promise.resolve(params);
                    break;
                case "contentTap":
                    promise.resolve(params);
                    break;
                case "missedCallTape":
                    promise.resolve(params);
                    break;
                case "callDismiss":
                    promise.resolve(params);
                default:
                    promise.reject("error","no new Call Notications");
                    break;
            }
        }catch (Exception e){
            promise.reject("error",e.toString());
        }
    }

    public  void sentEventToJsModule(Intent intent){
        try{
            String action = intent.getAction();
            WritableMap params = Arguments.createMap();
            params.putString("action",action);
            params.putInt("notificationId", intent.getIntExtra("notificationId", 0));
            params.putBoolean("isVideo", intent.getBooleanExtra("isVideo", false));
            params.putString("callerId", intent.getStringExtra("callerId"));
            params.putString("callerName", intent.getStringExtra("callerName"));
            params.putString("callerPic", intent.getStringExtra("callerPic"));
            switch (Objects.requireNonNull(action)){
                case "callAnswer":
                    sendEvent(mReactContext,"RNVoipCallPerformAnswerCallAction", params);
                    break;
                case "fullScreenIntent":
                    sendEvent(mReactContext,"RNVoipCallFullScreenIntent", params);
                    break;
                case "contentTap":
                    sendEvent(mReactContext,"RNVoipCallNotificationTap", params);
                    break;
                case "callDismiss":
                    sendEvent(mReactContext,"RNVoipCallPerformEndCallAction", params);
                    break;
                case "missedCallTape":
                    sendEvent(mReactContext,"RNVoipCallMissedCallTap", params);
                    break;
                default:
                    break;
            }
        }catch(NullPointerException e){

        }
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

}
