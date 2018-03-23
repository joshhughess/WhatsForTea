package com.example.n0584052.whatsfortea;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> web = new ArrayList<>();
    private ArrayList<Uri> imageId = new ArrayList<>();
    public CustomList(Activity context,
                      ArrayList<String> web, ArrayList<Uri> imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        Collections.sort(imageId);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web.get(position));

        Glide.with(getContext()).load(imageId.get(position)).into(imageView);

        return rowView;
    }
}
