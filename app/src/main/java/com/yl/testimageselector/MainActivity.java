package com.yl.testimageselector;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yl.imageselector.view.ImageSelector;

public class MainActivity extends AppCompatActivity {

    private ImageSelector imageSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageSelector = (ImageSelector) findViewById(R.id.image_selector);

        // imageSelector.clearAll();
        // imageSelector.getImagesPath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageSelector.selectImageResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imageSelector.requestPermissionResult(requestCode, grantResults);
    }
}
