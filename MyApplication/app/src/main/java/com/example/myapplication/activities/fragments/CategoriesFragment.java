package com.example.myapplication.activities.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.AdapterCategory;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ListDataCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriesFragment extends Fragment implements View.OnClickListener {

    // Cmponents view
    private RecyclerView rvList;
    private RecyclerView.Adapter rvAdapter;
    private TextView textView_instanceName;
    private TextView textView_nodata;
    private ImageButton imageButton_addCategory;

    // Global variables
    private List<ListDataCategory> listItems;
    private ArrayList<String> ids;
    private ArrayList<String> icons;
    private ArrayList<String> names;
    private String icName;
    private int categories_count;
    // Database manager instance
    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Initialize db
        db = new DatabaseManager(view.getContext());

        // Initialize view components and click listeners
        textView_instanceName = view.findViewById(R.id.textView_fragmentCategories_instanceName);
        textView_nodata = view.findViewById(R.id.textView_fragmentCategories_nodata);
        imageButton_addCategory = view.findViewById(R.id.imageButton_categories_add);
        imageButton_addCategory.setOnClickListener(this);

        // Show FAB add entry from screen
        MainActivity.fab_addEntry.show();

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.categories_name));

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String instanceName = prefs.getString("NAME", null);
        textView_instanceName.setText(instanceName);

        // Set recycler view component
        rvList = view.findViewById(R.id.categoriesRecyclerView);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        // List of data
        listItems = new ArrayList<>();
        ids = new ArrayList<>();
        icons = new ArrayList<>();
        names = new ArrayList<>();
        // Get categories data from db
        Cursor result = db.getCategoriesByInstance(MainActivity.idInstance);
        categories_count = result.getCount();
        // Check categories count
        if (categories_count == 0) {
            textView_nodata.setVisibility(View.VISIBLE);
        } else {
            textView_nodata.setVisibility(View.GONE);
            while (result.moveToNext()) {
                ids.add(result.getString(0));
                names.add(result.getString(1));
                icons.add(result.getString(2));
            }
            result.moveToPosition(-1);
        }

        for (int i = 0; i < names.size(); i++) {
            assert icons != null;
            ListDataCategory listData = new ListDataCategory(
                    ids.get(i),
                    names.get(i),
                    icons.get(i)
            );

            listItems.add(listData);
        }

        rvAdapter = new AdapterCategory(listItems, getContext(), getActivity());
        rvList.setAdapter(rvAdapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton_categories_add:
                // Check categories count limit
                if (categories_count < 25) {
                    // if category_name = Add...
                    openDialog();
                } else {
                    Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_limit), Toast.LENGTH_LONG).show();
                }

                break;
        }
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

            final ImageView imageView_ic_candy = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_candy);
            final ImageView imageView_ic_gardening = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_gardening);
            final ImageView imageView_ic_moneygift = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_moneygift);
            final ImageView imageView_ic_prize = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_prize);
            final ImageView imageView_ic_rent = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_rent);
            final ImageView imageView_ic_shovel = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_shovel);
            final ImageView imageView_ic_sowing = dialogIcCategories.findViewById(R.id.imageView_dialogAddIcon_sowing);

            // image views click listener
            imageView_ic_amazon.setOnClickListener(view1101 -> {
                icName = "ic_amazon_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_bankbuilding.setOnClickListener(view1100 -> {
                icName = "ic_bankbuilding_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_barbershop.setOnClickListener(view199 -> {
                icName = "ic_barbershop_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_beachball.setOnClickListener(view198 -> {
                icName = "ic_beachball_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_bill.setOnClickListener(view197 -> {
                icName = "ic_bill_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_book.setOnClickListener(view196 -> {
                icName = "ic_book_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_borrowmonew.setOnClickListener(view195 -> {
                icName = "ic_borrowmoney_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_box.setOnClickListener(view194 -> {
                icName = "ic_box_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cellphone.setOnClickListener(view193 -> {
                icName = "ic_cellphone_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_deposit.setOnClickListener(view192 -> {
                icName = "ic_deposit_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_documents.setOnClickListener(view191 -> {
                icName = "ic_documents_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_dogbone.setOnClickListener(view190 -> {
                icName = "ic_dogbone_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_donation.setOnClickListener(view189 -> {
                icName = "ic_donation_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_dvdlogo.setOnClickListener(view188 -> {
                icName = "ic_dvdlogo_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_email.setOnClickListener(view187 -> {
                icName = "ic_email_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_entertainnent.setOnClickListener(view186 -> {
                icName = "ic_entertainment_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_envelope.setOnClickListener(view185 -> {
                icName = "ic_envelope_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_food.setOnClickListener(view184 -> {
                icName = "ic_food_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_fuelstation.setOnClickListener(view183 -> {
                icName = "ic_fuelstation_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_garage.setOnClickListener(view182 -> {
                icName = "ic_garage_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gift.setOnClickListener(view181 -> {
                icName = "ic_gift_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_givenkey.setOnClickListener(view180 -> {
                icName = "ic_givenkey_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_glassandfork.setOnClickListener(view179 -> {
                icName = "ic_glassandfork_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_graduationhat.setOnClickListener(view178 -> {
                icName = "ic_graduationhat_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gym.setOnClickListener(view177 -> {
                icName = "ic_gym_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_hand.setOnClickListener(view176 -> {
                icName = "ic_hand_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_health.setOnClickListener(view175 -> {
                icName = "ic_health_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_homephone.setOnClickListener(view174 -> {
                icName = "ic_homephone_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_hotelfivestars.setOnClickListener(view173 -> {
                icName = "ic_hotelfivestars_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_house.setOnClickListener(view172 -> {
                icName = "ic_house_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_ingredients.setOnClickListener(view171 -> {
                icName = "ic_ingredients_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_laundry.setOnClickListener(view170 -> {
                icName = "ic_laundry_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_lightbulb.setOnClickListener(view169 -> {
                icName = "ic_lightbulb_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moneyflow.setOnClickListener(view168 -> {
                icName = "ic_moneyflow_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moneyquery.setOnClickListener(view167 -> {
                icName = "ic_moneyquery_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moneytransfer.setOnClickListener(view166 -> {
                icName = "ic_moneytransfer_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moviefilm.setOnClickListener(view165 -> {
                icName = "ic_moviefilm_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moving.setOnClickListener(view164 -> {
                icName = "ic_moving_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_movingstock.setOnClickListener(view163 -> {
                icName = "ic_movingstock_100";

                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_onecoin.setOnClickListener(view162 -> {
                icName = "ic_onecoin_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_peoplegroup.setOnClickListener(view161 -> {
                icName = "ic_peoplegroup_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pigmoney.setOnClickListener(view160 -> {
                icName = "ic_pigmoney_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pizza.setOnClickListener(view159 -> {
                icName = "ic_pizza_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pokecoins.setOnClickListener(view158 -> {
                icName = "ic_pokecoins_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_questionmark.setOnClickListener(view157 -> {
                icName = "ic_questionmark_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_rentacar.setOnClickListener(view156 -> {
                icName = "ic_rentacar_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_restaurantbuilding.setOnClickListener(view155 -> {
                icName = "ic_restaurantbuilding_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_roster.setOnClickListener(view154 -> {
                icName = "ic_roster_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_rubberstamp.setOnClickListener(view153 -> {
                icName = "ic_rubberstamp_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_sale.setOnClickListener(view152 -> {
                icName = "ic_sale_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_security.setOnClickListener(view151 -> {
                icName = "ic_security_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_sell.setOnClickListener(view150 -> {
                icName = "ic_sell_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_shirt.setOnClickListener(view149 -> {
                icName = "ic_shirt_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_sleeping.setOnClickListener(view148 -> {
                icName = "ic_sleeping_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_stapler.setOnClickListener(view147 -> {
                icName = "ic_stapler_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_suv.setOnClickListener(view146 -> {
                icName = "ic_suv_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_transport.setOnClickListener(view145 -> {
                icName = "ic_transport_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_trash.setOnClickListener(view144 -> {
                icName = "ic_trash_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_travelworld.setOnClickListener(view143 -> {
                icName = "ic_travelworld_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_twoheart.setOnClickListener(view142 -> {
                icName = "ic_twoheart_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_videogamecontroller.setOnClickListener(view141 -> {
                icName = "ic_videogamecontroller_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_waterdrop.setOnClickListener(view140 -> {
                icName = "ic_waterdrop_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_wifi.setOnClickListener(view139 -> {
                icName = "ic_wifi_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_camera.setOnClickListener(view138 -> {
                icName = "ic_camera_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_marihuana.setOnClickListener(view137 -> {
                icName = "ic_marihuana_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_painthouse.setOnClickListener(view136 -> {
                icName = "ic_painthouse_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_painting.setOnClickListener(view135 -> {
                icName = "ic_painting_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_partyballoons.setOnClickListener(view134 -> {
                icName = "ic_partyballoons_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_pcportatil.setOnClickListener(view133 -> {
                icName = "ic_pcportatil_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_toolsmaint.setOnClickListener(view132 -> {
                icName = "ic_toolsmaint_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_tv.setOnClickListener(view131 -> {
                icName = "ic_tv_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_ac.setOnClickListener(view130 -> {
                icName = "ic_ac_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_baby.setOnClickListener(view129 -> {
                icName = "ic_baby_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_beer.setOnClickListener(view128 -> {
                icName = "ic_beer_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_casino.setOnClickListener(view127 -> {
                icName = "ic_casino_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cigarette.setOnClickListener(view126 -> {
                icName = "ic_cigarette_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_coupon.setOnClickListener(view125 -> {
                icName = "ic_coupon_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_creditcard.setOnClickListener(view124 -> {
                icName = "ic_creditcard_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cycling.setOnClickListener(view123 -> {
                icName = "ic_cycling_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_electronic.setOnClickListener(view122 -> {
                icName = "ic_electronic_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_guns.setOnClickListener(view121 -> {
                icName = "ic_guns_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gunsstore.setOnClickListener(view120 -> {
                icName = "ic_gunsstore_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_lottery.setOnClickListener(view119 -> {
                icName = "ic_lottery_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_makeup.setOnClickListener(view118 -> {
                icName = "ic_makeup_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_refund.setOnClickListener(view117 -> {
                icName = "ic_refund_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_shoppingbag.setOnClickListener(view116 -> {
                icName = "ic_shoppingbag_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_battery.setOnClickListener(view115 -> {
                icName = "ic_battery_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_bicycle.setOnClickListener(view114 -> {
                icName = "ic_bicycle_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_cloud.setOnClickListener(view113 -> {
                icName = "ic_cloud_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_family.setOnClickListener(view112 -> {
                icName = "ic_family_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gas.setOnClickListener(view111 -> {
                icName = "ic_gas_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_insurance.setOnClickListener(view110 -> {
                icName = "ic_insurance_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_internet.setOnClickListener(view19 -> {
                icName = "ic_internet_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_jewelry.setOnClickListener(view18 -> {
                icName = "ic_jewelry_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_motorcycle.setOnClickListener(view17 -> {
                icName = "ic_motorcycle_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_multimedia.setOnClickListener(view16 -> {
                icName = "ic_multimedia_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_network.setOnClickListener(view15 -> {
                icName = "ic_network_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_photocamera.setOnClickListener(view14 -> {
                icName = "ic_photocamera_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_plug.setOnClickListener(view13 -> {
                icName = "ic_plug_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_recycle.setOnClickListener(view12 -> {
                icName = "ic_recycle_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_wifilogo.setOnClickListener(view1 -> {
                icName = "ic_wifilogo_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });

            imageView_ic_candy.setOnClickListener(view116 -> {
                icName = "ic_candy_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_gardening.setOnClickListener(view116 -> {
                icName = "ic_gardening_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_moneygift.setOnClickListener(view116 -> {
                icName = "ic_moneygift_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_prize.setOnClickListener(view116 -> {
                icName = "ic_prize_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_rent.setOnClickListener(view116 -> {
                icName = "ic_rent_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_shovel.setOnClickListener(view116 -> {
                icName = "ic_shovel_100";
                int imageID = contextImgBttn.getResources().getIdentifier(icName, "drawable", contextImgBttn.getPackageName());
                imageButton_addIcon.setImageResource(imageID);
                dialogIcCategories.dismiss();
            });
            imageView_ic_sowing.setOnClickListener(view116 -> {
                icName = "ic_sowing_100";
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
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().
                            replace(R.id.content_main_layout, new CategoriesFragment()).commit();
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
            }
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
        });

        builder.show();
    }
}
