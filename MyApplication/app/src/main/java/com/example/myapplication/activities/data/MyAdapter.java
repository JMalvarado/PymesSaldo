package com.example.myapplication.activities.data;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.DataSearch;
import com.example.myapplication.activities.activities.EditEntryActivity;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.fragments.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ListData> listData;
    private Context context;
    private DatabaseManager SaldoDB;

    public MyAdapter(List<ListData> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.data_list, viewGroup, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final ListData data = listData.get(i);

        SaldoDB = new DatabaseManager(context);

        // Show time in format HH:MM
        String realTime = data.getHora().substring(0,5);

        // Show date in format DD-MM-YY
        String dateToShow = data.getFecha();
        String year = dateToShow.substring(0,4);
        String month = dateToShow.substring(5,7);
        String day = dateToShow.substring(8,10);
        String sepearator = "-";
        dateToShow = day+sepearator+month+sepearator+year;

        myViewHolder.tvDescr.setText(data.getDescr());
        myViewHolder.tvFecha.setText(dateToShow);
        myViewHolder.tvHora.setText(realTime);
        myViewHolder.tvProfit.setText(data.getIngreso());
        myViewHolder.tvSpend.setText(data.getGasto());
        myViewHolder.tvCategory.setText(SaldoDB.getCategoryName(data.getCategId()));

        myViewHolder.imBttnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(view.getContext(), EditEntryActivity.class);
                editIntent.putExtra("ID", data.getId());
                editIntent.putExtra("INGRESO", data.getIngreso());
                editIntent.putExtra("GASTO", data.getGasto());
                editIntent.putExtra("FECHA", data.getFecha());
                editIntent.putExtra("HORA", data.getHora());
                editIntent.putExtra("DESCR", data.getDescr());
                editIntent.putExtra("CATEG", data.getCategId());

                context.startActivity(editIntent);
            }
        });

        final CharSequence[] opciones;
        opciones = new CharSequence[]{context.getResources().getString(R.string.alert_optSi), context.getResources().getString(R.string.alert_optNo)};

        myViewHolder.imBttnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(false);
                builder.setTitle(R.string.alert_title_deleteData);
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SaldoDB.deleteEntryData(MainActivity.idInstance, data.getId());
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

        /*myViewHolder.linearLayout_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInfo = new Intent(context, ItemInfo.class);

                intentInfo.putExtra("INGRESODATA", data.getIngreso());
                intentInfo.putExtra("GASTODATA", data.getGasto());
                intentInfo.putExtra("ID", data.getId());
                intentInfo.putExtra("DESCRIPCION", data.getDescr());
                intentInfo.putExtra("FECHA", data.getFecha());


                context.startActivity(intentInfo);
            }
        });*/
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
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            context.startActivity(mainActivityIntent);
        } else {
            Intent intentSearch = new Intent(context, DataSearch.class);

            ArrayList<String> descripciones = new ArrayList<>();
            ArrayList<String> fechas = new ArrayList<>();
            ArrayList<String> horas = new ArrayList<>();
            ArrayList<String> ingresos = new ArrayList<>();
            ArrayList<String> gastos = new ArrayList<>();
            ArrayList<String> ids = new ArrayList<>();
            ArrayList<String> categids = new ArrayList<>();

            while (resultado.moveToNext()) {
                descripciones.add(resultado.getString(7));
                categids.add(resultado.getString(2));
                fechas.add(resultado.getString(3));
                horas.add(resultado.getString(4));
                ingresos.add(resultado.getString(5));
                gastos.add(resultado.getString(6));
                ids.add(resultado.getString(0));
            }

            intentSearch.putStringArrayListExtra("DESCRIPCIONES", descripciones);
            intentSearch.putStringArrayListExtra("FECHAS", fechas);
            intentSearch.putStringArrayListExtra("HORAS", horas);
            intentSearch.putStringArrayListExtra("INGRESOS", ingresos);
            intentSearch.putStringArrayListExtra("GASTOS", gastos);
            intentSearch.putStringArrayListExtra("IDS", ids);
            intentSearch.putStringArrayListExtra("CATEGIDS", categids);

            context.startActivity(intentSearch);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageButton imBttnEdit;
        public ImageButton imBttnDelete;
        public TextView tvDescr;
        public TextView tvFecha;
        public TextView tvHora;
        public TextView tvProfit;
        public TextView tvSpend;
        public TextView tvCategory;
        public LinearLayout linearLayout_data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescr = itemView.findViewById(R.id.tvListDescr);
            tvFecha = itemView.findViewById(R.id.tvListFecha);
            tvHora = itemView.findViewById(R.id.tvListHora);
            tvProfit = itemView.findViewById(R.id.textView_dataList_ingr);
            tvSpend = itemView.findViewById(R.id.textView_dataList_gast);
            linearLayout_data = itemView.findViewById(R.id.linearLayout_data);
            tvCategory = itemView.findViewById(R.id.textView_dataList_category);
            imBttnEdit = itemView.findViewById(R.id.imageButton_dataList_edit);
            imBttnDelete = itemView.findViewById(R.id.imageButton_dataList_delete);
        }
    }
}
