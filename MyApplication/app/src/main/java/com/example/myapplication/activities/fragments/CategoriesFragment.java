package com.example.myapplication.activities.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
        // Check categories count
        if (result.getCount() == 0) {
            textView_nodata.setVisibility(View.VISIBLE);
        } else {
            textView_nodata.setVisibility(View.GONE);
            while (result.moveToNext()) {
                ids.add(result.getString(0));
                names.add(result.getString(1));
                icons.add(result.getString(2));
            }
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
                // if category_name = Add...
                openDialog();

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
        View subView = inflater.inflate(R.layout.dialog_add_category, null);

        // Initialize edit Text for category name
        final EditText editText_categoryName = subView.findViewById(R.id.dialogLayoutAddCategory_editText_categoryName);
        final ImageButton imageButton_addIcon = subView.findViewById(R.id.imageButton_addCategory_addIcon);
        final Context contextImgBttn = imageButton_addIcon.getContext();

        // Icon selector button click listener
        imageButton_addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(getString(R.string.alert_title_addCategory));
        builder.setMessage(getString(R.string.alert_mssg_addCategory));
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
            }
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.show();
    }
}
