package com.example.myapplication.activities.data;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.R;
import com.example.myapplication.activities.fragments.ReportGraphicsFragment;
import com.example.myapplication.activities.fragments.ReportProfitFragment;
import com.example.myapplication.activities.fragments.ReportSpendFragment;

import java.util.Objects;

/**
 * Adapter for tabs in report fragment
 */
public class ReportCategViewPagerAdapter extends FragmentStatePagerAdapter {

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
                fragment = new ReportSpendFragment();
                break;

            case 1:
                fragment = new ReportProfitFragment();
                break;

            case 2:
                fragment = new ReportGraphicsFragment();
                break;
        }

        return Objects.requireNonNull(fragment);
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
                title = context.getResources().getString(R.string.reportViewCategPagerAdapter_title2);
                break;

            case 1:
                title = context.getResources().getString(R.string.reportViewCategPagerAdapter_title1);
                break;

            case 2:
                title = context.getResources().getString(R.string.reportViewCategPagerAdapter_title3);
        }
        return title;
    }
}
