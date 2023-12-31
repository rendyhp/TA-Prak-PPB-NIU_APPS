package com.example.ta_ppb_niu_apps.firebase;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ta_ppb_niu_apps.R;
import com.example.ta_ppb_niu_apps.activities.ChatActivity;
import com.example.ta_ppb_niu_apps.models.User;
import com.example.ta_ppb_niu_apps.utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;


public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        User senderUser = new User();
        senderUser.id = remoteMessage.getData().get(Constants.KEY_USER_ID);
        senderUser.name = remoteMessage.getData().get(Constants.KEY_NAME);
        senderUser.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);
        senderUser.image = remoteMessage.getData().get(Constants.KEY_IMAGE);
        senderUser.email = remoteMessage.getData().get(Constants.KEY_EMAIL);
        if (remoteMessage.getData().get(Constants.KEY_USER_INFO) != null) {
            senderUser.info = remoteMessage.getData().get(Constants.KEY_USER_INFO);
        } else {
            senderUser.info = remoteMessage.getData().get("Blm update info");
        }

        String channelId = "chat_message_" + senderUser.id;

        int notificationId = new Random().nextInt();

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER, senderUser);

        intent.putExtra(Constants.KEY_IMAGE, senderUser);
        intent.putExtra(Constants.KEY_FCM_TOKEN, senderUser);
        intent.putExtra(Constants.KEY_IMAGE, senderUser);
        intent.putExtra(Constants.KEY_EMAIL, senderUser);
        intent.putExtra(Constants.KEY_USER_INFO, senderUser);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(senderUser.name);
        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get(Constants.KEY_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Chat Message";
            String channelDescription = "This notification channel is used for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(notificationId, builder.build());
    }

}
