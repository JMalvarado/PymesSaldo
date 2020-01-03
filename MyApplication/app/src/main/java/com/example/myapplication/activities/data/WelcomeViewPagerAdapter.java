package com.example.myapplication.activities.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;

import java.util.Objects;

/**
 * Slide pages adapter for welcome screen
 */
public class WelcomeViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;

    /// List of elements in pager
    // Images
    public int[] images = {
            R.drawable.ic_ac_100,
            R.drawable.ic_add_64,
            R.drawable.ic_amazon_100,
            R.drawable.ic_arrowdropdown_24
    };

    // Titles
    public int[] titles = {
            R.string.welcome_title1,
            R.string.welcome_title2,
            R.string.welcome_title3,
            R.string.welcome_title4
    };

    // Subtitles
    public int[] subtitles = {
            R.string.welcome_subtitle1,
            R.string.welcome_subtitle2,
            R.string.welcome_subtitle3,
            R.string.welcome_subtitle4
    };

    public WelcomeViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = Objects.requireNonNull(inflater).inflate(R.layout.welcome_slide, container, false);
        ImageView imageSlide = view.findViewById(R.id.imageView_welcomeSlide_image);
        TextView textTitleSlide = view.findViewById(R.id.textView_welcomeSlide_title);
        TextView textSubtitleSlide = view.findViewById(R.id.textView_welcomeSlide_subtitle);

        imageSlide.setImageResource(images[position]);
        textTitleSlide.setText(titles[position]);
        textSubtitleSlide.setText(subtitles[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
