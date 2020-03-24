package com.example.myapplication.activities.data;

import android.app.Application;

import androidx.annotation.Nullable;

import com.maltaisn.icondialog.pack.IconPack;
import com.maltaisn.icondialog.pack.IconPackLoader;
import com.maltaisn.iconpack.defaultpack.IconPackDefault;

public class IconPackApp extends Application {

    @Nullable
    private IconPack iconPack;

    @Override
    public void onCreate() {
        super.onCreate();

        // Load the icon Pack on application start
        loadIconPack();
    }

    @Nullable
    public IconPack getIconPack() {
        return iconPack != null ? iconPack : loadIconPack();
    }

    private IconPack loadIconPack() {
        // Create an icon pack loader with application context
        IconPackLoader loader = new IconPackLoader(this);

        // Create and icon pack and load all drawables
        iconPack = IconPackDefault.createDefaultIconPack(loader);
        iconPack.loadDrawables(loader.getDrawableLoader());

        return iconPack;
    }
}
