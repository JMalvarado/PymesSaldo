package com.example.myapplication.activities.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

/**
 * Adapter for card view in report fragment
 */
public class AdapterReport extends RecyclerView.Adapter<AdapterReport.MyViewHolder> {

    private List<ListDataReport> listData;
    private Context context;
    private FragmentActivity activity;

    public AdapterReport(List<ListDataReport> listData, Context context, FragmentActivity activity) {
        this.listData = listData;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_list, parent, false);

        return new AdapterReport.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ListDataReport data = listData.get(position);

        // Set card view info

        // Category name
        holder.textView_reportCategName.setText(data.getName());

        // Category Icon
        String ic = data.getIc();
        int imageID = context.getResources().getIdentifier(ic, "drawable", context.getPackageName());
        holder.imageView_reportIc.setImageResource(imageID);

        // Total ammount
        holder.textView_reportAmmount.setText(data.getAmmount());

        // Percentage
        holder.textView_reportPercentage.setText(data.getPercentage());

        // Precentage in progress bar
        holder.progressBar_reportPercentage.setProgress(data.getPercentage());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView_reportCategName;
        TextView textView_reportAmmount;
        TextView textView_reportPercentage;
        ImageView imageView_reportIc;
        ProgressBar progressBar_reportPercentage;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_reportCategName = itemView.findViewById(R.id.textView_reportList_categName);
            textView_reportAmmount = itemView.findViewById(R.id.textView_reportList_ammount);
            textView_reportPercentage = itemView.findViewById(R.id.textView_reportList_percentage);
            imageView_reportIc = itemView.findViewById(R.id.imageView_reportList_ic);
            progressBar_reportPercentage = itemView.findViewById(R.id.progressBar_reportList_percentage);
        }
    }

}
