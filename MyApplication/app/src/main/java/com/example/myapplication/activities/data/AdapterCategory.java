package com.example.myapplication.activities.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.fragments.CategoriesFragment;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.maltaisn.icondialog.pack.IconPack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Adapter for card view in categories fragment
 */
public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {

    private List<ListDataCategory> listData;
    private Context context;
    private DatabaseManager db;
    private FragmentActivity activity;
    private CategoriesFragment fragment;

    // Icon dialog tag
    private static final String ICON_DIALOG_TAG = "icon-dialog";

    public AdapterCategory(List<ListDataCategory> listData, Context context, FragmentActivity activity, CategoriesFragment fragment) {
        this.listData = listData;
        this.context = context;
        this.activity = activity;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_list, viewGroup, false);

        return new AdapterCategory.MyViewHolder(v);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final ListDataCategory data = listData.get(i);

        db = new DatabaseManager(context);

        myViewHolder.textView_name.setText(data.getName());
        String ic = data.getIc();

        // Get Icon by Id and set to the image view
        IconPack iconPack = ((IconPackApp) activity.getApplication()).getIconPack();
        Drawable icDrawable = Objects.requireNonNull(iconPack).getIconDrawable(Integer.parseInt(ic), new IconDrawableLoader(Objects.requireNonNull(context)));
        myViewHolder.imageView_ic.setImageDrawable(icDrawable);

//        int imageID = context.getResources().getIdentifier(ic, "drawable", context.getPackageName());
//        myViewHolder.imageView_ic.setImageResource(imageID);

        // Check if is a default category. If true: disable delete and edit options
        String othersCat = context.getResources().getString(R.string.mainActivity_addCategory_others);
        String savingCat = context.getResources().getString(R.string.mainActivity_addCategory_saving);
        String transCat = context.getResources().getString(R.string.mainActivity_addCategory_transfer);
        if (!(data.getName().equals(othersCat)) && !(data.getName().equals(savingCat)) && !(data.getName().equals(transCat))) {
            myViewHolder.cardView.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, view);
                try {
                    Field[] fields = popupMenu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popupMenu);
                            Class<?> classPopupHelper = Class.forName(Objects.requireNonNull(menuPopupHelper).getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.entryOption_edit:
                            openDialog(data.getId(), data.getName(), data.getIc());

                            return true;

                        case R.id.entryOption_delete:
                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setCancelable(false);
                            builder.setTitle(R.string.alert_title_deleteData);
                            builder.setMessage(R.string.alert_msg_deleteData);
                            builder.setPositiveButton(context.getResources().getString(R.string.alert_optSi),
                                    (dialogInterface, i12) -> {
                                        db.deleteCategory(data.getId(), MainActivity.idInstance);

                                        activity.getSupportFragmentManager().beginTransaction().
                                                replace(R.id.content_main_layout, new CategoriesFragment()).commit();
                                    });

                            builder.setNegativeButton(context.getResources().getString(R.string.alert_optNo),
                                    (dialogInterface, i1) -> dialogInterface.cancel());

                            builder.show();

                            return true;

                        default:
                            return false;
                    }
                });
                popupMenu.inflate(R.menu.entry_options);
                popupMenu.show();
            });
        } else {
            myViewHolder.cardView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    /**
     * Alert dialog to edit category
     */
    private void openDialog(final String categoryId, final String name, final String ic) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_add_category, null);

        // Initialize edit Text for category name
        final EditText editText_categoryName = subView.findViewById(R.id.dialogLayoutAddCategory_editText_categoryName);
        fragment.imageButton_addIcon = subView.findViewById(R.id.imageButton_addCategory_addIcon);
        fragment.icId = "";

        // Set text name on blank space
        editText_categoryName.setText(name);

        // Set image on icon space
        fragment.icId = ic;
        IconPack iconPack = ((IconPackApp) activity.getApplication()).getIconPack();
        Drawable icDrawable = iconPack != null ? Objects.requireNonNull(iconPack).getIconDrawable(Integer.parseInt(ic), new IconDrawableLoader(Objects.requireNonNull(context))) : null;
        fragment.imageButton_addIcon.setImageDrawable(icDrawable);

        //int currentImageID = contextImgBttn.getResources().getIdentifier(icId, "drawable", contextImgBttn.getPackageName());
        //imageButton_addIcon.setImageResource(currentImageID);

        IconDialog dialog = (IconDialog) Objects.requireNonNull(activity.getSupportFragmentManager()).findFragmentByTag(ICON_DIALOG_TAG);
        IconDialog iconDialog = dialog != null ? dialog : IconDialog.newInstance(new IconDialogSettings.Builder().build());

        // Icon selector button click listener
        fragment.imageButton_addIcon.setOnClickListener(view -> iconDialog.show(fragment.getChildFragmentManager(), ICON_DIALOG_TAG));

        // Alert dialog build
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.alert_title_addCategory));
        builder.setMessage(context.getResources().getString(R.string.alert_mssg_editCategory));
        builder.setView(subView);

        // Positive option
        builder.setPositiveButton(context.getResources().getString(R.string.alert_positiveBttn_addCategory),
                (dialogInterface, i) -> {
                    // Add category to data base
                    if ((!editText_categoryName.getText().toString().equals("")) && (!fragment.icId.equals(""))) {
                        db.editCategory(categoryId, editText_categoryName.getText().toString(), fragment.icId, MainActivity.idInstance);

                        activity.getSupportFragmentManager().beginTransaction().
                                replace(R.id.content_main_layout, new CategoriesFragment()).commit();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_addEntryActivity_alertAddCateg_Canceled), Toast.LENGTH_LONG).show();
                    }

                });

        // Negative option
        builder.setNegativeButton(context.getResources().getString(R.string.alert_negativeBttn_addCategory), (dialogInterface, i) -> {
        });

        builder.show();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imageView_ic;
        TextView textView_name;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_categoryList);
            imageView_ic = itemView.findViewById(R.id.imageView_categoryList_ic);
            textView_name = itemView.findViewById(R.id.textView_categoryList_name);
        }
    }
}
