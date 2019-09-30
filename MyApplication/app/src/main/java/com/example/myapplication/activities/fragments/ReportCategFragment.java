package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.ReportCategViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ReportCategFragment extends Fragment {

    private ViewPager viewPager;
    private ReportCategViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private TextView textView_instanceName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_categ, container, false);

        textView_instanceName = view.findViewById(R.id.textView_fragmentReportCateg_instanceName);
        viewPager = view.findViewById(R.id.viewPager_reportCateg);
        tabLayout = view.findViewById(R.id.tabLayout_reportCateg);

        // Set instance name as title
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // initialize viewPager Adapter
        viewPagerAdapter = new ReportCategViewPagerAdapter(getFragmentManager(), view.getContext());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
