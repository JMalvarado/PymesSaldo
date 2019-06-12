package com.pymes.pymessaldo;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Busqueda extends AppCompatActivity {

    private CheckBox cbInicio;
    private CheckBox cbFinal;
    private EditText anyo;
    private EditText anyoFinal;
    private Spinner diaInicioSpinner;
    private Spinner mesInicioSpinner;
    private Spinner diaFinalSpinner;
    private Spinner mesFinalSpinner;

    private String diaInicSelec;
    private String mesInicSelec;
    private String anyoInicSelec;
    private String diaFinalSelec;
    private String mesFinalSelec;
    private String anyoFinalSelec;

    DateTimeFormatter dtf;
    String DateNow;

    public static DatabaseManager SaldoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

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
                    mesInicSelec = mesInicioSpinner.getSelectedItem().toString();

                    if (anyo.getText().toString().equals("")) {
                        Toast.makeText(this, "Por favor complete todos los espacios", Toast.LENGTH_LONG).show();
                        break;
                    }

                    anyoInicSelec = anyo.getText().toString();

                }

                if (!cbFinal.isChecked()) {

                    diaFinalSelec = diaFinalSpinner.getSelectedItem().toString();
                    mesFinalSelec = mesFinalSpinner.getSelectedItem().toString();

                    if (anyoFinal.getText().toString().equals("")) {
                        Toast.makeText(this, "Por favor complete todos los espacios", Toast.LENGTH_LONG).show();
                        break;
                    }

                    anyoFinalSelec = anyoFinal.getText().toString();

                }

                Cursor resultado;

                if ( (cbInicio.isChecked()) && (!cbFinal.isChecked()) ) {
                    String finalDate = anyoFinalSelec+"-"+mesFinalSelec+"-"+diaFinalSelec;
                    resultado = SaldoDB.getDataFromBegToDate(finalDate);
                }
                else if ( (!cbInicio.isChecked()) && (cbFinal.isChecked()) ) {
                    String begDate = anyoInicSelec+"-"+mesInicSelec+"-"+diaInicSelec;
                    resultado = SaldoDB.getDataFromDateToToday(begDate);
                }
                else if ( (cbInicio.isChecked()) && (cbFinal.isChecked() ) ) {
                    resultado = SaldoDB.getAllData();
                }
                else {
                    if ( (Integer.parseInt(anyoInicSelec)) < (Integer.parseInt(anyoFinalSelec)) ) {
                        Toast.makeText(this, "El año inicial debe ser menor al final", Toast.LENGTH_LONG).show();
                        break;
                    }

                    String begDate = anyoInicSelec+"-"+mesInicSelec+"-"+diaInicSelec;
                    String finalDate = anyoFinalSelec+"-"+mesFinalSelec+"-"+diaFinalSelec;
                    resultado = SaldoDB.getDataInDate(begDate, finalDate);
                }

                if(resultado.getCount()==0){
                    showMessage("Alerta","No existen datos para mostrar");
                }
                else {
                    Intent intentSearch = new Intent (this, DataSearch.class);

                    ArrayList<String> descripciones = new ArrayList<>();
                    ArrayList<String> fechas = new ArrayList<>();
                    ArrayList<String> ingresos = new ArrayList<>();
                    ArrayList<String> gastos = new ArrayList<>();

                    while (resultado.moveToNext()) {
                        descripciones.add(resultado.getString(4));
                        fechas.add(resultado.getString(1));
                        ingresos.add(resultado.getString(2));
                        gastos.add(resultado.getString(3));
                    }

                    intentSearch.putStringArrayListExtra("DESCRIPCIONES", descripciones);
                    intentSearch.putStringArrayListExtra("FECHAS", fechas);
                    intentSearch.putStringArrayListExtra("INGRESOS", ingresos);
                    intentSearch.putStringArrayListExtra("GASTOS", gastos);

                    startActivity(intentSearch);
                }

                break;
        }
    }

    public void showMessage(String titulo, String mensaje){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }
}
