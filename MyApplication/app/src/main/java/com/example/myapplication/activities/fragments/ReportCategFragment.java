package com.example.myapplication.activities.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ReportCategViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ReportCategFragment extends Fragment implements View.OnClickListener {

    // Local variables

    // Views
    private ViewPager viewPager;
    private ReportCategViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private TextView textView_instanceName;
    private FloatingActionButton fab_dateBegin;
    private FloatingActionButton fab_dateFinal;
    private TextView textView_dateBegin;
    private TextView textView_dateFinal;
    private ImageButton imageButton_download;
    private EditText editText_fileName;

    //Other
    private Calendar calendar;
    private static int intBegDay, intBegMonth, intBegYear, intFinDay, intFinMonth, intFinYear;
    private static String begDay, begMonth, begYear, finDay, finMonth, finYear, period;
    private DatePickerDialog datePickerDialog;
    private DatabaseManager db;
    private ArrayList<String> categoriesNames;
    private ArrayList<Float> categoriesTotalAmountProfits;
    private ArrayList<Float> categoriesTotalAmountSpends;
    private ArrayList<Float> percentagesProfitsList;
    private ArrayList<Float> percentagesSpendsList;
    private String[] fileName;
    private Workbook workbook;
    private Dialog dialogSaving;
    private Dialog dialogLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_categ, container, false);

        // Include POI Library
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        // Initialize Database Manager
        db = new DatabaseManager(view.getContext());

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.report_name));

        // Assign layout views variables
        textView_instanceName = view.findViewById(R.id.textView_fragmentReportCateg_instanceName);
        viewPager = view.findViewById(R.id.viewPager_reportCateg);
        tabLayout = view.findViewById(R.id.tabLayout_reportCateg);
        fab_dateBegin = view.findViewById(R.id.fab_calendarBegin_reportFragment);
        fab_dateBegin.setOnClickListener(this);
        fab_dateFinal = view.findViewById(R.id.fab_calendarFinal_reportFragment);
        fab_dateFinal.setOnClickListener(this);
        textView_dateBegin = view.findViewById(R.id.textView_reportFragment_dateBegin);
        textView_dateFinal = view.findViewById(R.id.textView_reportFragment_dateFinal);
        imageButton_download = view.findViewById(R.id.imageButton_fragmentReport_download);
        imageButton_download.setOnClickListener(this);

        // Hide FAB add entry from screen
        MainActivity.fab_addEntry.hide();

        // Set instance name as title
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Initialize and assign viewPager Adapter
        viewPagerAdapter = new ReportCategViewPagerAdapter(getFragmentManager(), view.getContext());
        viewPager.setAdapter(viewPagerAdapter);

        // Initialize and assign tab layout
        tabLayout.setupWithViewPager(viewPager);


        // If there is default period previously selected, set that period
        // Get the shared preferences period of instance
        SharedPreferences prefsReportPeriod = getActivity().getSharedPreferences("profilereportperiod", Context.MODE_PRIVATE);
        String reportPeriod = prefsReportPeriod.getString(MainActivity.idInstance, null);

        if (reportPeriod != null) {
            // Set text views with the default period
            String begPeriodDate = reportPeriod.substring(0, 10);
            String finPeriodDate = reportPeriod.substring(11, 21);

            // Set initial value for date variables

            begDay = begPeriodDate.substring(8, 10);
            begMonth = begPeriodDate.substring(5, 7);
            begYear = begPeriodDate.substring(0, 4);
            finDay = finPeriodDate.substring(8, 10);
            finMonth = finPeriodDate.substring(5, 7);
            finYear = finPeriodDate.substring(0, 4);

            period = reportPeriod;

            String begPeriodDateFormated = begDay + "-" + begMonth + "-" + begYear;
            String finPeriodDateFormated = finDay + "-" + finMonth + "-" + finYear;

            textView_dateBegin.setText(begPeriodDateFormated);
            textView_dateFinal.setText(finPeriodDateFormated);
        }

        return view;
    }

    /**
     * On Click actions
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_calendarBegin_reportFragment:
                calendar = Calendar.getInstance();
                intBegDay = calendar.get(Calendar.DAY_OF_MONTH);
                intBegMonth = calendar.get(Calendar.MONTH);
                intBegYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(v.getContext(), (datePicker, i, i1, i2) -> {
                    textView_dateBegin.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
                    begDay = Integer.toString(i2);
                    begMonth = Integer.toString(i1 + 1);
                    begYear = Integer.toString(i);

                    // Verify if there is a selected date in both blank spaces
                    if (!textView_dateFinal.getText().toString().equals("")) {
                        storeDefaultPeriod();
                    }
                }, intBegYear, intBegMonth, intBegDay);
                datePickerDialog.show();

                break;


            case R.id.fab_calendarFinal_reportFragment:
                calendar = Calendar.getInstance();
                intFinDay = calendar.get(Calendar.DAY_OF_MONTH);
                intFinMonth = calendar.get(Calendar.MONTH);
                intFinYear = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(v.getContext(), (datePicker, i, i1, i2) -> {
                    textView_dateFinal.setText(new StringBuilder().append(i2).append("-").append(i1 + 1).append("-").append(i).toString());
                    finDay = Integer.toString(i2);
                    finMonth = Integer.toString(i1 + 1);
                    finYear = Integer.toString(i);

                    // Verify if there is a selected date in both blank spaces
                    if (!textView_dateBegin.getText().toString().equals("")) {
                        // Store period in shared preference
                        storeDefaultPeriod();
                    }
                }, intFinYear, intFinMonth, intFinDay);
                datePickerDialog.show();

                break;


            case R.id.imageButton_fragmentReport_download:

                // Check if there is a selected period
                if (!textView_dateBegin.getText().toString().equals("") && !textView_dateFinal.getText().toString().equals("")) {
                    // Check write external storage permission
                    if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                        break;
                    }
                    // Look for db entries
                    String begPeriodDate = period.substring(0, 10);
                    String finPeriodDate = period.substring(11, 21);

                    new Task().execute(begPeriodDate, finPeriodDate);

                    openDialog();

                } else {
                    Toast.makeText(v.getContext(), getString(R.string.toast_reportCategFragment_info_noPeriodSelected), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * Alert dialog to add category
     */
    private void openDialog() {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_export_file, null);

        // Initialize
        editText_fileName = subView.findViewById(R.id.dialogLayoutExportFile_editText_fileName);

        // Set actual date and time as file name
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        String dateNow;
        LocalDateTime now = LocalDateTime.now();
        dateNow = dtf.format(now);
        String timeNow = java.time.LocalTime.now().toString();
        String hour = timeNow.substring(0, 2);
        String minutes = timeNow.substring(3, 5);
        String seconds = timeNow.substring(6, 8);
        String milliseconds = timeNow.substring(9);

        String timeToShow = hour + "." + minutes + "." + seconds + "." + milliseconds;

        fileName = new String[]{dateNow + "_" + timeToShow};
        editText_fileName.setText(fileName[0]);

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(getString(R.string.dialogExportFile_title));
        builder.setMessage(getString(R.string.dialogExportFile_mssg));
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), (dialogInterface, i) -> {
            new TaskCreateXLSX().execute();
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
        });

        builder.show();
    }

    /**
     * get data from db dialog.
     */
    private class Task extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            dialogLoading = new Dialog(Objects.requireNonNull(getContext()));

            dialogLoading.setContentView(R.layout.dialog_loading);
            dialogLoading.setTitle(getString(R.string.dialogInfo_title_help));

            dialogLoading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialogLoading.dismiss();
        }

        @Override
        protected Void doInBackground(String... strings) {
            // Get Entries from database with period and instance as filter
            Cursor profits = db.getEntryProfitInDate(MainActivity.idInstance, strings[0], strings[1], 0);
            // Get Entries from database with period and instance as filter
            Cursor spends = db.getEntrySpendInDate(MainActivity.idInstance, strings[0], strings[1], 0);

            // Get categories from database with instance as filter
            Cursor categories = db.getCategoriesByInstance(MainActivity.idInstance);

            // Process categories
            categoriesNames = new ArrayList<>();
            categoriesTotalAmountProfits = new ArrayList<>();
            categoriesTotalAmountSpends = new ArrayList<>();
            percentagesProfitsList = new ArrayList<>();
            percentagesSpendsList = new ArrayList<>();
            while (categories.moveToNext()) {
                float totalAmountProfits = 0;
                float totalAmountSpends = 0;
                String categId = categories.getString(0);
                while (profits.moveToNext()) {
                    String profitCategId = profits.getString(2);
                    if (profitCategId.equals(categId)) {
                        float amount = profits.getFloat(5);
                        totalAmountProfits += amount;
                    }
                }
                while (spends.moveToNext()) {
                    String spendCategId = spends.getString(2);
                    if (spendCategId.equals(categId)) {
                        float amount = spends.getFloat(6);
                        totalAmountSpends += amount;
                    }
                }

                // Add data in temporal lists
                categoriesNames.add(categories.getString(1));
                categoriesTotalAmountProfits.add(totalAmountProfits);
                categoriesTotalAmountSpends.add(totalAmountSpends);

                // Reset profits and spends Cursor position
                profits.moveToPosition(-1);
                spends.moveToPosition(-1);
            }
            categories.moveToPosition(-1);

            // Get total amount to calculate percentage
            float totalProfits = 0;
            float totalSpends = 0;
            for (int i = 0; i < categoriesTotalAmountProfits.size(); i++) {
                totalProfits += categoriesTotalAmountProfits.get(i);
            }
            for (int i = 0; i < categoriesTotalAmountSpends.size(); i++) {
                totalSpends += categoriesTotalAmountSpends.get(i);
            }
            // Calculate percentage for each category
            for (int i = 0; i < categoriesNames.size(); i++) {
                assert categoriesNames != null;
                // Calculate percentage
                float totalAmountCategoryProfits = categoriesTotalAmountProfits.get(i);
                float totalAmountCategorySpends = categoriesTotalAmountSpends.get(i);
                float percentageProfits = (totalAmountCategoryProfits * 100) / (totalProfits);
                float percentageSpends = (totalAmountCategorySpends * 100) / (totalSpends);

                percentagesProfitsList.add(percentageProfits);
                percentagesSpendsList.add(percentageSpends);
            }

            return null;
        }
    }

    /**
     * Create XLSX file
     */
    private class TaskCreateXLSX extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dialogSaving = new Dialog(Objects.requireNonNull(getContext()));

            dialogSaving.setContentView(R.layout.dialog_saving);
            dialogSaving.setTitle(getString(R.string.dialogInfo_title_help));
            dialogSaving.setCanceledOnTouchOutside(false);

            dialogSaving.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fileName[0] = editText_fileName.getText().toString() + ".xlsx";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, getString(R.string.dialogExportFile_folderName));
            folder.mkdir();
            File file = new File(folder, fileName[0]);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            dialogSaving.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            workbook = new XSSFWorkbook();
            Sheet sheetProfits = workbook.createSheet(getString(R.string.dialogExportFile_sheetProfitTitle));
            Sheet sheetSpends = workbook.createSheet(getString(R.string.dialogExportFile_sheetSpendTitle));

            // Create cell text styles
            ///////////////////////////////////////////////////////
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("Arial");
            font.setColor(IndexedColors.BLACK.getIndex());
            font.setBold(true);
            font.setItalic(false);

            CellStyle cellStyle2 = workbook.createCellStyle();
            Font font2 = workbook.createFont();
            font2.setFontHeightInPoints((short) 10);
            font2.setFontName("Arial");
            font2.setColor(IndexedColors.BLACK.getIndex());
            font2.setBold(false);
            font2.setItalic(false);
            ///////////////////////////////////////////////////////

            // Add titles
            //////////////////////////////////////////////////////
            Row rowTitleProfit = sheetProfits.createRow(0);
            Cell cellCategoriesTitle = rowTitleProfit.createCell(0);
            Cell cellAmountTitle = rowTitleProfit.createCell(1);
            Cell cellPercentageTitle = rowTitleProfit.createCell(2);
            cellStyle.setFont(font);
            cellStyle2.setFont(font2);
            cellCategoriesTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleCategory));
            cellAmountTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleAmount));
            cellPercentageTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitlePercentage));
            cellCategoriesTitle.setCellStyle(cellStyle);
            cellAmountTitle.setCellStyle(cellStyle);
            cellPercentageTitle.setCellStyle(cellStyle);

            Row rowTitleSpend = sheetSpends.createRow(0);
            cellCategoriesTitle = rowTitleSpend.createCell(0);
            cellAmountTitle = rowTitleSpend.createCell(1);
            cellPercentageTitle = rowTitleSpend.createCell(2);
            cellCategoriesTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleCategory));
            cellAmountTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleAmount));
            cellPercentageTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitlePercentage));
            cellCategoriesTitle.setCellStyle(cellStyle);
            cellAmountTitle.setCellStyle(cellStyle);
            cellPercentageTitle.setCellStyle(cellStyle);
            //////////////////////////////////////////////////////

            int lastRow = 0;
            // Add cells
            for (int j = 1; j <= categoriesNames.size(); j++) {
                // Profit data
                Row rowProfit = sheetProfits.createRow(j);
                rowProfit.createCell(0).setCellValue(categoriesNames.get(j - 1));
                rowProfit.createCell(1).setCellValue(categoriesTotalAmountProfits.get(j - 1));
                rowProfit.createCell(2).setCellValue(percentagesProfitsList.get(j - 1));

                // Spend data
                Row rowSpend = sheetSpends.createRow(j);
                rowSpend.createCell(0).setCellValue(categoriesNames.get(j - 1));
                rowSpend.createCell(1).setCellValue(categoriesTotalAmountSpends.get(j - 1));
                rowSpend.createCell(2).setCellValue(percentagesSpendsList.get(j - 1));

                lastRow = j;
            }
            lastRow += 1;

            // Period title
            String periodFromTitle = getString(R.string.dialogExportFile_sheetTitleFrom);
            String periodFromDate = begDay + "-" + begMonth + "-" + begYear;
            String periodToTitle = getString(R.string.dialogExportFile_sheetTitleTo);
            String periodToDate = finDay + "-" + finMonth + "-" + finYear;

            // Add period title
            //////////////////////////////////////////////////////
            /// Profits sheet
            // From
            Row rowPeriodProfitFrom = sheetProfits.createRow(lastRow);
            Cell cellFromTitle = rowPeriodProfitFrom.createCell(0);
            Cell cellFromDate = rowPeriodProfitFrom.createCell(1);
            cellFromTitle.setCellValue(periodFromTitle);
            cellFromDate.setCellValue(periodFromDate);
            cellFromTitle.setCellStyle(cellStyle);
            cellFromDate.setCellStyle(cellStyle2);
            // To
            Row rowPeriodProfitTo = sheetProfits.createRow(lastRow + 1);
            Cell cellToTitle = rowPeriodProfitTo.createCell(0);
            Cell cellToDate = rowPeriodProfitTo.createCell(1);
            cellToTitle.setCellValue(periodToTitle);
            cellToDate.setCellValue(periodToDate);
            cellToTitle.setCellStyle(cellStyle);
            cellToDate.setCellStyle(cellStyle2);

            /// Spend Sheet
            // From
            Row rowPeriodSpendFrom = sheetSpends.createRow(lastRow);
            cellFromTitle = rowPeriodSpendFrom.createCell(0);
            cellFromDate = rowPeriodSpendFrom.createCell(1);
            cellFromTitle.setCellValue(periodFromTitle);
            cellFromDate.setCellValue(periodFromDate);
            cellFromTitle.setCellStyle(cellStyle);
            cellFromDate.setCellStyle(cellStyle2);
            // To
            Row rowPeriodSpendTo = sheetSpends.createRow(lastRow + 1);
            cellToTitle = rowPeriodSpendTo.createCell(0);
            cellToDate = rowPeriodSpendTo.createCell(1);
            cellToTitle.setCellValue(periodToTitle);
            cellToDate.setCellValue(periodToDate);
            cellToTitle.setCellStyle(cellStyle);
            cellToDate.setCellStyle(cellStyle2);
            //////////////////////////////////////////////////////

            return null;
        }
    }

    /**
     * Store the instance custom period for the report fragment in a shared preference
     */
    private void storeDefaultPeriod() {
        // Cast begin day with 1 digit to 2
        switch (begDay) {
            case "1":
                begDay = "01";
                break;
            case "2":
                begDay = "02";
                break;
            case "3":
                begDay = "03";
                break;
            case "4":
                begDay = "04";
                break;
            case "5":
                begDay = "05";
                break;
            case "6":
                begDay = "06";
                break;
            case "7":
                begDay = "07";
                break;
            case "8":
                begDay = "08";
                break;
            case "9":
                begDay = "09";
                break;
        }

        // Cast begin month with 1 digit to 2
        switch (begMonth) {
            case "1":
                begMonth = "01";
                break;
            case "2":
                begMonth = "02";
                break;
            case "3":
                begMonth = "03";
                break;
            case "4":
                begMonth = "04";
                break;
            case "5":
                begMonth = "05";
                break;
            case "6":
                begMonth = "06";
                break;
            case "7":
                begMonth = "07";
                break;
            case "8":
                begMonth = "08";
                break;
            case "9":
                begMonth = "09";
                break;
            default:
                break;
        }

        // Cast Final day with 1 digit to 2
        switch (finDay) {
            case "1":
                finDay = "01";
                break;
            case "2":
                finDay = "02";
                break;
            case "3":
                finDay = "03";
                break;
            case "4":
                finDay = "04";
                break;
            case "5":
                finDay = "05";
                break;
            case "6":
                finDay = "06";
                break;
            case "7":
                finDay = "07";
                break;
            case "8":
                finDay = "08";
                break;
            case "9":
                finDay = "09";
                break;
            default:
                break;
        }

        // Cast Final month with 1 digit to 2
        switch (finMonth) {
            case "1":
                finMonth = "01";
                break;
            case "2":
                finMonth = "02";
                break;
            case "3":
                finMonth = "03";
                break;
            case "4":
                finMonth = "04";
                break;
            case "5":
                finMonth = "05";
                break;
            case "6":
                finMonth = "06";
                break;
            case "7":
                finMonth = "07";
                break;
            case "8":
                finMonth = "08";
                break;
            case "9":
                finMonth = "09";
                break;
            default:
                break;
        }

        String begDatePeriodDefault = begYear + "-" + begMonth + "-" + begDay;
        String finDatePeriodDefault = finYear + "-" + finMonth + "-" + finDay;
        period = begDatePeriodDefault + "/" + finDatePeriodDefault;

        SharedPreferences prefsReportPeriod = Objects.requireNonNull(getActivity()).
                getSharedPreferences("profilereportperiod", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsReportPeriod.edit();
        editor.putString(MainActivity.idInstance, period);
        editor.apply();

        // Refresh layouts
        // Initialize and assign viewPager Adapter
        viewPagerAdapter = new ReportCategViewPagerAdapter(getFragmentManager(), getContext());
        viewPager.setAdapter(viewPagerAdapter);

        // Initialize and assign tab layout
        tabLayout.setupWithViewPager(viewPager);
    }
}
