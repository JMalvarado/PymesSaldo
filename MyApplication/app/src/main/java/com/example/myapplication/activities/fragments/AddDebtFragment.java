package com.example.myapplication.activities.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

public class AddDebtFragment extends Fragment implements View.OnClickListener {

    // View elements
    private EditText editText_amount;
    private EditText editText_description;
    private TextView textView_date;
    private FloatingActionButton fab_date;
    private Button button_ok;
    private Button button_cancel;

    // Global variables
    private DatabaseManager db;
    private DateTimeFormatter dtf;
    private String strDay, strMonth, strYear;
    private String date;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_debt, container, false);

        // Initialize Components view
        editText_amount = view.findViewById(R.id.editText_fragmentAddDebt_amount);
        editText_description = view.findViewById(R.id.editText_fragmentAddDebt_description);
        textView_date = view.findViewById(R.id.textView_fragmentAddDebt_date);
        fab_date = view.findViewById(R.id.fab_fragmentAddDebt_date);
        fab_date.setOnClickListener(this);
        button_ok = view.findViewById(R.id.button_fragmentAddDebt_addData);
        button_ok.setOnClickListener(this);
        button_cancel = view.findViewById(R.id.button_fragmentAddDebt_cancel);
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

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab_fragmentAddDebt_date:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (datePicker, i, i1, i2) -> {
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

            case R.id.button_fragmentAddDebt_addData:
                // Verify blank spaces
                if ((editText_amount.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }
                if (editText_description.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                String descripcion = editText_description.getText().toString();
                double amount = Double.parseDouble(editText_amount.getText().toString());

                // Get id instance
                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
                String id = prefs.getString("ID", null);

                boolean isResult = db.addDept(id, descripcion, amount, date);

                // Check result
                if (isResult) {
                    Toast.makeText(v.getContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(v.getContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }
                editText_amount.setText("");
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

                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.button_fragmentAddDebt_cancel:
                Objects.requireNonNull(getActivity()).onBackPressed();
                break;
        }
    }

    /**
     * Show given message in screen
     *
     * @param titulo
     * @param mensaje
     */
    private void showMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }
}
