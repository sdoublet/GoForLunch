package com.example.goforlunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.MainActivity;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.User;
import com.example.goforlunch.utils.DataHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public class AlertReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID ="CHANNEL_ID" ;
    private static final String NOTIFICATION_TAG ="GOFORLUNCH" ;
    private static final int NOTIFICATION_ID = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        sendVisualNotification(context );
        Log.e("alarm", "onReceive success");
    }
    public void sendVisualNotification(Context context) {
        // Create an intent that will be shown when user will click on the Notification
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a style for the notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(""+R.string.AppTitle);
        inboxStyle.addLine("vous avez choisit de manger");
        inboxStyle.addLine(" au restaurant ");
        inboxStyle.addLine(""+ DataHolder.getInstance().getRestoName());
        inboxStyle.addLine("avec " );

        //Build a notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_local_dining_black_24dp)
                        .setContentTitle(""+ R.string.app_name)
                        .setContentText(""+(R.string.Reminder))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());

        UserHelper.getRestoId(DataHolder.getInstance().getRestaurantId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.toObjects(User.class);
                User user = new User();
                Log.e("user", user.getUsername());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("alarm", "listener failed");
            }
        });





        // Support version >=Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "myChannelName";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,100});
            //Add the notification to the notification manager and show it
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }



    }
}
