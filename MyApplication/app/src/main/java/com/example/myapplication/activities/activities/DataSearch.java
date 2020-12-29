package com.example.myapplication.activities.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ListData;
import com.example.myapplication.activities.data.MyAdapter;
import com.example.myapplication.activities.fragments.SearchFragment;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataSearch extends AppCompatActivity {

    // Cmponents view
    private RecyclerView rvList;
    private RecyclerView.Adapter rvAdapter;
    private TextView textView_instanceName;
    private TextView textView_periodTitle;
    private TextView textView_profit;
    private TextView textView_spend;
    private TextView textView_balance;
    //private TextView textView_directory;
    private ImageButton imageButton_download;
    private EditText editText_fileName;

    // Global variables
    private DatabaseManager db;
    private List<ListData> listItems;
    private String[] fileName;
    private String period;
    private String profitTotal;
    private String spendTotal;
    private Dialog dialogSaving;
    private Workbook workbook;

    private ArrayList<String> descripciones;
    private ArrayList<String> fechas;
    private ArrayList<String> horas;
    private ArrayList<String> ingresos;
    private ArrayList<String> gastos;
    private ArrayList<String> ids;
    private ArrayList<String> categIds;

    // Constants
    private static final int WRITE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_search);

        // Include POI Library
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        // Initialize view components
        textView_instanceName = findViewById(R.id.textView_dataSearch_instanceName);
        textView_periodTitle = findViewById(R.id.tvTitPeriodoLista);
        textView_profit = findViewById(R.id.textview_activitydatasearch_profit);
        textView_spend = findViewById(R.id.textview_activitydatasearch_spend);
        textView_balance = findViewById(R.id.textview_activitydatasearch_balance);
        imageButton_download = findViewById(R.id.imageButton_activityDataSearch_download);

        // Database instance
        db = new DatabaseManager(this);

        // Set instance name as title
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Set recycler view component
        rvList = findViewById(R.id.dataRecyclerView);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        descripciones = new ArrayList<>();
        fechas = new ArrayList<>();
        horas = new ArrayList<>();
        ingresos = new ArrayList<>();
        gastos = new ArrayList<>();
        ids = new ArrayList<>();

        descripciones = getIntent().getStringArrayListExtra("DESCRIPCIONES");
        fechas = getIntent().getStringArrayListExtra("FECHAS");
        horas = getIntent().getStringArrayListExtra("HORAS");
        ingresos = getIntent().getStringArrayListExtra("INGRESOS");
        gastos = getIntent().getStringArrayListExtra("GASTOS");
        ids = getIntent().getStringArrayListExtra("IDS");
        categIds = getIntent().getStringArrayListExtra("CATEGIDS");

        // Get range date for period
        if (SearchFragment.radioButtonMonthIsChecked) {
            // Set month and year in text view
            String month = SearchFragment.monthForLabel;
            String year = SearchFragment.yearForLabel;
            String monthAndYear = month + ", " + year;

            period = monthAndYear;
            textView_periodTitle.setText(monthAndYear);

        } else if ((SearchFragment.checkboxBegIsChecked) && (!SearchFragment.checkboxFinalIsChecked) && (SearchFragment.radioButtonDatesIsChecked)) {
            // Initial date
            String dateTmp = fechas.get(0);
            // Set on format DD-MM-YYYY
            String year = dateTmp.substring(0, 4);
            String month = dateTmp.substring(5, 7);
            String day = dateTmp.substring(8, 10);
            String separator = "/";
            String dateInit = day + separator + month + separator + year;
            // Final date
            year = SearchFragment.finYear;
            month = SearchFragment.finMonth;
            day = SearchFragment.finDay;
            String dateFin = day + separator + month + separator + year;

            String periodTitle = getString(R.string.reportGraphicsFragment_periodoTitle_from) + " " +
                    dateInit + " " +
                    getString(R.string.reportGraphicsFragment_periodoTitle_to) + " " +
                    dateFin;

            period = periodTitle;
            textView_periodTitle.setText(periodTitle);

        } else if ((!SearchFragment.checkboxBegIsChecked) && (SearchFragment.checkboxFinalIsChecked) && (SearchFragment.radioButtonDatesIsChecked)) {
            // Initial date
            String year = SearchFragment.begYear;
            String month = SearchFragment.begMonth;
            String day = SearchFragment.begDay;
            String separator = "/";
            String dateInit = day + separator + month + separator + year;
            // Final date
            String dateTmp = fechas.get(fechas.size() - 1);
            // Set on format DD-MM-YYYY
            year = dateTmp.substring(0, 4);
            month = dateTmp.substring(5, 7);
            day = dateTmp.substring(8, 10);
            String dateFin = day + separator + month + separator + year;

            String periodTitle = getString(R.string.reportGraphicsFragment_periodoTitle_from) + " " +
                    dateInit + " " +
                    getString(R.string.reportGraphicsFragment_periodoTitle_to) + " " +
                    dateFin;

            period = periodTitle;
            textView_periodTitle.setText(periodTitle);

        } else if ((SearchFragment.checkboxBegIsChecked) && (SearchFragment.checkboxFinalIsChecked) && (SearchFragment.radioButtonDatesIsChecked)) {
            period = getString(R.string.fragment_search_period_title_all);
            textView_periodTitle.setText(period);
        } else {
            // Initial date
            String year = SearchFragment.begYear;
            String month = SearchFragment.begMonth;
            String day = SearchFragment.begDay;
            String separator = "/";
            String dateInit = day + separator + month + separator + year;
            // Final date
            year = SearchFragment.finYear;
            month = SearchFragment.finMonth;
            day = SearchFragment.finDay;
            String dateFin = day + separator + month + separator + year;

            String periodTitle = getString(R.string.reportGraphicsFragment_periodoTitle_from) + " " +
                    dateInit + " " +
                    getString(R.string.reportGraphicsFragment_periodoTitle_to) + " " +
                    dateFin;

            period = periodTitle;
            textView_periodTitle.setText(periodTitle);
        }

        // Get entries data and calculate total profit, spend and balance
        double totalProfit = 0;
        double totalSpend = 0;
        double totalBalance;
        for (int i = 0; i < descripciones.size(); i++) {
            assert ids != null;
            assert categIds != null;
            //Set List Data object with entry data
            ListData listData = new ListData(
                    descripciones.get(i),
                    fechas.get(i),
                    horas.get(i),
                    ingresos.get(i),
                    gastos.get(i),
                    ids.get(i),
                    categIds.get(i)
            );

            listItems.add(listData);

            // Add profit and spend
            double profitTmp = Double.parseDouble(ingresos.get(i));
            double spendTmp = Double.parseDouble(gastos.get(i));
            totalProfit += profitTmp;
            totalSpend += spendTmp;
        }

        // Set total profit, spend and balance data in text views
        // Calculate balance
        totalBalance = totalProfit - totalSpend;
        // Format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String balanceDf = df.format(totalBalance);
        String profitDf = df.format(totalProfit);
        String spendDf = df.format(totalSpend);
        // Set balance, profit and spend on textView
        textView_balance.setText(balanceDf);
        if (totalBalance < 0) {
            textView_balance.setTextColor(this.getColor(R.color.colorRed));
        } else if (totalBalance > 0) {
            textView_balance.setTextColor(this.getColor(R.color.colorGreen));
        } else {
            textView_balance.setTextColor(this.getColor(R.color.colorBlack));
        }
        profitTotal = Double.toString(totalProfit);
        spendTotal = Double.toString(totalSpend);

        textView_profit.setText(profitDf);
        textView_spend.setText(spendDf);

        rvAdapter = new MyAdapter(listItems, this, this);

        rvList.setAdapter(rvAdapter);


        // On click download entries button
        imageButton_download.setOnClickListener(v -> {
            // Check write external storage permission
            if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                openDialog();
            }
        });
    }

    /**
     * Alert dialog to download
     */
    private void openDialog() {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_export_file, null);

        // Initialize
        editText_fileName = subView.findViewById(R.id.dialogLayoutExportFile_editText_fileName);
        //textView_directory = subView.findViewById(R.id.dialogLayoutExportFile_textView_directory);

        // Set actual date and time as file name
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
        String directory = getString(R.string.dialogExportFile_directoryName);
        //textView_directory.setText(directory.concat("/").concat(getString(R.string.dialogExportFile_folderName_Entries)));

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setTitle(getString(R.string.dialogExportFile_title));
        builder.setMessage(getString(R.string.dialogExportFile_mssg));
        builder.setView(subView);

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), (dialogInterface, i) -> new TaskCreateXLSX().execute());

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null
                            && data.getData() != null) {
                        writeInFile(data.getData());
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    /**
     * Write in xlsx file the workbook content
     */
    private void writeInFile(@NonNull Uri uri) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            workbook.write(outputStream);
            assert outputStream != null;
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create XLSX file
     */
    @SuppressLint("StaticFieldLeak")
    private class TaskCreateXLSX extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dialogSaving = new Dialog(DataSearch.this);

            dialogSaving.setContentView(R.layout.dialog_saving);
            dialogSaving.setTitle(getString(R.string.dialogInfo_title_help));
            dialogSaving.setCancelable(false);

            dialogSaving.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!editText_fileName.getText().toString().equals("")) {
                fileName[0] = editText_fileName.getText().toString() + ".xlsx";
            } else {
                fileName[0] = fileName[0].concat(".xlsx");
            }

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.putExtra(Intent.EXTRA_TITLE, fileName[0]);

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when your app creates the document.
            // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

            startActivityForResult(intent, WRITE_REQUEST_CODE);



            /*String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, getString(R.string.dialogExportFile_folderName_Entries));
            folder.mkdir();
            File file = new File(folder, fileName[0]);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }*/

            /*try {
                File file = new File();
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            dialogSaving.dismiss();

            Toast.makeText(DataSearch.this, getString(R.string.toast_fileSaved), Toast.LENGTH_SHORT).show();
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
            Cell cellDescriptionTitle = rowTitleProfit.createCell(0);
            Cell cellDateTitle = rowTitleProfit.createCell(1);
            Cell cellTimeTitle = rowTitleProfit.createCell(2);
            Cell cellAmountTitle = rowTitleProfit.createCell(3);
            Cell cellCategoryTitle = rowTitleProfit.createCell(4);
            cellStyle.setFont(font);
            cellStyle2.setFont(font2);
            cellDescriptionTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleDescription));
            cellAmountTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleAmount));
            cellDateTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleDate));
            cellTimeTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleTime));
            cellCategoryTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleCategory));
            cellDescriptionTitle.setCellStyle(cellStyle);
            cellDateTitle.setCellStyle(cellStyle);
            cellTimeTitle.setCellStyle(cellStyle);
            cellAmountTitle.setCellStyle(cellStyle);
            cellCategoryTitle.setCellStyle(cellStyle);

            Row rowTitleSpend = sheetSpends.createRow(0);
            cellDescriptionTitle = rowTitleSpend.createCell(0);
            cellDateTitle = rowTitleSpend.createCell(1);
            cellTimeTitle = rowTitleSpend.createCell(2);
            cellAmountTitle = rowTitleSpend.createCell(3);
            cellCategoryTitle = rowTitleSpend.createCell(4);
            cellDescriptionTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleDescription));
            cellAmountTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleAmount));
            cellDateTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleDate));
            cellTimeTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleTime));
            cellCategoryTitle.setCellValue(getString(R.string.dialogExportFile_sheetTitleCategory));
            cellDescriptionTitle.setCellStyle(cellStyle);
            cellDateTitle.setCellStyle(cellStyle);
            cellTimeTitle.setCellStyle(cellStyle);
            cellAmountTitle.setCellStyle(cellStyle);
            cellCategoryTitle.setCellStyle(cellStyle);
            //////////////////////////////////////////////////////

            int currentRow = 1;
            int lastRowProfit = 0;
            // Add cells Profit
            for (int j = 0; j < ids.size(); j++) {
                // Get category name
                String categoryName = db.getCategoryName(categIds.get(j), MainActivity.idInstance);

                // Profit data
                if (!ingresos.get(j).equals("0.0")) {
                    Row rowProfit = sheetProfits.createRow(currentRow);
                    rowProfit.createCell(0).setCellValue(descripciones.get(j));
                    rowProfit.createCell(1).setCellValue(fechas.get(j));
                    rowProfit.createCell(2).setCellValue(horas.get(j));
                    rowProfit.createCell(3).setCellValue(ingresos.get(j));
                    rowProfit.createCell(4).setCellValue(categoryName);

                    currentRow += 1;
                }

                lastRowProfit = currentRow;
            }
            currentRow = 1;
            int lastRowSpend = 0;
            // Add cells Spend
            for (int j = 0; j < ids.size(); j++) {
                // Get category name
                String categoryName = db.getCategoryName(categIds.get(j), MainActivity.idInstance);

                // Profit data
                if (!gastos.get(j).equals("0.0")) {
                    Row rowSpend = sheetSpends.createRow(currentRow);
                    rowSpend.createCell(0).setCellValue(descripciones.get(j));
                    rowSpend.createCell(1).setCellValue(fechas.get(j));
                    rowSpend.createCell(2).setCellValue(horas.get(j));
                    rowSpend.createCell(3).setCellValue(gastos.get(j));
                    rowSpend.createCell(4).setCellValue(categoryName);

                    currentRow += 1;
                }

                lastRowSpend = currentRow;
            }
            lastRowProfit += 1;
            lastRowSpend += 1;

            // Total title
            String totalTitle = getString(R.string.dialogExportFile_sheetTitleTotal);

            // Add period and total titles
            //////////////////////////////////////////////////////
            /// Total
            // Profits sheet
            Row rowTotalProfit = sheetProfits.createRow(lastRowProfit);
            Cell cellTotalTitle = rowTotalProfit.createCell(0);
            Cell cellTotalAmount = rowTotalProfit.createCell(1);
            cellTotalTitle.setCellValue(totalTitle);
            cellTotalTitle.setCellStyle(cellStyle);
            cellTotalAmount.setCellValue(profitTotal);
            cellTotalTitle.setCellStyle(cellStyle2);
            // Spend Sheet
            Row rowTotalSpend = sheetSpends.createRow(lastRowSpend);
            cellTotalTitle = rowTotalSpend.createCell(0);
            cellTotalAmount = rowTotalSpend.createCell(1);
            cellTotalTitle.setCellValue(totalTitle);
            cellTotalTitle.setCellStyle(cellStyle);
            cellTotalAmount.setCellValue(spendTotal);
            cellTotalTitle.setCellStyle(cellStyle2);
            lastRowProfit += 1;
            lastRowSpend += 1;
            /// Period
            // Profits sheet
            Row rowPeriodProfit = sheetProfits.createRow(lastRowProfit);
            Cell cellPeriodTitle = rowPeriodProfit.createCell(0);
            cellPeriodTitle.setCellValue(period);
            cellPeriodTitle.setCellStyle(cellStyle);
            // Spends sheet
            Row rowPeriodSpend = sheetSpends.createRow(lastRowSpend);
            cellPeriodTitle = rowPeriodSpend.createCell(0);
            cellPeriodTitle.setCellValue(period);
            cellPeriodTitle.setCellStyle(cellStyle);
            //////////////////////////////////////////////////////

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
