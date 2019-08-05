package com.creation.android.folkapp2019;

import android.app.Application;

//import com.onesignal.OneSignal;

public class MyNotifications extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Add OneSignal initialization here
        // OneSignal Initialization
        /*OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();*/
    }

}
