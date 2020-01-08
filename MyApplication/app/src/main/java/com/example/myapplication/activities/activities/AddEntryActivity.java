package com.example.myapplication.activities.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.AddEntryViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;


public class AddEntryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private AddEntryViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private TextView textView_instanceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        textView_instanceName = findViewById(R.id.textView_addEntry_instanceName);
        viewPager = findViewById(R.id.viewPager_addEntryActivity);
        tabLayout = findViewById(R.id.tabLayout_addEntry);

        // Set instance name as title
        SharedPreferences prefs = getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // initialize viewPager Adapter
        viewPagerAdapter = new AddEntryViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_coininhand_48);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_pigmoney_48);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_transfercoin_48);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_debt_48);
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}

