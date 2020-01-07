package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.WelcomeViewPagerAdapter;

/**
 * Welcome activity
 */
public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPagerWelcome;
    private WelcomeViewPagerAdapter welcomeViewPagerAdapter;
    private ImageButton imageButton_next;
    private ImageButton imageButton_wa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        viewPagerWelcome = findViewById(R.id.viewPager_activityWelcome);
        imageButton_next = findViewById(R.id.imageButton_activityWelcome_next);
        imageButton_next.setOnClickListener(v -> {
            int currentPage = viewPagerWelcome.getCurrentItem();

            if (currentPage == 3) {
                Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(mainIntent);
            } else {
                viewPagerWelcome.setCurrentItem(getItem(), true);
            }
        });
        imageButton_wa = findViewById(R.id.imageButton_activityWelcome_wa);
        imageButton_wa.setOnClickListener(v -> {
            Intent intentURLWA = new Intent(Intent.ACTION_VIEW);
            intentURLWA.setData(Uri.parse("https://bit.ly/2PJjOuq"));
            startActivity(intentURLWA);
        });

        welcomeViewPagerAdapter = new WelcomeViewPagerAdapter(this);
        viewPagerWelcome.setAdapter(welcomeViewPagerAdapter);
    }

    private int getItem() {
        return viewPagerWelcome.getCurrentItem() + 1;
    }
}
