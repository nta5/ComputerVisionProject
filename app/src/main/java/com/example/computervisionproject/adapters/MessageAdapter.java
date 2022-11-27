package com.example.computervisionproject.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.computervisionproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for the list views which display the information obtained from the web socket server.
 */
public class MessageAdapter extends BaseAdapter {

    private List<String> messages;
    private Activity activity;

    public MessageAdapter(Activity activity) {
        messages = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.message_list_item, parent, false);
        }
        TextView receivedMessage = view.findViewById(R.id.receivedMessage);
        String currentMessage = messages.get(position);
        if (currentMessage != null && !currentMessage.isEmpty()) {
            System.out.println("Current message is: " + currentMessage);
            receivedMessage.setText(currentMessage);
        }
        return view;
    }

    public void addItem(String message) {
        messages.add(message);

        // updates the message list to include this new message
        notifyDataSetChanged();
    }
}
