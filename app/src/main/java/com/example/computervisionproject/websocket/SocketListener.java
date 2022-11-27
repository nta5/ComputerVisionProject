package com.example.computervisionproject.websocket;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.computervisionproject.adapters.MessageAdapter;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Client side web socket connection.
 */
public class SocketListener extends WebSocketListener {

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
        System.out.println("RECEIVED MESSAGE");
        activity.runOnUiThread(() -> {
            System.out.println("Obtained message from the server: " + text);
            adapter.addItem(text);
        });
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
        activity.runOnUiThread(() -> Toast.makeText(activity, "Established a connection!", Toast.LENGTH_LONG).show());
    }

}