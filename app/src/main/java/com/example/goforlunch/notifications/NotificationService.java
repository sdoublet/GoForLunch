package com.example.goforlunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private final int NOTIFICATION_ID =1;
    private final String NOTIFICATION_TAG = "GOFORLUNCH";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            //Get message sent by firebase
            String message = remoteMessage.getNotification().getBody();
            Log.e("TAG", message);
            this.sendVisualNotificaiton(message);
        }
    }
    private void sendVisualNotificaiton(String messageBody){

        // Create an intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a style for the notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.AppTitle));
        inboxStyle.addLine(messageBody);
        

        //Build a notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_local_dining_black_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.Reminder))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        //Add the notification to the notification manager and show it
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Support version >=Android 8
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence channelName = "Message from Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        //Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());


    }

}
