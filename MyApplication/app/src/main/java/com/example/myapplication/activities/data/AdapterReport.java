package com.example.myapplication.activities.data;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.maltaisn.icondialog.pack.IconPack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Adapter for card view in report fragment
 */
public class AdapterReport extends RecyclerView.Adapter<AdapterReport.MyViewHolder> {

    private List<ListDataReport> listData;
    private String type, begDate, finDate;
    private Context context;
    private FragmentActivity activity;
    private DatabaseManager db;

    public AdapterReport(List<ListDataReport> listData, Context context, FragmentActivity activity, String type, String begDate, String finDate) {
        this.listData = listData;
        this.context = context;
        this.type = type;
        this.begDate = begDate;
        this.finDate = finDate;
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
        db = new DatabaseManager(context);

        // Set card view info

        // Category name
        holder.textView_reportCategName.setText(data.getName());

        // Category Icon
        String ic = data.getIc();
        IconPack iconPack = ((IconPackApp) activity.getApplication()).getIconPack();
        Drawable icDrawable = Objects.requireNonNull(iconPack).getIconDrawable(Integer.parseInt(ic), new IconDrawableLoader(Objects.requireNonNull(context)));
        holder.imageView_reportIc.setImageDrawable(icDrawable);

        // Total ammount (Parse amount with properly format)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String amountStr = df.format(data.getAmount());
        holder.textView_reportAmount.setText(amountStr);

        // Percentage (Parse percentage with properly format: two decimals)
        DecimalFormat dfPerc = new DecimalFormat(("###.##"));
        String percentage = dfPerc.format(data.getPercentage());
        if (percentage.equals("NaN")) {
            percentage = "0";
        }
        String percentageComplete = percentage.concat(" %");
        holder.textView_reportPercentage.setText(percentageComplete);

        // Precentage in progress bar (Round the percentage value to nearest integer)
        int progressInt = (int) Math.round(data.getPercentage());
        holder.progressBar_reportPercentage.setProgress(progressInt);

        holder.cardView.setOnClickListener(v -> {
            final Dialog dialogEntriesReport = new Dialog(context);

            // Set custom layout to dialog help
            dialogEntriesReport.setContentView(R.layout.dialog_entries_report);
            dialogEntriesReport.setTitle(v.getContext().getString(R.string.dialogInfo_title_help));

            // Dialog help
            TextView textView_type = dialogEntriesReport.findViewById(R.id.textView_dialogEntriesReport_type);
            TextView textView_category = dialogEntriesReport.findViewById(R.id.textView_dialogEntriesReport_category);
            TextView textView_period = dialogEntriesReport.findViewById(R.id.textView_dialogEntriesReport_period);
            ListView listView_entries = dialogEntriesReport.findViewById(R.id.listview_dialogEntriesReport_entries);

            // Set text
            // Type
            if (type.equals("I")) {
                textView_type.setText(v.getContext().getString(R.string.reportGraphicsFragment_profitTitle));
            } else {
                textView_type.setText(v.getContext().getString(R.string.reportGraphicsFragment_spendTitle));
            }
            // Category
            textView_category.setText(data.getName());
            // Period
            // Get the period components
            String begPeriodDay = begDate.substring(8, 10);
            String begPeriodMonth = begDate.substring(5, 7);
            String begPeriodYear = begDate.substring(0, 4);
            String finPeriodDay = finDate.substring(8, 10);
            String finPeriodMonth = finDate.substring(5, 7);
            String finPeriodYear = finDate.substring(0, 4);

            String separator = "/";

            // Period title
            String periodTitle = v.getContext().getString(R.string.reportGraphicsFragment_periodoTitle_from) + " " +
                    begPeriodDay +
                    separator +
                    begPeriodMonth +
                    separator +
                    begPeriodYear + " " +
                    v.getContext().getString(R.string.reportGraphicsFragment_periodoTitle_to) + " " +
                    finPeriodDay +
                    separator +
                    finPeriodMonth +
                    separator +
                    finPeriodYear;
            textView_period.setText(periodTitle);

            // Entries listview
            // Get Entries from database with period and instance as filter
            int categId = Integer.parseInt(data.getId());

            ArrayList<String> entriesDescription = new ArrayList<>();
            ArrayList<String> entriesDate = new ArrayList<>();
            ArrayList<String> entriesTime = new ArrayList<>();
            ArrayList<Double> entriesAmount = new ArrayList<>();
            if (type.equals("I")) {
                Cursor profits = db.getEntryProfitInDate(MainActivity.idInstance, begDate, finDate, categId);

                while (profits.moveToNext()) {
                    String description = profits.getString(7);

                    String date = profits.getString(3);
                    // Show date in format DD-MM-YY
                    String year = date.substring(0, 4);
                    String month = date.substring(5, 7);
                    String day = date.substring(8, 10);
                    String sepearator = "-";
                    date = day + sepearator + month + sepearator + year;

                    String time = profits.getString(4).substring(0, 5);

                    Double amountEntry = profits.getDouble(5);

                    entriesDescription.add(description);
                    entriesDate.add(date);
                    entriesTime.add(time);
                    entriesAmount.add(amountEntry);
                }
                profits.moveToPosition(-1);
            } else {
                Cursor spends = db.getEntrySpendInDate(MainActivity.idInstance, begDate, finDate, categId);

                while (spends.moveToNext()) {
                    String description = spends.getString(7);

                    String date = spends.getString(3);
                    // Show date in format DD-MM-YY
                    String year = date.substring(0, 4);
                    String month = date.substring(5, 7);
                    String day = date.substring(8, 10);
                    String sepearator = "-";
                    date = day + sepearator + month + sepearator + year;

                    String time = spends.getString(4).substring(0, 5);
                    // Show time in format HH:MM

                    Double amountEntry = spends.getDouble(6);

                    entriesDescription.add(description);
                    entriesDate.add(date);
                    entriesTime.add(time);
                    entriesAmount.add(amountEntry);
                }
                spends.moveToPosition(-1);
            }


            // Set List View Adapter
            ListEntriesAdapter listEntriesAdapter = new ListEntriesAdapter(activity, entriesDescription, entriesDate, entriesTime, entriesAmount);
            listView_entries.setAdapter(listEntriesAdapter);

            dialogEntriesReport.show();
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textView_reportCategName;
        TextView textView_reportAmount;
        TextView textView_reportPercentage;
        ImageView imageView_reportIc;
        ProgressBar progressBar_reportPercentage;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_reportList);
            textView_reportCategName = itemView.findViewById(R.id.textView_reportList_categName);
            textView_reportAmount = itemView.findViewById(R.id.textView_reportList_amount);
            textView_reportPercentage = itemView.findViewById(R.id.textView_reportList_percentage);
            imageView_reportIc = itemView.findViewById(R.id.imageView_reportList_ic);
            progressBar_reportPercentage = itemView.findViewById(R.id.progressBar_reportList_percentage);
        }
    }

}
