package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditProfileActivity extends AppCompatActivity {

    // Components view
    private EditText editText_profileName;
    private TextView textView_instanceName;

    // Database instance / Global variables
    private DatabaseManager db;
    String nameInstance;
    String idInstance;
    CharSequence[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize components view
        editText_profileName = findViewById(R.id.editText_editProfile_name);
        textView_instanceName = findViewById(R.id.textView_editProfile_instanceName);

        // Set instance name as title
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Initialize database instance
        db = new DatabaseManager(this);

        // Profile data
        nameInstance = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);

        // Set Text name with previous extracted data
        editText_profileName.setText(nameInstance);

        // Options for the floating action button
        options = new CharSequence[]{getString(R.string.alert_optSi), getString(R.string.alert_optNo)};
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

                                // Set new default profile
                                // Store the instance as default
                                SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("NAME", itemName);
                                editor.putString("ID", itemId);
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

            case R.id.Ingreso_editProfile:
                if (editText_profileName.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.toast_addprofileactivity_noname, Toast.LENGTH_LONG).show();
                } else {
                    Cursor instances = db.getInstancesAllData();
                    if (instances.getCount() > 0) {
                        String name = editText_profileName.getText().toString();
                        while (instances.moveToNext()) {
                            if (name.equals(instances.getString(1))) {
                                Toast.makeText(this, R.string.activity_add_profile_msg_samename, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    db.editInstance(idInstance, editText_profileName.getText().toString());

                    // Get new default profile
                    String itemName = editText_profileName.getText().toString();

                    // Set new default profile
                    // Store the instance as default
                    SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("NAME", itemName);
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
