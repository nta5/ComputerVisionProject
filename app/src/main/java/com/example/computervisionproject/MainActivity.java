package com.example.computervisionproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","OpenCV is loaded");
        }
        else {
            Log.d("MainActivity: ","OpenCV failed to load");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button opencvButton = findViewById(R.id.opencv);
        Button faceButton = findViewById(R.id.face_recognition);
        Button facialExpressionButton = findViewById(R.id.button_facial_expression);

        opencvButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, OpenCVCamera.class);
            startActivity(intent);
        });

        faceButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RealtimeFaceDetection.class);
            startActivity(intent);
        });

        facialExpressionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        });
    }
}