package com.example.myapplication.activities.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.fragments.BalanceFragment;
import com.example.myapplication.activities.fragments.SearchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Components view
    private ImageButton imageButton_addProfile;
    private ImageButton imageButton_confProfile;

    // Variables
    public static String idInstance;

    // Database instance
    public DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize toolbar view components
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize floating button view
        FloatingActionButton fab = findViewById(R.id.fab);

        // Floating Button action
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add entry
                Intent addEntryIntent = new Intent(view.getContext(), AddEntryActivity.class);
                startActivity(addEntryIntent);
            }
        });

        // Initialize Navigation drawer view components
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Instance of the Database Manager
        db = new DatabaseManager(this);

        /* Make an initial query to verify if there is any instance created.
           If not, show the activity to create a profile. */
        Cursor instancesData = db.getInstancesAllData();
        if (instancesData.getCount() == 0) {
            Intent intentAddProfile = new Intent(this, AddProfileActivity.class);
            intentAddProfile.putExtra("IS_NEW_USER", true);
            startActivity(intentAddProfile);
        } else {
            // Array List to store the profiles names
            ArrayList<String> profilesList = new ArrayList<>();

            // Instantiate text view, spinner and buttons for the header of the drawer
            final TextView tv_navheader_title = navHeader.findViewById(R.id.textview_navheadermain_title);
            Spinner spinner_instances = navHeader.findViewById(R.id.spinner_navHeader_profiles);
            imageButton_addProfile = navHeader.findViewById(R.id.imageButton_navHeader_addProfile);
            imageButton_confProfile = navHeader.findViewById(R.id.imageButton_navHeader_confProfile);

            // Add profiles names to profilesList
            while (instancesData.moveToNext()) {
                profilesList.add(instancesData.getString(1));
            }

            // Create adapter for the spinner of profiles
            ArrayAdapter<String> spinnerAdapter;
            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, profilesList);
            spinner_instances.setAdapter(spinnerAdapter);

            // Set text title and id with shared preference
            SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
            String name = prefs.getString("NAME", null);
            idInstance = prefs.getString("ID", null);
            tv_navheader_title.setText(name);

            int spinner_DefaultPosition = 0;
            for (int i = 0; i < profilesList.size(); i++) {
                if (profilesList.get(i).equals(name)) {
                    spinner_DefaultPosition = i;
                    break;
                }
            }
            spinner_instances.setSelection(spinner_DefaultPosition);

            // Set spinner itemClickListener
            spinner_instances.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = adapterView.getItemAtPosition(i).toString();
                    String id = db.getInstanceId(name);

                    // Store the instance as default
                    SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("NAME", name);
                    editor.putString("ID", id);
                    editor.apply();

                    idInstance = id;

                    tv_navheader_title.setText(name);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            // Set clickListener for the add and edit profile buttons
            imageButton_addProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addProfileIntent = new Intent(view.getContext(), AddProfileActivity.class);
                    startActivity(addProfileIntent);
                }
            });

            imageButton_confProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent editProfileIntent = new Intent(view.getContext(), EditProfileActivity.class);
                    startActivity(editProfileIntent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.snack_mainActivity_exit), Snackbar.LENGTH_LONG)
                    .setAction("Salir", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentSalir = new Intent(Intent.ACTION_MAIN);
                            intentSalir.addCategory(Intent.CATEGORY_HOME);
                            intentSalir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentSalir);
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_month_balance) {
            ArrayList<Integer> profit = db.getEntryCurrentMonthIngresos(idInstance);
            ArrayList<Integer> spend = db.getEntryCurrentMonthGastos(idInstance);

            int totalProfit = 0;
            int totalSpend = 0;

            for (Integer integer1 : profit) {
                int ing;
                ing = integer1;
                totalProfit += ing;
            }

            for (Integer integer : spend) {
                int gas;
                gas = integer;
                totalSpend += gas;
            }

            int balance = totalProfit - totalSpend;
            String strBalance = Integer.toString(balance);

            Bundle balanceBundle = new Bundle();
            balanceBundle.putString("BALANCE", strBalance);
            BalanceFragment balanceFragment = new BalanceFragment();
            balanceFragment.setArguments(balanceBundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, balanceFragment).commit();

        } else if (id == R.id.nav_explore) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new SearchFragment()).commit();

        } else if (id == R.id.nav_exit) {
            Intent intentSalir = new Intent(Intent.ACTION_MAIN);
            intentSalir.addCategory(Intent.CATEGORY_HOME);
            intentSalir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentSalir);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
