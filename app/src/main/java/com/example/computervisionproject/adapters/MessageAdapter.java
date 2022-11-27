package com.example.computervisionproject.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

    public List<JSONObject> getMessages() {
        return messages;
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
        TextView OCR_Result = view.findViewById(R.id.ocr_result);
        TextView face_Result = view.findViewById(R.id.face_result);

        JSONObject currentObject = messages.get(position);
        try {
            String message = "client name: " + currentObject.getString("clientName")
                            + "\nclient message: " + currentObject.getString("message");
            if(currentObject.getString("type").equals("face")) {
                face_Result.setText(message);
                OCR_Result.setText("");
            } else {
                OCR_Result.setText(message);
                face_Result.setText("");
            }

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
