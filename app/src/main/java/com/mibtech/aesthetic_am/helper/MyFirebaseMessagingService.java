package com.mibtech.aesthetic_am.helper;


import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.mibtech.aesthetic_am.activity.MainActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    void sendPushNotification(JSONObject json) {
        try {

            JSONObject data = json.getJSONObject(Constant.DATA);

            String type = data.getString("type");
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");
            String id = data.getString("id");

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            if (type.equals("category")) {

                intent.putExtra("id", id);
                intent.putExtra("name", title);
                intent.putExtra(Constant.FROM, type);

            } else if (type.equals("product")) {

                intent.putExtra("id", id);
                intent.putExtra("vpos", 0);
                intent.putExtra(Constant.FROM, type);

            } else if (type.equals("order")) {
                intent.putExtra(Constant.FROM, type);
                intent.putExtra("model", "");
                intent.putExtra("id", id);
            } else {
                intent.putExtra(Constant.FROM, "");
            }

            if (type.equals("payment_transaction")) {
                Session.setCount(Constant.UNREAD_TRANSACTION_COUNT, (Session.getCount(Constant.UNREAD_TRANSACTION_COUNT, getApplicationContext()) + 1), getApplicationContext());
            } else if (type.equals("wallet_transaction")) {
                Session.setCount(Constant.UNREAD_WALLET_COUNT, (Session.getCount(Constant.UNREAD_WALLET_COUNT, getApplicationContext()) + 1), getApplicationContext());
            } else if (type.equals("default") || type.equals("category") || type.equals("product")) {
                Session.setCount(Constant.UNREAD_NOTIFICATION_COUNT, (Session.getCount(Constant.UNREAD_NOTIFICATION_COUNT, getApplicationContext()) + 1), getApplicationContext());
            }


            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            if (imageUrl.equals("null") || imageUrl.equals("")) {
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getLocalizedMessage());

        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

}
