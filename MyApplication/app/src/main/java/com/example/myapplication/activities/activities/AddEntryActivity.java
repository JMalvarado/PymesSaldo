package com.example.myapplication.activities.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public class AddEntryActivity extends AppCompatActivity {

    // View components
    private EditText editText_profit;
    private EditText editText_spend;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_instanceName;
    private Button button_in;
    private Button button_addDate;

    // Global variables
    DateTimeFormatter dtf;
    private String strDay;
    private String strMonth;
    private String strYear;
    private String date;

    // Database instance
    private DatabaseManager SaldoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // Initialize components
        button_in = findViewById(R.id.Ingreso);
        editText_profit = findViewById(R.id.etIngreso);
        editText_spend = findViewById(R.id.etGasto);
        editText_description = findViewById(R.id.etdescripcion);
        textView_date = findViewById(R.id.textView_addEntry_date);
        button_addDate = findViewById(R.id.button_addEntry_date);
        textView_instanceName = findViewById(R.id.textView_addEntry_instanceName);

        // Database instance
        SaldoDB = new DatabaseManager(this);

        // Set date format
        dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        // Set instance name as title
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void onClickAddEntry(View v) {
        String IngresoVar = editText_profit.getText().toString();
        String GastoVar = editText_spend.getText().toString();

        int IngresoVarint;
        int GastoVarint;

        if (!textView_date.getText().toString().equals("")) {
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

            // Set complete selected date
            date = strYear + "-" + strMonth + "-" + strDay;
        } else {
            // Set now date
            String DateNow;
            LocalDateTime now = LocalDateTime.now();
            DateNow = dtf.format(now);

            date = DateNow;
        }

        // Set 0 to blank spaces
        if (IngresoVar.equals("")) {
            IngresoVarint = 0;
        } else {
            IngresoVarint = Integer.parseInt(IngresoVar);
        }

        if (GastoVar.equals("")) {
            GastoVarint = 0;
        } else {
            GastoVarint = Integer.parseInt(GastoVar);
        }

        switch (v.getId()) {
            case R.id.Ingreso:

                if ((editText_profit.getText().toString().equals("")) && (editText_spend.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                if (editText_description.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                String descripcion = editText_description.getText().toString();

                // Get id instance
                SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                String id = prefs.getString("ID", null);

                boolean isResultadd = SaldoDB.addEntry(date, GastoVarint, IngresoVarint, descripcion, id);

                if (isResultadd) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }
                editText_profit.setText("");
                editText_spend.setText("");
                editText_description.setText("");

                break;

            case R.id.button_addEntry_date:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        textView_date.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
                        strDay = Integer.toString(i2);
                        strMonth = Integer.toString(i1 + 1);
                        strYear = Integer.toString(i);
                    }
                }, yearPick, monthPick, dayPick);
                datePickerDialog.show();

                break;
        }

    }

    public void showMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }
}

