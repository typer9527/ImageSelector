package com.yl.imageselector.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yl.imageselector.R;
import com.yl.imageselector.activity.DisplayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 照片适配器
 * Created by Luke on 2017/8/23.
 */

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> chosenImages = new ArrayList<>();
    private Context mContext;

    void refresh(List<String> addImages) {
        chosenImages.addAll(addImages);
        notifyDataSetChanged();
    }

    List<String> getChosenImages() {
        return chosenImages;
    }

    void clearImages() {
        chosenImages.clear();
        notifyDataSetChanged();
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_delete_image, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String[] imagesArr = new String[chosenImages.size()];
                chosenImages.toArray(imagesArr);
                Intent intent = new Intent(mContext, DisplayActivity.class);
                intent.putExtra("INDEX", position);
                intent.putExtra("PATH", imagesArr);
                mContext.startActivity(intent);
            }
        });
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String[] temp = new String[chosenImages.size()];
                chosenImages.toArray(temp);
                chosenImages = new ArrayList<>();
                for (int i = 0; i < temp.length; i++) {
                    if (i != position)
                        chosenImages.add(temp[i]);
                }
                notifyDataSetChanged();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
        Glide.with(mContext).load(chosenImages.get(position))
                .centerCrop().into(holder.cardImage);
    }

    @Override
    public int getItemCount() {
        return chosenImages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        Button deleteImage;

        ViewHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            deleteImage = (Button) itemView.findViewById(R.id.delete_image);
        }
    }
}
