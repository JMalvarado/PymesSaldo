package com.example.myapplication.activities.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.CustomAdapter;
import com.example.myapplication.activities.data.CustomItems;
import com.example.myapplication.activities.data.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AddMovFragment extends Fragment implements View.OnClickListener {

    // View components
    private EditText editText_profit;
    private EditText editText_description;
    private TextView textView_date;
    private TextView textView_time;
    private RadioGroup radioGroup_addMov;
    private RadioButton radioButton_in;
    private RadioButton radioButton_spend;
    private FloatingActionButton fab_in;
    private FloatingActionButton fab_cancel;
    private FloatingActionButton fab_addDate;
    private FloatingActionButton fab_addTime;
    private Spinner spinner_categories;

    // Global variables
    private DateTimeFormatter dtf;
    private String strDay, strMonth, strYear, strHour, strMinute;
    private String date;
    private String time;
    private String category_id;
    private String icName;

    // Database instance
    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_mov, container, false);

        // Initialize components
        fab_in = view.findViewById(R.id.Ingreso_addMov);
        fab_in.setOnClickListener(this);
        fab_cancel = view.findViewById(R.id.cancel_addMov);
        fab_cancel.setOnClickListener(this);
        editText_profit = view.findViewById(R.id.etIngreso);
        editText_description = view.findViewById(R.id.etdescripcion);
        textView_date = view.findViewById(R.id.textView_addEntry_date);
        textView_time = view.findViewById(R.id.textView_addEntry_time);
        fab_addDate = view.findViewById(R.id.fab_calendar_addMovFragment);
        fab_addDate.setOnClickListener(this);
        fab_addTime = view.findViewById(R.id.fab_clock_addMovFragment);
        fab_addTime.setOnClickListener(this);
        radioGroup_addMov = view.findViewById(R.id.radioGroup_addMov);
        radioButton_in = view.findViewById(R.id.radioButton_fragmentAddMov_Ingreso);
        radioButton_in.setOnClickListener(this);
        radioButton_spend = view.findViewById(R.id.radioButton_fragmentAddMov_Gasto);
        radioButton_spend.setOnClickListener(this);

        // Database instance
        db = new DatabaseManager(view.getContext());

        // Set date format
        dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        // Set date now in the textView
        String dateNow;
        LocalDateTime now = LocalDateTime.now();
        dateNow = dtf.format(now);
        // Store in format YY-MM-DD
        date = dateNow;
        // Show date in format DD-MM-YY
        String dateToShow;
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String sepearator = "-";
        dateToShow = day + sepearator + month + sepearator + year;
        textView_date.setText(dateToShow);

        // Store time in format HH:MM:SS.SSSS
        time = java.time.LocalTime.now().toString();
        String timeToShow = time.substring(0, 5);
        // Show time in format HH:MM
        textView_time.setText(timeToShow);

        // Set Spinner category data
        spinner_categories = view.findViewById(R.id.spinner_addEntry_category);

        // Get categories
        Cursor categoriesData = db.getCategoriesByInstance(MainActivity.idInstance);

        // Array List to store the categories
        ArrayList<CustomItems> categoriesList = new ArrayList<>();

        // Add categories names to categoriesList
        while (categoriesData.moveToNext()) {
            String categName = categoriesData.getString(1);
            String categIcon = categoriesData.getString(2);
            int categIconId = getResources().getIdentifier(categIcon, "drawable", view.getContext().getPackageName());
            categoriesList.add(new CustomItems(categName, categIconId));
        }
        categoriesData.moveToPosition(-1);

        // Add option: "Agregar..." category
        categoriesList.add(new CustomItems(getString(R.string.activity_addEntry_addCategory_spinner), R.drawable.ic_addblack_64));

        // Create adapter for the spinner of categories
        CustomAdapter customAdapter = new CustomAdapter(view.getContext(), categoriesList);
        spinner_categories.setAdapter(customAdapter);

        // Set default position
        spinner_categories.setSelection(0);

        // Set spinner categories onClickListener
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomItems items = (CustomItems) adapterView.getSelectedItem();
                String category_name = items.getSpinnerText();

                // Get categories data from db
                Cursor result = db.getCategoriesByInstance(MainActivity.idInstance);
                if (category_name.equals(getString(R.string.activity_addEntry_addCategory_spinner))) {
                    // Check categories count limit
                    if (result.getCount() < 25) {
                        // if category_name = Add...
                        openDialog();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_limit), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // else, get category id with the given name
                    category_id = db.getCategoryId(category_name, MainActivity.idInstance);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Set ingreso-gasto radio buttons default checked
        radioButton_spend.setChecked(true);

        return view;
    }

    /**
     * Alert dialog to add category
     */
    private void openDialog() {
        // Set icon name empty
        icName = "";

        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_add_category, null);

        // Initialize edit Text for category name
        final EditText editText_categoryName = subView.findViewById(R.id.dialogLayoutAddCategory_editText_categoryName);
        final ImageButton imageButton_addIcon = subView.findViewById(R.id.imageButton_addCategory_addIcon);
        final Context contextImgBttn = imageButton_addIcon.getContext();

        // Icon selector button click listener
        imageButton_addIcon.setOnClickListener(view -> {
            final Dialog dialogIcCategories = new Dialog(Objects.requireNonNull(getContext()));

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
            final ImageView imageView_ic_battery = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_battery);
            final ImageView imageView_ic_bicycle = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_bicycle);
            final ImageView imageView_ic_cloud = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_cloud);
            final ImageView imageView_ic_family = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_family);
            final ImageView imageView_ic_gas = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_gas);
            final ImageView imageView_ic_insurance = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_insurance);
            final ImageView imageView_ic_internet = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_internet);
            final ImageView imageView_ic_jewelry = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_jewelry);
            final ImageView imageView_ic_motorcycle = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_motorcycle);
            final ImageView imageView_ic_multimedia = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_multimedia);
            final ImageView imageView_ic_network = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_network);
            final ImageView imageView_ic_photocamera = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_photocamera);
            final ImageView imageView_ic_plug = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_plug);
            final ImageView imageView_ic_recycle = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_recycle);
            final ImageView imageView_ic_wifilogo = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_wifilogo);

            // image views click listener
            imageView_ic_amazon.setOnClickListener(view1 -> {
                icName = "ic_amazon_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_bankbuilding.setOnClickListener(view12 -> {
                icName = "ic_bankbuilding_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_barbershop.setOnClickListener(view13 -> {
                icName = "ic_barbershop_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_beachball.setOnClickListener(view14 -> {
                icName = "ic_beachball_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_bill.setOnClickListener(view15 -> {
                icName = "ic_bill_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_book.setOnClickListener(view16 -> {
                icName = "ic_book_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_borrowmonew.setOnClickListener(view17 -> {
                icName = "ic_borrowmoney_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_box.setOnClickListener(view18 -> {
                icName = "ic_box_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cellphone.setOnClickListener(view19 -> {
                icName = "ic_cellphone_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_deposit.setOnClickListener(view110 -> {
                icName = "ic_deposit_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_documents.setOnClickListener(view111 -> {
                icName = "ic_documents_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_dogbone.setOnClickListener(view112 -> {
                icName = "ic_dogbone_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_donation.setOnClickListener(view113 -> {
                icName = "ic_donation_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_dvdlogo.setOnClickListener(view114 -> {
                icName = "ic_dvdlogo_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_email.setOnClickListener(view115 -> {
                icName = "ic_email_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_entertainnent.setOnClickListener(view1101 -> {
                icName = "ic_entertainment_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_envelope.setOnClickListener(view1100 -> {
                icName = "ic_envelope_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_food.setOnClickListener(view199 -> {
                icName = "ic_food_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_fuelstation.setOnClickListener(view198 -> {
                icName = "ic_fuelstation_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_garage.setOnClickListener(view197 -> {
                icName = "ic_garage_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gift.setOnClickListener(view196 -> {
                icName = "ic_gift_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_givenkey.setOnClickListener(view195 -> {
                icName = "ic_givenkey_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_glassandfork.setOnClickListener(view194 -> {
                icName = "ic_glassandfork_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_graduationhat.setOnClickListener(view193 -> {
                icName = "ic_graduationhat_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gym.setOnClickListener(view192 -> {
                icName = "ic_gym_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_hand.setOnClickListener(view191 -> {
                icName = "ic_hand_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_health.setOnClickListener(view190 -> {
                icName = "ic_health_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_homephone.setOnClickListener(view189 -> {
                icName = "ic_homephone_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_hotelfivestars.setOnClickListener(view188 -> {
                icName = "ic_hotelfivestars_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_house.setOnClickListener(view187 -> {
                icName = "ic_house_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_ingredients.setOnClickListener(view186 -> {
                icName = "ic_ingredients_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_laundry.setOnClickListener(view185 -> {
                icName = "ic_laundry_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_lightbulb.setOnClickListener(view184 -> {
                icName = "ic_lightbulb_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moneyflow.setOnClickListener(view183 -> {
                icName = "ic_moneyflow_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moneyquery.setOnClickListener(view182 -> {
                icName = "ic_moneyquery_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moneytransfer.setOnClickListener(view181 -> {
                icName = "ic_moneytransfer_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moviefilm.setOnClickListener(view180 -> {
                icName = "ic_moviefilm_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moving.setOnClickListener(view179 -> {
                icName = "ic_moving_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_movingstock.setOnClickListener(view178 -> {
                icName = "ic_movingstock_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_onecoin.setOnClickListener(view177 -> {
                icName = "ic_onecoin_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_peoplegroup.setOnClickListener(view176 -> {
                icName = "ic_peoplegroup_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pigmoney.setOnClickListener(view175 -> {
                icName = "ic_pigmoney_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pizza.setOnClickListener(view174 -> {
                icName = "ic_pizza_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pokecoins.setOnClickListener(view173 -> {
                icName = "ic_pokecoins_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_questionmark.setOnClickListener(view172 -> {
                icName = "ic_questionmark_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_rentacar.setOnClickListener(view171 -> {
                icName = "ic_rentacar_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_restaurantbuilding.setOnClickListener(view170 -> {
                icName = "ic_restaurantbuilding_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_roster.setOnClickListener(view169 -> {
                icName = "ic_roster_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_rubberstamp.setOnClickListener(view168 -> {
                icName = "ic_rubberstamp_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_sale.setOnClickListener(view167 -> {
                icName = "ic_sale_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_security.setOnClickListener(view166 -> {
                icName = "ic_security_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_sell.setOnClickListener(view165 -> {
                icName = "ic_sell_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_shirt.setOnClickListener(view164 -> {
                icName = "ic_shirt_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_sleeping.setOnClickListener(view163 -> {
                icName = "ic_sleeping_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_stapler.setOnClickListener(view162 -> {
                icName = "ic_stapler_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_suv.setOnClickListener(view161 -> {
                icName = "ic_suv_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_transport.setOnClickListener(view160 -> {
                icName = "ic_transport_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_trash.setOnClickListener(view159 -> {
                icName = "ic_trash_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_travelworld.setOnClickListener(view158 -> {
                icName = "ic_travelworld_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_twoheart.setOnClickListener(view157 -> {
                icName = "ic_twoheart_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_videogamecontroller.setOnClickListener(view156 -> {
                icName = "ic_videogamecontroller_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_waterdrop.setOnClickListener(view155 -> {
                icName = "ic_waterdrop_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_wifi.setOnClickListener(view154 -> {
                icName = "ic_wifi_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_camera.setOnClickListener(view153 -> {
                icName = "ic_camera_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_marihuana.setOnClickListener(view152 -> {
                icName = "ic_marihuana_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_painthouse.setOnClickListener(view151 -> {
                icName = "ic_painthouse_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_painting.setOnClickListener(view150 -> {
                icName = "ic_painting_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_partyballoons.setOnClickListener(view149 -> {
                icName = "ic_partyballoons_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pcportatil.setOnClickListener(view148 -> {
                icName = "ic_pcportatil_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_toolsmaint.setOnClickListener(view147 -> {
                icName = "ic_toolsmaint_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_tv.setOnClickListener(view146 -> {
                icName = "ic_tv_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_ac.setOnClickListener(view145 -> {
                icName = "ic_ac_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_baby.setOnClickListener(view144 -> {
                icName = "ic_baby_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_beer.setOnClickListener(view143 -> {
                icName = "ic_beer_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_casino.setOnClickListener(view142 -> {
                icName = "ic_casino_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cigarette.setOnClickListener(view141 -> {
                icName = "ic_cigarette_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_coupon.setOnClickListener(view140 -> {
                icName = "ic_coupon_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_creditcard.setOnClickListener(view139 -> {
                icName = "ic_creditcard_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cycling.setOnClickListener(view138 -> {
                icName = "ic_cycling_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_electronic.setOnClickListener(view137 -> {
                icName = "ic_electronic_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_guns.setOnClickListener(view136 -> {
                icName = "ic_guns_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gunsstore.setOnClickListener(view135 -> {
                icName = "ic_gunsstore_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_lottery.setOnClickListener(view134 -> {
                icName = "ic_lottery_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_makeup.setOnClickListener(view133 -> {
                icName = "ic_makeup_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_refund.setOnClickListener(view132 -> {
                icName = "ic_refund_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_shoppingbag.setOnClickListener(view131 -> {
                icName = "ic_shoppingbag_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_battery.setOnClickListener(view130 -> {
                icName = "ic_battery_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_bicycle.setOnClickListener(view129 -> {
                icName = "ic_bicycle_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cloud.setOnClickListener(view128 -> {
                icName = "ic_cloud_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_family.setOnClickListener(view127 -> {
                icName = "ic_family_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gas.setOnClickListener(view126 -> {
                icName = "ic_gas_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_insurance.setOnClickListener(view125 -> {
                icName = "ic_insurance_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_internet.setOnClickListener(view124 -> {
                icName = "ic_internet_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_jewelry.setOnClickListener(view123 -> {
                icName = "ic_jewelry_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_motorcycle.setOnClickListener(view122 -> {
                icName = "ic_motorcycle_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_multimedia.setOnClickListener(view121 -> {
                icName = "ic_multimedia_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_network.setOnClickListener(view120 -> {
                icName = "ic_network_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_photocamera.setOnClickListener(view119 -> {
                icName = "ic_photocamera_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_plug.setOnClickListener(view118 -> {
                icName = "ic_plug_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_recycle.setOnClickListener(view117 -> {
                icName = "ic_recycle_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_wifilogo.setOnClickListener(view116 -> {
                icName = "ic_wifilogo_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });


            dialogIcCategories.show();
        });

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(getString(R.string.alert_title_addCategory));
        builder.setMessage(getString(R.string.alert_mssg_addCategory));
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), (dialogInterface, i) -> {
            // Check if there is icon selected
            if (icName.equals("")) {
                icName = "ic_questionmark_100";
                //Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_noicon), Toast.LENGTH_LONG).show();
            }

            // Add category to data base
            if (!editText_categoryName.getText().toString().equals("")) {
                // check if the category exist
                Cursor cursorNames = db.getCategoriesByInstance(MainActivity.idInstance);
                boolean isExistCategory = false;
                while (cursorNames.moveToNext()) {
                    if (cursorNames.getString(1).equals(editText_categoryName.getText().toString())) {
                        isExistCategory = true;
                        break;
                    }
                }
                if (isExistCategory) {
                    Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_existCategory), Toast.LENGTH_LONG).show();
                } else {
                    db.addCategory(editText_categoryName.getText().toString(), icName, MainActivity.idInstance);
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
            }

            // Get categories
            Cursor categoriesData = db.getCategoriesByInstance(MainActivity.idInstance);

            // Array List to store the categories names
            ArrayList<CustomItems> categoriesList = new ArrayList<>();

            // Add categories names to categoriesList
            while (categoriesData.moveToNext()) {
                String categName = categoriesData.getString(1);
                String categIcon = categoriesData.getString(2);
                int categIconId = getResources().getIdentifier(categIcon, "drawable", Objects.requireNonNull(getContext()).getPackageName());
                categoriesList.add(new CustomItems(categName, categIconId));
            }

            // Add option: "Agregar..." category
            categoriesList.add(new CustomItems(getString(R.string.activity_addEntry_addCategory_spinner), R.drawable.ic_addblack_64));
            // Create adapter for the spinner of categories
            CustomAdapter customAdapter = new CustomAdapter(getContext(), categoriesList);
            spinner_categories.setAdapter(customAdapter);
            // Set default position
            spinner_categories.setSelection(categoriesList.size() - 2);
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
            Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();

            // Set default position
            spinner_categories.setSelection(0);
        });

        builder.show();
    }

    private void showMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.show();
    }

    @Override
    public void onClick(View view) {
        String montoStr = editText_profit.getText().toString();

        double ingresoInt;
        double gastoInt;

        switch (view.getId()) {
            case R.id.Ingreso_addMov:
                // Verify blank spaces
                if ((editText_profit.getText().toString().equals(""))) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }
                if (editText_description.getText().toString().equals("")) {
                    showMessage(getString(R.string.alert_title), getString(R.string.alert_addEntryActivity_nodata));
                    break;
                }

                // Set 0 to blank spaces
                if (radioGroup_addMov.getCheckedRadioButtonId() == R.id.radioButton_fragmentAddMov_Gasto) {
                    ingresoInt = 0;
                    gastoInt = Double.parseDouble(montoStr);
                } else {
                    ingresoInt = Double.parseDouble(montoStr);
                    gastoInt = 0;
                }

                String descripcion = editText_description.getText().toString();

                // Get id instance
                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
                String id = prefs.getString("ID", null);

                boolean isResult = db.addEntry(date, time, gastoInt, ingresoInt, descripcion, id, category_id);

                // Check result
                if (isResult) {
                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_succesAdd), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), getString(R.string.toast_addEntryActivity_noSuccesAdd), Toast.LENGTH_LONG).show();
                }
                editText_profit.setText("");
                editText_description.setText("");

                // Set date now in the textView
                String dateNow;
                LocalDateTime now = LocalDateTime.now();
                dateNow = dtf.format(now);
                // Store in format YYYY-MM-DD
                date = dateNow;
                // Show date in format DD-MM-YYYY
                String dateToShow;
                String year = date.substring(0, 4);
                String month = date.substring(5, 7);
                String day = date.substring(8, 10);
                String sepearator = "-";
                dateToShow = day + sepearator + month + sepearator + year;
                textView_date.setText(dateToShow);

                // Store time in format HH:MM:SS.SSSS
                time = java.time.LocalTime.now().toString();
                String timeToShow = time.substring(0, 5);
                // Show time in format HH:MM
                textView_time.setText(timeToShow);

                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.cancel_addMov:
                Objects.requireNonNull(getActivity()).onBackPressed();

                break;

            case R.id.fab_calendar_addMovFragment:
                Calendar calendar = Calendar.getInstance();
                int dayPick = calendar.get(Calendar.DAY_OF_MONTH);
                int monthPick = calendar.get(Calendar.MONTH);
                int yearPick = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), (datePicker, i, i1, i2) -> {
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
                    // Show in format DD-MM-YY
                    textView_date.setText(String.format("%s-%s-%s", strDay, strMonth, strYear));
                    // Store in format YY-MM-DD
                    date = strYear + "-" + strMonth + "-" + strDay;
                }, yearPick, monthPick, dayPick);
                datePickerDialog.show();

                break;

            case R.id.fab_clock_addMovFragment:
                Calendar timepick = Calendar.getInstance();
                int hourPick = timepick.get(Calendar.HOUR_OF_DAY);
                int minutesPick = timepick.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), (timePicker, i, i1) -> {
                    strHour = Integer.toString(i);
                    strMinute = Integer.toString(i1);

                    // Cast hour with 1 digit to 2
                    switch (strHour) {
                        case "0":
                            strHour = "00";
                            break;
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
                        case "0":
                            strMinute = "00";
                            break;
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

                    textView_time.setText(String.format("%s:%s", strHour, strMinute));
                    time = strHour + ":" + strMinute + ":00.000";
                }, hourPick, minutesPick, false);
                timePickerDialog.show();

                break;
        }
    }
}
