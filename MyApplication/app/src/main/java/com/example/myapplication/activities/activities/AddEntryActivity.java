package com.example.myapplication.activities.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private Spinner spinner_categories;

    // Global variables
    DateTimeFormatter dtf;
    private String strDay;
    private String strMonth;
    private String strYear;
    private String date;
    private String category_id;
    ArrayAdapter<String> spinnerAdapter;

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

        // Set Spinner category data
        spinner_categories = findViewById(R.id.spinner_addEntry_category);

        // Get categories
        Cursor categoriesData = SaldoDB.getCategoryAllData();

        // Array List to store the categories names
        ArrayList<String> categoriesList = new ArrayList<>();

        // Add categories names to categoriesList
        while (categoriesData.moveToNext()) {
            categoriesList.add(categoriesData.getString(1));
        }

        // Add option: "Agregar..." category
        categoriesList.add(getString(R.string.activity_addEntry_addCategory_spinner));

        // Create adapter for the spinner of categories
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        spinner_categories.setAdapter(spinnerAdapter);

        // Set default position
        spinner_categories.setSelection(0);

        // Set spinner categories onClickListener
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category_name = adapterView.getItemAtPosition(i).toString();

                if (category_name.equals(getString(R.string.activity_addEntry_addCategory_spinner))) {
                    // if category_name = Add...
                    openDialog();
                } else {
                    // else, get category id with the given name
                    category_id = SaldoDB.getCategoryId(category_name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Alert dialog to add category
     */
    private void openDialog() {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(AddEntryActivity.this);
        View subView = inflater.inflate(R.layout.dialog_add_category, null);

        // Initialize edit Text for category name
        final EditText editText_categoryName = subView.findViewById(R.id.dialogLayoutAddCategory_editText_categoryName);

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_title_addCategory));
        builder.setMessage(getString(R.string.alert_mssg_addCategory));
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Add category to data base
                if (!editText_categoryName.getText().toString().equals("")) {
                    // check if the category exist
                    Cursor cursorNames = SaldoDB.getCategoryAllData();
                    boolean isExistCategory = false;
                    while (cursorNames.moveToNext()) {
                        if (cursorNames.getString(1).equals(editText_categoryName.getText().toString())) {
                            isExistCategory = true;
                            break;
                        }
                    }
                    if (isExistCategory) {
                        Toast.makeText(AddEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_existCategory), Toast.LENGTH_LONG).show();
                    } else {
                        SaldoDB.addCategory(editText_categoryName.getText().toString());
                    }
                } else {
                    Toast.makeText(AddEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
                }
                // Get categories
                Cursor categoriesData = SaldoDB.getCategoryAllData();

                // Array List to store the categories names
                ArrayList<String> categoriesList = new ArrayList<>();

                // Add categories names to categoriesList
                while (categoriesData.moveToNext()) {
                    categoriesList.add(categoriesData.getString(1));
                }

                // Add option: "Agregar..." category
                categoriesList.add(getString(R.string.activity_addEntry_addCategory_spinner));
                // Create adapter for the spinner of categories
                spinnerAdapter = new ArrayAdapter<>(AddEntryActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                spinner_categories.setAdapter(spinnerAdapter);
                // Set default position
                spinner_categories.setSelection(categoriesList.size()-2);
            }
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(AddEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();

                // Set default position
                spinner_categories.setSelection(0);
            }
        });

        builder.show();
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

                boolean isResultadd = SaldoDB.addEntry(date, GastoVarint, IngresoVarint, descripcion, id, category_id);

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

