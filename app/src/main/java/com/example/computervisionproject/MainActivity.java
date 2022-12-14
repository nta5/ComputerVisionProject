package com.example.computervisionproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

        Button ocrCamera = findViewById(R.id.button_ocr_camera);
        Button ocr = findViewById(R.id.button_ocr);
        Button face = findViewById(R.id.button_face);
        Button activity = findViewById(R.id.button_activity);
        Button incomingData = findViewById(R.id.button_incoming_data);
        Button facialDetection = findViewById(R.id.button_facial_detection);

        ocrCamera.setOnClickListener(view -> {
            Intent intent = new Intent(this, OcrCameraActivity.class);
            startActivity(intent);
        });

        ocr.setOnClickListener(view -> {
            Intent intent = new Intent(this, OcrActivity.class);
            startActivity(intent);
        });

        face.setOnClickListener(view -> {
            Intent intent = new Intent(this, FaceDetectionActivity.class);
            startActivity(intent);
        });

        incomingData.setOnClickListener(view -> {
            Intent intent = new Intent(this, IncomingDataActivity.class);
            startActivity(intent);
        });

        facialDetection.setOnClickListener(view -> {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        });
        activity.setOnClickListener(view -> Toast.makeText(this, "NOT IMPLEMENTED YET", Toast.LENGTH_SHORT).show());
    }
}