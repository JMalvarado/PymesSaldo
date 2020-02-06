package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.CustomAdapter;
import com.example.myapplication.activities.data.CustomItems;
import com.example.myapplication.activities.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Calendar;

public class EditEntryActivity extends AppCompatActivity {

    // Components view
    private EditText editText_profit;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_time;
    private TextView textView_instanceName;
    private Spinner spinner_categories;
    private Spinner spinner_profiles;
    private RadioGroup radioGroup_addMov;
    private RadioButton radioButton_profit;
    private RadioButton radioButton_spend;

    // Global variables
    private String id;
    private String ingreso;
    private String gasto;
    private String descr;
    private String fecha;
    private String hora;
    private String idCateg;
    private DatabaseManager db;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private String category_id;
    private String id_inst;
    private String new_id_inst;
    private int spinner_DefaultPosition = 0;
    private ArrayAdapter<String> spinnerAdapterProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        // Database
        db = new DatabaseManager(this);

        // Get curent id instance
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        id_inst = prefs.getString("ID", null);

        // Initialize components view
        editText_profit = findViewById(R.id.etIngreso_editEntry);
        editText_description = findViewById(R.id.etdescripcion_editEntry);
        textView_date = findViewById(R.id.textView_editEntry_date);
        textView_time = findViewById(R.id.textView_editEntry_time);
        textView_instanceName = findViewById(R.id.textView_editEntry_instanceName);
        radioGroup_addMov = findViewById(R.id.radioGroup_addMov);
        radioButton_profit = findViewById(R.id.radioButton_activityEditMov_Ingreso);
        radioButton_spend = findViewById(R.id.radioButton_activityEditMov_Gasto);

        // Set Spinner category data
        spinner_categories = findViewById(R.id.spinner_editEntry_category);

        // Get categories
        Cursor categoriesData = db.getCategoriesByInstance(MainActivity.idInstance);

        // Array List to store the categories names
        ArrayList<CustomItems> categoriesList = new ArrayList<>();

        // Add categories names to categoriesList
        while (categoriesData.moveToNext()) {
            String categName = categoriesData.getString(1);
            String categIcon = categoriesData.getString(2);
            int categIconId = getResources().getIdentifier(categIcon, "drawable", this.getPackageName());
            categoriesList.add(new CustomItems(categName, categIconId));
        }

        // Create adapter for the spinner of categories
        CustomAdapter customAdapter = new CustomAdapter(this, categoriesList);
        spinner_categories.setAdapter(customAdapter);


        // Set spinner categories onClickListener
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomItems items = (CustomItems) adapterView.getSelectedItem();
                String category_name = items.getSpinnerText();
                category_id = db.getCategoryId(category_name, new_id_inst);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // Set spinner of profiles data
        spinner_profiles = findViewById(R.id.spinner_editEntry_profile);

        // Get profiles
        Cursor profilesData = db.getInstancesAllData();

        // Array List to store the categories names
        ArrayList<String> profilesList = new ArrayList<>();

        // Add profiles names to profilesList
        while (profilesData.moveToNext()) {
            profilesList.add(profilesData.getString(1));
        }

        // Create adapter for the spinner of profiles
        spinnerAdapterProfiles = new ArrayAdapter<String>(this, R.layout.spinner_items_theme_blacktext, profilesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);

