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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
            // Instantiate text views for the header of the drawer
            TextView tv_navheader_title = navHeader.findViewById(R.id.textview_navheadermain_title);

            // Set text title and id with shared preference
            SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
            String name = prefs.getString("NAME", null);
            idInstance = prefs.getString("ID", null);
            tv_navheader_title.setText(name);
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

            Iterator<Integer> profitIt = profit.iterator();
            while (profitIt.hasNext()) {
                int ing;
                ing = profitIt.next();
                totalProfit += ing;
            }

            Iterator<Integer> spendIt = spend.iterator();
            while (spendIt.hasNext()) {
                int gas;
                gas = spendIt.next();
                totalSpend += gas;
            }

            int balance = totalProfit - totalSpend;
            String strBalance = Integer.toString(balance);

            Bundle balanceBundle = new Bundle();
            balanceBundle.putString("BALANCE", strBalance);
            BalanceFragment balanceFragment = new BalanceFragment();
            balanceFragment.setArguments(balanceBundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, balanceFragment).commit();

        } else if (id == R.id.nav_change_profile) {

        } else if (id == R.id.nav_add_profile) {
            Intent addProfileIntent = new Intent(this, AddProfileActivity.class);
            startActivity(addProfileIntent);

        } else if (id == R.id.nav_explore) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new SearchFragment()).commit();

        } else if (id == R.id.nav_exit) {
            Intent intentSalir = new Intent(Intent.ACTION_MAIN);
            intentSalir.addCategory(Intent.CATEGORY_HOME);
            intentSalir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentSalir);

        } else if (id == R.id.nav_conf_profile) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
