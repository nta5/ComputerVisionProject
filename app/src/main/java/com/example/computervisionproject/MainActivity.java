package com.example.computervisionproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ocr = findViewById(R.id.button_ocr);
        Button face = findViewById(R.id.button_face);
        Button activity = findViewById(R.id.button_activity);
        Button incomingData = findViewById(R.id.button_incoming_data);

        ocr.setOnClickListener(view -> {
            Intent intent = new Intent(this, OcrActivity.class);
            startActivity(intent);
        });

        face.setOnClickListener(view -> {
            Intent intent = new Intent(this, FaceDetectionActivity.class);
            startActivity(intent);
        });

        activity.setOnClickListener(view -> Toast.makeText(this, "NOT IMPLEMENTED YET", Toast.LENGTH_SHORT).show());
    }

}