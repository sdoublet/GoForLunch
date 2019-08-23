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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.MainActivity;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//-------------------------------
//    TO RECEIVE NOTIFICATION
//-------------------------------


public class AlertReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String NOTIFICATION_TAG = "GOFORLUNCH";
    private static final int NOTIFICATION_ID = 10;
    private String restoId;
    private String restoName;
    private String name;
    private List<String> userList;

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkBooking(context);
        Log.e("notification", "ok");
    }

    // User concern by notification
    public void checkBooking(Context context) {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                restoId = user.getmRestaurantId();
                restoName = user.getmRestaurantName();
                name = user.getUsername();
                if (restoId != null) {
                    UserHelper.getRestoId(restoId).addOnSuccessListener(queryDocumentSnapshots -> {
                        userList = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                String client = Objects.requireNonNull(queryDocumentSnapshots.getDocuments().get(i).get("username")).toString();
                                userList.add(client);
                                if (client.equals(getCurrentUser().getDisplayName())) {
                                    userList.remove(client);
                                }
                            }
                            sendVisualNotification(context, name, restoName, userList);
                            Log.e("data", userList.toString());
                        }
                    });
                }
            }
        });
    }

    // Content of notification
    public void sendVisualNotification(Context context, String name, String restoName, List<String> userList) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a style for the notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.AppTitle));
        inboxStyle.addLine(context.getString(R.string.Hi) + name);
        inboxStyle.addLine(context.getString(R.string.choose_eating));
        inboxStyle.addLine(context.getString(R.string.restaurant));
        inboxStyle.addLine(restoName);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            stringBuilder.append(userList.get(i));
            if (!(i == userList.size() - 1)) {
                stringBuilder.append(", ");
            }
        }
        inboxStyle.addLine("avec " + stringBuilder);

        //Build a notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_local_dining_black_24dp)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.Reminder))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());


        // Support version >=Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "myChannelName";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 100});
            //Add the notification to the notification manager and show it
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }


    }
}
