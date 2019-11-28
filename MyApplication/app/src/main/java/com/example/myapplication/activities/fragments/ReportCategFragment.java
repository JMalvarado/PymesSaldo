package com.example.myapplication.activities.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.ReportCategViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Objects;

public class ReportCategFragment extends Fragment implements View.OnClickListener {

    // Local variables

    // Views
    private ViewPager viewPager;
    private ReportCategViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private TextView textView_instanceName;
    private FloatingActionButton fab_dateBegin;
    private FloatingActionButton fab_dateFinal;
    private TextView textView_dateBegin;
    private TextView textView_dateFinal;
    private ImageButton imageButton_download;

    //Others
    private Calendar calendar;
    private static int intBegDay, intBegMonth, intBegYear, intFinDay, intFinMonth, intFinYear;
    private static String begDay, begMonth, begYear, finDay, finMonth, finYear, period;
    private DatePickerDialog datePickerDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_categ, container, false);

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.report_name));

        // Assign layout views variables
        textView_instanceName = view.findViewById(R.id.textView_fragmentReportCateg_instanceName);
        viewPager = view.findViewById(R.id.viewPager_reportCateg);
        tabLayout = view.findViewById(R.id.tabLayout_reportCateg);
        fab_dateBegin = view.findViewById(R.id.fab_calendarBegin_reportFragment);
        fab_dateBegin.setOnClickListener(this);
        fab_dateFinal = view.findViewById(R.id.fab_calendarFinal_reportFragment);
        fab_dateFinal.setOnClickListener(this);
        textView_dateBegin = view.findViewById(R.id.textView_reportFragment_dateBegin);
        textView_dateFinal = view.findViewById(R.id.textView_reportFragment_dateFinal);
        imageButton_download = view.findViewById(R.id.imageButton_fragmentReport_download);
        imageButton_download.setOnClickListener(this);

        // Hide FAB add entry from screen
        MainActivity.fab_addEntry.hide();

        // Set instance name as title
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Initialize and assign viewPager Adapter
        viewPagerAdapter = new ReportCategViewPagerAdapter(getFragmentManager(), view.getContext());
        viewPager.setAdapter(viewPagerAdapter);

        // Initialize and assign tab layout
        tabLayout.setupWithViewPager(viewPager);


        // If there is default period previously selected, set that period
        // Get the shared preferences period of instance
        SharedPreferences prefsReportPeriod = getActivity().getSharedPreferences("profilereportperiod", Context.MODE_PRIVATE);
        String reportPeriod = prefsReportPeriod.getString(MainActivity.idInstance, null);

        if (reportPeriod != null) {
            // Set text views with the default period
            String begPeriodDate = reportPeriod.substring(0, 10);
            String finPeriodDate = reportPeriod.substring(11, 21);

            // Set initial value for date variables

            begDay = begPeriodDate.substring(8, 10);
            begMonth = begPeriodDate.substring(5, 7);
            begYear = begPeriodDate.substring(0, 4);
            finDay = finPeriodDate.substring(8, 10);
            finMonth = finPeriodDate.substring(5, 7);
            finYear = finPeriodDate.substring(0, 4);

            textView_dateBegin.setText(begPeriodDate);
            textView_dateFinal.setText(finPeriodDate);
        }

        return view;
    }

    /**
     * On Click actions
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_calendarBegin_reportFragment:
                calendar = Calendar.getInstance();
                intBegDay = calendar.get(Calendar.DAY_OF_MONTH);
                intBegMonth = calendar.get(Calendar.MONTH);
                intBegYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        textView_dateBegin.setText(new StringBuilder().append(i).append("-").append(i1 + 1).append("-").append(i2).toString());
                        begDay = Integer.toString(i2);
                        begMonth = Integer.toString(i1 + 1);
                        begYear = Integer.toString(i);

                        // Verify if there is a selected date in both blank spaces
                        if (!textView_dateFinal.getText().toString().equals("")) {
                            storeDefaultPeriod();
                        }
                    }
                }, intBegYear, intBegMonth, intBegDay);
                datePickerDialog.show();

                break;


            case R.id.fab_calendarFinal_reportFragment:
                calendar = Calendar.getInstance();
                intFinDay = calendar.get(Calendar.DAY_OF_MONTH);
                intFinMonth = calendar.get(Calendar.MONTH);
                intFinYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        textView_dateFinal.setText(new StringBuilder().append(i).append("-").append(i1 + 1).append("-").append(i2).toString());
                        finDay = Integer.toString(i2);
                        finMonth = Integer.toString(i1 + 1);
                        finYear = Integer.toString(i);

                        // Verify if there is a selected date in both blank spaces
                        if (!textView_dateBegin.getText().toString().equals("")) {
                            // Store period in shared preference
                            storeDefaultPeriod();
                        }
                    }
                }, intFinYear, intFinMonth, intFinDay);
                datePickerDialog.show();

                break;


            case R.id.imageButton_fragmentReport_download:

                break;
        }
    }

    /**
     * Store the instance custom period for the report fragment in a shared preference
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

        String begDatePeriodDefault = begYear + "-" + begMonth + "-" + begDay;
        String finDatePeriodDefault = finYear + "-" + finMonth + "-" + finDay;
        period = begDatePeriodDefault + "/" + finDatePeriodDefault;

        SharedPreferences prefsReportPeriod = Objects.requireNonNull(getActivity()).
                getSharedPreferences("profilereportperiod", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsReportPeriod.edit();
        editor.putString(MainActivity.idInstance, period);
        editor.apply();

        // Refresh layouts
        // Initialize and assign viewPager Adapter
        viewPagerAdapter = new ReportCategViewPagerAdapter(getFragmentManager(), getContext());
        viewPager.setAdapter(viewPagerAdapter);

        // Initialize and assign tab layout
        tabLayout.setupWithViewPager(viewPager);
    }
}
