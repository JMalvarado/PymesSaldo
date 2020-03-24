package com.example.myapplication.activities.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.CustomAdapter;
import com.example.myapplication.activities.data.CustomItems;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.IconPackApp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconPack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddMovFragment extends Fragment implements View.OnClickListener, IconDialog.Callback {

    // View components
    private EditText editText_profit;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_time;
    private RadioGroup radioGroup_addMov;
    private RadioButton radioButton_in;
    private RadioButton radioButton_spend;
    private Button button_in;
    private Button button_cancel;
    private FloatingActionButton fab_addDate;
    private FloatingActionButton fab_addTime;
    private Spinner spinner_categories;
    private ImageButton imageButton_addIcon;

    // Global variables
    private DateTimeFormatter dtf;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private String category_id;
    private String icId;

    // Database instance
    private DatabaseManager db;

    // Icon dialog tag
    private static final String ICON_DIALOG_TAG = "icon-dialog";

    // Add icon id
    private static final int addIconId = 935;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_mov, container, false);

        // Initialize components
        button_in = view.findViewById(R.id.button_fragmentAddMov_addData);
        button_in.setOnClickListener(this);
        button_cancel = view.findViewById(R.id.button_fragmentAddMov_cancel);
        button_cancel.setOnClickListener(this);
        editText_profit = view.findViewById(R.id.etIngreso);
        editText_description = view.findViewById(R.id.etdescripcion);
        textView_date = view.findViewById(R.id.textView_addEntry_date);
        textView_time = view.findViewById(R.id.textView_addEntry_time);
        fab_addDate = view.findViewById(R.id.fab_calendar_addMovFragment);
        fab_addDate.setOnClickListener(this);
        fab_addTime = view.findViewById(R.id.fab_clock_addMovFragment);
        fab_addTime.setOnClickListener(this);
        radioGroup_addMov = view.findViewById(R.id.radioGroup_addMov);
        radioButton_in = view.findViewById(R.id.radioButton_fragmentAddMov_Ingreso);
        radioButton_in.setOnClickListener(this);
        radioButton_spend = view.findViewById(R.id.radioButton_fragmentAddMov_Gasto);
        radioButton_spend.setOnClickListener(this);

        // Database instance
        db = new DatabaseManager(view.getContext());

        // Set date format
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Set date now in the textView
        String dateNow;
        LocalDateTime now = LocalDateTime.now();
        dateNow = dtf.format(now);
        // Store in format YY-MM-DD
        date = dateNow;
        // Show date in format DD-MM-YY
        String dateToShow;
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String sepearator = "-";
        dateToShow = day + sepearator + month + sepearator + year;
        textView_date.setText(dateToShow);

        // Store time in format HH:MM:SS.SSSS
        time = java.time.LocalTime.now().toString();
        String timeToShow = time.substring(0, 5);
        // Show time in format HH:MM
        textView_time.setText(timeToShow);

        // Set Spinner category data
        spinner_categories = view.findViewById(R.id.spinner_addEntry_category);

        // Get categories
        Cursor categoriesData = db.getCategoriesByInstance(MainActivity.idInstance);

        // Array List to store the categories
        ArrayList<CustomItems> categoriesList = new ArrayList<>();

        // Add categories names to categoriesList
        while (categoriesData.moveToNext()) {
            String categName = categoriesData.getString(1);
            String categIcon = categoriesData.getString(2);
            int categIconId = Integer.parseInt(categIcon);
            categoriesList.add(new CustomItems(categName, categIconId));
        }
        categoriesData.moveToPosition(-1);

        // Add option: "Agregar..." category
        categoriesList.add(new CustomItems(getString(R.string.activity_addEntry_addCategory_spinner), addIconId));

        // Create adapter for the spinner of categories
        CustomAdapter customAdapter = new CustomAdapter(view.getContext(), categoriesList, getActivity());
        spinner_categories.setAdapter(customAdapter);

        // Set default position
        spinner_categories.setSelection(0);

        // Set spinner categories onClickListener
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomItems items = (CustomItems) adapterView.getSelectedItem();
                String category_name = items.getSpinnerText();

                // Get categories data from db
                Cursor result = db.getCategoriesByInstance(MainActivity.idInstance);
                if (category_name.equals(getString(R.string.activity_addEntry_addCategory_spinner))) {
                    // Check categories count limit
                    if (result.getCount() < 25) {
                        // if category_name = Add...
                        openDialog();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_limit), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // else, get category id with the given name
                    category_id = db.getCategoryId(category_name, MainActivity.idInstance);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Set ingreso-gasto radio buttons default checked
        radioButton_spend.setChecked(true);

        return view;
    }

    /**
     * Alert dialog to add category
     */
    private void openDialog() {
        // Set icon name empty
        icId = "";

        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_add_category, null);

        // Initialize edit Text for category name
        final EditText editText_categoryName = subView.findViewById(R.id.dialogLayoutAddCategory_editText_categoryName);
        imageButton_addIcon = subView.findViewById(R.id.imageButton_addCategory_addIcon);

        IconDialog dialog = (IconDialog) Objects.requireNonNull(getFragmentManager()).findFragmentByTag(ICON_DIALOG_TAG);
        IconDialog iconDialog = dialog != null ? dialog : IconDialog.newInstance(new IconDialogSettings.Builder().build());

        // Icon selector button click listener
        imageButton_addIcon.setOnClickListener(view -> iconDialog.show(getChildFragmentManager(), ICON_DIALOG_TAG));

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(getString(R.string.alert_title_addCategory));
        builder.setMessage(getString(R.string.alert_mssg_addCategory));
        builder.setView(subView);
        builder.setCancelable(false);

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), (dialogInterface, i) -> {
            // Check if there is icon selected
            if (icId.equals("")) {
                icId = "957";
            }

            // Add category to data base
            if (!editText_categoryName.getText().toString().equals("")) {
                // check if the category exist
                Cursor cursorNames = db.getCategoriesByInstance(MainActivity.idInstance);
                boolean isExistCategory = false;
                while (cursorNames.moveToNext()) {
                    if (cursorNames.getString(1).equals(editText_categoryName.getText().toString())) {
                        isExistCategory = true;
                        break;
                    }
                }
                if (isExistCategory) {
                    Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_existCategory), Toast.LENGTH_LONG).show();
                } else {
                    db.addCategory(editText_categoryName.getText().toString(), icId, MainActivity.idInstance);
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
            }

            // Get categories
            Cursor categoriesData = db.getCategoriesByInstance(MainActivity.idInstance);

            // Array List to store the categories names
            ArrayList<CustomItems> categoriesList = new ArrayList<>();

            // Add categories names to categoriesList
            while (categoriesData.moveToNext()) {
                String categName = categoriesData.getString(1);
                String categIcon = categoriesData.getString(2);
                int categIconId = Integer.parseInt(categIcon);
                categoriesList.add(new CustomItems(categName, categIconId));
            }

            // Add option: "Agregar..." category
            categoriesList.add(new CustomItems(getString(R.string.activity_addEntry_addCategory_spinner), addIconId));
            // Create adapter for the spinner of categories
            CustomAdapter customAdapter = new CustomAdapter(getContext(), categoriesList, getActivity());
            spinner_categories.setAdapter(customAdapter);
            // Set default position
            spinner_categories.setSelection(categoriesList.size() - 2);
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
            // Set default position
            spinner_categories.setSelection(0);
        });

        builder.show();
    }

    /**
     * Show given message in screen
     *
     * @param titulo  Title
     * @param mensaje Message to show
     */
    private void showMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }

    @Override
    public void onClick(View view) {
        String montoStr = editText_profit.getText().toString();

        double ingresoInt;
        double gastoInt;

        switch (view.getId()) {
            case R.id.button_fragmentAddMov_addData:
                // Verify blank spaces
                if ((editText_profit.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }
                if (editText_description.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }
                if (editText_description.getText().toString().equals(getString(R.string.fragment_balance_class_remnantDescription))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_reservedWord));
                    break;
                }

                // Set 0 to blank spaces
                if (radioGroup_addMov.getCheckedRadioButtonId() == R.id.radioButton_fragmentAddMov_Gasto) {
                    ingresoInt = 0;
                    gastoInt = Double.parseDouble(montoStr);
                } else {
                    ingresoInt = Double.parseDouble(montoStr);
                    gastoInt = 0;
                }

                String descripcion = editText_description.getText().toString();

                // Get id instance
                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
                String id = prefs.getString("ID", null);

                boolean isResult = db.addEntry(date, time, gastoInt, ingresoInt, descripcion, id, category_id);

                // Check result
                if (isResult) {
                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }
                editText_profit.setText("");
                editText_description.setText("");

                // Set date now in the textView
                String dateNow;
                LocalDateTime now = LocalDateTime.now();
                dateNow = dtf.format(now);
                // Store in format YYYY-MM-DD
                date = dateNow;
                // Show date in format DD-MM-YYYY
                String dateToShow;
                String year = date.substring(0, 4);
                String month = date.substring(5, 7);
                String day = date.substring(8, 10);
                String sepearator = "-";
                dateToShow = day + sepearator + month + sepearator + year;
                textView_date.setText(dateToShow);

                // Store time in format HH:MM:SS.SSSS
                time = java.time.LocalTime.now().toString();
                String timeToShow = time.substring(0, 5);
                // Show time in format HH:MM
                textView_time.setText(timeToShow);

                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.button_fragmentAddMov_cancel:
                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.fab_calendar_addMovFragment:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), (datePicker, i, i1, i2) -> {
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
                    textView_date.setText(String.format("%s-%s-%s", strDay, strMonth, strYear));
                    // Store in format YY-MM-DD
                    date = strYear + "-" + strMonth + "-" + strDay;
                }, yearPick, monthPick, dayPick);
                datePickerDialog.show();

                break;

            case R.id.fab_clock_addMovFragment:
                Calendar timepick = Calendar.getInstance();
                int hourPick = timepick.get(Calendar.HOUR_OF_DAY);
                int minutesPick = timepick.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), (timePicker, i, i1) -> {
                    strHour = Integer.toString(i);
                    strMinute = Integer.toString(i1);

                    // Cast hour with 1 digit to 2
                    switch (strHour) {
                        case "0":
                            strHour = "00";
                            break;
                        case "1":
                            strHour = "01";
                            break;
                        case "2":
                            strHour = "02";
                            break;
                        case "3":
                            strHour = "03";
                            break;
                        case "4":
                            strHour = "04";
                            break;
                        case "5":
                            strHour = "05";
                            break;
                        case "6":
                            strHour = "06";
                            break;
                        case "7":
                            strHour = "07";
                            break;
                        case "8":
                            strHour = "08";
                            break;
                        case "9":
                            strHour = "09";
                            break;
                        default:
                            break;
                    }

                    // Cast minute with 1 digit to 2
                    switch (strMinute) {
                        case "0":
                            strMinute = "00";
                            break;
                        case "1":
                            strMinute = "01";
                            break;
                        case "2":
                            strMinute = "02";
                            break;
                        case "3":
                            strMinute = "03";
                            break;
                        case "4":
                            strMinute = "04";
                            break;
                        case "5":
                            strMinute = "05";
                            break;
                        case "6":
                            strMinute = "06";
                            break;
                        case "7":
                            strMinute = "07";
                            break;
                        case "8":
                            strMinute = "08";
                            break;
                        case "9":
                            strMinute = "09";
                            break;
                        default:
                            break;
                    }

                    textView_time.setText(String.format("%s:%s", strHour, strMinute));
                    time = strHour + ":" + strMinute + ":00.000";
                }, hourPick, minutesPick, false);
                timePickerDialog.show();

                break;
        }
    }

    @Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return ((IconPackApp) (Objects.requireNonNull(getActivity()).getApplication())).getIconPack();
    }


    @Override
    public void onIconDialogIconsSelected(@NotNull IconDialog iconDialog, @NotNull List<Icon> icons) {
        // Select an icon from list
        icId = Integer.toString(icons.get(0).getId());
        Drawable icDrawable = icons.get(0).getDrawable();
        imageButton_addIcon.setImageDrawable(icDrawable);
    }

    @Override
    public void onIconDialogCancelled() {
    }
}
