package com.example.myapplication.activities.data;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.R;
import com.example.myapplication.activities.fragments.ReportProfitFragment;
import com.example.myapplication.activities.fragments.SavingFragment;

/**
 * Adapter for tabs in report fragment
 */
public class ReportCategViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public ReportCategViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new ReportProfitFragment();
                break;

            case 1:
                fragment = new SavingFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = context.getResources().getString(R.string.reportViewCategPagerAdapter_title1);
                break;

            case 1:
                title = context.getResources().getString(R.string.reportCategViewPagerAdapter_title2);
                break;
        }
        return title;
    }
}
