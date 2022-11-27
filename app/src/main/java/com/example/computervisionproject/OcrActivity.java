package com.example.computervisionproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
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
import okhttp3.WebSocket;

public class OcrActivity extends AppCompatActivity {

    private String clientName;

    private EditText editText;
    private ImageView imageView;

    private WebSocket webSocket;
    private MessageAdapter adapter;
    private ListView OCR_List;

    // initialize web socket connection
    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient();
        // change -> ws://(your IP):8080"
        Request request = new Request.Builder().url("ws://192.168.1.30:8080").build();
        webSocket = client.newWebSocket(request, new SocketListener(this, adapter));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        clientName = String.valueOf(Math.random() * 100);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        editText = findViewById(R.id.img_link_ocr);
        imageView = findViewById(R.id.img_ocr);
        OCR_List = findViewById(R.id.OCR_listview);
        adapter = new MessageAdapter(this);
        OCR_List.setAdapter(adapter);
        initWebSocket();
    }

    public void buttonReadText(View view) {
        try {
            String path = Environment.getExternalStorageDirectory().getPath();
//            String stringFileName = "/storage/emulated/0/Download/textinjpeg.jpg";
            String stringFileName = path + "/Download/" + editText.getText().toString();//textinjpeg.jpg

            Bitmap bitmap = BitmapFactory.decodeFile(stringFileName);
            imageView.setImageBitmap(bitmap);


            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
            Frame frameImage = new Frame.Builder().setBitmap(bitmap).build();
            Log.d("hi", "here " +frameImage);

            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frameImage);
            StringBuilder stringImageText = new StringBuilder();
            for (int i = 0; i<textBlockSparseArray.size();i++){
                TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
                stringImageText.append(" ").append(textBlock.getValue());
            }
            Log.d("hi", "string " + stringImageText);

            // send the message to the websocket server so it can be broadcast back to all clients
            // (including this one)
            JSONObject object = new JSONObject();
            try {
                object.put("type", "OCR");
                object.put("message", stringImageText.toString());
                object.put("clientName", clientName);
                webSocket.send(object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    //    public void buttonReadVideo(View view) {
    //        String path = Environment.getExternalStorageDirectory().getPath();
    //        String fileName = path + "/Download/" +editText.getText().toString();
    //        bitmap = BitmapFactory.decodeFile(fileName);
    //        imageView.setImageBitmap(bitmap);
    //    }

}