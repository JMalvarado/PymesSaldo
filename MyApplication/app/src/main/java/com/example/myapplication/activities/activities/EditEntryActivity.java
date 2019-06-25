package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

import java.util.Calendar;

public class EditEntryActivity extends AppCompatActivity {

    // Components view
    private EditText editText_profit;
    private EditText editText_spend;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_instanceName;
    private Button button_in;
    private Button button_addDate;

    // Global variables
    private String id;
    private String ingreso;
    private String gasto;
    private String descr;
    private String fecha;
    private DatabaseManager SaldoDB;
    private String strDay;
    private String strMonth;
    private String strYear;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        // Initialize components view
        button_in = findViewById(R.id.button_editEntry_edit);
        editText_profit = findViewById(R.id.etIngreso_editEntry);
        editText_spend = findViewById(R.id.etGasto_editEntry);
        editText_description = findViewById(R.id.etdescripcion_editEntry);
        textView_date = findViewById(R.id.textView_editEntry_date);
        button_addDate = findViewById(R.id.button_editEntry_date);
        textView_instanceName = findViewById(R.id.textView_editEntry_instanceName);

        // Set instance name as title
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Database
        SaldoDB = new DatabaseManager(this);

        // Get intent extra info
        id = getIntent().getStringExtra("ID");
        ingreso = getIntent().getStringExtra("INGRESO");
        gasto = getIntent().getStringExtra("GASTO");
        descr = getIntent().getStringExtra("DESCR");
        fecha = getIntent().getStringExtra("FECHA");

        // Set actual data on views and variables
        editText_profit.setText(ingreso);
        editText_spend.setText(gasto);
        editText_description.setText(descr);
        textView_date.setText(fecha);

        strYear = fecha.substring(0, 4);
        strMonth = fecha.substring(5, 7);
        strDay = fecha.substring(8);
    }

    public void onClickEditEntry(View view) {
        switch (view.getId()) {
            case R.id.button_editEntry_date:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
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

            case R.id.button_editEntry_edit:
                String IngresoVar = editText_profit.getText().toString();
                String GastoVar = editText_spend.getText().toString();

                int IngresoVarint;
                int GastoVarint;

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
                String id_inst = prefs.getString("ID", null);

                boolean isResultadd = SaldoDB.editEntryData(id, date, Integer.toString(IngresoVarint),
                        Integer.toString(GastoVarint), descripcion, id_inst);

                if (isResultadd) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }

                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);

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
