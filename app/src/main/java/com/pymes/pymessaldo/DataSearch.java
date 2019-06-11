package com.pymes.pymessaldo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataSearch extends AppCompatActivity {

    private RecyclerView rvList;
    private RecyclerView.Adapter rvAdapter;

    private List<ListData> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_search);

        rvList = findViewById(R.id.dataRecyclerView);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();
    }
}
