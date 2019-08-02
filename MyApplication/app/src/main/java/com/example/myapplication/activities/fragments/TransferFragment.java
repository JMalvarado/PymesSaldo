package com.example.myapplication.activities.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.CustomItems;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class TransferFragment extends Fragment implements View.OnClickListener {

    // View components
    private EditText editText_mount;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_time;
    private FloatingActionButton fab_in;
    private FloatingActionButton fab_cancel;
    private FloatingActionButton fab_addDate;
    private FloatingActionButton fab_addTime;
    private Spinner spinner_from;
    private Spinner spinner_to;

    // Global variables
    private DateTimeFormatter dtf;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private String fromInstanceId;
    private String toInstanceId;
    // Database instance
    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        // Initialize components
        fab_in = view.findViewById(R.id.fab_transfer_in);
        fab_in.setOnClickListener(this);
        fab_cancel = view.findViewById(R.id.fab_transfer_cancel);
        fab_cancel.setOnClickListener(this);
        editText_mount = view.findViewById(R.id.editText_transfer_input);
        editText_description = view.findViewById(R.id.editText_transfer_description);
        textView_date = view.findViewById(R.id.textView_transfer_date);
        textView_time = view.findViewById(R.id.textView_transfer_time);
        fab_addDate = view.findViewById(R.id.fab_transfer_addDate);
        fab_addDate.setOnClickListener(this);
        fab_addTime = view.findViewById(R.id.fab_transfer_addTime);
        fab_addTime.setOnClickListener(this);
        spinner_from = view.findViewById(R.id.spinner_transfer_fromInstance);
        spinner_to = view.findViewById(R.id.spinner_transfer_toInstance);

        // Database instance
        db = new DatabaseManager(view.getContext());


        // Set date format
        dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        // Set date now in the textView
        String dateNow;
        LocalDateTime now = LocalDateTime.now();
        dateNow = dtf.format(now);
        // Store in format YY-MM-DD
        date = dateNow;
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


        // Set spinner profiles data
        ArrayAdapter<String> spinnerAdapterProfiles;

        // Get profiles
        Cursor profilesData = db.getInstancesAllData();

        // Array List to store the categories names
        ArrayList<String> profilesList = new ArrayList<>();

        // Add profiles names to profilesList
        while (profilesData.moveToNext()) {
            profilesList.add(profilesData.getString(1));
        }

        // Create adapter for the spinner of profiles "from"
        spinnerAdapterProfiles = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_items_theme_blacktext, profilesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);

                return textView;
            }
        };
        spinner_from.setAdapter(spinnerAdapterProfiles);

        // Create adapter for the spinner of profiles "to"
        spinner_to.setAdapter(spinnerAdapterProfiles);

        // Set spinner profiles "from" onClickListener
        spinner_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profile_name = adapterView.getItemAtPosition(i).toString();
                fromInstanceId = db.getInstanceId(profile_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Set spinner profiles "to" onClickListener
        spinner_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profile_name = adapterView.getItemAtPosition(i).toString();
                toInstanceId = db.getInstanceId(profile_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    private void showMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fab_transfer_in:
                String montoStr = editText_mount.getText().toString();

                double ingresoInt;
                double gastoInt;

                // Verify blank spaces
                if ((editText_mount.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }
                if (editText_description.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                String descripcion = editText_description.getText().toString();

                // Category id "from" instance
                String categoryIdFrom = db.getCategoryId(getString(R.string.mainActivity_addCategory_transfer), fromInstanceId);
                String categoryIdTo = db.getCategoryId(getString(R.string.mainActivity_addCategory_transfer), toInstanceId);

                // Add "ingreso" to "to" instance
                gastoInt = 0;
                ingresoInt = Double.parseDouble(montoStr);
                boolean isResult1 = db.addEntry(date, time, gastoInt, ingresoInt, descripcion, toInstanceId, categoryIdTo);

                // Add "gasto" to "from" instance
                gastoInt = Double.parseDouble(montoStr);
                ingresoInt =0;
                boolean isResult2 = db.addEntry(date, time, gastoInt, ingresoInt, descripcion, fromInstanceId, categoryIdFrom);

                // Check result
                if (isResult1 && isResult2) {
                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }

                editText_mount.setText("");
                editText_description.setText("");

                // Set date now in the textView
                String dateNow;
                LocalDateTime now = LocalDateTime.now();
                dateNow = dtf.format(now);
                // Store in format YYYY-MM-DD
                date = dateNow;
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

                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.fab_transfer_cancel:
                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.fab_transfer_addDate:
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

            case R.id.fab_transfer_addTime:
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
                    }
                }, hourPick, minutesPick, false);
                timePickerDialog.show();

                break;
        }
    }
}
