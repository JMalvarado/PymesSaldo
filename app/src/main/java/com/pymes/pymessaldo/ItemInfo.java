package com.pymes.pymessaldo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ItemInfo extends AppCompatActivity {

    private TextView ingresoTV;
    private TextView gastoTV;

    private String ingreso;
    private String gasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        ingresoTV = findViewById(R.id.tv_info_ingreso);
        gastoTV = findViewById(R.id.tv_info_gasto);

        ingreso = getIntent().getStringExtra("INGRESODATA");
        gasto = getIntent().getStringExtra("GASTODATA");

        ingresoTV.setText(ingreso);
        gastoTV.setText(gasto);
    }
}
