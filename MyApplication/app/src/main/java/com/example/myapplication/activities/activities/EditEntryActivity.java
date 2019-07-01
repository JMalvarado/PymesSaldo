package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Calendar;

public class EditEntryActivity extends AppCompatActivity {

    // Components view
    private EditText editText_profit;
    private EditText editText_spend;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_time;
    private TextView textView_instanceName;
    private Button button_in;
    private Button button_addDate;
    private Button button_addTime;
    private Spinner spinner_categories;
    private Spinner spinner_profiles;

    // Global variables
    private String id;
    private String ingreso;
    private String gasto;
    private String descr;
    private String fecha;
    private String hora;
    private String idCateg;
    private DatabaseManager SaldoDB;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private String category_id;
    private String id_inst;
    private String new_id_inst;
    private int spinner_DefaultPosition = 0;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayAdapter<String> spinnerAdapterProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        // Database
        SaldoDB = new DatabaseManager(this);

        // Get curent id instance
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        id_inst = prefs.getString("ID", null);

        // Initialize components view
        button_in = findViewById(R.id.button_editEntry_edit);
        editText_profit = findViewById(R.id.etIngreso_editEntry);
        editText_spend = findViewById(R.id.etGasto_editEntry);
        editText_description = findViewById(R.id.etdescripcion_editEntry);
        textView_date = findViewById(R.id.textView_editEntry_date);
        textView_time = findViewById(R.id.textView_editEntry_time);
        button_addDate = findViewById(R.id.button_editEntry_date);
        button_addTime = findViewById(R.id.button_editEntry_time);
        textView_instanceName = findViewById(R.id.textView_editEntry_instanceName);


        // Set Spinner category data
        spinner_categories = findViewById(R.id.spinner_editEntry_category);

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


        // Set spinner of profiles data
        spinner_profiles = findViewById(R.id.spinner_editEntry_profile);

        // Get profiles
        Cursor profilesData = SaldoDB.getInstancesAllData();

        // Array List to store the categories names
        ArrayList<String> profilesList = new ArrayList<>();

        // Add categories names to categoriesList
        while (profilesData.moveToNext()) {
            profilesList.add(profilesData.getString(1));
        }

        // Create adapter for the spinner of profiles
        spinnerAdapterProfiles = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, profilesList);
        spinner_profiles.setAdapter(spinnerAdapterProfiles);

        // Set spinner profiles onClickListener
        spinner_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profile_name = adapterView.getItemAtPosition(i).toString();
                new_id_inst = SaldoDB.getInstanceId(profile_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // Set instance name as title
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Get intent extra info
        id = getIntent().getStringExtra("ID");
        ingreso = getIntent().getStringExtra("INGRESO");
        gasto = getIntent().getStringExtra("GASTO");
        descr = getIntent().getStringExtra("DESCR");
        fecha = getIntent().getStringExtra("FECHA");
        hora = getIntent().getStringExtra("HORA");
        idCateg = getIntent().getStringExtra("CATEG");

        // Set actual data on views and variables
        editText_profit.setText(ingreso);
        editText_spend.setText(gasto);
        editText_description.setText(descr);
        textView_date.setText(fecha);
        textView_time.setText(hora);

        // Set actual category position in spinner
        String categName = SaldoDB.getCategoryName(idCateg);
        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).equals(categName)) {
                spinner_DefaultPosition = i;
                break;
            }
        }
        spinner_categories.setSelection(spinner_DefaultPosition);

        // Set actual profile position in spinner
        String id_prefs = prefs.getString("ID", null);
        String profileName = SaldoDB.getInstanceName(id_prefs);
        for (int i = 0; i < profilesList.size(); i++) {
            if (profilesList.get(i).equals(profileName)) {
                spinner_DefaultPosition = i;
                break;
            }
        }
        spinner_categories.setSelection(spinner_DefaultPosition);

        strYear = fecha.substring(0, 4);
        strMonth = fecha.substring(5, 7);
        strDay = fecha.substring(8);
    }

    /**
     * Alert dialog to add category
     */
    private void openDialog() {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(EditEntryActivity.this);
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
                        Toast.makeText(EditEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_existCategory), Toast.LENGTH_LONG).show();
                    } else {
                        SaldoDB.addCategory(editText_categoryName.getText().toString());
                    }
                } else {
                    Toast.makeText(EditEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
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
                spinnerAdapter = new ArrayAdapter<>(EditEntryActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                spinner_categories.setAdapter(spinnerAdapter);

                spinner_categories.setSelection(categoriesList.size()-2);
            }
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(EditEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();

                spinner_categories.setSelection(spinner_DefaultPosition);
            }
        });

        builder.show();
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

            case R.id.button_editEntry_time:
                Calendar timepick = Calendar.getInstance();
                int hourPick = timepick.get(Calendar.HOUR_OF_DAY);
                int minutesPick = timepick.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
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

                        textView_time.setText(new StringBuilder().append(strHour).append(":").append(strMinute).append(":00.000").toString());
                    }
                }, hourPick, minutesPick, false);
                timePickerDialog.show();

                break;

            case R.id.button_editEntry_edit:
                String IngresoVar = editText_profit.getText().toString();
                String GastoVar = editText_spend.getText().toString();

                time = textView_time.getText().toString();

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

                boolean isResultadd = SaldoDB.editEntryData(id, date, time, Integer.toString(IngresoVarint),
                        Integer.toString(GastoVarint), descripcion, id_inst, new_id_inst, category_id);

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
