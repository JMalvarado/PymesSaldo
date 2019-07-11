package com.example.myapplication.activities.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.fragments.BalanceFragment;
import com.example.myapplication.activities.fragments.SaveMoneyFragment;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

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
        final NavigationView navigationView = findViewById(R.id.nav_view);
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
            // Add default categories
            db.addCategory(getString(R.string.mainActivity_addCategory_others));

            Intent intentAddProfile = new Intent(this, AddProfileActivity.class);
            intentAddProfile.putExtra("IS_NEW_USER", true);
            startActivity(intentAddProfile);
        } else {
            /* Verify the actual month, if it change, add the previous positive
               balance as a profit in the actual month */

            // Get the current year and month
            Calendar calendar = Calendar.getInstance();
            String currentMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
            String currentYear = Integer.toString(calendar.get(Calendar.YEAR));

            // Get the shared preferences month and year
            SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
            String spMonth = prefs.getString("LASTMONTH", null);
            String spYear = prefs.getString("LASTYEAR", null);

            // Verify not null data
            if ((spMonth != null) && (spYear != null)) {
                // Compare month and year
                if ((!spMonth.equals(currentMonth)) || (!spYear.equals(currentYear))) {
                    // Verify if the balance is positive
                    String balance = getInMonthYearBalance(spMonth, spYear);
                    long intBalance = Long.parseLong(balance);
                    if (intBalance > 0) {
                        // Get current date
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
                        String dateNow;
                        LocalDateTime now = LocalDateTime.now();
                        dateNow = dtf.format(now);

                        // Get current time
                        String timeNow = java.time.LocalTime.now().toString();

                        // Get id instance
                        String id = prefs.getString("ID", null);

                        String newEntryDescription = getString(R.string.mainActivity_addEntry_lastBalance) + spMonth + "-" + spYear;
                        db.addEntry(dateNow, timeNow, 0, intBalance, newEntryDescription, id, "1");
                    }
                }
            }


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
            prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
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
                    String period = Integer.toString(db.getInstancePeriod(name));

                    // Store the instance as default
                    SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("NAME", name);
                    editor.putString("ID", id);
                    editor.putString("PERIOD", period);
                    editor.apply();

                    idInstance = id;

                    BalanceFragment balanceFragment = new BalanceFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, balanceFragment).commit();

                    navigationView.getMenu().getItem(1).setChecked(true);

                    // Set title head text
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

            // Get and show the default fragment
            BalanceFragment balanceFragment = new BalanceFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, balanceFragment).commit();
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
                            onExit();
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
        int id = item.getItemId();

        if (id == R.id.nav_month_balance) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new BalanceFragment()).commit();

        } else if (id == R.id.nav_explore) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new SearchFragment()).commit();

        } else if (id == R.id.nav_exit) {
            onExit();
        } else if (id == R.id.nav_money_save) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new SaveMoneyFragment()).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Exit operation
     */
    private void onExit() {
        // Get the current year and month before exit
        Calendar calendar = Calendar.getInstance();
        String lastMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String lastYear = Integer.toString(calendar.get(Calendar.YEAR));

        // Store the currrent year and month
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LASTMONTH", lastMonth);
        editor.putString("LASTYEAR", lastYear);
        editor.apply();

        Intent intentSalir = new Intent(Intent.ACTION_MAIN);
        intentSalir.addCategory(Intent.CATEGORY_HOME);
        intentSalir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentSalir);
    }

    /**
     * Get integer with the total profit of the given month and year
     *
     * @return total profit
     */
    private long getTotalProfit(String month, String year) {
        ArrayList<Long> profit = db.getEntryInMonthYearIngresos(idInstance, month, year);

        long totalProfit = 0;

        for (Long integer1 : profit) {
            long ing;
            ing = integer1;
            totalProfit += ing;
        }

        return totalProfit;
    }

    /**
     * Get integer with the total spend of the given month and year
     *
     * @return spend
     */
    private long getTotalSpend(String month, String year) {
        ArrayList<Long> spend = db.getEntryInMonthYearGastos(idInstance, month, year);

        long totalSpend = 0;

        for (Long integer1 : spend) {
            long gas;
            gas = integer1;
            totalSpend += gas;
        }

        return totalSpend;
    }

    /**
     * Get String with the balance (totalProfit - totalSpend) of the given month and year
     *
     * @return balance
     */
    private String getInMonthYearBalance(String month, String year) {
        long totalProfit = getTotalProfit(month, year);
        long totalSpend = getTotalSpend(month, year);

        long balance = totalProfit - totalSpend;

        return Long.toString(balance);
    }
}
