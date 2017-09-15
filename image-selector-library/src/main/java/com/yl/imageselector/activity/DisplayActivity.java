package com.yl.imageselector.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yl.imageselector.R;
import com.yl.imageselector.view.ZoomImageView;

import java.util.Arrays;
import java.util.List;

/**
 * 查看图片，支持缩放和移动
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
        public ZoomImageView imageView;

        public ImagePager(Activity activity) {
            imageView = new ZoomImageView(activity);
        }

        public void setImage(String path) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
        }
    }
}
