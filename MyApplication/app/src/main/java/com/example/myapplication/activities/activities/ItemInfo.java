package com.example.myapplication.activities.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.fragments.SearchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ItemInfo extends AppCompatActivity {

    // View components
    private TextView ingresoTV;
    private TextView gastoTV;
    private TextView descrTV;
    private FloatingActionButton borrarFAB;
    private FloatingActionButton editarFAB;

    private String ingreso;
    private String gasto;
    private String fecha;
    private String id;
    private String descr;

    public static DatabaseManager SaldoDB;

    CharSequence[] opciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        SaldoDB = new DatabaseManager(this);

        opciones = new CharSequence[]{getString(R.string.alert_optSi), getString(R.string.alert_optNo)};

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        ingresoTV = findViewById(R.id.tv_info_ingreso);
        gastoTV = findViewById(R.id.tv_info_gasto);
        borrarFAB = findViewById(R.id.fab_eliminar);
        editarFAB = findViewById(R.id.fab_editar);
        descrTV = findViewById(R.id.tv_info_descr);

        ingreso = getIntent().getStringExtra("INGRESODATA");
        gasto = getIntent().getStringExtra("GASTODATA");
        id = getIntent().getStringExtra("ID");
        descr = getIntent().getStringExtra("DESCRIPCION");
        fecha = getIntent().getStringExtra("FECHA");

        ingresoTV.setText(ingreso);
        gastoTV.setText(gasto);
        descrTV.setText(descr);

        // Floating button delete, click listener
        borrarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(false);
                builder.setTitle(getString(R.string.alert_title_deleteData));
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SaldoDB.deleteEntryData(MainActivity.idInstance, id);
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

        // Floating button edit, click listener
        editarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(v.getContext(), EditEntryActivity.class);
                editIntent.putExtra("ID", id);
                editIntent.putExtra("INGRESO", ingreso);
                editIntent.putExtra("GASTO", gasto);
                editIntent.putExtra("FECHA", fecha);
                editIntent.putExtra("DESCR", descr);

                startActivity(editIntent);
            }
        });
    }

    private void search() {

        Cursor resultado;

        if (SearchFragment.checkboxMonthIsChecked) {
            resultado = SaldoDB.getEntryMonthData(MainActivity.idInstance);
        } else if ((SearchFragment.checkboxBegIsChecked) && (!SearchFragment.checkboxFinalIsChecked)) {
            String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
            resultado = SaldoDB.getEntryDataFromBegToDate(MainActivity.idInstance, finalDate);
        } else if ((!SearchFragment.checkboxBegIsChecked) && (SearchFragment.checkboxFinalIsChecked)) {
            String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.finDay;
            resultado = SaldoDB.getEntryDataFromDateToToday(MainActivity.idInstance, begDate);
        } else if (SearchFragment.checkboxBegIsChecked) {
            resultado = SaldoDB.getEntryAllData(MainActivity.idInstance);
        } else {
            String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
            String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
            resultado = SaldoDB.getEntryDataInDate(MainActivity.idInstance, begDate, finalDate);
        }

        if (resultado.getCount() == 0) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        } else {
            Intent intentSearch = new Intent(this, DataSearch.class);

            ArrayList<String> descripciones = new ArrayList<>();
            ArrayList<String> fechas = new ArrayList<>();
            ArrayList<String> ingresos = new ArrayList<>();
            ArrayList<String> gastos = new ArrayList<>();
            ArrayList<String> ids = new ArrayList<>();

            while (resultado.moveToNext()) {
                descripciones.add(resultado.getString(5));
                fechas.add(resultado.getString(2));
                ingresos.add(resultado.getString(3));
                gastos.add(resultado.getString(4));
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
