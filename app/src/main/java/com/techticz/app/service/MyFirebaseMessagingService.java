package com.techticz.app.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.techticz.app.ui.event.NewFCMToken;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private Context mContext;

    /* Default Constructor */
    public MyFirebaseMessagingService() {
    }

    public MyFirebaseMessagingService(Context context) {
        this.mContext = context;
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Timber.d("Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        EventBus.getDefault().postSticky(new NewFCMToken(token));
    }



}
