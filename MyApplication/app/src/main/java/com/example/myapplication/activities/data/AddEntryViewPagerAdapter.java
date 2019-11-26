package com.example.myapplication.activities.data;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.R;
import com.example.myapplication.activities.fragments.AddMovFragment;
import com.example.myapplication.activities.fragments.SavingFragment;
import com.example.myapplication.activities.fragments.TransferFragment;

/**
 * Adapter for tabs in add entry activity
 */
public class AddEntryViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public AddEntryViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new AddMovFragment();
                break;

            case 1:
                fragment = new SavingFragment();
                break;

            case 2:
                fragment = new TransferFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = context.getResources().getString(R.string.addEntryViewPagerAdapter_title1);
                break;

            case 1:
                title = context.getResources().getString(R.string.addEntryViewPagerAdapter_title2);
                break;

            case 2:
                title = context.getResources().getString(R.string.addEntryViewPagerAdapter_title3);
                break;
        }
        return title;
    }
}
