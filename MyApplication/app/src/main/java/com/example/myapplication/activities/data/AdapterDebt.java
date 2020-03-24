package com.example.myapplication.activities.data;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.fragments.DebtsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AdapterDebt extends RecyclerView.Adapter<AdapterDebt.MyViewHolder> {

    private List<ListDataDebt> listData;
    private Context context;
    private DatabaseManager db;
    private FragmentActivity activity;

    private String strDay;
    private String strMonth;
    private String strYear;
    private String dateEdit;

    public AdapterDebt(List<ListDataDebt> listData, Context context, FragmentActivity activity) {
        this.listData = listData;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterDebt.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.debt_list, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDebt.MyViewHolder holder, int position) {
        final ListDataDebt data = listData.get(position);

        db = new DatabaseManager(context);

        holder.textView_description.setText(data.getDescripcion());
        // Format date (dd-mm-yyyy)
        // Show date in format DD-MM-YYYY
        String date = data.getDate();
        dateEdit = date;
        String dateToShow;
        strYear = date.substring(0, 4);
        strMonth = date.substring(5, 7);
        strDay = date.substring(8, 10);
        String sepearator = "-";
        dateToShow = strDay + sepearator + strMonth + sepearator + strYear;
        holder.textView_date.setText(dateToShow);

        // Set mount data in text view
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String amount;
        amount = df.format(Double.parseDouble(data.getAmount()));
        holder.textView_amount.setText(amount);

        holder.cardView.setOnClickListener(view -> {
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
                    case R.id.debtOption_edit:
                        openDialog(data.getId(), data.getDescripcion(), data.getAmount(), dateToShow);

                        return true;

                    case R.id.debtOption_delete:
                        // Delete button action
                        final CharSequence[] opciones;
                        opciones = new CharSequence[]{context.getResources().getString(R.string.alert_optSi), context.getResources().getString(R.string.alert_optNo)};
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setCancelable(false);
                        builder.setTitle(R.string.alert_title_deleteData);
                        builder.setItems(opciones, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    db.deleteDept(data.getId(), MainActivity.idInstance);
                                    activity.getSupportFragmentManager().beginTransaction().
                                            replace(R.id.content_main_layout, new DebtsFragment()).commit();
                                    break;

                                case 1:
                                    break;
                            }
                        });
                        builder.show();

                        return true;

                    case R.id.debtOption_add:
                        openDialogUpdate(data.getId(), data.getAmount(), data.getDescripcion(), true);

                        return true;

                    case R.id.debtOption_subtract:
                        openDialogUpdate(data.getId(), data.getAmount(), data.getDescripcion(), false);

                        return true;

                    default:
                        return false;
                }
            });
            popupMenu.inflate(R.menu.debt_options);
            popupMenu.show();
        });

        /*// Delete button action
        final CharSequence[] opciones;
        opciones = new CharSequence[]{context.getResources().getString(R.string.alert_optSi), context.getResources().getString(R.string.alert_optNo)};
        holder.fab_delete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setCancelable(false);
            builder.setTitle(R.string.alert_title_deleteData);
            builder.setItems(opciones, (dialog, which) -> {
                switch (which) {
                    case 0:
                        db.deleteDept(data.getId(), MainActivity.idInstance);
                        activity.getSupportFragmentManager().beginTransaction().
                                replace(R.id.content_main_layout, new DebtsFragment()).commit();
                        break;

                    case 1:
                        break;
                }
            });
            builder.show();
        });*/

        // Edit button action
        //holder.fab_edit.setOnClickListener(view -> openDialog(data.getId(), data.getDescripcion(), data.getAmount(), dateToShow));

        // Add to amount
        /*holder.fab_add.setOnClickListener(view -> {
            openDialogUpdate(data.getId(), data.getAmount(), data.getDescripcion(), true);
        });*/

        // Subtract to amount
        /*holder.fab_subtract.setOnClickListener(view -> {
            openDialogUpdate(data.getId(), data.getAmount(), data.getDescripcion(), false);
        });*/
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    /**
     * Alert dialog to add to amount
     */
    private void openDialogUpdate(String id, String amount, String description, boolean isAdd) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_update_debt, null);

        // Initialize components
        final EditText editText_value = subView.findViewById(R.id.editText_dialogUpdateDebt_amount);
        final ImageView imageView_type = subView.findViewById(R.id.imageView_dialogUpdateDebt_type);

        if (isAdd) {
            imageView_type.setImageResource(R.drawable.ic_profitgreen_48);
        } else {
            imageView_type.setImageResource(R.drawable.ic_spendred_48);
        }

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.alert_title_editDebt));
        if (isAdd) {
            builder.setMessage(context.getResources().getString(R.string.alert_mssg_addToDebt));
        } else {
            builder.setMessage(context.getResources().getString(R.string.alert_mssg_subtractToDebt));
        }

        builder.setView(subView);

        // Positive option
        builder.setPositiveButton(context.getResources().getString(R.string.alert_positiveBttn_addCategory),
                (dialogInterface, i) -> {
                    // Add category to data base
                    if ((!editText_value.getText().toString().equals(""))) {
                        double newAmount;
                        if (isAdd) {
                            newAmount = Double.parseDouble(amount) + Double.parseDouble(editText_value.getText().toString());
                        } else {
                            newAmount = Double.parseDouble(amount) - Double.parseDouble(editText_value.getText().toString());
                        }
                        db.editDept(MainActivity.idInstance, MainActivity.idInstance, id, description, newAmount, dateEdit);

                        // If new amount is less or equal to 0, delete entry
                        if (newAmount <= 0) {
                            final CharSequence[] opciones;
                            opciones = new CharSequence[]{context.getResources().getString(R.string.alert_optSi), context.getResources().getString(R.string.alert_optNo)};
                            AlertDialog.Builder builderDelete = new AlertDialog.Builder(subView.getContext());
                            builderDelete.setCancelable(false);
                            builderDelete.setTitle(R.string.alert_title_deleteData2);
                            builderDelete.setItems(opciones, (dialog, which) -> {
                                switch (which) {
                                    case 0:
                                        db.deleteDept(id, MainActivity.idInstance);
                                        activity.getSupportFragmentManager().beginTransaction().
                                                replace(R.id.content_main_layout, new DebtsFragment()).commit();
                                        break;

                                    case 1:
                                        break;
                                }
                            });
                            builderDelete.show();
                        }

                        activity.getSupportFragmentManager().beginTransaction().
                                replace(R.id.content_main_layout, new DebtsFragment()).commit();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
                    }

                });

        // Negative option
        builder.setNegativeButton(context.getResources().getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
        });

        builder.show();
    }

    /**
     * Alert dialog to edit debt
     */
    private void openDialog(final String id, final String description, final String amount, final String date) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_edit_debt, null);

        // Initialize components
        final EditText editText_description = subView.findViewById(R.id.editText_dialogEditDebt_description);
        final EditText editText_amount = subView.findViewById(R.id.editText_dialogEditDebt_amount);
        final TextView textView_date = subView.findViewById(R.id.textView_dialogEditDebt_date);
        final FloatingActionButton fab_date = subView.findViewById(R.id.fab_dialogEditDebt_date);

        editText_description.setText(description);
        editText_amount.setText(amount);
        textView_date.setText(date);

        fab_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
            int monthPick = calendar.get(Calendar.MONTH);
            int yearPick = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (datePicker, i, i1, i2) -> {
                strDay = Integer.toString(i2);
                strMonth = Integer.toString(i1 + 1);
                strYear = Integer.toString(i);

                // Cast day with 1 digit to 2
                switch (strDay) {
                    case "1":
                        strDay = "01";
                        break;
                    case "2":
                        strDay = "02";
                        break;
                    case "3":
                        strDay = "03";
                        break;
                    case "4":
                        strDay = "04";
                        break;
                    case "5":
                        strDay = "05";
                        break;
                    case "6":
                        strDay = "06";
                        break;
                    case "7":
                        strDay = "07";
                        break;
                    case "8":
                        strDay = "08";
                        break;
                    case "9":
                        strDay = "09";
                        break;
                    default:
                        break;
                }

                // Cast month with 1 digit to 2
                switch (strMonth) {
                    case "1":
                        strMonth = "01";
                        break;
                    case "2":
                        strMonth = "02";
                        break;
                    case "3":
                        strMonth = "03";
                        break;
                    case "4":
                        strMonth = "04";
                        break;
                    case "5":
                        strMonth = "05";
                        break;
                    case "6":
                        strMonth = "06";
                        break;
                    case "7":
                        strMonth = "07";
                        break;
                    case "8":
                        strMonth = "08";
                        break;
                    case "9":
                        strMonth = "09";
                        break;
                    default:
                        break;
                }
                // Show in format DD-MM-YY
                textView_date.setText(new StringBuilder().append(strDay).append("-").append(strMonth).append("-").append(strYear).toString());
                // Store in format YY-MM-DD
                dateEdit = new StringBuilder().append(strYear).append("-").append(strMonth).append("-").append(strDay).toString();
            }, yearPick, monthPick, dayPick);
            datePickerDialog.show();
        });

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.alert_title_editDebt));
        builder.setMessage(context.getResources().getString(R.string.alert_mssg_editDebt));
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        // Positive option
        builder.setPositiveButton(context.getResources().getString(R.string.alert_positiveBttn_addCategory),
                (dialogInterface, i) -> {
                    // Add category to data base
                    if ((!editText_description.getText().toString().equals("")) && (!editText_description.getText().toString().equals(""))) {
                        db.editDept(MainActivity.idInstance, MainActivity.idInstance, id, editText_description.getText().toString(), Double.parseDouble(editText_amount.getText().toString()), dateEdit);

                        activity.getSupportFragmentManager().beginTransaction().
                                replace(R.id.content_main_layout, new DebtsFragment()).commit();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
                    }

                });

        // Negative option
        builder.setNegativeButton(context.getResources().getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
        });

        builder.show();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textView_description;
        TextView textView_amount;
        TextView textView_date;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView_debtList);
            textView_description = itemView.findViewById(R.id.textView_debtList_description);
            textView_amount = itemView.findViewById(R.id.textView_debtList_amount);
            textView_date = itemView.findViewById(R.id.textView_debtList_date);
        }
    }
}
