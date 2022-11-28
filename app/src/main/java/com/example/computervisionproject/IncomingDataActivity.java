package com.example.computervisionproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.computervisionproject.adapters.MessageAdapter;
import com.example.computervisionproject.websocket.SocketListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class IncomingDataActivity extends AppCompatActivity {

    private WebSocket webSocket;

    private ListView ocrListView;
    private ListView faceListView;
    private ListView cameraOcrListView;

    private MessageAdapter ocrAdapter;
    private MessageAdapter ocrCameraAdapter;
    private MessageAdapter faceAdapter;

    private static final String faceName = "face";
    private static final String ocrName = "ocr";
    private static final String ocrCameraName = "ocrCamera";

    // initialize web socket connection
    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient();
        // change -> ws://(your IP):8080"
        Request request = new Request.Builder().url(SocketListener.url).build();
        webSocket = client.newWebSocket(request, new IncomingDataWebSocketListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_data);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        // init the list views and set their adapter
        ocrAdapter = new MessageAdapter(this, ocrName);
        ocrListView = findViewById(R.id.OCR_list);
        ocrListView.setAdapter(ocrAdapter);

        faceAdapter = new MessageAdapter(this, faceName);
        faceListView = findViewById(R.id.Facial_list);
        faceListView.setAdapter(faceAdapter);

        ocrCameraAdapter = new MessageAdapter(this, ocrCameraName);
        cameraOcrListView = findViewById(R.id.Camera_list);
        cameraOcrListView.setAdapter(ocrCameraAdapter);

        // initialize the websockets' connection
        initWebSocket();
    }

    /**
     * Client side web socket connection.
     */
    class IncomingDataWebSocketListener extends WebSocketListener {

        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            IncomingDataActivity.this.runOnUiThread(() -> Toast.makeText(IncomingDataActivity.this, "Disconnected from the server!", Toast.LENGTH_LONG).show());
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            IncomingDataActivity.this.runOnUiThread(() -> Toast.makeText(IncomingDataActivity.this, "About to end connection to the server!", Toast.LENGTH_LONG).show());
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            t.printStackTrace();
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            IncomingDataActivity.this.runOnUiThread(() -> {
                try {
                    JSONObject object = new JSONObject(text);

                    // if the type of this data corresponds to the name, add it to the adapter
                    if(object.getString("type").equals(ocrName)) {
                        ocrAdapter.addItem(object);
                    } else if (object.getString("type").equals(faceName)) {
                        faceAdapter.addItem(object);
                    } else {
                        ocrCameraAdapter.addItem(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            IncomingDataActivity.this.runOnUiThread(() -> Toast.makeText(IncomingDataActivity.this, "Established a connection!", Toast.LENGTH_LONG).show());
        }

    }

}