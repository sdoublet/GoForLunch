package com.example.goforlunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.MainActivity;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.User;
import com.example.goforlunch.utils.DataHolder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;




public class NotificationService extends FirebaseMessagingService  {

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private final int NOTIFICATION_ID = 1;
    private final String NOTIFICATION_TAG = "GOFORLUNCH";
    private String restoName;

    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            //Get message sent by firebase
            checkIfUserBooking();
            String message =   remoteMessage.getNotification().getBody() + getCurrentUser().getDisplayName()    ;
            Log.e("TAG", message);
            sendVisualNotification(message);
        }
    }

    public void sendVisualNotification(String messageBody) {

        // Create an intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a style for the notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.AppTitle));
        inboxStyle.addLine(messageBody);
        inboxStyle.addLine("vous avez choisit de manger");
        inboxStyle.addLine(" au restaurant ");
        inboxStyle.addLine(""+DataHolder.getInstance().getRestoName());
        inboxStyle.addLine("avec" );


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message from Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,100});
            notificationManager.createNotificationChannel(mChannel);
        }

        //Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());


    }

    private void checkIfUserBooking(){

        // }
        UserHelper.getUser(DataHolder.getInstance().getUserUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
           // if (user!=null&&user.getmRestaurantName()!=null){
            assert user != null;
            restoName = user.getmRestaurantName();
               DataHolder.getInstance().setRestoName(restoName);
                Log.e("notif", restoName);
            });


    }


}
