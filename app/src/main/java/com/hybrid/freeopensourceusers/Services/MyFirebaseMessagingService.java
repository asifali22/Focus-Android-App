package com.hybrid.freeopensourceusers.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.hybrid.freeopensourceusers.Activities.Comment_Actiivity;
import com.hybrid.freeopensourceusers.Activities.FirstActivity;
import com.hybrid.freeopensourceusers.Activities.WebViewActivity;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Volley.NotificationID;

/**
 * Created by monster on 30/8/16.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationID notificationID = new NotificationID();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(MyApplication.getAppContext());

            if (remoteMessage.getNotification().getClickAction().equals("OPEN_COMMENT_ACTIVITY")) {
                String body = remoteMessage.getNotification().getBody();
                String arr[] = body.split(" ", 2);
                String api_key = sharedPrefManager.getApiKey();
                Intent intent = new Intent(this, Comment_Actiivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                SharedPreferences sharedPreferences = getSharedPreferences("comment", MODE_PRIVATE);
                sharedPreferences.edit().putString("comment_pid", arr[1]).apply();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("PID_VALUE", arr[1] + "");
                intent.putExtra("API_KEY", api_key);
                intent.putExtra("FLAG", 0);
                startActivity(intent);
            }
            else {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("TITLE", remoteMessage.getNotification().getTitle());
            intent.putExtra("LINK", remoteMessage.getData().get("links"));
            String string = remoteMessage.getNotification().getClickAction();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Intent i = new Intent(string);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
            builder.setAutoCancel(true);
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            builder.setLargeIcon(largeIcon);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID.getID(), builder.build());
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
