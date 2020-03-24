package com.example.myapplication.activities.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.DataSearch;
import com.example.myapplication.activities.activities.EditEntryActivity;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.fragments.SearchFragment;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.maltaisn.icondialog.pack.IconPack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Adapter for card view in search activity
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ListData> listData;
    private Context context;
    private FragmentActivity activity;
    private DatabaseManager db;

    public MyAdapter(List<ListData> listData, Context context, FragmentActivity activity) {
        this.listData = listData;
        this.context = context;
        this.activity = activity;
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

        // Set mount data in text view
        double mountIngInt = Double.parseDouble(data.getIngreso());
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String mount;
        if (mountIngInt == 0) {
            mount = df.format(Double.parseDouble(data.getGasto()));
            myViewHolder.tvProfit.setText(mount);
            myViewHolder.imageView_item.setImageResource(R.drawable.ic_spendred_48);
        } else {
            mount = df.format(Double.parseDouble(data.getIngreso()));
            myViewHolder.tvProfit.setText(mount);
            myViewHolder.imageView_item.setImageResource(R.drawable.ic_profitgreen_48);
        }

        myViewHolder.tvCategory.setText(db.getCategoryName(data.getCategId(), MainActivity.idInstance));


        IconPack iconPack = ((IconPackApp) activity.getApplication()).getIconPack();
        // Get and set category icon
        String categoryIcId = db.getCategoryIconName(db.getCategoryName(data.getCategId(), MainActivity.idInstance), MainActivity.idInstance);
        int imageID = Integer.parseInt(categoryIcId);
        Drawable icDrawable = Objects.requireNonNull(iconPack).getIconDrawable(imageID, new IconDrawableLoader(Objects.requireNonNull(context)));
        myViewHolder.imageView_ic.setImageDrawable(icDrawable);

        myViewHolder.cardView.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, view);
            try {
                Field[] fields = popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popupMenu);
                        Class<?> classPopupHelper = Class.forName(Objects.requireNonNull(menuPopupHelper).getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.entryOption_edit:
                        Intent editIntent = new Intent(context, EditEntryActivity.class);
                        editIntent.putExtra("ID", data.getId());
                        editIntent.putExtra("INGRESO", data.getIngreso());
                        editIntent.putExtra("GASTO", data.getGasto());
                        editIntent.putExtra("FECHA", data.getFecha());
                        editIntent.putExtra("HORA", data.getHora());
                        editIntent.putExtra("DESCR", data.getDescr());
                        editIntent.putExtra("CATEG", data.getCategId());

                        context.startActivity(editIntent);

                        return true;

                    case R.id.entryOption_delete:
                        final CharSequence[] opciones;
                        opciones = new CharSequence[]{context.getResources().getString(R.string.alert_optSi), context.getResources().getString(R.string.alert_optNo)};

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setTitle(R.string.alert_title_deleteData);
                        builder.setItems(opciones, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    db.deleteEntryData(MainActivity.idInstance, data.getId());

                                    // Get save entry to delete
                                    double amount;
                                    if (data.getIngreso().equals("0.0")) {
                                        amount = Double.parseDouble(data.getGasto());
                                    } else {
                                        amount = Double.parseDouble(data.getIngreso());
                                    }
                                    Cursor saveEntry = db.getSaveEntryByDateTimeAmount(MainActivity.idInstance, data.getFecha(), data.getHora(), amount);
                                    if (saveEntry.getCount() != 0) {
                                        // Edit save entry
                                        saveEntry.moveToNext();
                                        String id = saveEntry.getString(0);
                                        db.deleteSave(id, MainActivity.idInstance);
                                    }

                                    new Task().execute();
                                    break;

                                case 1:
                                    break;
                            }
                        });
                        builder.show();

                        return true;

                    default:
                        return false;
                }
            });
            popupMenu.inflate(R.menu.entry_options);
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //        public FloatingActionButton fab_dit;
//        public FloatingActionButton fab_delete;
        public ImageView imageView_item;
        public ImageView imageView_ic;
        public TextView tvDescr;
        public TextView tvFecha;
        public TextView tvHora;
        public TextView tvProfit;
        public TextView tvCategory;
        public LinearLayout linearLayout_data;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescr = itemView.findViewById(R.id.tvListDescr);
            tvFecha = itemView.findViewById(R.id.tvListFecha);
            tvHora = itemView.findViewById(R.id.tvListHora);
            tvProfit = itemView.findViewById(R.id.textView_dataList_ingr);
            linearLayout_data = itemView.findViewById(R.id.linearLayout_data);
            tvCategory = itemView.findViewById(R.id.textView_dataList_category);
            imageView_item = itemView.findViewById(R.id.imageView_dataList_item);
            imageView_ic = itemView.findViewById(R.id.imageView_dataList_ic);
            cardView = itemView.findViewById(R.id.cardView_dataList);
        }
    }

    @SuppressLint("StaticFieldLeak")
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

            if (SearchFragment.type.equals(context.getResources().getString(R.string.fragment_search_type_spinner_all))) {
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
            } else if (SearchFragment.type.equals(context.getResources().getString(R.string.fragment_search_type_spinner_profit))) {
                if (SearchFragment.radioButtonMonthIsChecked) {
                    resultado = db.getEntryMonthProfit(MainActivity.idInstance, SearchFragment.categoryIDSelected);
                } else if (SearchFragment.radioButtonDatesIsChecked) {
                    if ((SearchFragment.checkboxBegIsChecked) && (!SearchFragment.checkboxFinalIsChecked)) {
                        String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                        resultado = db.getEntryProfitFromBegToDate(MainActivity.idInstance, finalDate, SearchFragment.categoryIDSelected);
                    } else if ((!SearchFragment.checkboxBegIsChecked) && (SearchFragment.checkboxFinalIsChecked)) {
                        String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                        resultado = db.getEntryProfitFromDateToToday(MainActivity.idInstance, begDate, SearchFragment.categoryIDSelected);
                    } else if (SearchFragment.checkboxBegIsChecked) {
                        resultado = db.getEntryAllProfit(MainActivity.idInstance, SearchFragment.categoryIDSelected);
                    } else {
                        String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                        String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                        resultado = db.getEntryProfitInDate(MainActivity.idInstance, begDate, finalDate, SearchFragment.categoryIDSelected);
                    }
                } else {
                    String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                    String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                    resultado = db.getEntryProfitInDate(MainActivity.idInstance, begDate, finalDate, SearchFragment.categoryIDSelected);
                }
            } else {
                if (SearchFragment.radioButtonMonthIsChecked) {
                    resultado = db.getEntryMonthSpend(MainActivity.idInstance, SearchFragment.categoryIDSelected);
                } else if (SearchFragment.radioButtonDatesIsChecked) {
                    if ((SearchFragment.checkboxBegIsChecked) && (!SearchFragment.checkboxFinalIsChecked)) {
                        String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                        resultado = db.getEntrySpendFromBegToDate(MainActivity.idInstance, finalDate, SearchFragment.categoryIDSelected);
                    } else if ((!SearchFragment.checkboxBegIsChecked) && (SearchFragment.checkboxFinalIsChecked)) {
                        String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                        resultado = db.getEntrySpendFromDateToToday(MainActivity.idInstance, begDate, SearchFragment.categoryIDSelected);
                    } else if (SearchFragment.checkboxBegIsChecked) {
                        resultado = db.getEntryAllSpend(MainActivity.idInstance, SearchFragment.categoryIDSelected);
                    } else {
                        String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                        String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                        resultado = db.getEntrySpendInDate(MainActivity.idInstance, begDate, finalDate, SearchFragment.categoryIDSelected);
                    }
                } else {
                    String begDate = SearchFragment.begYear + "-" + SearchFragment.begMonth + "-" + SearchFragment.begDay;
                    String finalDate = SearchFragment.finYear + "-" + SearchFragment.finMonth + "-" + SearchFragment.finDay;
                    resultado = db.getEntrySpendInDate(MainActivity.idInstance, begDate, finalDate, SearchFragment.categoryIDSelected);
                }
            }

            return resultado;
        }
    }
}