                return textView;
            }
        };
        spinner_profiles.setAdapter(spinnerAdapterProfiles);

        // Set spinner profiles onClickListener
        spinner_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profile_name = adapterView.getItemAtPosition(i).toString();
                new_id_inst = db.getInstanceId(profile_name);

                // Get categories
                Cursor categoriesData = db.getCategoriesByInstance(new_id_inst);

                // Array List to store the categories names
                ArrayList<CustomItems> categoriesList = new ArrayList<>();

                // Add categories names to categoriesList
                while (categoriesData.moveToNext()) {
                    String categName = categoriesData.getString(1);
                    String categIcon = categoriesData.getString(2);
                    int categIconId = getResources().getIdentifier(categIcon, "drawable",
                            adapterView.getContext().getPackageName());
                    categoriesList.add(new CustomItems(categName, categIconId));
                }

                // Create adapter for the spinner of categories
                CustomAdapter customAdapter = new CustomAdapter(adapterView.getContext(), categoriesList);
                spinner_categories.setAdapter(customAdapter);

                if (new_id_inst.equals(MainActivity.idInstance)) {
                    // Set actual category position in spinner
                    String categName = db.getCategoryName(idCateg, MainActivity.idInstance);
                    for (int j = 0; j < categoriesList.size(); j++) {
                        if (categoriesList.get(j).getSpinnerText().equals(categName)) {
                            spinner_DefaultPosition = j;
                            break;
                        }
                    }
                    spinner_categories.setSelection(spinner_DefaultPosition);
                }
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
        fecha = date = getIntent().getStringExtra("FECHA");
        hora = time = getIntent().getStringExtra("HORA");
        idCateg = getIntent().getStringExtra("CATEG");

        // Set actual data on views and variables
        if (ingreso.equals("0.0")) {
            radioButton_spend.setChecked(true);
            editText_profit.setText(gasto);
        } else {
            radioButton_profit.setChecked(true);
            editText_profit.setText(ingreso);
        }
        editText_description.setText(descr);

        // Show date in format DD-MM-YY
        String year = fecha.substring(0, 4);
        String month = fecha.substring(5, 7);
        String day = fecha.substring(8, 10);
        String sepearator = "-";
        String dateToShow = day + sepearator + month + sepearator + year;
        textView_date.setText(dateToShow);
        // Show time in format HH:MM
        String timeToShow = hora.substring(0, 5);
        textView_time.setText(timeToShow);

        // Set actual category position in spinner
        String categName = db.getCategoryName(idCateg, MainActivity.idInstance);
        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).getSpinnerText().equals(categName)) {
                spinner_DefaultPosition = i;
                break;
            }
        }
        spinner_categories.setSelection(spinner_DefaultPosition);

        // Set actual profile position in spinner
        String id_prefs = prefs.getString("ID", null);
        String profileName = db.getInstanceName(id_prefs);
        for (int i = 0; i < profilesList.size(); i++) {
            if (profilesList.get(i).equals(profileName)) {
                spinner_DefaultPosition = i;
                break;
            }
        }
        spinner_profiles.setSelection(spinner_DefaultPosition);

        strYear = fecha.substring(0, 4);
        strMonth = fecha.substring(5, 7);
        strDay = fecha.substring(8);
    }

    public void onClickEditEntry(View view) {
        switch (view.getId()) {
            case R.id.fab_calendar_editMovActivity:
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

                        // Show in format DD-MM-YYYY
                        textView_date.setText(new StringBuilder().append(strDay).append("-").append(strMonth).append("-").append(strYear).toString());
                        // Store in format YYYY-MM-DD
                        date = new StringBuilder().append(strYear).append("-").append(strMonth).append("-").append(strDay).toString();
                    }
                }, yearPick, monthPick, dayPick);
                datePickerDialog.show();

                break;

            case R.id.fab_clock_editMovActivity:
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

            case R.id.button_activityEditEntry_addData:
                String montoStr = editText_profit.getText().toString();

                if ((editText_profit.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                if (editText_description.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                double ingresoInt;
                double gastoInt;

                // Set 0 to blank spaces
                if (radioButton_spend.isChecked()) {
                    ingresoInt = 0;
                    gastoInt = Double.parseDouble(montoStr);
                } else {
                    ingresoInt = Double.parseDouble(montoStr);
                    gastoInt = 0;
                }

                String descripcion = editText_description.getText().toString();

                boolean isResultadd = db.editEntryData(id, date, time, ingresoInt,
                        gastoInt, descripcion, id_inst, new_id_inst, category_id);

                if (isResultadd) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }

                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);

                break;

            case R.id.button_activityEditEntry_cancel:
                onBackPressed();

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
