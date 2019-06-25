package com.example.myapplication.activities.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.*;
import com.example.myapplication.activities.activities.DataSearch;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SearchFragment extends Fragment implements View.OnClickListener {

    // View components
    private CheckBox checkBox_monthData, checkBox_begining, checkBox_final;
    private Button button_search, button_dateBegin, button_dateFinal;
    private TextView textView_dateBeging, textView_dateFinal;
    private TextView textView_instanceName;

    // Global variables
    public static String begDay, begMonth, begYear, finDay, finMonth, finYear;
    public static int intBegDay, intBegMonth, intBegYear, intFinDay, intFinMonth, intFinYear;
    public static boolean checkboxMonthIsChecked, checkboxBegIsChecked, checkboxFinalIsChecked;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    // Database manager instance
    private DatabaseManager SaldoDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.search_name));

        // Initialize view componnents and click listeners
        checkBox_monthData = view.findViewById(R.id.cbMes);
        checkBox_monthData.setOnClickListener(this);

        checkBox_begining = view.findViewById(R.id.cbInicio);
        checkBox_begining.setOnClickListener(this);

        checkBox_final = view.findViewById(R.id.cbFinal);
        checkBox_final.setOnClickListener(this);

        button_search = view.findViewById(R.id.buttonBuscar);
        button_search.setOnClickListener(this);

        button_dateBegin = view.findViewById(R.id.button_search_selectdatebeg);
        button_dateBegin.setOnClickListener(this);

        button_dateFinal = view.findViewById(R.id.button_search_selectdatefinal);
        button_dateFinal.setOnClickListener(this);

        textView_dateBeging = view.findViewById(R.id.textview_search_datebeg);
        textView_dateFinal = view.findViewById(R.id.textview_search_datefinal);
        textView_instanceName = view.findViewById(R.id.textView_fragmentSearch_instanceName);

        // Set instance name as title
        SharedPreferences prefs =  getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        SaldoDB = new DatabaseManager(view.getContext());

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cbMes:
                if (!checkBox_monthData.isChecked()) {
                    checkBox_begining.setEnabled(true);
                    button_dateBegin.setEnabled(true);

                    checkBox_final.setEnabled(true);
                    button_dateFinal.setEnabled(true);

                } else {
                    checkBox_begining.setEnabled(false);
                    button_dateBegin.setEnabled(false);

                    checkBox_final.setEnabled(false);
                    button_dateFinal.setEnabled(false);
                }

                break;

            case R.id.cbInicio:
                if (!checkBox_begining.isChecked()) {
                    button_dateBegin.setEnabled(true);

                } else {
                    button_dateBegin.setEnabled(false);
                }

                break;

            case R.id.cbFinal:
                if (!checkBox_final.isChecked()) {
                    button_dateFinal.setEnabled(true);

                } else {
                    button_dateFinal.setEnabled(false);
                }

                break;

            case R.id.button_search_selectdatebeg:
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

            case R.id.button_search_selectdatefinal:
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

            case R.id.buttonBuscar:
                if ((!checkBox_begining.isChecked()) && (!checkBox_monthData.isChecked())) {
                    if ((textView_dateBeging.getText().toString().equals("")) && (!checkBox_monthData.isChecked())) {
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

                if ((!checkBox_final.isChecked()) && (!checkBox_monthData.isChecked())) {
                    if ((textView_dateFinal.getText().toString().equals("")) && (!checkBox_monthData.isChecked())) {
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

                Cursor resultado;

                if (checkBox_monthData.isChecked()) {
                    resultado = SaldoDB.getEntryMonthData(MainActivity.idInstance);
                } else if ((checkBox_begining.isChecked()) && (!checkBox_final.isChecked())) {
                    String finalDate = finYear + "-" + finMonth + "-" + finDay;
                    resultado = SaldoDB.getEntryDataFromBegToDate(MainActivity.idInstance, finalDate);
                } else if ((!checkBox_begining.isChecked()) && (checkBox_final.isChecked())) {
                    String begDate = begYear + "-" + begMonth + "-" + begDay;
                    resultado = SaldoDB.getEntryDataFromDateToToday(MainActivity.idInstance, begDate);
                } else if ((checkBox_begining.isChecked()) && (checkBox_final.isChecked())) {
                    resultado = SaldoDB.getEntryAllData(MainActivity.idInstance);
                } else {
                    String begDate = begYear + "-" + begMonth + "-" + begDay;
                    String finalDate = finYear + "-" + finMonth + "-" + finDay;
                    resultado = SaldoDB.getEntryDataInDate(MainActivity.idInstance, begDate, finalDate);
                }

                if (resultado.getCount() == 0) {
                    Toast.makeText(view.getContext(), getString(R.string.toast_searchfragment_nodatafound), Toast.LENGTH_LONG).show();
                } else {
                    Intent intentSearch = new Intent(view.getContext(), DataSearch.class);

                    ArrayList<String> descripciones = new ArrayList<>();
                    ArrayList<String> fechas = new ArrayList<>();
                    ArrayList<String> ingresos = new ArrayList<>();
                    ArrayList<String> gastos = new ArrayList<>();
                    ArrayList<String> ids = new ArrayList<>();

                    while (resultado.moveToNext()) {
                        descripciones.add(resultado.getString(6));
                        fechas.add(resultado.getString(3));
                        ingresos.add(resultado.getString(4));
                        gastos.add(resultado.getString(5));
                        ids.add(resultado.getString(0));
                    }

                    intentSearch.putStringArrayListExtra("DESCRIPCIONES", descripciones);
                    intentSearch.putStringArrayListExtra("FECHAS", fechas);
                    intentSearch.putStringArrayListExtra("INGRESOS", ingresos);
                    intentSearch.putStringArrayListExtra("GASTOS", gastos);
                    intentSearch.putStringArrayListExtra("IDS", ids);

                    checkboxMonthIsChecked = checkBox_monthData.isChecked();
                    checkboxBegIsChecked = checkBox_begining.isChecked();
                    checkboxFinalIsChecked = checkBox_final.isChecked();

                    startActivity(intentSearch);
                }

                break;
        }
    }

    /*@Override
    public void onBackPressed() {
        //Intent intentMain = new Intent (this, MainActivity.class);
        //startActivity(intentMain);
    }*/
}
