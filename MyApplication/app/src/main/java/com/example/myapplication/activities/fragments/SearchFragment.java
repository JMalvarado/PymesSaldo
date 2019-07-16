package com.example.myapplication.activities.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.*;
import com.example.myapplication.activities.activities.DataSearch;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.CustomAdapter;
import com.example.myapplication.activities.data.CustomItems;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SearchFragment extends Fragment implements View.OnClickListener {

    // View components
    private RadioGroup radioGroup_filter;
    private RadioButton radioButton_period, radioButton_month, radioButton_dates;
    private CheckBox checkBox_begining, checkBox_final;
    private FloatingActionButton fab_dateBegin, fab_dateFinal;
    private FloatingActionButton fab_find;
    private ProgressBar progressBar;
    private TextView textView_dateBeging, textView_dateFinal, textView_instanceName, textView_period;
    private Spinner spinner_categories;
    private Spinner spinner_type;

    // Global variables
    public static String begDay, begMonth, begYear, finDay, finMonth, finYear, period, type;
    public static int intBegDay, intBegMonth, intBegYear, intFinDay, intFinMonth, intFinYear;
    public static boolean radioButtonMonthIsChecked, checkboxBegIsChecked, checkboxFinalIsChecked, radioButtonPeriodIsChecked, radioButtonDatesIsChecked;
    public static int categoryIDSelected;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private ArrayAdapter<String> spinnerAdapterType;

    // Database manager instance
    private DatabaseManager db;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.search_name));

        // Initialize view componnents and click listeners
        radioGroup_filter = view.findViewById(R.id.radioGroup_fragmmentSearch_filter);

        radioButton_month = view.findViewById(R.id.radioButton_mes);
        radioButton_month.setOnClickListener(this);

        radioButton_dates = view.findViewById(R.id.radioButton_porfecha);
        radioButton_dates.setOnClickListener(this);

        checkBox_begining = view.findViewById(R.id.cbInicio);
        checkBox_begining.setOnClickListener(this);

        checkBox_final = view.findViewById(R.id.cbFinal);
        checkBox_final.setOnClickListener(this);

        radioButton_period = view.findViewById(R.id.radioButton_periodo);
        radioButton_period.setOnClickListener(this);

        fab_find = view.findViewById(R.id.Buscar_searchFragment);
        fab_find.setOnClickListener(this);

        fab_dateBegin = view.findViewById(R.id.fab_calendar1_searchFragment);
        fab_dateBegin.setOnClickListener(this);

        fab_dateFinal = view.findViewById(R.id.fab_calendar2_searchFragment);
        fab_dateFinal.setOnClickListener(this);

        textView_dateBeging = view.findViewById(R.id.textview_search_datebeg);
        textView_dateFinal = view.findViewById(R.id.textview_search_datefinal);
        textView_instanceName = view.findViewById(R.id.textView_fragmentSearch_instanceName);
        textView_period = view.findViewById(R.id.textView_fragmentSearch_period);

        spinner_categories = view.findViewById(R.id.spinner_search_category);
        spinner_type = view.findViewById(R.id.spinner_search_type);

        progressBar = view.findViewById(R.id.progressBar_searchFragment);

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        db = new DatabaseManager(view.getContext());

        // Set state for period option
        period = prefs.getString("PERIOD", null);
        assert period != null;
        if (period.equals("0")) {
            radioButton_period.setEnabled(false);
            radioButton_month.setChecked(true);

            checkBox_begining.setEnabled(false);
            fab_dateBegin.setEnabled(false);

            checkBox_final.setEnabled(false);
            fab_dateFinal.setEnabled(false);
        } else {
            radioButton_period.setEnabled(true);
            radioButton_period.setChecked(true);
            textView_period.setText(new StringBuilder().append("( ").
                    append(getString(R.string.fragment_search_period_period_header)).
                    append(" ").
                    append(period).
                    append(" )").toString());

            checkBox_begining.setEnabled(false);
            fab_dateBegin.setEnabled(false);
            fab_dateBegin.setVisibility(View.GONE);

            checkBox_final.setEnabled(false);
            fab_dateFinal.setEnabled(false);
            fab_dateFinal.setVisibility(View.GONE);

        }

        // Set Spinner category data

        // Get categories
        Cursor categoriesData = db.getCategoryAllData();

        // Array List to store the categories names
        // ArrayList<String> categoriesList = new ArrayList<>();
        ArrayList<CustomItems> categoriesList = new ArrayList<>();

        // Add option: "Todas"
        //categoriesList.add(getString(R.string.fragment_search_category_spinner));
        categoriesList.add(new CustomItems(getString(R.string.fragment_search_category_spinner), R.drawable.ic_categories_64));

        // Add categories names to categoriesList
        while (categoriesData.moveToNext()) {
            String categName = categoriesData.getString(1);
            String categIcon = categoriesData.getString(2);
            int categIconId = getResources().getIdentifier(categIcon, "drawable", view.getContext().getPackageName());
            categoriesList.add(new CustomItems(categName, categIconId));
        }

        // Create adapter for the spinner of categories
        //spinnerAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, categoriesList);
        //spinner_categories.setAdapter(spinnerAdapter);
        CustomAdapter customAdapter = new CustomAdapter(view.getContext(), categoriesList);
        spinner_categories.setAdapter(customAdapter);

        // Set default position
        spinner_categories.setSelection(0);

        // Set spinner categories onClickListener
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String itemSelected = adapterView.getItemAtPosition(i).toString();
                int categID;
                CustomItems items = (CustomItems) adapterView.getSelectedItem();
                String category_name = items.getSpinnerText();

                if (category_name.equals(getString(R.string.fragment_search_category_spinner))) {
                    categID = 0;
                } else {
                    categID = Integer.parseInt(db.getCategoryId(category_name));
                }
                categoryIDSelected = categID;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Set items on spinner mov type
        // Array List to store the type names
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add(getString(R.string.fragment_search_type_spinner_all));
        typeList.add(getString(R.string.fragment_search_type_spinner_profit));
        typeList.add(getString(R.string.fragment_search_type_spinner_spend));

        // Create adapter for the spinner of profiles
        spinnerAdapterType = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_items_theme_blacktext, typeList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);

                return textView;
            }
        };
        spinner_type.setAdapter(spinnerAdapterType);

        // Set spinner type onClickListener
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioButton_periodo:
                checkBox_begining.setEnabled(false);
                fab_dateBegin.setEnabled(false);
                fab_dateBegin.setVisibility(View.GONE);

                checkBox_final.setEnabled(false);
                fab_dateFinal.setEnabled(false);
                fab_dateFinal.setVisibility(View.GONE);

                checkBox_begining.setChecked(false);
                checkBox_final.setChecked(false);

                textView_dateBeging.setText("");
                textView_dateFinal.setText("");

                break;

            case R.id.radioButton_mes:
                checkBox_begining.setEnabled(false);
                fab_dateBegin.setEnabled(false);
                fab_dateBegin.setVisibility(View.GONE);

                checkBox_final.setEnabled(false);
                fab_dateFinal.setEnabled(false);
                fab_dateFinal.setVisibility(View.GONE);

                checkBox_begining.setChecked(false);
                checkBox_final.setChecked(false);

                textView_dateBeging.setText("");
                textView_dateFinal.setText("");

                break;

            case R.id.radioButton_porfecha:
                checkBox_begining.setEnabled(true);
                fab_dateBegin.setEnabled(true);
                fab_dateBegin.setVisibility(View.VISIBLE);

                checkBox_final.setEnabled(true);
                fab_dateFinal.setEnabled(true);
                fab_dateFinal.setVisibility(View.VISIBLE);

                break;

            case R.id.cbInicio:
                if (!checkBox_begining.isChecked()) {
                    fab_dateBegin.setEnabled(true);
                    fab_dateBegin.setVisibility(View.VISIBLE);

                } else {
                    fab_dateBegin.setEnabled(false);
                    fab_dateBegin.setVisibility(View.GONE);
                    textView_dateBeging.setText("");
                }

                break;

            case R.id.cbFinal:
                if (!checkBox_final.isChecked()) {
                    fab_dateFinal.setEnabled(true);
                    fab_dateFinal.setVisibility(View.VISIBLE);

                } else {
                    fab_dateFinal.setEnabled(false);
                    fab_dateFinal.setVisibility(View.GONE);
                    textView_dateFinal.setText("");
                }

                break;

            case R.id.fab_calendar1_searchFragment:
                calendar = Calendar.getInstance();
                intBegDay = calendar.get(Calendar.DAY_OF_MONTH);
                intBegMonth = calendar.get(Calendar.MONTH);
                intBegYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        textView_dateBeging.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
                        begDay = Integer.toString(i2);
                        begMonth = Integer.toString(i1 + 1);
                        begYear = Integer.toString(i);
                    }
                }, intBegYear, intBegMonth, intBegDay);
                datePickerDialog.show();

                break;

            case R.id.fab_calendar2_searchFragment:
                calendar = Calendar.getInstance();
                intFinDay = calendar.get(Calendar.DAY_OF_MONTH);
                intFinMonth = calendar.get(Calendar.MONTH);
                intFinYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        textView_dateFinal.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
                        finDay = Integer.toString(i2);
                        finMonth = Integer.toString(i1 + 1);
                        finYear = Integer.toString(i);
                    }
                }, intFinYear, intFinMonth, intFinDay);
                datePickerDialog.show();

                break;

            case R.id.Buscar_searchFragment:
                radioButtonMonthIsChecked = radioGroup_filter.getCheckedRadioButtonId() == R.id.radioButton_mes;
                checkboxBegIsChecked = checkBox_begining.isChecked();
                checkboxFinalIsChecked = checkBox_final.isChecked();
                radioButtonPeriodIsChecked = radioGroup_filter.getCheckedRadioButtonId() == R.id.radioButton_periodo;
                radioButtonDatesIsChecked = radioGroup_filter.getCheckedRadioButtonId() == R.id.radioButton_porfecha;

                if (radioGroup_filter.getCheckedRadioButtonId() == R.id.radioButton_periodo) {
                    // Cast day with 1 digit to 2
                    switch (period) {
                        case "1":
                            begDay = finDay = "01";
                            break;
                        case "2":
                            begDay = finDay = "02";
                            break;
                        case "3":
                            begDay = finDay = "03";
                            break;
                        case "4":
                            begDay = finDay = "04";
                            break;
                        case "5":
                            begDay = finDay = "05";
                            break;
                        case "6":
                            begDay = finDay = "06";
                            break;
                        case "7":
                            begDay = finDay = "07";
                            break;
                        case "8":
                            begDay = finDay = "08";
                            break;
                        case "9":
                            begDay = finDay = "09";
                            break;
                        default:
                            begDay = finDay = period;
                            break;
                    }

                    // get actual month
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
                    String dateNow;
                    LocalDateTime now = LocalDateTime.now();
                    dateNow = dtf.format(now);
                    String year = dateNow.substring(0, 4);
                    String month = dateNow.substring(5, 7);
                    String day = dateNow.substring(8, 10);

                    // Check for january actual date, if true: then the previous date must be december
                    // and the previous year must be different
                    if (Integer.parseInt(day) < Integer.parseInt(period)) {
                        if (month.equals("01")) {
                            begMonth = "12";
                            begYear = Integer.toString(Integer.parseInt(year) - 1);
                        } else {
                            begMonth = Integer.toString(Integer.parseInt(month) - 1);
                            switch (begMonth) {
                                case "1":
                                    begMonth = "01";
                                    break;
                                case "2":
                                    begMonth = "02";
                                    break;
                                case "3":
                                    begMonth = "03";
                                    break;
                                case "4":
                                    begMonth = "04";
                                    break;
                                case "5":
                                    begMonth = "05";
                                    break;
                                case "6":
                                    begMonth = "06";
                                    break;
                                case "7":
                                    begMonth = "07";
                                    break;
                                case "8":
                                    begMonth = "08";
                                    break;
                                case "9":
                                    begMonth = "09";
                                    break;
                                default:
                                    break;
                            }
                            begYear = year;
                        }

                        // finMonth and year
                        finMonth = month;
                        finYear = year;
                    } else {
                        if (month.equals("12")) {
                            finMonth = "01";
                            finYear = Integer.toString(Integer.parseInt(year) + 1);
                        } else {
                            finMonth = Integer.toString(Integer.parseInt(month) + 1);
                            switch (finMonth) {
                                case "1":
                                    finMonth = "01";
                                    break;
                                case "2":
                                    finMonth = "02";
                                    break;
                                case "3":
                                    finMonth = "03";
                                    break;
                                case "4":
                                    finMonth = "04";
                                    break;
                                case "5":
                                    finMonth = "05";
                                    break;
                                case "6":
                                    finMonth = "06";
                                    break;
                                case "7":
                                    finMonth = "07";
                                    break;
                                case "8":
                                    finMonth = "08";
                                    break;
                                case "9":
                                    finMonth = "09";
                                    break;
                                default:
                                    break;
                            }
                            finYear = year;
                        }

                        // finMonth and year
                        begMonth = month;
                        begYear = year;
                    }

                } else if (radioGroup_filter.getCheckedRadioButtonId() == R.id.radioButton_porfecha) {
                    if (!checkBox_begining.isChecked()) {
                        if (textView_dateBeging.getText().toString().equals("")) {
                            Toast.makeText(view.getContext(), getString(R.string.toast_searchfragment_nodata), Toast.LENGTH_LONG).show();
                            break;
                        }

                        // Cast day with 1 digit to 2
                        switch (begDay) {
                            case "1":
                                begDay = "01";
                                break;
                            case "2":
                                begDay = "02";
                                break;
                            case "3":
                                begDay = "03";
                                break;
                            case "4":
                                begDay = "04";
                                break;
                            case "5":
                                begDay = "05";
                                break;
                            case "6":
                                begDay = "06";
                                break;
                            case "7":
                                begDay = "07";
                                break;
                            case "8":
                                begDay = "08";
                                break;
                            case "9":
                                begDay = "09";
                                break;
                            default:
                                break;
                        }

                        // Cast month with 1 digit to 2
                        switch (begMonth) {
                            case "1":
                                begMonth = "01";
                                break;
                            case "2":
                                begMonth = "02";
                                break;
                            case "3":
                                begMonth = "03";
                                break;
                            case "4":
                                begMonth = "04";
                                break;
                            case "5":
                                begMonth = "05";
                                break;
                            case "6":
                                begMonth = "06";
                                break;
                            case "7":
                                begMonth = "07";
                                break;
                            case "8":
                                begMonth = "08";
                                break;
                            case "9":
                                begMonth = "09";
                                break;
                            default:
                                break;
                        }
                    }

                    if (!checkBox_final.isChecked()) {
                        if (textView_dateFinal.getText().toString().equals("")) {
                            Toast.makeText(view.getContext(), getString(R.string.toast_searchfragment_nodata), Toast.LENGTH_LONG).show();
                            break;
                        }

                        // Cast day with 1 digit to 2
                        switch (finDay) {
                            case "1":
                                finDay = "01";
                                break;
                            case "2":
                                finDay = "02";
                                break;
                            case "3":
                                finDay = "03";
                                break;
                            case "4":
                                finDay = "04";
                                break;
                            case "5":
                                finDay = "05";
                                break;
                            case "6":
                                finDay = "06";
                                break;
                            case "7":
                                finDay = "07";
                                break;
                            case "8":
                                finDay = "08";
                                break;
                            case "9":
                                finDay = "09";
                                break;
                            default:
                                break;
                        }

                        // Cast month with 1 digit to 2
                        switch (finMonth) {
                            case "1":
                                finMonth = "01";
                                break;
                            case "2":
                                finMonth = "02";
                                break;
                            case "3":
                                finMonth = "03";
                                break;
                            case "4":
                                finMonth = "04";
                                break;
                            case "5":
                                finMonth = "05";
                                break;
                            case "6":
                                finMonth = "06";
                                break;
                            case "7":
                                finMonth = "07";
                                break;
                            case "8":
                                finMonth = "08";
                                break;
                            case "9":
                                finMonth = "09";
                                break;
                            default:
                                break;
                        }
                    }
                }

                new Task(view.getContext()).execute();

                break;
        }
    }

    private class Task extends AsyncTask<String, Void, Cursor> {

        private Context myContext;

        Task(Context context) {
            myContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            fab_find.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Cursor resultado) {
            progressBar.setVisibility(View.INVISIBLE);
            fab_find.setEnabled(true);

            if (resultado.getCount() == 0) {
                Toast.makeText(myContext, getString(R.string.toast_searchfragment_nodatafound), Toast.LENGTH_LONG).show();
            } else {
                Intent intentSearch = new Intent(myContext, DataSearch.class);

                ArrayList<String> descripciones = new ArrayList<>();
                ArrayList<String> fechas = new ArrayList<>();
                ArrayList<String> horas = new ArrayList<>();
                ArrayList<String> ingresos = new ArrayList<>();
                ArrayList<String> gastos = new ArrayList<>();
                ArrayList<String> ids = new ArrayList<>();
                ArrayList<String> categids = new ArrayList<>();

                while (resultado.moveToNext()) {
                    descripciones.add(resultado.getString(7));
                    categids.add(resultado.getString(2));
                    fechas.add(resultado.getString(3));
                    horas.add(resultado.getString(4));
                    ingresos.add(resultado.getString(5));
                    gastos.add(resultado.getString(6));
                    ids.add(resultado.getString(0));
                }

                intentSearch.putStringArrayListExtra("DESCRIPCIONES", descripciones);
                intentSearch.putStringArrayListExtra("FECHAS", fechas);
                intentSearch.putStringArrayListExtra("HORAS", horas);
                intentSearch.putStringArrayListExtra("INGRESOS", ingresos);
                intentSearch.putStringArrayListExtra("GASTOS", gastos);
                intentSearch.putStringArrayListExtra("IDS", ids);
                intentSearch.putStringArrayListExtra("CATEGIDS", categids);

                startActivity(intentSearch);
            }
        }

        @Override
        protected Cursor doInBackground(String... strings) {
            Cursor resultado;

            if (type.equals(getResources().getString(R.string.fragment_search_type_spinner_all))) {
                if (radioButtonMonthIsChecked) {
                    resultado = db.getEntryMonthData(MainActivity.idInstance, categoryIDSelected);
                } else if (radioButtonDatesIsChecked) {
                    if ((checkboxBegIsChecked) && (!checkboxFinalIsChecked)) {
                        String finalDate = finYear + "-" + finMonth + "-" + finDay;
                        resultado = db.getEntryDataFromBegToDate(MainActivity.idInstance, finalDate, categoryIDSelected);
                    } else if ((!checkboxBegIsChecked) && (checkboxFinalIsChecked)) {
                        String begDate = begYear + "-" + begMonth + "-" + begDay;
                        resultado = db.getEntryDataFromDateToToday(MainActivity.idInstance, begDate, categoryIDSelected);
                    } else if (checkboxBegIsChecked) {
                        resultado = db.getEntryAllData(MainActivity.idInstance, categoryIDSelected);
                    } else {
                        String begDate = begYear + "-" + begMonth + "-" + begDay;
                        String finalDate = finYear + "-" + finMonth + "-" + finDay;
                        resultado = db.getEntryDataInDate(MainActivity.idInstance, begDate, finalDate, categoryIDSelected);
                    }
                } else {
                    String begDate = begYear + "-" + begMonth + "-" + begDay;
                    String finalDate = finYear + "-" + finMonth + "-" + finDay;
                    resultado = db.getEntryDataInDate(MainActivity.idInstance, begDate, finalDate, categoryIDSelected);
                }
            } else if (type.equals(getResources().getString(R.string.fragment_search_type_spinner_profit))) {
                if (radioButtonMonthIsChecked) {
                    resultado = db.getEntryMonthProfit(MainActivity.idInstance, categoryIDSelected);
                } else if (radioButtonDatesIsChecked) {
                    if ((checkboxBegIsChecked) && (!checkboxFinalIsChecked)) {
                        String finalDate = finYear + "-" + finMonth + "-" + finDay;
                        resultado = db.getEntryProfitFromBegToDate(MainActivity.idInstance, finalDate, categoryIDSelected);
                    } else if ((!checkboxBegIsChecked) && (checkboxFinalIsChecked)) {
                        String begDate = begYear + "-" + begMonth + "-" + begDay;
                        resultado = db.getEntryProfitFromDateToToday(MainActivity.idInstance, begDate, categoryIDSelected);
                    } else if (checkboxBegIsChecked) {
                        resultado = db.getEntryAllProfit(MainActivity.idInstance, categoryIDSelected);
                    } else {
                        String begDate = begYear + "-" + begMonth + "-" + begDay;
                        String finalDate = finYear + "-" + finMonth + "-" + finDay;
                        resultado = db.getEntryProfitInDate(MainActivity.idInstance, begDate, finalDate, categoryIDSelected);
                    }
                } else {
                    String begDate = begYear + "-" + begMonth + "-" + begDay;
                    String finalDate = finYear + "-" + finMonth + "-" + finDay;
                    resultado = db.getEntryProfitInDate(MainActivity.idInstance, begDate, finalDate, categoryIDSelected);
                }
            } else {
                if (radioButtonMonthIsChecked) {
                    resultado = db.getEntryMonthSpend(MainActivity.idInstance, categoryIDSelected);
                } else if (radioButtonDatesIsChecked) {
                    if ((checkboxBegIsChecked) && (!checkboxFinalIsChecked)) {
                        String finalDate = finYear + "-" + finMonth + "-" + finDay;
                        resultado = db.getEntrySpendFromBegToDate(MainActivity.idInstance, finalDate, categoryIDSelected);
                    } else if ((!checkboxBegIsChecked) && (checkboxFinalIsChecked)) {
                        String begDate = begYear + "-" + begMonth + "-" + begDay;
                        resultado = db.getEntrySpendFromDateToToday(MainActivity.idInstance, begDate, categoryIDSelected);
                    } else if (checkboxBegIsChecked) {
                        resultado = db.getEntryAllSpend(MainActivity.idInstance, categoryIDSelected);
                    } else {
                        String begDate = begYear + "-" + begMonth + "-" + begDay;
                        String finalDate = finYear + "-" + finMonth + "-" + finDay;
                        resultado = db.getEntrySpendInDate(MainActivity.idInstance, begDate, finalDate, categoryIDSelected);
                    }
                } else {
                    String begDate = begYear + "-" + begMonth + "-" + begDay;
                    String finalDate = finYear + "-" + finMonth + "-" + finDay;
                    resultado = db.getEntrySpendInDate(MainActivity.idInstance, begDate, finalDate, categoryIDSelected);
                }
            }

            return resultado;
        }
    }
}
