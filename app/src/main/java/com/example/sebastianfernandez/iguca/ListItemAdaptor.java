package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListItemAdaptor extends BaseAdapter {

    private LayoutInflater mInflator;
    private String[] listItems;
    private String[] listItemsDescriptions;

    public ListItemAdaptor(Context c, String[] i, String[] d) {
        listItems = i;
        listItemsDescriptions = d;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listItems.length;
    }

    @Override
    public Object getItem(int position) {
        return listItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflator.inflate(R.layout.main_list, null);
        TextView nameTV = (TextView) v.findViewById(R.id.main_list_item_name);
        TextView descriptionTV = (TextView) v.findViewById(R.id.main_list_item_description);

        String name = listItems[position];
        String desc = listItemsDescriptions[position];

        nameTV.setText(name);
        descriptionTV.setText(desc);

        return v;
    }
}
