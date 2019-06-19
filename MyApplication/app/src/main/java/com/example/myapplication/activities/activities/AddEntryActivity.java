package com.example.myapplication.activities.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AddEntryActivity extends AppCompatActivity {

    // View components
    private EditText editText_profit;
    private EditText editText_spend;
    private EditText editText_description;
    private Button button_in;

    // Global variables
    DateTimeFormatter dtf;

    // Database instance
    public static DatabaseManager SaldoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // Initialize components
        button_in = findViewById(R.id.Ingreso);
        editText_profit = findViewById(R.id.etIngreso);
        editText_spend = findViewById(R.id.etGasto);
        editText_description = findViewById(R.id.etdescripcion);

        // Database instance
        SaldoDB = new DatabaseManager(this);

        // Set date format
        dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");

    }

    public void onClickAddEntry(View v) {
        String IngresoVar = editText_profit.getText().toString();
        String GastoVar = editText_spend.getText().toString();

        int IngresoVarint;
        int GastoVarint;

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

        // Set now date
        String DateNow;
        LocalDateTime now = LocalDateTime.now();
        DateNow = dtf.format(now);

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

                boolean isResultadd = SaldoDB.addEntry(DateNow, GastoVarint, IngresoVarint, descripcion, id);

                if (isResultadd) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }
                editText_profit.setText("");
                editText_spend.setText("");
                editText_description.setText("");
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

