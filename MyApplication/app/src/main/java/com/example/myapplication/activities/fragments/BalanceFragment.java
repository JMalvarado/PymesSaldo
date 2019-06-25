package com.example.myapplication.activities.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.Objects;


public class BalanceFragment extends Fragment {

    // Instantiate view components
    private TextView textView_balance;
    private TextView textView_instanceName;

    // Global variables
    private String balance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.balance_name));

        // Initialize view components
        textView_balance = view.findViewById(R.id.textView_balance);
        textView_instanceName = view.findViewById(R.id.textView_fragmentBalance_instanceName);

        // Set instance name as title
        SharedPreferences prefs =  getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        textView_instanceName.setText(name);

        // Get data from main activity
        assert getArguments() != null;
        balance = getArguments().getString("BALANCE");

        // Set balance on textView
        textView_balance.setText(balance);

        // Set text color
        if (Integer.parseInt(balance) < 0) {
            textView_balance.setTextColor(Color.RED);
        } else if (Integer.parseInt(balance) == 0) {
            textView_balance.setTextColor(Color.BLACK);
        } else {
            textView_balance.setTextColor(Color.GREEN);
        }

        return view;
    }
}
