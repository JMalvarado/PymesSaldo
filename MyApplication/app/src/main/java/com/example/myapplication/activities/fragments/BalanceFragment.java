package com.example.myapplication.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class BalanceFragment extends Fragment {

    // Instantiate view components
    private TextView textView_balance;
    private TextView textView_rem;
    private TextView textView_balanceplusrem;
    private TextView textView_instanceName;
    private TextView textView_profit;
    private TextView textView_spend;
    private TextView textView_date;
    private LinearLayout layout_profit;
    private LinearLayout layout_spend;
    private LinearLayout layout_equal;
    private LinearLayout layout_remanents;
    private LinearLayout layout_equalPlusRemanents;

    // Global variables
    private String idInstance;

    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_balance, container, false);

        // Initialize db manager instance
        db = new DatabaseManager(view.getContext());

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.balance_name));

        // Initialize view components
        textView_balance = view.findViewById(R.id.textView_balance);
        textView_rem = view.findViewById(R.id.textView_balanceprev);
        textView_balanceplusrem = view.findViewById(R.id.textView_balanceplusrem);
        textView_instanceName = view.findViewById(R.id.textView_fragmentBalance_instanceName);
        textView_profit = view.findViewById(R.id.textView_fragmentBalance_insgresos);
        textView_spend = view.findViewById(R.id.textView_fragmentBalance_gastos);
        textView_date = view.findViewById(R.id.textView_fragmentBalance_date);
        layout_profit = view.findViewById(R.id.layout_fragmentbalance_profit);
        layout_spend = view.findViewById(R.id.layout_fragmentbalance_spend);
        layout_equal = view.findViewById(R.id.layout_fragmentbalance_equalBalance);
        layout_remanents = view.findViewById(R.id.layout_fragmentbalance_remanents);
        layout_equalPlusRemanents = view.findViewById(R.id.layout_fragmentbalance_equalBalancePlusRemanents);

        // Info Click Listeners for layouts
        layout_profit.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_profit), Toast.LENGTH_SHORT).show());
        layout_spend.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_spend), Toast.LENGTH_SHORT).show());
        layout_equal.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_equal), Toast.LENGTH_SHORT).show());
        layout_remanents.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_remanents), Toast.LENGTH_SHORT).show());
        layout_equalPlusRemanents.setOnClickListener(v -> Toast.makeText(view.getContext(), getString(R.string.toast_balanceFragment_info_equalPlusRemanents), Toast.LENGTH_SHORT).show());

        // Show FAB add entry from screen
        MainActivity.fab_addEntry.show();

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);
        textView_instanceName.setText(name);

        // Set month and year in text view
        String monthAndYear;

        String[] monthNames = {getString(R.string.month_January),
                getString(R.string.month_February),
                getString(R.string.month_March),
                getString(R.string.month_April),
                getString(R.string.month_May),
                getString(R.string.month_June),
                getString(R.string.month_July),
                getString(R.string.month_August),
                getString(R.string.month_September),
                getString(R.string.month_October),
                getString(R.string.month_November),
                getString(R.string.month_December)};

        Calendar calendar = Calendar.getInstance();
        String month = monthNames[calendar.get(Calendar.MONTH)];
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        monthAndYear = month + ", " + year;

        textView_date.setText(monthAndYear);

        // Get actual month with 2 digits
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateNow;
        LocalDateTime now = LocalDateTime.now();
        dateNow = dtf.format(now);
        String monthtwodigits = dateNow.substring(5, 7);

        // Get data
        // actual month
        String balance = getMonthBalance(monthtwodigits, year);
        String profit = Double.toString(getTotalProfit(monthtwodigits, year));
        String spend = Double.toString(getTotalSpend(monthtwodigits, year));

        // Get previous month
        String monthprev;
        String yearprev;
        // Check if actual month is January, if true: then the previous month must be december
        if (monthtwodigits.equals("01")) {
            monthprev = "12";
            yearprev = Integer.toString(Integer.parseInt(year) - 1);
        } else {
            monthprev = Integer.toString(Integer.parseInt(monthtwodigits) - 1);
            switch (monthprev) {
                case "1":
                    monthprev = "01";
                    break;
                case "2":
                    monthprev = "02";
                    break;
                case "3":
                    monthprev = "03";
                    break;
                case "4":
                    monthprev = "04";
                    break;
                case "5":
                    monthprev = "05";
                    break;
                case "6":
                    monthprev = "06";
                    break;
                case "7":
                    monthprev = "07";
                    break;
                case "8":
                    monthprev = "08";
                    break;
                case "9":
                    monthprev = "09";
                    break;
                default:
                    break;
            }
            yearprev = year;
        }

        // Previous balance (remnants)
        String balanceprev = getMonthBalance(monthprev, yearprev);

        // Calc total balance
        double totalBalanceDoub = Double.parseDouble(balance) + Double.parseDouble(balanceprev);
        String totalBalance = Double.toHexString(totalBalanceDoub);

        // Format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String balanceDf = df.format(Double.parseDouble(balance));
        String profitDf = df.format(Double.parseDouble(profit));
        String spendDf = df.format(Double.parseDouble(spend));
        String balanceprevDf = df.format(Double.parseDouble(balanceprev));
        String balanceplusremDf = df.format(Double.parseDouble(totalBalance));
        // Set balance, profit asn spend on textView
        textView_balance.setText(balanceDf);
        textView_profit.setText(profitDf);
        textView_spend.setText(spendDf);
        textView_rem.setText(balanceprevDf);
        textView_balanceplusrem.setText(balanceplusremDf);

        // Set text color
        if (Double.parseDouble(balance) < 0) {
            textView_balance.setTextColor(Color.RED);
        } else if (Double.parseDouble(balance) == 0) {
            textView_balance.setTextColor(Color.BLACK);
        } else {
            textView_balance.setTextColor(Color.GREEN);
        }

        if (Double.parseDouble(totalBalance) < 0) {
            textView_balanceplusrem.setTextColor(Color.RED);
        } else if (Double.parseDouble(totalBalance) == 0) {
            textView_balanceplusrem.setTextColor(Color.BLACK);
        } else {
            textView_balanceplusrem.setTextColor(Color.GREEN);
        }

        return view;
    }

    /**
     * Get integer with the total profit of the month
     *
     * @return total profit
     */
    private double getTotalProfit(String month, String year) {
        ArrayList<Double> profit = db.getEntryInMonthYearIngresos(idInstance, month, year);

        double totalProfit = 0;

        for (Double integer1 : profit) {
            double ing;
            ing = integer1;
            totalProfit += ing;
        }

        return totalProfit;
    }

    /**
     * Get integer with the total spend of the month
     *
     * @return spend
     */
    private double getTotalSpend(String month, String year) {
        ArrayList<Double> spend = db.getEntryInMonthYearGastos(idInstance, month, year);

        double totalSpend = 0;

        for (Double integer1 : spend) {
            double gas;
            gas = integer1;
            totalSpend += gas;
        }

        return totalSpend;
    }

    /**
     * Get String with the balance (totalProfit - totalSpend) of current the month
     *
     * @return balance
     */
    private String getMonthBalance(String month, String year) {
        double totalProfit = getTotalProfit(month, year);
        double totalSpend = getTotalSpend(month, year);

        double balance = totalProfit - totalSpend;

        return Double.toString(balance);
    }
}
