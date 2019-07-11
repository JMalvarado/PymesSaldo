package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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

public class EditProfileActivity extends AppCompatActivity {

    // Components view
    private EditText editText_profileName;
    private TextView textView_instanceName;
    private EditText editText_period;
    private Switch switch_addPeriod;

    // Database instance / Global variables
    private DatabaseManager db;
    String nameInstance;
    String idInstance;
    String periodInstance;
    CharSequence[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize components view
        editText_profileName = findViewById(R.id.editText_editProfile_name);
        textView_instanceName = findViewById(R.id.textView_editProfile_instanceName);
        editText_period = findViewById(R.id.edittext_editprofile_period);
        switch_addPeriod = findViewById(R.id.switch_editProfile_addPeriod);

        // Set instance name as title
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Initialize database instance
        db = new DatabaseManager(this);

        // Profile data
        nameInstance = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);
        periodInstance = prefs.getString("PERIOD", null);

        // Set Text name with previous extracted data
        editText_profileName.setText(nameInstance);
        // Set period with previous extracted data
        if (!periodInstance.equals("0")) {
            editText_period.setText(periodInstance);
            switch_addPeriod.setChecked(true);
            editText_period.setEnabled(true);
        }

        // Options for the floating action button
        options = new CharSequence[]{getString(R.string.alert_optSi), getString(R.string.alert_optNo)};

        // Set filter for period edit text
        editText_period.setFilters(new InputFilter[]{new InputFilterMinMax(1, 28)});
    }

    public void onClickEditProfile(final View view) {

        switch (view.getId()) {
            case R.id.fab_editProfile_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(false);
                builder.setTitle(getString(R.string.alert_title_deleteProfile));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Delete Data
                                db.deleteInstance(idInstance);

                                // Get new default profile
                                Cursor cursor = db.getInstancesAllData();
                                cursor.moveToNext();
                                String itemId = cursor.getString(0);
                                String itemName = cursor.getString(1);
                                String itemPeriod = Integer.toString(cursor.getInt(2));

                                // Set new default profile
                                // Store the instance as default
                                SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("NAME", itemName);
                                editor.putString("ID", itemId);
                                editor.putString("PERIOD", itemPeriod);
                                editor.apply();

                                // Start main activity
                                Intent mainActivityIntent = new Intent(view.getContext(), MainActivity.class);
                                startActivity(mainActivityIntent);

                                break;

                            case 1:
                                break;
                        }
                    }
                });
                builder.show();

                break;

            case R.id.switch_editProfile_addPeriod:
                if (switch_addPeriod.isChecked()) {
                    editText_period.setEnabled(true);
                    editText_period.setHint(getString(R.string.activity_add_profile_hint_period));
                } else {
                    editText_period.setEnabled(false);
                    editText_period.setHint("");
                }

                break;

            case R.id.Ingreso_editProfile:
                if (editText_profileName.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.toast_addprofileactivity_noname, Toast.LENGTH_LONG).show();
                } else if ((switch_addPeriod.isChecked()) && (editText_period.getText().toString().equals(""))) {
                    Toast.makeText(this, R.string.toast_addprofileactivity_noperiod, Toast.LENGTH_LONG).show();
                } else {
                    // Add period
                    int period = 0;
                    if (switch_addPeriod.isChecked()) {
                        period = Integer.parseInt(editText_period.getText().toString());
                    }

                    db.editInstance(idInstance, editText_profileName.getText().toString(), period);

                    // Get new default profile
                    String itemName = editText_profileName.getText().toString();

                    // Set new default profile
                    // Store the instance as default
                    SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("NAME", itemName);
                    editor.putString("PERIOD", Integer.toString(period));
                    editor.apply();

                    editText_profileName.setText("");

                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    startActivity(mainActivityIntent);
                }

                break;

            case R.id.cancel_editProfile:
                onBackPressed();

                break;
        }
    }
}
