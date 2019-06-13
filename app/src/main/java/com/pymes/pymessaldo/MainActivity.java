package com.pymes.pymessaldo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button TRYIT;
    private EditText Ingreso;
    private EditText Ingasto;
    private EditText Indescripcion;
    private Button btsaldo;
    private Button btBusqueda;
    DateTimeFormatter dtf;
    private Button VERTODO;

    public static DatabaseManager SaldoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TRYIT=findViewById(R.id.Ingreso);
        btsaldo=findViewById(R.id.btsaldo);
        btBusqueda=findViewById(R.id.busqueda);
        Ingreso=findViewById(R.id.etIngreso);
        Ingasto=findViewById(R.id.etGasto);
        Indescripcion=findViewById(R.id.etdescripcion);
        VERTODO=findViewById(R.id.tabla);
        TRYIT.setOnClickListener(this);
        btsaldo.setOnClickListener(this);
        VERTODO.setOnClickListener(this);
        btBusqueda.setOnClickListener(this);
        SaldoDB=new DatabaseManager(this);
        dtf=DateTimeFormatter.ofPattern("YYYY-MM-dd");

    }

    @Override
    public void onClick(View v) {
        String IngresoVar= Ingreso.getText().toString();
        String GastoVar= Ingasto.getText().toString();

        int IngresoVarint;
        int GastoVarint;

        if (IngresoVar.equals("")){
            IngresoVarint = 0;
        }

        else {
            IngresoVarint = Integer.parseInt(IngresoVar);
        }

        if (GastoVar.equals("")){
            GastoVarint = 0;
        }

        else  {
            GastoVarint = Integer.parseInt(GastoVar);
        }


        String DateNow;
        LocalDateTime now = LocalDateTime.now();
        DateNow = dtf.format(now);

        switch (v.getId()){
            case R.id.Ingreso:

                if ( (Ingreso.getText().toString().equals("")) && (Ingasto.getText().toString().equals("")) ) {
                    showMessage("Alerta", "Por favor complete todos los espacios");
                    break;
                }

                String descripcion = Indescripcion.getText().toString();

                boolean isResultadd = SaldoDB.addEntry(DateNow,GastoVarint,IngresoVarint,descripcion);

                if (isResultadd){
                    Toast.makeText(getApplicationContext(),getString(R.string.snackIsAdd),Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),getString(R.string.snackNotAdd),Toast.LENGTH_LONG).show();
                }
                Ingreso.setText("");
                Ingasto.setText("");
                Indescripcion.setText("");
                break;

            case R.id.btsaldo:
                ArrayList<Integer> ingresos = SaldoDB.getCurrentMonthIngresos();
                ArrayList<Integer> gastos = SaldoDB.getCurrentMonthGastos();

                int TotalIngresos=0;
                int TotalGastos=0;

                Iterator<Integer> IngresosIt = ingresos.iterator();
                while(IngresosIt.hasNext()){
                    int ing;
                    ing = IngresosIt.next();
                    TotalIngresos+=ing;
                }

                Iterator<Integer> GastosIt = gastos.iterator();
                while(GastosIt.hasNext()){
                    int gas;
                    gas = GastosIt.next();
                    TotalGastos+=gas;
                }

                int totalSaldo = TotalIngresos - TotalGastos;
                String strTotalSaldo = Integer.toString(totalSaldo);

                showMessage("Saldo a la fecha", "Su saldo para el mes en curso es:\n"+strTotalSaldo);

                break;

            case R.id.tabla:
                Cursor resultado = SaldoDB.getMonthData();
                if(resultado.getCount()==0){
                    showMessage("Alerta","No existen datos para mostrar");
                    break;
                }
                StringBuffer buffer = new StringBuffer();
                while (resultado.moveToNext()){
                    buffer.append("Fecha: "+resultado.getString(1)+"\n");
                    buffer.append("Ingreso: "+resultado.getString(2)+"\n");
                    buffer.append("Gasto: "+resultado.getString(3)+"\n");
                    buffer.append("Descripcion: "+resultado.getString(4)+"\n\n");
                }

                showMessage("Cuenta",buffer.toString());
                break;

            case R.id.busqueda:
                Intent intentBusqueda = new Intent (this,Busqueda.class);

                startActivity(intentBusqueda);

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

    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(R.id.Layout), "Presione el botón 'Salir'", Snackbar.LENGTH_LONG)
                .setAction("Salir", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentSalir = new Intent(Intent.ACTION_MAIN);
                        intentSalir.addCategory(Intent.CATEGORY_HOME);
                        intentSalir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentSalir);
                    }
                })
                .show();
    }
}

