package com.yl.imageselector.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yl.imageselector.R;

import java.util.Arrays;
import java.util.List;

/**
 * 查看图片
 * Created by Luke on 2017/9/1.
 */

public class DisplayActivity extends AppCompatActivity {

    private List<String> chosenImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        chosenImages = Arrays.asList(getIntent().getStringArrayExtra("PATH"));
        int currentItemPosition = getIntent().getIntExtra("INDEX", 0);
        viewPager.setAdapter(new DisplayAdapter());
        viewPager.setCurrentItem(currentItemPosition);
    }

    class DisplayAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return chosenImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImagePager imagePager = new ImagePager(DisplayActivity.this);
            imagePager.setImage(chosenImages.get(position));
            container.addView(imagePager.imageView);
            return imagePager.imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class ImagePager {
        private Activity mActivity;
        public ImageView imageView;

        public ImagePager(Activity mActivity) {
            this.mActivity = mActivity;
            imageView = new ImageView(mActivity);
        }

        public void setImage(String path) {
            Glide.with(mActivity).load(path).into(imageView);
        }
    }
}
