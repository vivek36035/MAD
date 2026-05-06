package com.example.intent2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnFacebook, btnInstagram, btnWhatsApp, btnYouTube, btnTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFacebook = findViewById(R.id.btnFacebook);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnYouTube = findViewById(R.id.btnYouTube);
        btnTwitter = findViewById(R.id.btnTwitter);

        btnFacebook.setOnClickListener(v -> openApp("https://www.facebook.com"));
        btnInstagram.setOnClickListener(v -> openApp("https://www.instagram.com"));
        btnWhatsApp.setOnClickListener(v -> openApp("https://wa.me/"));
        btnYouTube.setOnClickListener(v -> openApp("https://www.youtube.com"));
        btnTwitter.setOnClickListener(v -> openApp("https://twitter.com"));
    }

    private void openApp(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}