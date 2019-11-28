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
                        textView_dateBegin.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
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
                        textView_dateFinal.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
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
        String begDatePeriodDefault = begDay + "-" + begMonth + "-" + begYear;
        String finDatePeriodDefault = finDay + "-" + finMonth + "-" + finYear;
        period = begDatePeriodDefault + "/" + finDatePeriodDefault;

        SharedPreferences prefsReportPeriod = Objects.requireNonNull(getActivity()).
                getSharedPreferences("profilereportperiod", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsReportPeriod.edit();
        editor.putString(MainActivity.idInstance, period);
        editor.apply();

        // Refresh layouts
        FragmentTransaction ft1 = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        ft1.detach(new ReportProfitFragment()).attach(new ReportProfitFragment()).commit();

        FragmentTransaction ft2 = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        ft2.detach(new ReportSpendFragment()).attach(new ReportSpendFragment()).commit();

        FragmentTransaction ft3 = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        ft3.detach(new ReportGraphicsFragment()).attach(new ReportGraphicsFragment()).commit();


//        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().
//                replace(R.id.reportProfit_layout, new ReportProfitFragment()).commit();
//        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().
//                replace(R.id.reportSpend_layout, new ReportSpendFragment()).commit();
//        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().
//                replace(R.id.reportGraphic_layout, new ReportGraphicsFragment()).commit();
    }
}
