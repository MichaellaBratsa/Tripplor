package com.mbrats01.epl498_group_project.ui.Firebase;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mbrats01.epl498_group_project.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
@Override
    public void onNewToken(@NonNull String token){
    Log.d("FCM","Token: +"+token );
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        if(remoteMessage.getNotification()!=null){
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void requestNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void showNotification(String title, String message){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
                requestNotificationPermission();
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FCM_CHANNEL")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_logo)
                .setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}
