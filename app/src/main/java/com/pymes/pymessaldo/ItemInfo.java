package com.pymes.pymessaldo;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ItemInfo extends AppCompatActivity {

    private TextView ingresoTV;
    private TextView gastoTV;
    private FloatingActionButton borrarFAB;
    private FloatingActionButton editarFAB;

    private String ingreso;
    private String gasto;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

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
                Snackbar.make(v, "DELETE", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        editarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "EDITAR", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }
}
