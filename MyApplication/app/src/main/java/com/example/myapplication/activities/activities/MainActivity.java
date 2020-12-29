package com.example.myapplication.activities.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.fragments.BalanceCustomFragment;
import com.example.myapplication.activities.fragments.BalanceFragment;
import com.example.myapplication.activities.fragments.BalancePeriodFragment;
import com.example.myapplication.activities.fragments.CategoriesFragment;
import com.example.myapplication.activities.fragments.DebtsFragment;
import com.example.myapplication.activities.fragments.ReportCategFragment;
import com.example.myapplication.activities.fragments.SaveMoneyFragment;
import com.example.myapplication.activities.fragments.SearchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Components view
    private FloatingActionButton fab_addProfile;
    private FloatingActionButton fab_confProfile;
    //private TextView textView_webLink;
    //private ImageButton imageButton_subscribe;
    public static FloatingActionButton fab_addEntry;

    // Variables
    public static String idInstance;
    public static boolean isFirstStart;
    public static boolean isEulaAccepted;
    private Fragment fragment = null;
    private NavigationView navigationView;

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
        fab_addEntry = findViewById(R.id.fab);

        // Textview web link
        //textView_webLink = findViewById(R.id.textView_activityMain_webLink);
        /*textView_webLink.setOnClickListener(v -> {
            Intent intentURLWeb = new Intent(Intent.ACTION_VIEW);
            intentURLWeb.setData(Uri.parse("https://nuevaescueladigital.com"));
            startActivity(intentURLWeb);
        });*/

        // ImageButton subscribe link
        //imageButton_subscribe = findViewById(R.id.imageButton_activityMain_subscribe);
        /*imageButton_subscribe.setOnClickListener(v -> {
            Intent intentURLSubscribe = new Intent(Intent.ACTION_VIEW);
            intentURLSubscribe.setData(Uri.parse("https://forms.gle/HMgg8fP6njJYVBCd9"));
            startActivity(intentURLSubscribe);
        });*/

        // Floating Button action
        fab_addEntry.setOnClickListener(view -> {
            // Add entry
            Intent addEntryIntent = new Intent(view.getContext(), AddEntryActivity.class);
            startActivity(addEntryIntent);
        });

        // Initialize Navigation drawer view components
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Instance of the Database Manager
        db = new DatabaseManager(this);

        // Get shared preferences of instance
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);

        // Shared preferences of main configuration
        SharedPreferences prefsConfig = getSharedPreferences("config", Context.MODE_PRIVATE);
        isFirstStart = prefsConfig.getBoolean("FIRSTSTART", true);
        isEulaAccepted = prefsConfig.getBoolean("EULAAccepted", false);

        // Start welcome screen if is the first run of the app
        if (isFirstStart) {
            if (!isEulaAccepted) {
                Intent EulaIntent = new Intent(this, EulaActivity.class);
                startActivity(EulaIntent);
            } else {
                isFirstStart = false;
                SharedPreferences.Editor editorConfig = prefsConfig.edit();
                editorConfig.putBoolean("FIRSTSTART", isFirstStart);
                editorConfig.apply();

                Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
                startActivity(welcomeIntent);
            }
        } else {
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
                Spinner spinner_instances = navHeader.findViewById(R.id.spinner_navHeader_profiles);
                fab_addProfile = navHeader.findViewById(R.id.fab_navHeader_addProfile);
                fab_confProfile = navHeader.findViewById(R.id.fab_navHeader_confProfile);

                // Add profiles names to profilesList
                while (instancesData.moveToNext()) {
                    profilesList.add(instancesData.getString(1));
                }

                // Create adapter for the spinner of profiles
                ArrayAdapter<String> spinnerAdapter;
                spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_items_theme, profilesList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);

                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(20);
                        textView.setGravity(Gravity.CENTER);

                        return textView;
                    }
                };
                spinner_instances.setAdapter(spinnerAdapter);

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

                        // Check if there is defined period to enable nav item period balance
                        Menu menuNav = navigationView.getMenu();
                        MenuItem navItemPeriodBalance = menuNav.findItem(R.id.nav_period_balance);
                        // Set state for period option
                        navItemPeriodBalance.setEnabled(!period.equals("0"));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                // Set clickListener for the add and edit profile buttons
                fab_addProfile.setOnClickListener(view -> {
                    Intent addProfileIntent = new Intent(view.getContext(), AddProfileActivity.class);
                    startActivity(addProfileIntent);
                });

                fab_confProfile.setOnClickListener(view -> {
                    Intent editProfileIntent = new Intent(view.getContext(), EditProfileActivity.class);
                    startActivity(editProfileIntent);
                });

                // Get and show the default fragment
                BalanceFragment balanceFragment = new BalanceFragment();
                fragment = balanceFragment;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, balanceFragment).commit();

                // Check if there is defined period to enable nav item period balance
                Menu menuNav = navigationView.getMenu();
                MenuItem navItemPeriodBalance = menuNav.findItem(R.id.nav_period_balance);
                // Set state for period option
                String period = prefs.getString("PERIOD", null);
                assert period != null;
                navItemPeriodBalance.setEnabled(!period.equals("0"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragment != null) {
                if (fragment instanceof BalanceFragment) {
                    Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.snack_mainActivity_exit), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snack_mainActivity_exit_button), v -> onExit())
                            .show();
                } else {
                    showHomeFragment();
                }
            } else {
                onExit();
            }
        }
    }

    /**
     * Show Balance fragment
     */
    private void showHomeFragment() {
        fragment = new BalanceFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new BalanceFragment()).commit();
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            // Help screen
            Intent helpIntent = new Intent(this, HelpActivity.class);
            startActivity(helpIntent);
        } else if (id == R.id.action_about) {
            // Constant
            final Dialog dialogAbout = new Dialog(MainActivity.this);
            // Set custom layout to dialog help
            dialogAbout.setContentView(R.layout.dialog_about);
            dialogAbout.show();

            /*ImageButton imageButton_facebook = dialogAbout.findViewById(R.id.imageButton_dialogAbout_facebook);
            ImageButton imageButton_youtube = dialogAbout.findViewById(R.id.imageButton_dialogAbout_youtube);
            ImageButton imageButton_instagram = dialogAbout.findViewById(R.id.imageButton_dialogAbout_instagram);

            imageButton_facebook.setOnClickListener(v -> {
                Intent intentURLFacebook = new Intent(Intent.ACTION_VIEW);
                intentURLFacebook.setData(Uri.parse("https://www.facebook.com/Nueva-Escuela-Digital-109624583896807/"));
                startActivity(intentURLFacebook);
            });
            imageButton_youtube.setOnClickListener(v -> {
                Intent intentURLYoutube = new Intent(Intent.ACTION_VIEW);
                intentURLYoutube.setData(Uri.parse("https://www.youtube.com/channel/UCjRK0Z3920z0GSSuIRBE8JA/?guided_help_flow=5"));
                startActivity(intentURLYoutube);
            });
            imageButton_instagram.setOnClickListener(v -> {
                Intent intentURLInstagram = new Intent(Intent.ACTION_VIEW);
                intentURLInstagram.setData(Uri.parse("http://instagram.com/ned_nuevaescueladigital/"));
                startActivity(intentURLInstagram);
            });*/

        } else if (id == R.id.action_contact) {
            // Constant
            final Dialog dialogContact = new Dialog(MainActivity.this);
            // Set custom layout to dialog help
            dialogContact.setContentView(R.layout.dialog_contact);
            dialogContact.show();

            ImageButton imageButton_WA = dialogContact.findViewById(R.id.imageButton_dialogContact_wa);

            imageButton_WA.setOnClickListener(v -> {
                Intent intentURLWA = new Intent(Intent.ACTION_VIEW);
                intentURLWA.setData(Uri.parse("https://api.whatsapp.com/send?phone=50663139143"));
                startActivity(intentURLWA);
            });
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_month_balance) {
            fragment = new BalanceFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new BalanceFragment()).commit();
        } else if (id == R.id.nav_explore) {
            fragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new SearchFragment()).commit();
        } else if (id == R.id.nav_money_save) {
            fragment = new SaveMoneyFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new SaveMoneyFragment()).commit();
        } else if (id == R.id.nav_period_balance) {
            fragment = new BalancePeriodFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new BalancePeriodFragment()).commit();
        } else if (id == R.id.nav_custom_balance) {
            fragment = new BalanceCustomFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new BalanceCustomFragment()).commit();
        } else if (id == R.id.nav_categories) {
            fragment = new CategoriesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new CategoriesFragment()).commit();
        } else if (id == R.id.nav_report_categ) {
            fragment = new CategoriesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new ReportCategFragment()).commit();
        } else if (id == R.id.nav_debt) {
            fragment = new DebtsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, new DebtsFragment()).commit();
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
}
