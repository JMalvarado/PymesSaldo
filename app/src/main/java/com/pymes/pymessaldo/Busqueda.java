package com.pymes.pymessaldo;

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
    private String diaFinalSelec;
    private String mesFinalSelec;

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

                }

                if (!cbFinal.isChecked()) {

                    diaFinalSelec = diaFinalSpinner.getSelectedItem().toString();
                    mesFinalSelec = mesFinalSpinner.getSelectedItem().toString();

                }

                break;
        }
    }
}
