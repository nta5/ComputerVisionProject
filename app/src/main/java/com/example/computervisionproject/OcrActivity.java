package com.example.computervisionproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.computervisionproject.websocket.SocketListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class OcrActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private ImageView imageView;

    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        textView = findViewById(R.id.extracted_text);
        editText = findViewById(R.id.img_link_ocr);
        imageView = findViewById(R.id.img_ocr);

        instantiateWebSocket();
        //MessageAdapter adapter = new MessageAdapter();
    }

    public void buttonReadText(View view){
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
            textView.setText(stringImageText.toString());

            webSocket.send(stringImageText.toString());
        }
        catch (Exception e){
            textView.setText(R.string.fail);
        }

    }

    // apps.44472.14266069062940839.7622a220-e3b7-47fe-9320-b2106621e5cb.dd5f8cb2-c6d4-4930-b7dd-81d9c9385116

    private void instantiateWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://10.0.0.16:8080").build();
        webSocket = client.newWebSocket(request, new SocketListener(this));
    }

}