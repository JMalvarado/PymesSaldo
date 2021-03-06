package com.example.myapplication.activities.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;


public class SavingFragment extends Fragment implements View.OnClickListener {

    // Components view
    private RadioGroup radioGroup_addSavingMov;
    private RadioButton radioButton_payment;
    private RadioButton radioButton_withdrawal;
    private Button button_enter;
    private Button button_cancel;
    private FloatingActionButton fab_addDateSaving;
    private FloatingActionButton fab_addTimeSaving;
    private TextView textView_date;
    private TextView textView_time;
    private EditText editText_amount;
    private CheckBox checkBox_addSpendOrProfit;

    // Global variables
    private DateTimeFormatter dtf;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saving, container, false);

        // Initialize Components view
        checkBox_addSpendOrProfit = view.findViewById(R.id.checkbox_fragment_saving_addSpendOrProfit);

        radioGroup_addSavingMov = view.findViewById(R.id.radioGroup_addSavingMov);
        textView_date = view.findViewById(R.id.textView_addSaving_date);
        textView_time = view.findViewById(R.id.textView_addSaving_time);
        editText_amount = view.findViewById(R.id.editText_fragmentSaving_amount);

        radioButton_payment = view.findViewById(R.id.radioButton_fragmentSaving_payment);
        radioButton_payment.setOnClickListener(this);

        radioButton_withdrawal = view.findViewById(R.id.radioButton_fragmentSaving_withdrawal);
        radioButton_withdrawal.setOnClickListener(this);

        fab_addDateSaving = view.findViewById(R.id.fab_calendar_addSavingFragment);
        fab_addDateSaving.setOnClickListener(this);

        fab_addTimeSaving = view.findViewById(R.id.fab_clock_addSavingFragment);
        fab_addTimeSaving.setOnClickListener(this);

        button_enter = view.findViewById(R.id.button_fragmentAddSave_addData);
        button_enter.setOnClickListener(this);

        button_cancel = view.findViewById(R.id.button_fragmentAddSave_cancel);
        button_cancel.setOnClickListener(this);

        // Database instance
        db = new DatabaseManager(view.getContext());

        // Set date format
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Set date now in the textView
        String DateNow;
        LocalDateTime now = LocalDateTime.now();
        DateNow = dtf.format(now);
        // Store in format YYYY-MM-DD
        date = DateNow;
        // Show date in format DD-MM-YYYY
        String dateToShow;
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String sepearator = "-";
        dateToShow = day + sepearator + month + sepearator + year;
        textView_date.setText(dateToShow);

        // Store time in format HH:MM:SS.SSSS
        time = java.time.LocalTime.now().toString();
        String timeToShow = time.substring(0, 5);
        // Show time in format HH:MM
        textView_time.setText(timeToShow);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioButton_fragmentSaving_payment:
                checkBox_addSpendOrProfit.setText(getString(R.string.fragment_saving_checkbox_addSpend));
                editText_amount.setText("");

                break;

            case R.id.radioButton_fragmentSaving_withdrawal:
                checkBox_addSpendOrProfit.setText(getString(R.string.fragment_saving_checkbox_addProfit));
                editText_amount.setText("");

                break;

            case R.id.fab_calendar_addSavingFragment:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), (datePicker, i, i1, i2) -> {
                    strDay = Integer.toString(i2);
                    strMonth = Integer.toString(i1 + 1);
                    strYear = Integer.toString(i);

                    // Cast day with 1 digit to 2
                    switch (strDay) {
                        case "1":
                            strDay = "01";
                            break;
                        case "2":
                            strDay = "02";
                            break;
                        case "3":
                            strDay = "03";
                            break;
                        case "4":
                            strDay = "04";
                            break;
                        case "5":
                            strDay = "05";
                            break;
                        case "6":
                            strDay = "06";
                            break;
                        case "7":
                            strDay = "07";
                            break;
                        case "8":
                            strDay = "08";
                            break;
                        case "9":
                            strDay = "09";
                            break;
                        default:
                            break;
                    }

                    // Cast month with 1 digit to 2
                    switch (strMonth) {
                        case "1":
                            strMonth = "01";
                            break;
                        case "2":
                            strMonth = "02";
                            break;
                        case "3":
                            strMonth = "03";
                            break;
                        case "4":
                            strMonth = "04";
                            break;
                        case "5":
                            strMonth = "05";
                            break;
                        case "6":
                            strMonth = "06";
                            break;
                        case "7":
                            strMonth = "07";
                            break;
                        case "8":
                            strMonth = "08";
                            break;
                        case "9":
                            strMonth = "09";
                            break;
                        default:
                            break;
                    }
                    // Show in format DD-MM-YY
                    textView_date.setText(new StringBuilder().append(strDay).append("-").append(strMonth).append("-").append(strYear).toString());
                    // Store in format YY-MM-DD
                    date = new StringBuilder().append(strYear).append("-").append(strMonth).append("-").append(strDay).toString();
                }, yearPick, monthPick, dayPick);
                datePickerDialog.show();

                break;

            case R.id.fab_clock_addSavingFragment:
                Calendar timepick = Calendar.getInstance();
                int hourPick = timepick.get(Calendar.HOUR_OF_DAY);
                int minutesPick = timepick.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), (timePicker, i, i1) -> {
                    strHour = Integer.toString(i);
                    strMinute = Integer.toString(i1);

                    // Cast hour with 1 digit to 2
                    switch (strHour) {
                        case "0":
                            strHour = "00";
                            break;
                        case "1":
                            strHour = "01";
                            break;
                        case "2":
                            strHour = "02";
                            break;
                        case "3":
                            strHour = "03";
                            break;
                        case "4":
                            strHour = "04";
                            break;
                        case "5":
                            strHour = "05";
                            break;
                        case "6":
                            strHour = "06";
                            break;
                        case "7":
                            strHour = "07";
                            break;
                        case "8":
                            strHour = "08";
                            break;
                        case "9":
                            strHour = "09";
                            break;
                        default:
                            break;
                    }

                    // Cast minute with 1 digit to 2
                    switch (strMinute) {
                        case "0":
                            strMinute = "00";
                            break;
                        case "1":
                            strMinute = "01";
                            break;
                        case "2":
                            strMinute = "02";
                            break;
                        case "3":
                            strMinute = "03";
                            break;
                        case "4":
                            strMinute = "04";
                            break;
                        case "5":
                            strMinute = "05";
                            break;
                        case "6":
                            strMinute = "06";
                            break;
                        case "7":
                            strMinute = "07";
                            break;
                        case "8":
                            strMinute = "08";
                            break;
                        case "9":
                            strMinute = "09";
                            break;
                        default:
                            break;
                    }

                    textView_time.setText(new StringBuilder().append(strHour).append(":").append(strMinute).toString());
                    time = new StringBuilder().append(strHour).append(":").append(strMinute).append(":00.000").toString();
                }, hourPick, minutesPick, false);
                timePickerDialog.show();

                break;

            case R.id.button_fragmentAddSave_addData:
                // Check if there is blank spaces
                if (editText_amount.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                // Main data to enter in database
                double inData;
                String type;

                if (radioGroup_addSavingMov.getCheckedRadioButtonId() == R.id.radioButton_fragmentSaving_payment) {
                    inData = Double.parseDouble(editText_amount.getText().toString());
                    type = "A";
                } else {
                    inData = Double.parseDouble(editText_amount.getText().toString());
                    type = "R";
                }

                // Get id instance
                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
                String id = prefs.getString("ID", null);

                // Add data to database
                boolean isResult = db.addSaving(id, inData, date, time, type);

                // Check result
                if (isResult) {
                    // Check add as a profit or spend option
                    if ((checkBox_addSpendOrProfit.isChecked()) && (radioGroup_addSavingMov.getCheckedRadioButtonId() == R.id.radioButton_fragmentSaving_payment)) {
                        double spend = Double.parseDouble(editText_amount.getText().toString());
                        double in = 0;
                        String description = getString(R.string.fragment_saving_addSpend_description);
                        // Category id
                        String categoryId = db.getCategoryId(getString(R.string.mainActivity_addCategory_saving), MainActivity.idInstance);

                        db.addEntry(date, time, spend, in, description, id, categoryId);
                    } else if ((checkBox_addSpendOrProfit.isChecked()) && (radioGroup_addSavingMov.getCheckedRadioButtonId() == R.id.radioButton_fragmentSaving_withdrawal)) {
                        double spend = 0;
                        double in = Double.parseDouble(editText_amount.getText().toString());
                        String description = getString(R.string.fragment_saving_addProfit_description);
                        // Category id
                        String categoryId = db.getCategoryId(getString(R.string.mainActivity_addCategory_saving), MainActivity.idInstance);

                        db.addEntry(date, time, spend, in, description, id, categoryId);
                    }

                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();

                    editText_amount.setText("");

                    // Set date now in the textView
                    String DateNow;
                    LocalDateTime now = LocalDateTime.now();
                    DateNow = dtf.format(now);
                    // Store in format YY-MM-DD
                    date = DateNow;
                    // Show date in format DD-MM-YY
                    String dateToShow;
                    String year = date.substring(0, 4);
                    String month = date.substring(5, 7);
                    String day = date.substring(8, 10);
                    String sepearator = "-";
                    dateToShow = day + sepearator + month + sepearator + year;
                    textView_date.setText(dateToShow);

                    // Store time in format HH:MM:SS.SSSS
                    time = java.time.LocalTime.now().toString();
                    String timeToShow = time.substring(0, 5);
                    // Show time in format HH:MM
                    textView_time.setText(timeToShow);

                } else {
                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }

                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.button_fragmentAddSave_cancel:
                Objects.requireNonNull(getActivity()).onBackPressed();
                break;
        }
    }

    private void showMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }
}
