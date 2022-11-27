package com.example.computervisionproject.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.computervisionproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for the list views which display the information obtained from the web socket server.
 */
public class MessageAdapter extends BaseAdapter {

    private List<JSONObject> messages;
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
        // inflate the view if it is null
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.message_list_item, parent, false);
        }
        // obtain the textview to store the message
        TextView receivedMessage = view.findViewById(R.id.receivedMessage);

        JSONObject currentObject = messages.get(position);
        try {
            String message = "client name: " + currentObject.getString("clientName")
                            + "\nclient message: " + currentObject.getString("message");
            receivedMessage.setText(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void addItem(JSONObject object) {
        messages.add(object);

        // updates the message list to include this new message
        notifyDataSetChanged();
    }

}
