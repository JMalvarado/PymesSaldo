package com.example.myapplication.activities.data;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ListData> listData;
    private Context context;
    private DatabaseManager db;

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
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final ListData data = listData.get(i);

        db = new DatabaseManager(context);

        // Show time in format HH:MM
        String realTime = data.getHora().substring(0, 5);

        // Show date in format DD-MM-YY
        String dateToShow = data.getFecha();
        String year = dateToShow.substring(0, 4);
        String month = dateToShow.substring(5, 7);
        String day = dateToShow.substring(8, 10);
        String sepearator = "-";
        dateToShow = day + sepearator + month + sepearator + year;

        myViewHolder.tvDescr.setText(data.getDescr());
        myViewHolder.tvFecha.setText(dateToShow);
        myViewHolder.tvHora.setText(realTime);

        float mountIngInt = Float.parseFloat(data.getIngreso());

        if (mountIngInt == 0) {
            myViewHolder.tvProfit.setText(data.getGasto());
            myViewHolder.imageView_item.setImageResource(R.drawable.ic_out_96);
        } else {
            myViewHolder.tvProfit.setText(data.getIngreso());
            myViewHolder.imageView_item.setImageResource(R.drawable.ic_in_96);
        }

        myViewHolder.tvCategory.setText(db.getCategoryName(data.getCategId()));

        // Get and set category icon
        String categoryIcName = db.getCategoryIconName(db.getCategoryName(data.getCategId()));
        int imageID = context.getResources().getIdentifier(categoryIcName, "drawable", context.getPackageName());
        myViewHolder.imageView_ic.setImageResource(imageID);

        myViewHolder.fab_dit.setOnClickListener(new View.OnClickListener() {
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

        myViewHolder.fab_delete.setOnClickListener(new View.OnClickListener() {
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
                                db.deleteEntryData(MainActivity.idInstance, data.getId());
                                //search();
                                new Task().execute();
                                break;

                            case 1:
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public FloatingActionButton fab_dit;
        public FloatingActionButton fab_delete;
        public ImageView imageView_item;
        public ImageView imageView_ic;
        public TextView tvDescr;
        public TextView tvFecha;
        public TextView tvHora;
        public TextView tvProfit;
        public TextView tvCategory;
        public LinearLayout linearLayout_data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescr = itemView.findViewById(R.id.tvListDescr);
            tvFecha = itemView.findViewById(R.id.tvListFecha);
            tvHora = itemView.findViewById(R.id.tvListHora);
            tvProfit = itemView.findViewById(R.id.textView_dataList_ingr);
            linearLayout_data = itemView.findViewById(R.id.linearLayout_data);
            tvCategory = itemView.findViewById(R.id.textView_dataList_category);
            fab_dit = itemView.findViewById(R.id.fab_dataList_edit);
            fab_delete = itemView.findViewById(R.id.fab_dataList_delete);
            imageView_item = itemView.findViewById(R.id.imageView_dataList_item);
            imageView_ic = itemView.findViewById(R.id.imageView_dataList_ic);
        }
    }

    private class Task extends AsyncTask<String, Void, Cursor> {

        Task() {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Cursor resultado) {
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
        protected Cursor doInBackground(String... strings) {
            Cursor resultado;

            if (SearchFragment.radioButtonMonthIsChecked) {
                resultado = db.getEntryMonthData(MainActivity.idInstance, SearchFragment.categoryIDSelected);
            } else if (SearchFragment.radioButtonDatesIsChecked) {
                if ((SearchFragment.checkboxBegIsChecked) && (!SearchFragment.checkboxFinalIsChecked)) {
                    String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                    resultado = db.getEntryDataFromBegToDate(MainActivity.idInstance, finalDate, SearchFragment.categoryIDSelected);
                } else if ((!SearchFragment.checkboxBegIsChecked) && (SearchFragment.checkboxFinalIsChecked)) {
                    String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                    resultado = db.getEntryDataFromDateToToday(MainActivity.idInstance, begDate, SearchFragment.categoryIDSelected);
                } else if (SearchFragment.checkboxBegIsChecked) {
                    resultado = db.getEntryAllData(MainActivity.idInstance, SearchFragment.categoryIDSelected);
                } else {
                    String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                    String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                    resultado = db.getEntryDataInDate(MainActivity.idInstance, begDate, finalDate, SearchFragment.categoryIDSelected);
                }
            } else {
                String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                resultado = db.getEntryDataInDate(MainActivity.idInstance, begDate, finalDate, SearchFragment.categoryIDSelected);
            }

            return resultado;
        }
    }
}
