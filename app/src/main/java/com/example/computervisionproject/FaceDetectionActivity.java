package com.example.computervisionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.computervisionproject.adapters.MessageAdapter;
import com.example.computervisionproject.websocket.SocketListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

//https://developers.google.com/ml-kit/vision/face-detection/android#java

public class FaceDetectionActivity extends AppCompatActivity {


    private String clientName;

    private EditText editText;
    private ImageView imageView;

    private WebSocket webSocket;
    private MessageAdapter adapter;

    private TextView resultText;
    private EditText imgLink;
    private FaceDetectorOptions options;


    // initialize web socket connection
    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient();
        // change -> ws://(your IP):8080"
        Request request = new Request.Builder().url(SocketListener.url).build();
        webSocket = client.newWebSocket(request, new SocketListener(this, adapter));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        // Enable classification mode(eg. smiling)
        options =
                new FaceDetectorOptions.Builder()
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        Button detectFace = findViewById(R.id.detect_face);
        resultText = findViewById(R.id.result_face);
        imgLink = findViewById(R.id.img_link_face);
        imageView = findViewById(R.id.img_face);

        detectFace.setOnClickListener(this::detectFace);
        clientName = String.valueOf(Math.random() * 100);
        adapter = new MessageAdapter(this, "face");
        initWebSocket();
    }

    public void detectFace(View view) {
        InputImage image;
        String path = Environment.getExternalStorageDirectory().getPath();
        String stringFileName = path + "/Download/" + imgLink.getText().toString();//textinjpeg.jpg
        Bitmap imgBitMap = BitmapFactory.decodeFile(stringFileName);
        imageView.setImageBitmap(imgBitMap);

        image = InputImage.fromBitmap(imgBitMap, 0);

        FaceDetector detector = FaceDetection.getClient(options);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                faces -> {
                                    // Task completed successfully
                                    for (Face face : faces) {
                                        Rect bounds = face.getBoundingBox();
                                        float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                        float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                        // If classification was enabled:
                                        if (face.getSmilingProbability() != null) {
                                            float smileProb = face.getSmilingProbability();

                                            JSONObject object = new JSONObject();

                                            try {
                                                object.put("type", "face");
                                                object.put("clientName", clientName);
                                                if (smileProb < 0.5) {
                                                    resultText.setText("not smiling");
                                                    object.put("message", "not smiling much sad");
                                                } else {
                                                    resultText.setText("smiling");
                                                    object.put("message", "Smiling");
                                                }

                                                webSocket.send(object.toString());

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                        if (face.getRightEyeOpenProbability() != null) {
                                            float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                        }

                                    }
                                })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                    resultText.setText(R.string.fail);
                                });


    }
    //JermaSus.jpg

}