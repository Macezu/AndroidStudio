package com.imber.hangmanglobal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class customAdapted extends ArrayAdapter<String> {
    Context context;
    String[] names;
    int[] images;


    public customAdapted(@NonNull Context context, String[] names, int[] images) {
        super(context, R.layout.language_spinner,names);
        this.context = context;
        this.names = names;
        this.images = images;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.language_spinner,null);
            TextView t1 = (TextView)row.findViewById(R.id.textView);
            ImageView i1 = (ImageView)row.findViewById(R.id.imageView);

            t1.setText(names[position]);
            i1.setImageResource(images[position]);

        return row;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.language_spinner,null);
        TextView t1 = (TextView)row.findViewById(R.id.textView);
        ImageView i1 = (ImageView)row.findViewById(R.id.imageView);

        t1.setText(names[position]);
        i1.setImageResource(images[position]);

        return row;
    }
}
