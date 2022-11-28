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
        Request request = new Request.Builder().url("ws://192.168.67.142:8080").build();
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
            Bitmap topLeft = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth()/2,bitmap.getHeight()/2);
            Bitmap topRight = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2,0, bitmap.getWidth()/2,bitmap.getHeight()/2);
            Bitmap bottomLeft = Bitmap.createBitmap(bitmap, 0,bitmap.getHeight()/2, bitmap.getWidth()/2,bitmap.getHeight()/2);
            Bitmap bottomRight = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2,bitmap.getHeight()/2, bitmap.getWidth()/2,bitmap.getHeight()/2);
            imageView.setImageBitmap(bitmap);


            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

            Frame topLeftFrame = new Frame.Builder().setBitmap(topLeft).build();
            SparseArray<TextBlock> topLeftBlock = textRecognizer.detect(topLeftFrame);

            Frame topRightFrame = new Frame.Builder().setBitmap(topRight).build();
            SparseArray<TextBlock> topRightBlock = textRecognizer.detect(topRightFrame);

            Frame bottomLeftFrame = new Frame.Builder().setBitmap(bottomLeft).build();
            SparseArray<TextBlock> bottomLeftBlock = textRecognizer.detect(bottomLeftFrame);

            Frame bottomRightFrame = new Frame.Builder().setBitmap(bottomRight).build();
            SparseArray<TextBlock> bottomRightBlock = textRecognizer.detect(bottomRightFrame);

            StringBuilder stringImageText = new StringBuilder();
//            TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(0));
//            stringImageText.append(textBlock.getValue());
            stringImageText.append("\n");
            for (int i = 0; i<2;i++) {
                TextBlock textBlock1 = topLeftBlock.get(topLeftBlock.keyAt(i));
                stringImageText.append(" ").append(textBlock1.getValue());
            }
            stringImageText.append("\n");
            for (int i = 0; i<3;i++) {
                TextBlock textBlock2 = topRightBlock.get(topRightBlock.keyAt(i));
                stringImageText.append(" ").append(textBlock2.getValue());
                i++;
            }
            stringImageText.append("\n");
            for (int i = 0; i<3;i++) {
                TextBlock textBlock3 = bottomLeftBlock.get(bottomLeftBlock.keyAt(i));
                stringImageText.append(" ").append(textBlock3.getValue());
                i++;
            }
            stringImageText.append("\n");
            for (int i = 1; i<3;i++) {
                TextBlock textBlock4 = bottomRightBlock.get(bottomRightBlock.keyAt(i));
                stringImageText.append(" ").append(textBlock4.getValue());
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

