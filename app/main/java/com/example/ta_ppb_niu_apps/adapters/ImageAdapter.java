package com.example.ta_ppb_niu_apps.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ta_ppb_niu_apps.R;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private int[] sliderImageId = new int[]{
            R.drawable.banner1, R.drawable.banner2, R.drawable.banner3,
    };

    private String[] links = {
            "https://developer.android.com/studio",
            "https://youtube.com/playlist?list=PLQ_Ai1O7sMV3D3GHb7H7naWHOJP18iV17&si=54dhI0NPNadckHtG",
            "https://youtube.com/playlist?list=PLam6bY5NszYOhXkY7jOS4EQAKcQwkXrp4&si=mxVguxfwFlsJaRDR"
    };

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(sliderImageId[position]);
        ((ViewPager) container).addView(imageView, 0);

        // Handle image click to open a link
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the link associated with the clicked image
                openLink(position);
            }
        });

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return sliderImageId.length;
    }

    // Auto image sliding logic
    public void startAutoSlider(ViewPager viewPager, long delayMillis) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % getCount();
                viewPager.setCurrentItem(nextItem);
                handler.postDelayed(this, delayMillis);
            }
        };
        handler.postDelayed(runnable, delayMillis);
    }

    public void stopAutoSlider() {
        // Stop the auto sliding logic if needed
    }

    // Open link based on the position
    private void openLink(int position) {
        if (position >= 0 && position < links.length) {
            String link = links[position];

            // Open the link in a browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            mContext.startActivity(intent);
        }
    }
}
