package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.AdapterDebt;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ListDataDebt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DebtsFragment extends Fragment {

    // Cmponents view
    private RecyclerView rvList;
    private RecyclerView.Adapter rvAdapter;
    private TextView textView_instanceName;
    private TextView textView_nodata;
    private ImageView imageView_nodata;

    // Global variables
    private DatabaseManager db;
    // Global variables
    private List<ListDataDebt> listItems;
    private ArrayList<String> id;
    private ArrayList<String> description;
    private ArrayList<String> amount;
    private ArrayList<String> date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debts, container, false);

        // Initialize db
        db = new DatabaseManager(view.getContext());

        // Initialize view components
        textView_instanceName = view.findViewById(R.id.textView_fragmentDebt_instanceName);
        textView_nodata = view.findViewById(R.id.textView_fragmentDebt_nodata);
        imageView_nodata = view.findViewById(R.id.imageView_fragmentDebt_nodata);

        // Hide FAB add entry from screen
        MainActivity.fab_addEntry.hide();

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.debt_name));

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String instanceName = prefs.getString("NAME", null);
        textView_instanceName.setText(instanceName);

        // Set recycler view component
        rvList = view.findViewById(R.id.recyclerView_debtsFragment_debt);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // List of data
        listItems = new ArrayList<>();
        id = new ArrayList<>();
        description = new ArrayList<>();
        amount = new ArrayList<>();
        date = new ArrayList<>();

        // Get categories data from db
        Cursor result = db.getAllDeptsData(MainActivity.idInstance);

        // Check categories count
        if (result.getCount() == 0) {
            rvList.setVisibility(View.GONE);
            textView_nodata.setVisibility(View.VISIBLE);
            imageView_nodata.setVisibility(View.VISIBLE);
        } else {
            textView_nodata.setVisibility(View.GONE);
            imageView_nodata.setVisibility(View.GONE);
            rvList.setVisibility(View.VISIBLE);
            while (result.moveToNext()) {
                id.add(result.getString(0));
                description.add(result.getString(2));
                amount.add(Double.toString(result.getDouble(3)));
                date.add(result.getString(4));
            }
            result.moveToPosition(-1);

            for (int i = 0; i < id.size(); i++) {
                ListDataDebt listData = new ListDataDebt(
                        id.get(i),
                        description.get(i),
                        amount.get(i),
                        date.get(i)
                );

                listItems.add(listData);
            }

            rvAdapter = new AdapterDebt(listItems, getContext(), getActivity());
            rvList.setAdapter(rvAdapter);
        }

        return view;
    }

}
