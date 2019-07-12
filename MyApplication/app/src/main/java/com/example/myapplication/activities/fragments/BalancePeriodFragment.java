package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class BalancePeriodFragment extends Fragment {

    // Instantiate view components
    private TextView textView_balance;
    private TextView textView_instanceName;
    private TextView textView_profit;
    private TextView textView_spend;
    private TextView textView_date;

    // Global variables
    private String idInstance, begDay, begMonth, begYear, finDay, finMonth, finYear, period;

    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance_period, container, false);

        // Initialize db manager instance
        db = new DatabaseManager(view.getContext());

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.balance_period_name));

        // Initialize view components
        textView_balance = view.findViewById(R.id.textView_balancePeriod);
        textView_instanceName = view.findViewById(R.id.textView_fragmentBalancePeriod_instanceName);
        textView_profit = view.findViewById(R.id.textView_fragmentBalancePeriod_ingresos);
        textView_spend = view.findViewById(R.id.textView_fragmentBalancePeriod_gastos);
        textView_date = view.findViewById(R.id.textView_fragmentBalancePeriod_period);

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);
        textView_instanceName.setText(name);

        // Set period subtitle
        // Set state for period option
        period = prefs.getString("PERIOD", null);
        assert period != null;
        String date = getPeriod(period);
        textView_date.setText(date);

        // Set dates for check ingresos and gastos
        String dateBegin = begYear + "-" + begMonth + "-" + begDay;
        String dateFinal = finYear + "-" + finMonth + "-" + finDay;

        String periodBalance = getPeriodBalance(dateBegin, dateFinal);
        String profit = Long.toString(getTotalProfit(dateBegin, dateFinal));
        String spend = Long.toString(getTotalSpend(dateBegin, dateFinal));

        // Set data in text views
        textView_profit.setText(profit);
        textView_spend.setText(spend);
        textView_balance.setText(periodBalance);

        return view;
    }

    /**
     * Get integer with the total profit of the month
     *
     * @return total profit
     */
    private long getTotalProfit(String dateBeg, String dateFin) {
        ArrayList<Long> profit = db.getEntryInDateIngresos(idInstance, dateBeg, dateFin);

        long totalProfit = 0;

        for (Long integer1 : profit) {
            long ing;
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
    private long getTotalSpend(String dateBeg, String dateFin) {
        ArrayList<Long> spend = db.getEntryInDateGastos(idInstance, dateBeg, dateFin);

        long totalSpend = 0;

        for (Long integer1 : spend) {
            long gas;
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
    private String getPeriodBalance(String dateBeg, String dateFin) {
        long totalProfit = getTotalProfit(dateBeg, dateFin);
        long totalSpend = getTotalSpend(dateBeg, dateFin);

        long balance = totalProfit - totalSpend;

        return Long.toString(balance);
    }

    /**
     * Get the dates range with the given period day
     *
     * @param period day of month from instance
     * @return String with the complete range of dates
     */
    private String getPeriod(String period) {
        // Cast day with 1 digit to 2
        switch (period) {
            case "1":
                begDay = finDay = "01";
                break;
            case "2":
                begDay = finDay = "02";
                break;
            case "3":
                begDay = finDay = "03";
                break;
            case "4":
                begDay = finDay = "04";
                break;
            case "5":
                begDay = finDay = "05";
                break;
            case "6":
                begDay = finDay = "06";
                break;
            case "7":
                begDay = finDay = "07";
                break;
            case "8":
                begDay = finDay = "08";
                break;
            case "9":
                begDay = finDay = "09";
                break;
            default:
                begDay = finDay = period;
                break;
        }

        // get actual month
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        String dateNow;
        LocalDateTime now = LocalDateTime.now();
        dateNow = dtf.format(now);
        String year = dateNow.substring(0, 4);
        String month = dateNow.substring(5, 7);
        String day = dateNow.substring(8, 10);

        // Check for january actual date, if true: then the previous date must be december
        // and the previous year must be different
        if (Integer.parseInt(day) < Integer.parseInt(period)) {
            if (month.equals("01")) {
                begMonth = "12";
                begYear = Integer.toString(Integer.parseInt(year) - 1);
            } else {
                begMonth = Integer.toString(Integer.parseInt(month) - 1);
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
                begYear = year;
            }

            // finMonth and year
            finMonth = month;
            finYear = year;
        } else {
            if (month.equals("12")) {
                finMonth = "01";
                finYear = Integer.toString(Integer.parseInt(year) + 1);
            } else {
                finMonth = Integer.toString(Integer.parseInt(month) + 1);
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
                finYear = year;
            }

            // finMonth and year
            begMonth = month;
            begYear = year;
        }

        return begDay + "-" + begMonth + "-" + begYear + " -- " + finDay + "-" + finMonth + "-" + finYear;
    }

}
