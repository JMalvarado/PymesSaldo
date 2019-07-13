package com.example.myapplication.activities.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

/**
 * Adapter for the custom category spinners
 */
public class CustomAdapter extends ArrayAdapter<CustomItems> {

    public CustomAdapter(Context context, ArrayList<CustomItems> customList) {
        super(context, 0, customList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_categories, parent, false);
        }

        CustomItems items = getItem(position);
        ImageView spinnerImage = convertView.findViewById(R.id.imageView_customSpinnerCategories);
        TextView spinnerName = convertView.findViewById(R.id.textView_customSpinnerCategories);

        if (items != null) {
            spinnerImage.setImageResource(items.getSpinnerImage());
            spinnerName.setText(items.getSpinnerText());
        }

        return convertView;
    }
}
