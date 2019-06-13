package com.pymes.pymessaldo;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Busqueda extends AppCompatActivity {

    private CheckBox cbMes;
    private CheckBox cbInicio;
    private CheckBox cbFinal;
    private EditText anyo;
    private EditText anyoFinal;
    private Spinner diaInicioSpinner;
    private Spinner mesInicioSpinner;
    private Spinner diaFinalSpinner;
    private Spinner mesFinalSpinner;

    public static String diaInicSelec;
    public static String mesInicSelec;
    public static String anyoInicSelec;
    public static String diaFinalSelec;
    public static String mesFinalSelec;
    public static String anyoFinalSelec;
    public static boolean cbMesIsChecked;
    public static boolean cbInicioIsChecked;
    public static boolean cbFinalIsChecked;

    DateTimeFormatter dtf;
    String DateNow;

    public static DatabaseManager SaldoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        cbMes = findViewById(R.id.cbMes);
        cbInicio = findViewById(R.id.cbInicio);
        cbFinal = findViewById(R.id.cbFinal);
        anyo = findViewById(R.id.etAnyo);
        anyoFinal = findViewById(R.id.etAnyoFinal);


        diaInicioSpinner = findViewById(R.id.spinnerDia);
        mesInicioSpinner = findViewById(R.id.spinnerMes);
        diaFinalSpinner = findViewById(R.id.spinnerDiaFinal);
        mesFinalSpinner = findViewById(R.id.spinnerMesFinal);

        //Spinner items
        ArrayAdapter<CharSequence> adapterSpinnerDias = ArrayAdapter.createFromResource(this, R.array.dias, android.R.layout.simple_spinner_item);
        adapterSpinnerDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterSpinnerMeses = ArrayAdapter.createFromResource(this, R.array.meses, android.R.layout.simple_spinner_item);
        adapterSpinnerMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        diaInicioSpinner.setAdapter(adapterSpinnerDias);
        mesInicioSpinner.setAdapter(adapterSpinnerMeses);
        diaFinalSpinner.setAdapter(adapterSpinnerDias);
        mesFinalSpinner.setAdapter(adapterSpinnerMeses);

        SaldoDB=new DatabaseManager(this);

        dtf=DateTimeFormatter.ofPattern("YYYY-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        DateNow = dtf.format(now);

    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cbMes:
                if(!cbMes.isChecked()) {
                    cbInicio.setEnabled(true);
                    diaInicioSpinner.setEnabled(true);
                    mesInicioSpinner.setEnabled(true);
                    anyo.setEnabled(true);

                    cbFinal.setEnabled(true);
                    diaFinalSpinner.setEnabled(true);
                    mesFinalSpinner.setEnabled(true);
                    anyoFinal.setEnabled(true);

                }
                else {
                    cbInicio.setEnabled(false);
                    diaInicioSpinner.setEnabled(false);
                    mesInicioSpinner.setEnabled(false);
                    anyo.setEnabled(false);

                    cbFinal.setEnabled(false);
                    diaFinalSpinner.setEnabled(false);
                    mesFinalSpinner.setEnabled(false);
                    anyoFinal.setEnabled(false);
                }

                break;

            case R.id.cbInicio:

                if(!cbInicio.isChecked()) {
                    diaInicioSpinner.setEnabled(true);
                    mesInicioSpinner.setEnabled(true);
                    anyo.setEnabled(true);

                }
                else {
                    diaInicioSpinner.setEnabled(false);
                    mesInicioSpinner.setEnabled(false);
                    anyo.setEnabled(false);
                }

                break;

            case R.id.cbFinal:

                if(!cbFinal.isChecked()) {
                    diaFinalSpinner.setEnabled(true);
                    mesFinalSpinner.setEnabled(true);
                    anyoFinal.setEnabled(true);
                }
                else {
                    diaFinalSpinner.setEnabled(false);
                    mesFinalSpinner.setEnabled(false);
                    anyoFinal.setEnabled(false);
                }

                break;

            case R.id.buttonBuscar:

                if (!cbInicio.isChecked()) {

                    diaInicSelec = diaInicioSpinner.getSelectedItem().toString();

                    String mesInicialPalabra = mesInicioSpinner.getSelectedItem().toString();
                    switch (mesInicialPalabra) {
                        case "Ene":
                            mesInicSelec = "01";
                            break;
                        case "Feb":
                            mesInicSelec = "02";
                            break;
                        case "Mar":
                            mesInicSelec = "03";
                            break;
                        case "Abr":
                            mesInicSelec = "04";
                            break;
                        case "May":
                            mesInicSelec = "05";
                            break;
                        case "Jun":
                            mesInicSelec = "06";
                            break;
                        case "Jul":
                            mesInicSelec = "07";
                            break;
                        case "Ago":
                            mesInicSelec = "08";
                            break;
                        case "Set":
                            mesInicSelec = "09";
                            break;
                        case "Oct":
                            mesInicSelec = "10";
                            break;
                        case "Nov":
                            mesInicSelec = "11";
                            break;
                        default:
                            mesInicSelec = "12";
                            break;
                    }

                    if ( (anyo.getText().toString().equals("")) && (!cbMes.isChecked()) ) {
                        Toast.makeText(this, "Por favor complete todos los espacios", Toast.LENGTH_LONG).show();
                        break;
                    }

                    anyoInicSelec = anyo.getText().toString();

                }

                if (!cbFinal.isChecked()) {

                    diaFinalSelec = diaFinalSpinner.getSelectedItem().toString();
                    String mesInicialPalabra = mesFinalSpinner.getSelectedItem().toString();
                    switch (mesInicialPalabra) {
                        case "Ene":
                            mesFinalSelec = "01";
                            break;
                        case "Feb":
                            mesFinalSelec = "02";
                            break;
                        case "Mar":
                            mesFinalSelec = "03";
                            break;
                        case "Abr":
                            mesFinalSelec = "04";
                            break;
                        case "May":
                            mesFinalSelec = "05";
                            break;
                        case "Jun":
                            mesFinalSelec = "06";
                            break;
                        case "Jul":
                            mesFinalSelec = "07";
                            break;
                        case "Ago":
                            mesFinalSelec = "08";
                            break;
                        case "Set":
                            mesFinalSelec = "09";
                            break;
                        case "Oct":
                            mesFinalSelec = "10";
                            break;
                        case "Nov":
                            mesFinalSelec = "11";
                            break;
                        default:
                            mesFinalSelec = "12";
                            break;
                    }

                    if ( (anyoFinal.getText().toString().equals("")) && (!cbMes.isChecked()) ) {
                        Toast.makeText(this, "Por favor complete todos los espacios", Toast.LENGTH_LONG).show();
                        break;
                    }

                    anyoFinalSelec = anyoFinal.getText().toString();

                }

                Cursor resultado;

                if(cbMes.isChecked()) {
                    resultado = SaldoDB.getMonthData();
                }
                else if ( (cbInicio.isChecked()) && (!cbFinal.isChecked()) ) {
                    String finalDate = anyoFinalSelec+"-"+mesFinalSelec+"-"+diaFinalSelec;
                    resultado = SaldoDB.getDataFromBegToDate(finalDate);
                }
                else if ( (!cbInicio.isChecked()) && (cbFinal.isChecked()) ) {
                    String begDate = anyoInicSelec+"-"+mesInicSelec+"-"+diaInicSelec;
                    resultado = SaldoDB.getDataFromDateToToday(begDate);
                }
                else if ( (cbInicio.isChecked()) && (cbFinal.isChecked()) ) {
                    resultado = SaldoDB.getAllData();
                }
                else {
                    if ( (Integer.parseInt(anyoInicSelec)) > (Integer.parseInt(anyoFinalSelec)) ) {
                        Toast.makeText(this, "El año inicial debe ser menor al final", Toast.LENGTH_LONG).show();
                        break;
                    }

                    String begDate = anyoInicSelec+"-"+mesInicSelec+"-"+diaInicSelec;
                    String finalDate = anyoFinalSelec+"-"+mesFinalSelec+"-"+diaFinalSelec;
                    resultado = SaldoDB.getDataInDate(begDate, finalDate);
                }

                if(resultado.getCount()==0){
                    Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intentSearch = new Intent (this, DataSearch.class);

                    ArrayList<String> descripciones = new ArrayList<>();
                    ArrayList<String> fechas = new ArrayList<>();
                    ArrayList<String> ingresos = new ArrayList<>();
                    ArrayList<String> gastos = new ArrayList<>();
                    ArrayList<String> ids = new ArrayList<>();

                    while (resultado.moveToNext()) {
                        descripciones.add(resultado.getString(4));
                        fechas.add(resultado.getString(1));
                        ingresos.add(resultado.getString(2));
                        gastos.add(resultado.getString(3));
                        ids.add(resultado.getString(0));
                    }

                    intentSearch.putStringArrayListExtra("DESCRIPCIONES", descripciones);
                    intentSearch.putStringArrayListExtra("FECHAS", fechas);
                    intentSearch.putStringArrayListExtra("INGRESOS", ingresos);
                    intentSearch.putStringArrayListExtra("GASTOS", gastos);
                    intentSearch.putStringArrayListExtra("IDS", ids);

                    cbMesIsChecked = cbMes.isChecked();
                    cbInicioIsChecked = cbInicio.isChecked();
                    cbFinalIsChecked = cbFinal.isChecked();

                    startActivity(intentSearch);
                }

                break;
        }
    }
}
