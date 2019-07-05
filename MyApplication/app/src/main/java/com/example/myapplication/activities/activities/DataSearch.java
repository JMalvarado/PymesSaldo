package com.example.myapplication.activities.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.ListData;
import com.example.myapplication.activities.data.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataSearch extends AppCompatActivity {

    // Cmponents view
    private RecyclerView rvList;
    private RecyclerView.Adapter rvAdapter;
    private TextView textView_instanceName;
    private TextView textView_periodTitle;

    // Global variables
    private List<ListData> listItems;

    private ArrayList<String> descripciones;
    private ArrayList<String> fechas;
    private ArrayList<String> horas;
    private ArrayList<String> ingresos;
    private ArrayList<String> gastos;
    private ArrayList<String> ids;
    private ArrayList<String> categIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_search);

        // Initialize view components
        textView_instanceName = findViewById(R.id.textView_dataSearch_instanceName);
        textView_periodTitle = findViewById(R.id.tvTitPeriodoLista);

        // Set instance name as title
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        rvList = findViewById(R.id.dataRecyclerView);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        descripciones = new ArrayList<>();
        fechas = new ArrayList<>();
        horas = new ArrayList<>();
        ingresos = new ArrayList<>();
        gastos = new ArrayList<>();
        ids = new ArrayList<>();

        descripciones = getIntent().getStringArrayListExtra("DESCRIPCIONES");
        fechas = getIntent().getStringArrayListExtra("FECHAS");
        horas = getIntent().getStringArrayListExtra("HORAS");
        ingresos = getIntent().getStringArrayListExtra("INGRESOS");
        gastos = getIntent().getStringArrayListExtra("GASTOS");
        ids = getIntent().getStringArrayListExtra("IDS");
        categIds = getIntent().getStringArrayListExtra("CATEGIDS");

        // Get range date for period
        // Initial date
        String dateTmp = fechas.get(0);
        // Set on format DD-MM-YYYY
        String year = dateTmp.substring(0, 4);
        String month = dateTmp.substring(5, 7);
        String day = dateTmp.substring(8, 10);
        String sepearator = "-";
        String dateInit = day+sepearator+month+sepearator+year;
        // Final date
        dateTmp = fechas.get(fechas.size()-1);
        // Set on format DD-MM-YYYY
        year = dateTmp.substring(0, 4);
        month = dateTmp.substring(5, 7);
        day = dateTmp.substring(8, 10);
        String dateFin = day+sepearator+month+sepearator+year;

        for (int i = 0; i < descripciones.size(); i++) {
            assert ids != null;
            assert categIds != null;
            ListData listData = new ListData(
                    descripciones.get(i),
                    fechas.get(i),
                    horas.get(i),
                    ingresos.get(i),
                    gastos.get(i),
                    ids.get(i),
                    categIds.get(i)
            );

            listItems.add(listData);
        }

        rvAdapter = new MyAdapter(listItems, this);

        rvList.setAdapter(rvAdapter);

        textView_periodTitle.setText(new StringBuilder().append(getString(R.string.fragment_search_period_title)).append(dateInit).append(" - ").append(dateFin).toString());
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
