package com.pymes.pymessaldo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Saldos extends AppCompatActivity {

    private TextView SaldoTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldos);
        SaldoTotal=findViewById(R.id.SaldoTotal);
        Intent intentSaldo=getIntent();
        int saldo=intentSaldo.getIntExtra("saldototal",0);

        System.out.println(saldo);

        String saldostr = Integer.toString(saldo);

        SaldoTotal.setText(saldostr);
    }
}
