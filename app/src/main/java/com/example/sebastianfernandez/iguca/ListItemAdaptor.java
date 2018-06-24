package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class ListItemAdaptor extends BaseAdapter {

    private LayoutInflater mInflator;
    private String[] listItems;
    private String[] listItemsDescriptions;
    private String[] listItemsIcons;
    AssetManager assetManager;

    public ListItemAdaptor(Context c, String[] i, String[] d, String[] icons) {
        listItems = i;
        listItemsDescriptions = d;
        listItemsIcons = icons;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assetManager = c.getAssets();
    }

    public ListItemAdaptor(Context c, String[] i) {
        listItems = i;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assetManager = c.getAssets();
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
        ImageView iconIV = (ImageView) v.findViewById(R.id.main_list_item_icon);

        String name = listItems[position];
        String desc = "";
        String iconpath = "";
        if (listItemsDescriptions != null) {
            desc = listItemsDescriptions[position];
        }
        if (listItemsIcons != null) {
            iconpath = listItemsIcons[position];
        }

        nameTV.setText(name);
        descriptionTV.setText(desc);
        try {
            InputStream is = assetManager.open("" + iconpath);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            iconIV.setImageBitmap(bitmap);
            // iconIV.setImageURI( Uri.parse("//assets/" + iconpath));
        } catch (IOException e) {
            Log.e("ListITemAdaptor", e.getMessage());
        }


        return v;
    }

}
