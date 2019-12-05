package com.example.myapplication.activities.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class ListEntriesAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> description;
    private final ArrayList<String> date;
    private final ArrayList<String> time;
    private final ArrayList<Double> mount;

    public ListEntriesAdapter(Activity context,
                              ArrayList<String> description,
                              ArrayList<String> date,
                              ArrayList<String> time,
                              ArrayList<Double> mount) {
        super(context, R.layout.entries_report_list, description);

        this.context = context;
        this.description = description;
        this.date = date;
        this.time = time;
        this.mount = mount;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.entries_report_list, null, true);

        // Initilize views
        TextView descriptionTV = rowView.findViewById(R.id.textview_entriesReportList_description);
        TextView dateTV = rowView.findViewById(R.id.textview_entriesReportList_date);
        TextView timeTV = rowView.findViewById(R.id.textview_entriesReportList_time);
        TextView mountTV = rowView.findViewById(R.id.textview_entriesReportList_mount);

        // Set data
        descriptionTV.setText(description.get(position));
        dateTV.setText(date.get(position));
        timeTV.setText(time.get(position));

        // Set mount data in text view
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String mountStr = df.format(mount.get(position));
        mountTV.setText(mountStr);

        return rowView;
    }

}
