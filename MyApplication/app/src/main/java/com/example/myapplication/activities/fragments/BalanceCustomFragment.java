package com.example.myapplication.activities.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class BalanceCustomFragment extends Fragment implements View.OnClickListener {

    // Instantiate view components
    private TextView textView_instanceName;
    private TextView textView_profit;
    private TextView textView_spend;
    private TextView textView_balance;
    private TextView textView_dateBegin;
    private TextView textView_dateFinal;
    private TextView textView_instanceList;
    private CardView layout_profit;
    private CardView layout_spend;
    private CardView layout_balance;
    private ImageButton imageButton_addInstance;
    private FloatingActionButton fab_dateBegin;
    private FloatingActionButton fab_dateFinal;
    private ProgressBar progressBar;
    private LinearLayout linearLayout_mainContent;

    // Global variables
    private DatabaseManager db;
    private Calendar calendar;
    private int intBegDay, intBegMonth, intBegYear, intFinDay, intFinMonth, intFinYear;
    private String begDay, begMonth, begYear, finDay, finMonth, finYear, period, begPeriodDate, finPeriodDate, name;
    private double profit, spend, balance;
    private DatePickerDialog datePickerDialog;
    private String[] listItems;
    private boolean[] checkedItems;
    private ArrayList<Integer> userItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_balance_custom, container, false);

        // Initialize db manager instance
        db = new DatabaseManager(view.getContext());

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.balance_custom_name));

        // Initialize view components
        textView_instanceName = view.findViewById(R.id.textView_fragmentBalanceCustom_instanceName);
        textView_profit = view.findViewById(R.id.textView_fragmentBalanceCustom_ingresos);
        textView_spend = view.findViewById(R.id.textView_fragmentBalanceCustom_gastos);
        textView_balance = view.findViewById(R.id.textView_fragmentBalanceCustom_balance);
        textView_dateBegin = view.findViewById(R.id.textView_fragmentBalanceCustom_dateBegin);
        textView_dateFinal = view.findViewById(R.id.textView_fragmentBalanceCustom_dateFinal);
        textView_instanceList = view.findViewById(R.id.textView_fragmentBalanceCustom_instanceList);
        layout_profit = view.findViewById(R.id.layout_fragmentBalanceCustom_profit);
        layout_spend = view.findViewById(R.id.layout_fragmentBalanceCustom_spend);
        layout_balance = view.findViewById(R.id.layout_fragmentBalanceCustom_balance);
        imageButton_addInstance = view.findViewById(R.id.imageButton_fragmentBalanceCustom_addInstance);
        imageButton_addInstance.setOnClickListener(this);
        fab_dateBegin = view.findViewById(R.id.fab_fragmentBalanceCustom_calendarBegin);
        fab_dateBegin.setOnClickListener(this);
        fab_dateFinal = view.findViewById(R.id.fab_fragmentBalanceCustom_calendarFinal);
        fab_dateFinal.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar_fragmentBalanceCustom);
        linearLayout_mainContent = view.findViewById(R.id.layout_fragmentBalanceCustom_mainContent);

        // Info Click Listeners for layouts
        layout_profit.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_profit), Toast.LENGTH_SHORT).show());
        layout_spend.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_spend), Toast.LENGTH_SHORT).show());
        layout_balance.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_equal), Toast.LENGTH_SHORT).show());

        // Show FAB add entry from screen
        MainActivity.fab_addEntry.show();

        SharedPreferences prefsBalanceInstances = getActivity().getSharedPreferences("profilebalanceinstances", Context.MODE_PRIVATE);
        String balanceInstances = prefsBalanceInstances.getString(MainActivity.idInstance, null);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Instances dialog box
        ////////////////////////////////////////////////////////////////////////////////////////////

        // Get instances list
        // Fill temp list for selectable dialog box
        Cursor instanceData = db.getInstancesAllData();
        listItems = new String[instanceData.getCount() - 1];
        int index = 0;
        while (instanceData.moveToNext()) {
            String instanceName = instanceData.getString(1);
            String instanceId = instanceData.getString(0);
            if (!(instanceId.equals(MainActivity.idInstance))) {
                listItems[index] = instanceName;
                index++;
            }
        }

        // Initialize check items list
        checkedItems = new boolean[listItems.length];

        // Set default by user values
        if (balanceInstances != null) {
            for (int i = 0; i < listItems.length; i++) {
                String id = db.getInstanceId(listItems[i]);
                String[] idsByUser = balanceInstances.split(",");
                for (String idPref : idsByUser) {
                    if (idPref.equals(id)) {
                        checkedItems[i] = true;
                        break;
                    }
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // If there is default list of instance previously selected, set that list
        // Else, create shared preferences for instance ids with current id
        // Get the shared preferences list of instance
        if (balanceInstances != null) {
            String[] instanceList = balanceInstances.split(",");

            String tvString = "";

            for (int i = 0; i < instanceList.length; i++) {
                String instanceName = db.getInstanceName(instanceList[i]);
                if (i == instanceList.length - 1) {
                    tvString = tvString.concat(instanceName);
                } else {
                    tvString = tvString.concat(instanceName + ", ");
                }
            }

            textView_instanceList.setText(tvString);
        } else {
            textView_instanceList.setText(name);

            SharedPreferences.Editor editor = prefsBalanceInstances.edit();
            editor.putString(MainActivity.idInstance, MainActivity.idInstance + ",");
            editor.apply();
        }

        // If there is default period previously selected, set that period
        // Get the shared preferences period of instance
        SharedPreferences prefsBalancePeriod = getActivity().getSharedPreferences("profilebalanceperiod", Context.MODE_PRIVATE);
        String balancePeriod = prefsBalancePeriod.getString(MainActivity.idInstance, null);
        if (balancePeriod != null) {
            // Set text views with the default period
            begPeriodDate = balancePeriod.substring(0, 10);
            finPeriodDate = balancePeriod.substring(11, 21);

            // Set initial value for date variables

            begDay = begPeriodDate.substring(8, 10);
            begMonth = begPeriodDate.substring(5, 7);
            begYear = begPeriodDate.substring(0, 4);
            finDay = finPeriodDate.substring(8, 10);
            finMonth = finPeriodDate.substring(5, 7);
            finYear = finPeriodDate.substring(0, 4);

            period = balancePeriod;

            String begPeriodDateFormated = begDay + "-" + begMonth + "-" + begYear;
            String finPeriodDateFormated = finDay + "-" + finMonth + "-" + finYear;

            textView_dateBegin.setText(begPeriodDateFormated);
            textView_dateFinal.setText(finPeriodDateFormated);

            // Get data
            new Task().execute(begPeriodDate, finPeriodDate, balanceInstances);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_fragmentBalanceCustom_addInstance:
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(getString(R.string.dialog_instances_title));
                builder.setMultiChoiceItems(listItems, checkedItems, (dialogInterface, position, isChecked) -> {
                    if (isChecked) {
                        if (!userItems.contains(position)) {
                            userItems.add(position);
                        }
                    } else if (userItems.contains(position)) {
                        userItems.remove((Integer) position);
                    }
                });

                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.dialog_instances_positiveButton), (dialogInterface, which) -> {
                    String list = db.getInstanceName(MainActivity.idInstance);
                    String ids = MainActivity.idInstance + ",";
                    for (int i = 0; i < userItems.size(); i++) {
                        if (i == 0) {
                            list = list.concat(", ");
                        }
                        list = list.concat(listItems[userItems.get(i)]);
                        if (i != userItems.size() - 1) {
                            list = list.concat(", ");
                        }
                        ids = ids.concat(db.getInstanceId(listItems[userItems.get(i)]) + ",");
                    }

                    SharedPreferences prefsBalanceInstances = Objects.requireNonNull(getActivity()).getSharedPreferences("profilebalanceinstances", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefsBalanceInstances.edit();
                    editor.putString(MainActivity.idInstance, ids);
                    editor.apply();

                    new Task().execute(begPeriodDate, finPeriodDate, ids);

                    textView_instanceList.setText(list);
                });

                builder.setNegativeButton(getString(R.string.dialog_instances_negativeButton), (dialogInterface, which) -> {
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));

                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));

                break;

            case R.id.fab_fragmentBalanceCustom_calendarBegin:
                calendar = Calendar.getInstance();
                intBegDay = calendar.get(Calendar.DAY_OF_MONTH);
                intBegMonth = calendar.get(Calendar.MONTH);
                intBegYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(v.getContext(), (datePicker, i, i1, i2) -> {
                    textView_dateBegin.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
                    begDay = Integer.toString(i2);
                    begMonth = Integer.toString(i1 + 1);
                    begYear = Integer.toString(i);

                    // Verify if there is a selected date in both blank spaces
                    if (!textView_dateFinal.getText().toString().equals("")) {
                        storeDefaultPeriod();
                    }
                }, intBegYear, intBegMonth, intBegDay);
                datePickerDialog.show();

                break;

            case R.id.fab_fragmentBalanceCustom_calendarFinal:
                calendar = Calendar.getInstance();
                intFinDay = calendar.get(Calendar.DAY_OF_MONTH);
                intFinMonth = calendar.get(Calendar.MONTH);
                intFinYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(v.getContext(), (datePicker, i, i1, i2) -> {
                    textView_dateFinal.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
                    finDay = Integer.toString(i2);
                    finMonth = Integer.toString(i1 + 1);
                    finYear = Integer.toString(i);

                    // Verify if there is a selected date in both blank spaces
                    if (!textView_dateBegin.getText().toString().equals("")) {
                        // Store period in shared preference
                        storeDefaultPeriod();
                    }
                }, intFinYear, intFinMonth, intFinDay);
                datePickerDialog.show();

                break;
        }
    }

    /**
     * Store the instance custom period for the balance fragment in a shared preference
     */
    private void storeDefaultPeriod() {
        // Cast begin day with 1 digit to 2
        switch (begDay) {
            case "1":
                begDay = "01";
                break;
            case "2":
                begDay = "02";
                break;
            case "3":
                begDay = "03";
                break;
            case "4":
                begDay = "04";
                break;
            case "5":
                begDay = "05";
                break;
            case "6":
                begDay = "06";
                break;
            case "7":
                begDay = "07";
                break;
            case "8":
                begDay = "08";
                break;
            case "9":
                begDay = "09";
                break;
        }

        // Cast begin month with 1 digit to 2
        switch (begMonth) {
            case "1":
                begMonth = "01";
                break;
            case "2":
                begMonth = "02";
                break;
            case "3":
                begMonth = "03";
                break;
            case "4":
                begMonth = "04";
                break;
            case "5":
                begMonth = "05";
                break;
            case "6":
                begMonth = "06";
                break;
            case "7":
                begMonth = "07";
                break;
            case "8":
                begMonth = "08";
                break;
            case "9":
                begMonth = "09";
                break;
            default:
                break;
        }

        // Cast Final day with 1 digit to 2
        switch (finDay) {
            case "1":
                finDay = "01";
                break;
            case "2":
                finDay = "02";
                break;
            case "3":
                finDay = "03";
                break;
            case "4":
                finDay = "04";
                break;
            case "5":
                finDay = "05";
                break;
            case "6":
                finDay = "06";
                break;
            case "7":
                finDay = "07";
                break;
            case "8":
                finDay = "08";
                break;
            case "9":
                finDay = "09";
                break;
            default:
                break;
        }

        // Cast Final month with 1 digit to 2
        switch (finMonth) {
            case "1":
                finMonth = "01";
                break;
            case "2":
                finMonth = "02";
                break;
            case "3":
                finMonth = "03";
                break;
            case "4":
                finMonth = "04";
                break;
            case "5":
                finMonth = "05";
                break;
            case "6":
                finMonth = "06";
                break;
            case "7":
                finMonth = "07";
                break;
            case "8":
                finMonth = "08";
                break;
            case "9":
                finMonth = "09";
                break;
            default:
                break;
        }

        begPeriodDate = begYear + "-" + begMonth + "-" + begDay;
        finPeriodDate = finYear + "-" + finMonth + "-" + finDay;
        period = begPeriodDate + "/" + finPeriodDate;

        SharedPreferences prefsBalancePeriod = Objects.requireNonNull(getActivity()).
                getSharedPreferences("profilebalanceperiod", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsBalancePeriod.edit();
        editor.putString(MainActivity.idInstance, period);
        editor.apply();

        // Refresh layouts
        // Get data
        SharedPreferences prefsBalanceInstances = getActivity().getSharedPreferences("profilebalanceinstances", Context.MODE_PRIVATE);
        String balanceInstances = prefsBalanceInstances.getString(MainActivity.idInstance, null);
        new Task().execute(begPeriodDate, finPeriodDate, balanceInstances);
    }

    /**
     * show data.
     */
    @SuppressLint("StaticFieldLeak")
    private class Task extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            linearLayout_mainContent.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            linearLayout_mainContent.setVisibility(View.VISIBLE);

            // Format
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            DecimalFormat df = new DecimalFormat("###,###.##", symbols);
            String balanceDf = df.format(balance);
            String profitDf = df.format(profit);
            String spendDf = df.format(spend);
            // Set balance, profit and spend on textView
            textView_profit.setText(profitDf);
            textView_spend.setText(spendDf);
            textView_balance.setText(balanceDf);

            // Set text color
            if (balance < 0) {
                textView_balance.setTextColor(Color.RED);
            } else if (balance == 0) {
                textView_balance.setTextColor(Color.BLACK);
            } else {
                textView_balance.setTextColor(Color.GREEN);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            // Get WHERE clause for db query
            String[] instanceIdlist = strings[2].split(",");
            String whereClause = getWhereClause(instanceIdlist);

            // Get Entries from database with period and instance list as filter
            Cursor entries = db.getEntryByPeriodAndInstance(strings[0], strings[1], whereClause);

            // Get total profit
            double totalProfit = 0;
            while (entries.moveToNext()) {
                if (!(entries.getString(7).equals(getString(R.string.fragment_balance_class_remnantDescription)))) {
                    totalProfit += entries.getDouble(5);
                }
            }
            entries.moveToPosition(-1);

            // Get total spend
            double totalSpend = 0;
            while (entries.moveToNext()) {
                totalSpend += entries.getDouble(6);
            }
            entries.moveToPosition(-1);

            // Calculate balance
            double totalBalance = totalProfit - totalSpend;

            profit = totalProfit;
            spend = totalSpend;
            balance = totalBalance;

            return null;
        }
    }

    /**
     * Get the where clause to use in database query by certain instances
     *
     * @param instanceList
     * @return
     */
    private String getWhereClause(String[] instanceList) {
        String where = "";
        for (int i = 0; i < instanceList.length; i++) {
            String tmp = "Instancias_ID=";
            tmp = tmp.concat(instanceList[i]);
            if (!(i == instanceList.length - 1)) {
                where = where.concat(tmp + " OR ");
            } else {
                where = where.concat(tmp);
            }
        }

        return where;
    }
}
