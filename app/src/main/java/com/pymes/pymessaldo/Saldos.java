package com.pymes.pymessaldo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class Saldos extends AppCompatActivity {

    //private TextView SaldoTotal;
    private RecyclerView dataListRV;
    private RecyclerView.Adapter RVAdapter;
    private RecyclerView.LayoutManager RVLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldos);

        // Inicializar Recycler View
        dataListRV = findViewById(R.id.dataRecyclerView);
        RVLayoutManager = new LinearLayoutManager(this);
        dataListRV.setLayoutManager(RVLayoutManager);

        String[] dataSet;/// This need to be initialized
        dataSet = new String[]{"hi", "this", "is", "a", "test"};
        // Adapter
        RVAdapter = new MyAdapter(dataSet);
        dataListRV.setAdapter(RVAdapter);



        //SaldoTotal=findViewById(R.id.SaldoTotal);
        //Intent intentSaldo=getIntent();
        //int saldo=intentSaldo.getIntExtra("saldototal",0);

        //String saldostr = Integer.toString(saldo);

        //SaldoTotal.setText(saldostr);
    }
}
