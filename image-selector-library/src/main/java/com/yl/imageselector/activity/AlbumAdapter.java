package com.yl.imageselector.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yl.imageselector.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册适配器
 * Created by Luke on 2017/8/23.
 */

class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<String> imagesPath;
    private Context mContext;
    private List<String> chosenImages;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView mark;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            mark = (ImageView) itemView.findViewById(R.id.mark);
        }
    }

    AlbumAdapter(List<String> imagesPath) {
        this.imagesPath = imagesPath;
        chosenImages = new ArrayList<>();
    }

    List<String> getChosenImagesPath() {
        return chosenImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_images, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String path = imagesPath.get(position);
                if (chosenImages.contains(path)) {
                    holder.mark.setVisibility(View.INVISIBLE);
                    chosenImages.remove(path);
                } else {
                    holder.mark.setVisibility(View.VISIBLE);
                    chosenImages.add(path);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext).load(imagesPath.get(position))
                .centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagesPath.size();
    }
}
