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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

        // Total ammount (Parse amount with properly format)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String amountStr = df.format(data.getAmount());
        holder.textView_reportAmount.setText(amountStr);

        // Percentage (Parse percentage with properly format: two decimals)
        DecimalFormat dfPerc = new DecimalFormat(("###.##"));
        String percentage = dfPerc.format(data.getPercentage());
        String percentageComplete = percentage.concat(" %");
        holder.textView_reportPercentage.setText(percentageComplete);

        // Precentage in progress bar (Round the percentage value to nearest integer)
        int progressInt = (int) Math.round(data.getPercentage());
        holder.progressBar_reportPercentage.setProgress(progressInt);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView_reportCategName;
        TextView textView_reportAmount;
        TextView textView_reportPercentage;
        ImageView imageView_reportIc;
        ProgressBar progressBar_reportPercentage;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_reportCategName = itemView.findViewById(R.id.textView_reportList_categName);
            textView_reportAmount = itemView.findViewById(R.id.textView_reportList_amount);
            textView_reportPercentage = itemView.findViewById(R.id.textView_reportList_percentage);
            imageView_reportIc = itemView.findViewById(R.id.imageView_reportList_ic);
            progressBar_reportPercentage = itemView.findViewById(R.id.progressBar_reportList_percentage);
        }
    }

}
