package com.example.myapplication.activities.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.AdapterCategory;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ListDataCategory;
import com.example.myapplication.activities.data.IconPackApp;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconPack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriesFragment extends Fragment implements View.OnClickListener, IconDialog.Callback {

    // Cmponents view
    private RecyclerView rvList;
    private RecyclerView.Adapter rvAdapter;
    private TextView textView_instanceName;
    private TextView textView_nodata;
    private ImageButton imageButton_addCategory;
    public ImageButton imageButton_addIcon;

    // Global variables
    private List<ListDataCategory> listItems;
    private ArrayList<String> ids;
    private ArrayList<String> icons;
    private ArrayList<String> names;
    public String icId;
    private int categories_count;
    // Database manager instance
    private DatabaseManager db;

    // Icon dialog tag
    private static final String ICON_DIALOG_TAG = "icon-dialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Initialize db
        db = new DatabaseManager(view.getContext());

        // Initialize view components and click listeners
        textView_instanceName = view.findViewById(R.id.textView_fragmentCategories_instanceName);
        textView_nodata = view.findViewById(R.id.textView_fragmentCategories_nodata);
        imageButton_addCategory = view.findViewById(R.id.imageButton_categories_add);
        imageButton_addCategory.setOnClickListener(this);

        // Hide FAB add entry from screen
        MainActivity.fab_addEntry.hide();

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.categories_name));

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String instanceName = prefs.getString("NAME", null);
        textView_instanceName.setText(instanceName);

        // Set recycler view component
        rvList = view.findViewById(R.id.categoriesRecyclerView);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        // List of data
        listItems = new ArrayList<>();
        ids = new ArrayList<>();
        icons = new ArrayList<>();
        names = new ArrayList<>();
        // Get categories data from db
        Cursor result = db.getCategoriesByInstance(MainActivity.idInstance);
        categories_count = result.getCount();
        // Check categories count
        if (categories_count == 0) {
            textView_nodata.setVisibility(View.VISIBLE);
        } else {
            textView_nodata.setVisibility(View.GONE);
            while (result.moveToNext()) {
                ids.add(result.getString(0));
                names.add(result.getString(1));
                icons.add(result.getString(2));
            }
            result.moveToPosition(-1);
        }

        for (int i = 0; i < names.size(); i++) {
            assert icons != null;
            ListDataCategory listData = new ListDataCategory(
                    ids.get(i),
                    names.get(i),
                    icons.get(i)
            );

            listItems.add(listData);
        }

        rvAdapter = new AdapterCategory(listItems, getContext(), getActivity(), this);
        rvList.setAdapter(rvAdapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButton_categories_add) {// Check categories count limit
            if (categories_count < 25) {
                // if category_name = Add...
                openDialog();
            } else {
                Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_limit), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Alert dialog to add category
     */
    private void openDialog() {
        // Set icon name empty
        icId = "";

        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_add_category, null);

        // Initialize edit Text for category name
        final EditText editText_categoryName = subView.findViewById(R.id.dialogLayoutAddCategory_editText_categoryName);
        imageButton_addIcon = subView.findViewById(R.id.imageButton_addCategory_addIcon);

        IconDialog dialog = (IconDialog) Objects.requireNonNull(getFragmentManager()).findFragmentByTag(ICON_DIALOG_TAG);
        IconDialog iconDialog = dialog != null ? dialog : IconDialog.newInstance(new IconDialogSettings.Builder().build());

        // Icon selector button click listener
        imageButton_addIcon.setOnClickListener(view -> iconDialog.show(getChildFragmentManager(), ICON_DIALOG_TAG));

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(getString(R.string.alert_title_addCategory));
        builder.setMessage(getString(R.string.alert_mssg_addCategory));
        builder.setView(subView);

        // Positive option
        builder.setPositiveButton(getString(R.string.alert_positiveBttn_addCategory), (dialogInterface, i) -> {
            // Check if there is icon selected
            if (icId.equals("")) {
                icId = "957";
            }
            // Add category to data base
            if (!editText_categoryName.getText().toString().equals("")) {
                // check if the category exist
                Cursor cursorNames = db.getCategoriesByInstance(MainActivity.idInstance);
                boolean isExistCategory = false;
                while (cursorNames.moveToNext()) {
                    if (cursorNames.getString(1).equals(editText_categoryName.getText().toString())) {
                        isExistCategory = true;
                        break;
                    }
                }
                if (isExistCategory) {
                    Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_existCategory), Toast.LENGTH_LONG).show();
                } else {
                    db.addCategory(editText_categoryName.getText().toString(), icId, MainActivity.idInstance);
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().
                            replace(R.id.content_main_layout, new CategoriesFragment()).commit();
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
            }
        });

        // Negative option
        builder.setNegativeButton(getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
        });

        builder.show();
    }

    @Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return ((IconPackApp) (Objects.requireNonNull(getActivity()).getApplication())).getIconPack();
    }


    @Override
    public void onIconDialogIconsSelected(@NotNull IconDialog iconDialog, @NotNull List<Icon> icons) {
        // Select an icon from list
        icId = Integer.toString(icons.get(0).getId());
        Drawable icDrawable = icons.get(0).getDrawable();
        imageButton_addIcon.setImageDrawable(icDrawable);
    }

    @Override
    public void onIconDialogCancelled() {
    }
}
