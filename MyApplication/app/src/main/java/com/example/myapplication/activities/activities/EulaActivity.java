package com.example.myapplication.activities.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class EulaActivity extends AppCompatActivity {

    private TextView tv_eula_title;
    private TextView tv_eula_content;
    private TextView tv_eula_titleeula;
    private TextView tv_eula_subt_licence;
    private TextView tv_eula_content_licence;
    private TextView tv_eula_subt_dontallowed;
    private TextView tv_eula_content_dontallowed;
    private TextView tv_eula_subt_property;
    private TextView tv_eula_content_property;
    private TextView tv_eula_subt_termination;
    private TextView tv_eula_content_termination;
    private TextView tv_eula_subt_law;
    private TextView tv_eula_content_law;
    private Button bttn_eula_accept;
    private Button bttn_eula_cancel;

    private final String EULA_PREFIX = "cluf_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);

        PackageInfo versionInfo = getPackageInfo();

        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        if (!hasBeenShown) {

            tv_eula_title = findViewById(R.id.textview_eula_title);
            tv_eula_titleeula = findViewById(R.id.textview_eula_titleeula);
            tv_eula_content = findViewById(R.id.textview_eula_content);
            tv_eula_subt_licence = findViewById(R.id.textview_eula_subt_licence);
            tv_eula_subt_dontallowed = findViewById(R.id.textview_eula_subt_dontallowed);
            tv_eula_content_dontallowed = findViewById(R.id.textview_eula_content_dontallowed);
            tv_eula_content_licence = findViewById(R.id.textview_eula_content_licence);
            tv_eula_subt_property = findViewById(R.id.textview_eula_subt_property);
            tv_eula_content_property = findViewById(R.id.textview_eula_content_property);
            tv_eula_subt_termination = findViewById(R.id.textview_eula_subt_termination);
            tv_eula_content_termination = findViewById(R.id.textview_eula_content_termination);
            tv_eula_subt_law = findViewById(R.id.textview_eula_subt_law);
            tv_eula_content_law = findViewById(R.id.textview_eula_content_law);
            bttn_eula_accept = findViewById(R.id.button_eula_accept);
            bttn_eula_cancel = findViewById(R.id.button_eula_cancel);

            final String EULA_PREFIX = "cluf_";

            // Show the Eula
            String title = this.getString(R.string.app_name) + " v" + versionInfo.versionName;
            String titleEula = this.getString(R.string.activityEula_title);
            String subtLicence = this.getString(R.string.activityEula_subtitle_licenceconcesion);
            String contLicence = this.getString(R.string.activityEula_content_licenceconcesion);
            String subtDontallowed = this.getString(R.string.activityEula_subtitle_dontallowed);
            String contDontallowed = this.getString(R.string.activityEula_content_dontallowed);
            String subtProperty = this.getString(R.string.activityEula_subtitle_property);
            String contProperty = this.getString(R.string.activityEula_content_property);
            String subtTermination = this.getString(R.string.activityEula_subtitle_termination);
            String contTermiantion = this.getString(R.string.activityEula_content_termination);
            String subtLaw = this.getString(R.string.activityEula_subtitle_law);
            String contLaw = this.getString(R.string.activityEula_content_law);

            //Includes the updates as well so users know what changed.
            String content = this.getString(R.string.activityEula_updates) + "\n\n" + this.getString(R.string.activityEula_content);

            tv_eula_title.setText(title);
            tv_eula_titleeula.setText(titleEula);
            tv_eula_content.setText(content);
            tv_eula_subt_licence.setText(subtLicence);
            tv_eula_content_licence.setText(contLicence);
            tv_eula_subt_dontallowed.setText(subtDontallowed);
            tv_eula_content_dontallowed.setText(contDontallowed);
            tv_eula_subt_property.setText(subtProperty);
            tv_eula_content_property.setText(contProperty);
            tv_eula_subt_termination.setText(subtTermination);
            tv_eula_content_termination.setText(contTermiantion);
            tv_eula_subt_law.setText(subtLaw);
            tv_eula_content_law.setText(contLaw);

            bttn_eula_cancel.setOnClickListener(v -> onBackPressed());

            bttn_eula_accept.setOnClickListener(v -> {
                // Mark this version as read.
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(eulaKey, true);
                editor.apply();

                // Shared preferences of main configuration
                SharedPreferences prefsConfig = this.getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorConfig = prefsConfig.edit();
                editorConfig.putBoolean("EULAAccepted", true);
                editorConfig.apply();

                Intent mainIntent = new Intent(this, MainActivity.class);
                this.startActivity(mainIntent);
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intentSalir = new Intent(Intent.ACTION_MAIN);
        intentSalir.addCategory(Intent.CATEGORY_HOME);
        intentSalir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentSalir);
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }
}