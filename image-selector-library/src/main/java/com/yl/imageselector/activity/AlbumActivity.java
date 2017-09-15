package com.yl.imageselector.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.yl.imageselector.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义相册, 支持多选
 * Created by Luke on 2017/8/23.
 */

public class AlbumActivity extends AppCompatActivity {

    private List<String> mImagesPath = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        finish = (Button) findViewById(R.id.finish);

        initData();
        intiView();
    }

    private void intiView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        albumAdapter = new AlbumAdapter(mImagesPath);
        recyclerView.setAdapter(albumAdapter);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (albumAdapter.getChosenImagesPath().size() == 0) {
                    finish();
                } else {
                    List<String> chosenImages = albumAdapter.getChosenImagesPath();
                    String[] imageArray = new String[chosenImages.size()];
                    chosenImages.toArray(imageArray);
                    Intent intent = new Intent();
                    intent.putExtra("PATH", imageArray);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void initData() {
        // 获取相册图片的Uri
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(
                    MediaStore.Images.Media.DATA));
            mImagesPath.add(path);
        }
        cursor.close();
    }
}
