package com.hypnotriod.web_view_helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class URLListItemAdapter extends BaseAdapter implements View.OnClickListener {
    private ArrayList<String> urls;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int itemPosition);

        void onItemDelete(int itemPosition);
    }

    public URLListItemAdapter(Context context, ArrayList<String> urls, OnItemClickListener onItemClickListener) {
        this.urls = urls;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.url_list_item, parent, false);
        }

        Button deleteButton = (Button) view.findViewById(R.id.url_item_delete_button);
        TextView textView = (TextView) view.findViewById(R.id.url_item_text_view);

        deleteButton.setOnClickListener(this);
        textView.setOnClickListener(this);

        deleteButton.setTag(position);
        textView.setTag(position);

        textView.setText(urls.get(position));

        return view;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();

        if (view.getId() == R.id.url_item_delete_button) {
            urls.remove(position);
            notifyDataSetChanged();
            onItemClickListener.onItemDelete(position);
        } else if (view.getId() == R.id.url_item_text_view) {
            onItemClickListener.onItemClick(position);
        }
    }
}
