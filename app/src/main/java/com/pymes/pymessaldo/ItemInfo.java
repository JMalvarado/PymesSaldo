package com.pymes.pymessaldo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemInfo extends AppCompatActivity {

    private TextView ingresoTV;
    private TextView gastoTV;
    private FloatingActionButton borrarFAB;
    private FloatingActionButton editarFAB;

    private String ingreso;
    private String gasto;
    private String id;

    public static DatabaseManager SaldoDB;

    CharSequence opciones[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        SaldoDB = new DatabaseManager(this);

        opciones = new CharSequence[] {"Si", "No"};

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        ingresoTV = findViewById(R.id.tv_info_ingreso);
        gastoTV = findViewById(R.id.tv_info_gasto);
        borrarFAB = findViewById(R.id.fab_eliminar);
        editarFAB = findViewById(R.id.fab_editar);

        ingreso = getIntent().getStringExtra("INGRESODATA");
        gasto = getIntent().getStringExtra("GASTODATA");
        id = getIntent().getStringExtra("ID");

        ingresoTV.setText(ingreso);
        gastoTV.setText(gasto);

        borrarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(false);
                builder.setTitle("¿Desea eliminar el dato?");
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SaldoDB.deleteData(id);
                                search();
                                break;

                            case 1:
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        editarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "EDITAR", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void search () {

        Cursor resultado;

        if(Busqueda.cbMesIsChecked) {
            resultado = SaldoDB.getMonthData();
        }
        else if ( (Busqueda.cbInicioIsChecked) && (!Busqueda.cbFinalIsChecked) ) {
            String finalDate = Busqueda.anyoFinalSelec+"-"+Busqueda.mesFinalSelec+"-"+Busqueda.diaFinalSelec;
            resultado = SaldoDB.getDataFromBegToDate(finalDate);
        }
        else if ( (!Busqueda.cbInicioIsChecked) && (Busqueda.cbFinalIsChecked) ) {
            String begDate = Busqueda.anyoInicSelec+"-"+Busqueda.mesInicSelec+"-"+Busqueda.diaInicSelec;
            resultado = SaldoDB.getDataFromDateToToday(begDate);
        }
        else if ( (Busqueda.cbInicioIsChecked) && (Busqueda.cbFinalIsChecked) ) {
            resultado = SaldoDB.getAllData();
        }
        else {
            String begDate = Busqueda.anyoInicSelec+"-"+Busqueda.mesInicSelec+"-"+Busqueda.diaInicSelec;
            String finalDate = Busqueda.anyoFinalSelec+"-"+Busqueda.mesFinalSelec+"-"+Busqueda.diaFinalSelec;
            resultado = SaldoDB.getDataInDate(begDate, finalDate);
        }

        if(resultado.getCount()==0){
            Intent mainActivityIntent =  new Intent (this, MainActivity.class);
            startActivity(mainActivityIntent);
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

            startActivity(intentSearch);
        }
    }
}
