package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.InputFilterMinMax;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddProfileActivity extends AppCompatActivity {

    // View components
    private EditText editText_name;
    private EditText editText_initialProfit;
    private EditText editText_period;
    private Switch switch_addInitialProfit;
    private Switch switch_addPeriod;

    // Database instance
    DatabaseManager db;

    // Intent extras
    private boolean isNewUser;

    // Static categories id's
    // Otros
    private final static String otrosId = "957";
    // Ahorro
    private final static String ahorroId = "296";
    // Transferencia
    private final static String transferenciaId = "1014";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        // Initialize views
        editText_name = findViewById(R.id.edittext_addprofile_name);
        editText_initialProfit = findViewById(R.id.edittext_addprofile_initialProfit);
        editText_period = findViewById(R.id.edittext_addprofile_period);
        switch_addInitialProfit = findViewById(R.id.switch_addProfile_addInitialProfit);
        switch_addPeriod = findViewById(R.id.switch_addProfile_addPeriod);

        // Initialize db manager instance
        db = new DatabaseManager(this);

        // Get intent extras
        isNewUser = getIntent().getBooleanExtra("IS_NEW_USER", false);

        // Set filter for period edit text
        editText_period.setFilters(new InputFilter[]{new InputFilterMinMax(1, 31)});
    }

    @Override
    public void onBackPressed() {
        if (isNewUser) {
            Intent intentSalir = new Intent(Intent.ACTION_MAIN);
            intentSalir.addCategory(Intent.CATEGORY_HOME);
            intentSalir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentSalir);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * On click actions
     *
     * @param view view
     */
    public void onClickAddProfile(View view) {
        switch (view.getId()) {
            case R.id.imageButton_add_profile_help_saldoinicial:
                // Constant
                final Dialog dialogHelpInitialProfit = new Dialog(AddProfileActivity.this);

                // Set custom layout to dialog help
                dialogHelpInitialProfit.setContentView(R.layout.dialog_info);
                dialogHelpInitialProfit.setTitle(getString(R.string.dialogInfo_title_help));

                // Dialog help
                TextView textView_help = dialogHelpInitialProfit.findViewById(R.id.textView_dialogInfo_description);

                // Add text
                textView_help.setText(getString(R.string.dialogInfo_content_help_initialProfit));

                dialogHelpInitialProfit.show();

                break;

            case R.id.imageButton_add_profile_help_periodo:
                // Constant
                final Dialog dialogHelpPeriod = new Dialog(AddProfileActivity.this);

                // Set custom layout to dialog help
                dialogHelpPeriod.setContentView(R.layout.dialog_info);
                dialogHelpPeriod.setTitle(getString(R.string.dialogInfo_title_help));

                textView_help = dialogHelpPeriod.findViewById(R.id.textView_dialogInfo_description);

                // Add text
                textView_help.setText(getString(R.string.dialogInfo_content_help_period));

                dialogHelpPeriod.show();

                break;

            case R.id.switch_addProfile_addPeriod:
                if (switch_addPeriod.isChecked()) {
                    editText_period.setEnabled(true);
                    editText_period.setHint(getString(R.string.activity_add_profile_hint_period));
                } else {
                    editText_period.setEnabled(false);
                    editText_period.setHint("");
                }

                break;

            case R.id.switch_addProfile_addInitialProfit:
                if (switch_addInitialProfit.isChecked()) {
                    editText_initialProfit.setEnabled(true);
                    editText_initialProfit.setHint(getString(R.string.activity_add_profile_hint_saldoInicial));
                } else {
                    editText_initialProfit.setEnabled(false);
                    editText_initialProfit.setHint("");
                }

                break;

            case R.id.button_activityAddProfile_addData:
                if (editText_name.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.toast_addprofileactivity_noname, Toast.LENGTH_LONG).show();
                } else if ((switch_addInitialProfit.isChecked()) && (editText_initialProfit.getText().toString().equals(""))) {
                    Toast.makeText(this, R.string.toast_addprofileactivity_noinitialprofit, Toast.LENGTH_LONG).show();
                } else if ((switch_addPeriod.isChecked()) && (editText_period.getText().toString().equals(""))) {
                    Toast.makeText(this, R.string.toast_addprofileactivity_noperiod, Toast.LENGTH_LONG).show();
                } else {
                    Cursor instances = db.getInstancesAllData();
                    if (instances.getCount() > 0) {
                        String name = editText_name.getText().toString();
                        while (instances.moveToNext()) {
                            if (name.equals(instances.getString(1))) {
                                Toast.makeText(this, R.string.activity_add_profile_msg_samename, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    // Add period
                    int period = 0;
                    if (switch_addPeriod.isChecked()) {
                        period = Integer.parseInt(editText_period.getText().toString());
                    }

                    // Add instance to the table Instancias
                    String name = editText_name.getText().toString();
                    db.addInstance(name, period);
                    // Id
                    String id = db.getInstanceId(name);

                    // Add default categories
                    Cursor categories = db.getCategoriesByInstance(id);
                    if (categories.getCount() == 0) {
                        db.addCategory(getString(R.string.mainActivity_addCategory_others), otrosId, id);
                        db.addCategory(getString(R.string.mainActivity_addCategory_transfer), transferenciaId, id);
                        db.addCategory(getString(R.string.mainActivity_addCategory_saving), ahorroId, id);
                    }

                    // Add initial profit
                    if (switch_addInitialProfit.isChecked()) {
                        double spend = 0;
                        double in = Double.parseDouble(editText_initialProfit.getText().toString());
                        String categoryId = db.getCategoryId(getString(R.string.mainActivity_addCategory_others), id);
                        String description = getString(R.string.activity_addprofile_categoryInitial);
                        // Date now
                        String dateNow;
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDateTime now = LocalDateTime.now();
                        dateNow = dtf.format(now);
                        // Time now
                        String timeNow = java.time.LocalTime.now().toString();

                        db.addEntry(dateNow, timeNow, spend, in, description, id, categoryId);
                    }

                    // Store the instance as default
                    SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("NAME", name);
                    editor.putString("ID", id);
                    editor.putString("PERIOD", Integer.toString(period));
                    editor.apply();

                    editText_name.setText("");

                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    startActivity(mainActivityIntent);
                }

                break;

            case R.id.button_activityAddProfile_cancel:
                onBackPressed();

                break;
        }
    }
}
