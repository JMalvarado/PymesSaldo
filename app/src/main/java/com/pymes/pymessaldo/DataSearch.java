package com.pymes.pymessaldo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataSearch extends AppCompatActivity {

    private RecyclerView rvList;
    private RecyclerView.Adapter rvAdapter;

    private List<ListData> listItems;

    private ArrayList<String> descripciones;
    private ArrayList<String> fechas;
    private ArrayList<String> ingresos;
    private ArrayList<String> gastos;
    private ArrayList<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_search);

        rvList = findViewById(R.id.dataRecyclerView);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        descripciones = new ArrayList<>();
        fechas = new ArrayList<>();
        ingresos = new ArrayList<>();
        gastos = new ArrayList<>();
        ids = new ArrayList<>();

        descripciones = getIntent().getStringArrayListExtra("DESCRIPCIONES");
        fechas = getIntent().getStringArrayListExtra("FECHAS");
        ingresos = getIntent().getStringArrayListExtra("INGRESOS");
        gastos = getIntent().getStringArrayListExtra("GASTOS");
        ids = getIntent().getStringArrayListExtra("IDS");

        for (int i=0; i<descripciones.size(); i++) {
            ListData listData = new ListData(
                    descripciones.get(i),
                    fechas.get(i),
                    ingresos.get(i),
                    gastos.get(i),
                    ids.get(i)
            );

            listItems.add(listData);
        }

        rvAdapter = new MyAdapter(listItems, this);

        rvList.setAdapter(rvAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intentBuscar = new Intent (this, Busqueda.class);
        startActivity(intentBuscar);
    }
}
