package com.example.myapplication.activities.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;

public class HelpActivity extends AppCompatActivity {

    private CardView cardView_concepts;
    private CardView cardView_functions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        cardView_concepts = findViewById(R.id.cardview_activityHelp_concepts);
        cardView_functions = findViewById(R.id.cardview_activityHelp_functions);

        // Open concepts activity
        cardView_concepts.setOnClickListener(v -> {
            // Help concepts activity
            Intent helpConceptsIntent = new Intent(this, HelpConceptsActivity.class);
            startActivity(helpConceptsIntent);
        });

        // Open functions activity
        cardView_functions.setOnClickListener(v -> {
            // Help functions activity
            Intent helpFunctionsIntent = new Intent(this, HelpFunctionsActivity.class);
            startActivity(helpFunctionsIntent);
        });
    }
}
