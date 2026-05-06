package com.example.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    String channel_id = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔥 Request permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }

        Button b = findViewById(R.id.button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationManager nm =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Create channel (Android 8+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel nc = new NotificationChannel(
                            channel_id,
                            "CHANNEL_01",
                            NotificationManager.IMPORTANCE_HIGH
                    );
                    nm.createNotificationChannel(nc);
                }

                // Build notification
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MainActivity.this, channel_id)
                                .setSmallIcon(android.R.drawable.ic_dialog_info)
                                .setContentTitle("Notification of Application")
                                .setContentText("This is my first push notification")
                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                // 🔥 Show notification
                showNotification(nm, mBuilder);
            }
        });
    }

    // ✅ Separate method to avoid permission error
    @SuppressLint("MissingPermission")
    private void showNotification(NotificationManager nm, NotificationCompat.Builder builder) {
        nm.notify((int) (System.currentTimeMillis() % Integer.MAX_VALUE), builder.build());
    }

    // Optional (for permission result)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}