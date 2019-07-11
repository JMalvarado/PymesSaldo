package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

public class AddProfileActivity extends AppCompatActivity {

    // View components
    EditText et_name;

    // Database instance
    DatabaseManager db;

    // Intent extras
    boolean isNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        // Initialize views
        et_name = findViewById(R.id.edittext_addprofile_name);

        // Initialize db manager instance
        db = new DatabaseManager(this);

        // Get intent extras
        isNewUser = getIntent().getBooleanExtra("IS_NEW_USER", false);
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
     * @param view
     */
    public void onClickAddProfile(View view) {
        switch (view.getId()) {
            case R.id.Ingreso_addProfileActivity:
                if (et_name.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.toast_addprofileactivity_noname, Toast.LENGTH_LONG).show();
                } else {
                    Cursor instances = db.getInstancesAllData();
                    if (instances.getCount() > 0) {
                        String name = et_name.getText().toString();
                        while (instances.moveToNext()) {
                            if (name.equals(instances.getString(1))) {
                                Toast.makeText(this, R.string.activity_add_profile_msg_samename, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    // Add instance to the table Instancias
                    String name = et_name.getText().toString();
                    db.addInstance(name);
                    // Id
                    String id = "";
                    Cursor cursor = db.getInstancesAllData();
                    while (cursor.moveToNext()) {
                        id = cursor.getString(0);
                    }

                    // Store the instance as default
                    SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("NAME", name);
                    editor.putString("ID", id);
                    editor.apply();

                    et_name.setText("");

                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    startActivity(mainActivityIntent);
                }

                break;

            case R.id.cancel_addProfileActivity:
                onBackPressed();

                break;
        }
    }
}
