package com.example.computervisionproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button opencvButton = findViewById(R.id.opencv);
        Button faceButton = findViewById(R.id.face_recognition);

        opencvButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, OpenCVCamera.class);
            startActivity(intent);
        });

        faceButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RealtimeFaceDetection.class);
            startActivity(intent);
        });
    }
}