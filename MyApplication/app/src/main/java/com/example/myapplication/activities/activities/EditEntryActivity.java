package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.CustomAdapter;
import com.example.myapplication.activities.data.CustomItems;
import com.example.myapplication.activities.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class EditEntryActivity extends AppCompatActivity {

    // Components view
    private EditText editText_profit;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_time;
    private TextView textView_instanceName;
    private Spinner spinner_categories;
    private Spinner spinner_profiles;
    private RadioGroup radioGroup_addMov;
    private RadioButton radioButton_profit;
    private RadioButton radioButton_spend;

    // Global variables
    private String id;
    private String ingreso;
    private String gasto;
    private String descr;
    private String fecha;
    private String hora;
    private String idCateg;
    private String icName;
    private DatabaseManager db;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private String category_id;
    private String id_inst;
    private String new_id_inst;
    private int spinner_DefaultPosition = 0;
    private ArrayAdapter<String> spinnerAdapterProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        // Database
        db = new DatabaseManager(this);

        // Get curent id instance
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        id_inst = prefs.getString("ID", null);

        // Initialize components view
        editText_profit = findViewById(R.id.etIngreso_editEntry);
        editText_description = findViewById(R.id.etdescripcion_editEntry);
        textView_date = findViewById(R.id.textView_editEntry_date);
        textView_time = findViewById(R.id.textView_editEntry_time);
        textView_instanceName = findViewById(R.id.textView_editEntry_instanceName);
        radioGroup_addMov = findViewById(R.id.radioGroup_addMov);
        radioButton_profit = findViewById(R.id.radioButton_activityEditMov_Ingreso);
        radioButton_spend = findViewById(R.id.radioButton_activityEditMov_Gasto);

        // Set Spinner category data
        spinner_categories = findViewById(R.id.spinner_editEntry_category);

        // Get categories
        Cursor categoriesData = db.getCategoryAllData();

        // Array List to store the categories names
        ArrayList<CustomItems> categoriesList = new ArrayList<>();

        // Add categories names to categoriesList
        while (categoriesData.moveToNext()) {
            String categName = categoriesData.getString(1);
            String categIcon = categoriesData.getString(2);
            int categIconId = getResources().getIdentifier(categIcon, "drawable", this.getPackageName());
            categoriesList.add(new CustomItems(categName, categIconId));
        }

        // Add option: "Agregar..." category
        categoriesList.add(new CustomItems(getString(R.string.activity_addEntry_addCategory_spinner), R.drawable.ic_addblack_64));
        // Create adapter for the spinner of categories
        CustomAdapter customAdapter = new CustomAdapter(this, categoriesList);
        spinner_categories.setAdapter(customAdapter);


        // Set spinner categories onClickListener
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String category_name = adapterView.getItemAtPosition(i).toString();
                CustomItems items = (CustomItems) adapterView.getSelectedItem();
                String category_name = items.getSpinnerText();

                if (category_name.equals(getString(R.string.activity_addEntry_addCategory_spinner))) {
                    // if category_name = Add...
                    openDialog();
                } else {
                    // else, get category id with the given name
                    category_id = db.getCategoryId(category_name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // Set spinner of profiles data
        spinner_profiles = findViewById(R.id.spinner_editEntry_profile);

        // Get profiles
        Cursor profilesData = db.getInstancesAllData();

        // Array List to store the categories names
        ArrayList<String> profilesList = new ArrayList<>();

        // Add profiles names to profilesList
        while (profilesData.moveToNext()) {
            profilesList.add(profilesData.getString(1));
        }

        // Create adapter for the spinner of profiles
        spinnerAdapterProfiles = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, profilesList);
        spinner_profiles.setAdapter(spinnerAdapterProfiles);

        // Set spinner profiles onClickListener
        spinner_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profile_name = adapterView.getItemAtPosition(i).toString();
                new_id_inst = db.getInstanceId(profile_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Set instance name as title
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Get intent extra info
        id = getIntent().getStringExtra("ID");
        ingreso = getIntent().getStringExtra("INGRESO");
        gasto = getIntent().getStringExtra("GASTO");
        descr = getIntent().getStringExtra("DESCR");
        fecha = date = getIntent().getStringExtra("FECHA");
        hora = time = getIntent().getStringExtra("HORA");
        idCateg = getIntent().getStringExtra("CATEG");

        // Set actual data on views and variables
        if (ingreso.equals("0")) {
            radioButton_spend.setChecked(true);
            editText_profit.setText(gasto);
        } else {
            radioButton_profit.setChecked(true);
            editText_profit.setText(ingreso);
        }
        editText_description.setText(descr);

        // Show date in format DD-MM-YY
        String year = fecha.substring(0, 4);
        String month = fecha.substring(5, 7);
        String day = fecha.substring(8, 10);
        String sepearator = "-";
        String dateToShow = day + sepearator + month + sepearator + year;
        textView_date.setText(dateToShow);
        // Show time in format HH:MM
        String timeToShow = hora.substring(0, 5);
        textView_time.setText(timeToShow);

        // Set actual category position in spinner
        String categName = db.getCategoryName(idCateg);
        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).getSpinnerText().equals(categName)) {
                spinner_DefaultPosition = i;
                break;
            }
        }
        spinner_categories.setSelection(spinner_DefaultPosition);

        // Set actual profile position in spinner
        String id_prefs = prefs.getString("ID", null);
        String profileName = db.getInstanceName(id_prefs);
        for (int i = 0; i < profilesList.size(); i++) {
            if (profilesList.get(i).equals(profileName)) {
                spinner_DefaultPosition = i;
                break;
            }
        }
        spinner_profiles.setSelection(spinner_DefaultPosition);

        strYear = fecha.substring(0, 4);
        strMonth = fecha.substring(5, 7);
        strDay = fecha.substring(8);
    }

    /**
     * Alert dialog to add category
     */
    private void openDialog() {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(EditEntryActivity.this);
        View subView = inflater.inflate(R.layout.dialog_add_category, null);

        // Initialize edit Text for category name
        final EditText editText_categoryName = subView.findViewById(R.id.dialogLayoutAddCategory_editText_categoryName);
        final ImageButton imageButton_addIcon = subView.findViewById(R.id.imageButton_addCategory_addIcon);
        final Context contextImgBttn = imageButton_addIcon.getContext();

        // Icon selector button click listener
        imageButton_addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogIcCategories = new Dialog(view.getContext());

                // Set custom layout to dialog help
                dialogIcCategories.setContentView(R.layout.dialog_add_icon);
                dialogIcCategories.setTitle(getString(R.string.dialogInfo_title_help));

                // Initialize imageViews of category icons
                final ImageView imageView_ic_amazon = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_amazon);
                final ImageView imageView_ic_bankbuilding = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_bankbuilding);
                final ImageView imageView_ic_barbershop = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_barbershop);
                final ImageView imageView_ic_beachball = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_beachball);
                final ImageView imageView_ic_bill = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_bill);
                final ImageView imageView_ic_book = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_book);
                final ImageView imageView_ic_borrowmonew = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_borrowmoney);
                final ImageView imageView_ic_box = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_box);
                final ImageView imageView_ic_cellphone = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_cellphone);
                final ImageView imageView_ic_deposit = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_deposit);
                final ImageView imageView_ic_documents = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_documents);
                final ImageView imageView_ic_dogbone = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_dogbone);
                final ImageView imageView_ic_donation = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_donation);
                final ImageView imageView_ic_dvdlogo = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_dvdlogo);
                final ImageView imageView_ic_email = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_email);
                final ImageView imageView_ic_entertainnent = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_entertainment);
                final ImageView imageView_ic_envelope = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_envelope);
                final ImageView imageView_ic_food = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_food);
                final ImageView imageView_ic_fuelstation = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_fuelstation);
                final ImageView imageView_ic_garage = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_garage);
                final ImageView imageView_ic_gift = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_gift);
                final ImageView imageView_ic_givenkey = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_givenkey);
                final ImageView imageView_ic_glassandfork = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_glassandfork);
                final ImageView imageView_ic_graduationhat = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_graduationhat);
                final ImageView imageView_ic_gym = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_gym);
                final ImageView imageView_ic_hand = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_hand);
                final ImageView imageView_ic_health = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_health);
                final ImageView imageView_ic_homephone = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_homephone);
                final ImageView imageView_ic_hotelfivestars = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_hotelfivestars);
                final ImageView imageView_ic_house = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_house);
                final ImageView imageView_ic_ingredients = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_ingredients);
                final ImageView imageView_ic_laundry = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_laundry);
                final ImageView imageView_ic_lightbulb = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_lightbulb);
                final ImageView imageView_ic_moneyflow = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_moneyflow);
                final ImageView imageView_ic_moneyquery = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_moneyquery);
                final ImageView imageView_ic_moneytransfer = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_moneytransfer);
                final ImageView imageView_ic_moviefilm = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_moviefilm);
                final ImageView imageView_ic_moving = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_moving);
                final ImageView imageView_ic_movingstock = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_movingstock);
                final ImageView imageView_ic_onecoin = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_onecoin);
                final ImageView imageView_ic_peoplegroup = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_peoplegroup);
                final ImageView imageView_ic_pigmoney = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_pigmoney);
                final ImageView imageView_ic_pizza = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_pizza);
                final ImageView imageView_ic_pokecoins = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_pokecoins);
                final ImageView imageView_ic_questionmark = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_questionmark);
                final ImageView imageView_ic_rentacar = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_rentacar);
                final ImageView imageView_ic_restaurantbuilding = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_restaurantbuilding);
                final ImageView imageView_ic_roster = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_roster);
                final ImageView imageView_ic_rubberstamp = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_rubberstamp);
                final ImageView imageView_ic_sale = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_sale);
                final ImageView imageView_ic_security = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_security);
                final ImageView imageView_ic_sell = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_sell);
                final ImageView imageView_ic_shirt = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_shirt);
                final ImageView imageView_ic_sleeping = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_sleeping);
                final ImageView imageView_ic_stapler = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_stapler);
                final ImageView imageView_ic_suv = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_suv);
                final ImageView imageView_ic_transport = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_transport);
                final ImageView imageView_ic_trash = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_trash);
                final ImageView imageView_ic_travelworld = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_travelworld);
                final ImageView imageView_ic_twoheart = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_twoheart);
                final ImageView imageView_ic_videogamecontroller = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_videogamecontroller);
                final ImageView imageView_ic_waterdrop = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_waterdrop);
                final ImageView imageView_ic_wifi = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_wifi);
                final ImageView imageView_ic_camera = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_camera);
                final ImageView imageView_ic_marihuana = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_marihuana);
                final ImageView imageView_ic_painthouse = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_painthouse);
                final ImageView imageView_ic_painting = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_painting);
                final ImageView imageView_ic_partyballoons = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_partyballoons);
                final ImageView imageView_ic_pcportatil = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_pcportatil);
                final ImageView imageView_ic_toolsmaint = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_toolsmaint);
                final ImageView imageView_ic_tv = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_tv);
                final ImageView imageView_ic_ac = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_ac);
                final ImageView imageView_ic_baby = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_baby);
                final ImageView imageView_ic_beer = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_beer);
                final ImageView imageView_ic_casino = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_casino);
                final ImageView imageView_ic_cigarette = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_cigarette);
                final ImageView imageView_ic_coupon = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_coupon);
                final ImageView imageView_ic_creditcard = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_creditcard);
                final ImageView imageView_ic_cycling = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_cycling);
                final ImageView imageView_ic_electronic = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_electronic);
                final ImageView imageView_ic_guns = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_guns);
                final ImageView imageView_ic_gunsstore = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_gunsstore);
                final ImageView imageView_ic_lottery = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_lottery);
                final ImageView imageView_ic_makeup = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_makeup);
                final ImageView imageView_ic_refund = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_refund);
                final ImageView imageView_ic_shoppingbag = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_shoppingbag);

                // image views click listener
                imageView_ic_amazon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_amazon_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_bankbuilding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_bankbuilding_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_barbershop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_barbershop_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_beachball.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_beachball_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_bill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_bill_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_book_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_borrowmonew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_borrowmoney_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_box_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_cellphone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_cellphone_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_deposit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_deposit_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_documents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_documents_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_dogbone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_dogbone_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_donation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_donation_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_dvdlogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_dvdlogo_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_email_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_entertainnent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_entertainment_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_envelope.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_envelope_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_food.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_food_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_fuelstation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_fuelstation_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_garage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_garage_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_gift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_gift_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_givenkey.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_givenkey_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_glassandfork.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_glassandfork_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_graduationhat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_graduationhat_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_gym.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_gym_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_hand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_hand_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_health.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_health_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_homephone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_homephone_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_hotelfivestars.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_hotelfivestars_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_house.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_house_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_ingredients.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_ingredients_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_laundry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_laundry_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_lightbulb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_lightbulb_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_moneyflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_moneyflow_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_moneyquery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_moneyquery_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_moneytransfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_moneytransfer_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_moviefilm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_moviefilm_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_moving.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_moving_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_movingstock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_movingstock_100";

                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_onecoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_onecoin_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_peoplegroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_peoplegroup_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_pigmoney.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_pigmoney_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_pizza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_pizza_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_pokecoins.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_pokecoins_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_questionmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_questionmark_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_rentacar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_rentacar_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_restaurantbuilding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_restaurantbuilding_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_roster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_roster_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_rubberstamp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_rubberstamp_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_sale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_sale_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_security.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_security_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_sell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_sell_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_shirt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_shirt_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_sleeping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_sleeping_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_stapler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_stapler_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_suv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_suv_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_transport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_transport_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_trash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_trash_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_travelworld.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_travelworld_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_twoheart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_twoheart_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_videogamecontroller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_videogamecontroller_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_waterdrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_waterdrop_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_wifi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_wifi_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_camera_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_marihuana.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_marihuana_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_painthouse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_painthouse_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_painting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_painting_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_partyballoons.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_partyballoons_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_pcportatil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_pcportatil_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_toolsmaint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_toolsmaint_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_tv_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_ac.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_ac_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_baby.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_baby_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_beer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_beer_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_casino.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_casino_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_cigarette.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_cigarette_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_coupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_coupon_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_creditcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_creditcard_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_cycling.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_cycling_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_electronic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_electronic_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_guns.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_guns_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_gunsstore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_gunsstore_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_lottery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_lottery_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_makeup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_makeup_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_refund.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_refund_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });
                imageView_ic_shoppingbag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        icName = "ic_shoppingbag_100";
                        int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                        imageButton_addIcon.setImageResource(imageID);
                        dialogIcCategories.dismiss();
                    }
                });

                dialogIcCategories.show();
            }
        });

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_title_addCategory));
        builder.setMessage(getString(R.string.alert_mssg_addCategory));
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Add category to data base
                if (!editText_categoryName.getText().toString().equals("")) {
                    // check if the category exist
                    Cursor cursorNames = db.getCategoryAllData();
                    boolean isExistCategory = false;
                    while (cursorNames.moveToNext()) {
                        if (cursorNames.getString(1).equals(editText_categoryName.getText().toString())) {
                            isExistCategory = true;
                            break;
                        }
                    }
                    if (isExistCategory) {
                        Toast.makeText(EditEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_existCategory), Toast.LENGTH_LONG).show();
                    } else {
                        db.addCategory(editText_categoryName.getText().toString(), icName);
                    }
                } else {
                    Toast.makeText(EditEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
                }
                // Get categories
                Cursor categoriesData = db.getCategoryAllData();

                // Array List to store the categories names
                //ArrayList<String> categoriesList = new ArrayList<>();
                ArrayList<CustomItems> categoriesList = new ArrayList<>();

                // Add categories names to categoriesList
                while (categoriesData.moveToNext()) {
                    String categName = categoriesData.getString(1);
                    String categIcon = categoriesData.getString(2);
                    int categIconId = getResources().getIdentifier(categIcon, "drawable", EditEntryActivity.this.getPackageName());
                    categoriesList.add(new CustomItems(categName, categIconId));
                }

                // Add option: "Agregar..." category
                //categoriesList.add(getString(R.string.activity_addEntry_addCategory_spinner));
                categoriesList.add(new CustomItems(getString(R.string.activity_addEntry_addCategory_spinner), R.drawable.ic_addblack_64));
                // Create adapter for the spinner of categories
                //spinnerAdapter = new ArrayAdapter<>(EditEntryActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                //spinner_categories.setAdapter(spinnerAdapter);
                CustomAdapter customAdapter = new CustomAdapter(EditEntryActivity.this, categoriesList);
                spinner_categories.setAdapter(customAdapter);

                spinner_categories.setSelection(categoriesList.size() - 2);
            }
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(EditEntryActivity.this, getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();

                spinner_categories.setSelection(spinner_DefaultPosition);
            }
        });

        builder.show();
    }

    public void onClickEditEntry(View view) {
        switch (view.getId()) {
            case R.id.fab_calendar_editMovActivity:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        strDay = Integer.toString(i2);
                        strMonth = Integer.toString(i1 + 1);
                        strYear = Integer.toString(i);

                        // Cast day with 1 digit to 2
                        switch (strDay) {
                            case "1":
                                strDay = "01";
                                break;
                            case "2":
                                strDay = "02";
                                break;
                            case "3":
                                strDay = "03";
                                break;
                            case "4":
                                strDay = "04";
                                break;
                            case "5":
                                strDay = "05";
                                break;
                            case "6":
                                strDay = "06";
                                break;
                            case "7":
                                strDay = "07";
                                break;
                            case "8":
                                strDay = "08";
                                break;
                            case "9":
                                strDay = "09";
                                break;
                            default:
                                break;
                        }

                        // Cast month with 1 digit to 2
                        switch (strMonth) {
                            case "1":
                                strMonth = "01";
                                break;
                            case "2":
                                strMonth = "02";
                                break;
                            case "3":
                                strMonth = "03";
                                break;
                            case "4":
                                strMonth = "04";
                                break;
                            case "5":
                                strMonth = "05";
                                break;
                            case "6":
                                strMonth = "06";
                                break;
                            case "7":
                                strMonth = "07";
                                break;
                            case "8":
                                strMonth = "08";
                                break;
                            case "9":
                                strMonth = "09";
                                break;
                            default:
                                break;
                        }

                        // Show in format DD-MM-YYYY
                        textView_date.setText(new StringBuilder().append(strDay).append("-").append(strMonth).append("-").append(strYear).toString());
                        // Store in format YYYY-MM-DD
                        date = new StringBuilder().append(strYear).append("-").append(strMonth).append("-").append(strDay).toString();
                    }
                }, yearPick, monthPick, dayPick);
                datePickerDialog.show();

                break;

            case R.id.fab_clock_editMovActivity:
                Calendar timepick = Calendar.getInstance();
                int hourPick = timepick.get(Calendar.HOUR_OF_DAY);
                int minutesPick = timepick.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        strHour = Integer.toString(i);
                        strMinute = Integer.toString(i1);

                        // Cast hour with 1 digit to 2
                        switch (strHour) {
                            case "1":
                                strHour = "01";
                                break;
                            case "2":
                                strHour = "02";
                                break;
                            case "3":
                                strHour = "03";
                                break;
                            case "4":
                                strHour = "04";
                                break;
                            case "5":
                                strHour = "05";
                                break;
                            case "6":
                                strHour = "06";
                                break;
                            case "7":
                                strHour = "07";
                                break;
                            case "8":
                                strHour = "08";
                                break;
                            case "9":
                                strHour = "09";
                                break;
                            default:
                                break;
                        }

                        // Cast minute with 1 digit to 2
                        switch (strMinute) {
                            case "1":
                                strMinute = "01";
                                break;
                            case "2":
                                strMinute = "02";
                                break;
                            case "3":
                                strMinute = "03";
                                break;
                            case "4":
                                strMinute = "04";
                                break;
                            case "5":
                                strMinute = "05";
                                break;
                            case "6":
                                strMinute = "06";
                                break;
                            case "7":
                                strMinute = "07";
                                break;
                            case "8":
                                strMinute = "08";
                                break;
                            case "9":
                                strMinute = "09";
                                break;
                            default:
                                break;
                        }

                        textView_time.setText(new StringBuilder().append(strHour).append(":").append(strMinute).toString());
                        time = new StringBuilder().append(strHour).append(":").append(strMinute).append(":00.000").toString();
                    }
                }, hourPick, minutesPick, false);
                timePickerDialog.show();

                break;

            case R.id.Ingreso_editMov:
                String montoStr = editText_profit.getText().toString();
                long ingresoInt;
                long gastoInt;

                // Set 0 to blank spaces
                if (radioButton_spend.isChecked()) {
                    ingresoInt = 0;
                    gastoInt = Long.parseLong(montoStr);
                } else {
                    ingresoInt = Long.parseLong(montoStr);
                    gastoInt = 0;
                }

                if ((editText_profit.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                if (editText_description.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                String descripcion = editText_description.getText().toString();

                boolean isResultadd = db.editEntryData(id, date, time, ingresoInt,
                        gastoInt, descripcion, id_inst, new_id_inst, category_id);

                if (isResultadd) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }

                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);

                break;

            case R.id.cancel_editMov:
                onBackPressed();

                break;
        }
    }

    public void showMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }

}
