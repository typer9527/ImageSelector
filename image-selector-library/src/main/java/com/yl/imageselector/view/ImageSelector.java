package com.yl.imageselector.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yl.imageselector.R;
import com.yl.imageselector.activity.AlbumActivity;
import com.yl.imageselector.util.PermissionHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 图片选择和和查看控件
 * Created by Luke on 2017/8/24.
 */

public class ImageSelector extends LinearLayout {

    private static final int TAKE_PHOTO = 1;
    private static final int OPEN_ALBUM = 2;
    private final RecyclerView recyclerView;
    private Context mContext;
    private ImageAdapter mAdapter;
    private List<String> mImagesPath;
    private String imagePath;

    public ImageSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.image_selector, this);
        mContext = context;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ImageAdapter();
        recyclerView.setAdapter(mAdapter);
        final Button openAlbum = (Button) findViewById(R.id.open_album);
        openAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });
        Button takePhoto = (Button) findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    private void openAlbum() {
        PermissionHelper.with(mContext).requestCode(OPEN_ALBUM)
                .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setListener(new PermissionHelper.RequestListener() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(mContext, AlbumActivity.class);
                        ((Activity) mContext).startActivityForResult(intent, OPEN_ALBUM);
                    }

                    @Override
                    public void onDenied() {
                        Toast.makeText(mContext, "权限拒绝", Toast.LENGTH_SHORT).show();
                    }
                })
                .request();
    }

    private void takePhoto() {
        // 创建图片文件，用于存储拍照结果，目录地址为该应用在SD卡中的关联目录
        String fileName = System.currentTimeMillis() + ".jpg";
        File image = new File(mContext.getExternalCacheDir(), fileName);
        try {
            if (image.exists()) {
                image.delete();
            }
            image.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePath = image.getAbsolutePath();
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(mContext,
                    "com.yl.imageselector.fileprovider", image);
        } else {
            imageUri = Uri.fromFile(image);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        ((Activity) mContext).startActivityForResult(intent, TAKE_PHOTO);
    }

    private void refresh(List<String> imagesPath) {
        mAdapter.refresh(imagesPath);
    }

    // 获取当前recyclerView中所有图片的存储路径
    public List<String> getImagesPath() {
        return mAdapter.getChosenImages();
    }

    // 清空当前所选图片
    public void clearAll() {
        mAdapter.clearImages();
    }

    // onActivityResult()
    public void selectImageResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        mImagesPath = new ArrayList<>();
                        mImagesPath.add(imagePath);
                        refresh(mImagesPath);
                        // 将图片插入系统相册，方便在相册中选择
                        MediaStore.Images.Media.insertImage(
                                mContext.getContentResolver(), imagePath, "", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case OPEN_ALBUM:
                if (resultCode == RESULT_OK) {
                    mImagesPath = new ArrayList<>();
                    String[] imageArr = data.getStringArrayExtra("PATH");
                    mImagesPath = Arrays.asList(imageArr);
                    refresh(mImagesPath);
                }
            default:
                break;
        }
    }

    // 权限申请的回调
    public void requestPermissionResult(int requestCode, int[] grantResults) {
        PermissionHelper.requestPermissionResult(requestCode, grantResults);
    }
}
