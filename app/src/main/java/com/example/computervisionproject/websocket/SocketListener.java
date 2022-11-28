package com.example.computervisionproject.websocket;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.computervisionproject.adapters.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Client side web socket connection.
 */
public class SocketListener extends WebSocketListener {

    public static final String url = "ws://192.168.67.85:8080";

    private Activity activity;
    private MessageAdapter adapter;

    public SocketListener(Activity activity, MessageAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        activity.runOnUiThread(() -> Toast.makeText(activity, "Disconnected from the server!", Toast.LENGTH_LONG).show());
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        activity.runOnUiThread(() -> Toast.makeText(activity, "About to end connection to the server!", Toast.LENGTH_LONG).show());
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
        activity.runOnUiThread(() -> {
            try {
                JSONObject object = new JSONObject(text);

                // if the type of this data corresponds to the name, add it to the adapter
                if(object.getString("type").equals(adapter.getName())) {
                    adapter.addItem(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
        activity.runOnUiThread(() -> Toast.makeText(activity, "Established a connection!", Toast.LENGTH_LONG).show());
    }

}
