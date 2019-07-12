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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavingFragment extends Fragment implements View.OnClickListener {

    // Components view
    private RadioGroup radioGroup_addSavingMov;
    private RadioButton radioButton_payment;
    private RadioButton radioButton_withdrawal;
    private FloatingActionButton button_enter;
    private FloatingActionButton button_cancel;
    private FloatingActionButton fab_addDateSaving;
    private FloatingActionButton fab_addTimeSaving;
    private TextView textView_date;
    private TextView textView_time;
    private EditText editText_payment;
    private EditText editText_withdrawal;
    private CheckBox checkBox_addSpend;
    private CheckBox checkBox_addProfit;

    // Global variables
    private DateTimeFormatter dtf;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private DatabaseManager SaldoDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saving, container, false);

        // Initialize Components view
        checkBox_addSpend = view.findViewById(R.id.checkbox_fragment_saving_addSpend);
        checkBox_addProfit = view.findViewById(R.id.checkbox_fragment_saving_addProfit);

        radioGroup_addSavingMov = view.findViewById(R.id.radioGroup_addSavingMov);
        textView_date = view.findViewById(R.id.textView_addSaving_date);
        textView_time = view.findViewById(R.id.textView_addSaving_time);
        editText_payment = view.findViewById(R.id.etIngresoAbonoAhorro);
        editText_withdrawal = view.findViewById(R.id.etIngresoRetiroAhorro);

        radioButton_payment = view.findViewById(R.id.radioButton_fragmentSaving_payment);
        radioButton_payment.setOnClickListener(this);

        radioButton_withdrawal = view.findViewById(R.id.radioButton_fragmentSaving_withdrawal);
        radioButton_withdrawal.setOnClickListener(this);

        fab_addDateSaving = view.findViewById(R.id.fab_calendar_addSavingFragment);
        fab_addDateSaving.setOnClickListener(this);

        fab_addTimeSaving = view.findViewById(R.id.fab_clock_addSavingFragment);
        fab_addTimeSaving.setOnClickListener(this);

        button_enter = view.findViewById(R.id.Ingreso_saving);
        button_enter.setOnClickListener(this);

        button_cancel = view.findViewById(R.id.cancel_saving);
        button_cancel.setOnClickListener(this);

        // Database instance
        SaldoDB = new DatabaseManager(view.getContext());

        // Set date format
        dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");

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
                editText_withdrawal.setEnabled(false);
                editText_payment.setEnabled(true);
                checkBox_addProfit.setEnabled(false);
                checkBox_addSpend.setEnabled(true);
                checkBox_addProfit.setChecked(false);
                editText_withdrawal.setText("");

                break;

            case R.id.radioButton_fragmentSaving_withdrawal:
                editText_payment.setEnabled(false);
                editText_withdrawal.setEnabled(true);
                checkBox_addProfit.setEnabled(true);
                checkBox_addSpend.setEnabled(false);
                checkBox_addSpend.setChecked(false);
                editText_payment.setText("");

                break;

            case R.id.fab_calendar_addSavingFragment:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
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
                    }
                }, yearPick, monthPick, dayPick);
                datePickerDialog.show();

                break;

            case R.id.fab_clock_addSavingFragment:
                Calendar timepick = Calendar.getInstance();
                int hourPick = timepick.get(Calendar.HOUR_OF_DAY);
                int minutesPick = timepick.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        strHour = Integer.toString(i);
                        strMinute = Integer.toString(i1);

                        // Cast hour with 1 digit to 2
                        switch (strHour) {
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
                    }
                }, hourPick, minutesPick, false);
                timePickerDialog.show();

                break;

            case R.id.Ingreso_saving:
                // Check if there is blank spaces
                if ((editText_payment.getText().toString().equals("")) && (editText_withdrawal.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                // Main data to enter in database
                long inData;
                String type;

                if (radioGroup_addSavingMov.getCheckedRadioButtonId() == R.id.radioButton_fragmentSaving_payment) {
                    inData = Long.parseLong(editText_payment.getText().toString());
                    type = "A";
                } else {
                    inData = Long.parseLong(editText_withdrawal.getText().toString());
                    type = "R";
                }

                // Get id instance
                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
                String id = prefs.getString("ID", null);

                // Add data to database
                boolean isResult = SaldoDB.addSaving(id, inData, date, time, type);

                // Check result
                if (isResult) {
                    // Check add as a profit or spend option
                    if (checkBox_addSpend.isChecked()) {
                        long spend = Long.parseLong(editText_payment.getText().toString());
                        long in = 0;
                        String categoryId = "1";
                        String description = getString(R.string.fragment_saving_addSpend_description);

                        SaldoDB.addEntry(date, time, spend, in, description, id, categoryId);
                    } else if (checkBox_addProfit.isChecked()) {
                        long spend = 0;
                        long in = Long.parseLong(editText_withdrawal.getText().toString());
                        String categoryId = "1";
                        String description = getString(R.string.fragment_saving_addProfit_description);

                        SaldoDB.addEntry(date, time, spend, in, description, id, categoryId);
                    }

                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();

                    editText_payment.setText("");
                    editText_withdrawal.setText("");

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

                break;

            case R.id.cancel_saving:
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
