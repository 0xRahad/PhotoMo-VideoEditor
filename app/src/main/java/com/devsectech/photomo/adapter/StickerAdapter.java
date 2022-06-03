package com.devsectech.photomo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.devsectech.photomo.R;
import com.devsectech.photomo.model.StickerData;
import com.devsectech.photomo.utils.OnSingleClickListener;
import com.devsectech.photomo.callback.OnStickerClickListner;
import java.util.List;

public class StickerAdapter extends Adapter<StickerAdapter.ViewHolder> {
    public int checkedPosition = 0;
    public Bitmap moBaseImage;
    public Context moContext;
    public OnStickerClickListner moOnClickListner;
    public List<StickerData> moStickerList;


    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public ConstraintLayout loClItemMain;
        public ImageView loIvEffect;
        public ImageView loIvEffectBase;

        public ViewHolder(View view) {
            super(view);
            this.loClItemMain = (ConstraintLayout) view.findViewById(R.id.CvMain);
            this.loIvEffect =  view.findViewById(R.id.iv_effect);
            this.loIvEffectBase =  view.findViewById(R.id.iv_effect_base);
        }
    }

    public StickerAdapter(Context context, List<StickerData> list, Bitmap bitmap, OnStickerClickListner onStickerClickListner) {
        this.moStickerList = list;
        this.moContext = context;
        this.moBaseImage = bitmap;
        this.moOnClickListner = onStickerClickListner;
    }

    public int getItemCount() {
        return this.moStickerList.size();
    }

    public StickerData getSelectedEffect() {
        return (StickerData) this.moStickerList.get(this.checkedPosition);
    }

    public void resetAdapter() {
        this.checkedPosition = 0;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.loIvEffect.setImageDrawable(this.moContext.getResources().getDrawable(((StickerData) this.moStickerList.get(i)).getStickerThumb()));
        int i2 = this.checkedPosition;
        if (i2 == -1) {
            viewHolder.loClItemMain.setBackground(null);
        } else if (i2 == i) {
            viewHolder.loClItemMain.setBackground(this.moContext.getResources().getDrawable(R.drawable.bg_effect));
        } else {
            viewHolder.loClItemMain.setBackground(null);
        }
        viewHolder.loClItemMain.setOnClickListener(new OnSingleClickListener() {
            public void onSingleClick(View view) {
                viewHolder.loClItemMain.setBackground(moContext.getResources().getDrawable(R.drawable.bg_effect));
                //StickerAdapt stickerAdapter = StickerAdapt.this;
               // int i = stickerAdapter.checkedPosition;
                if (checkedPosition != i) {
                    StickerAdapter stickerAdapter = StickerAdapter.this;
                    stickerAdapter.notifyItemChanged(checkedPosition);
                    stickerAdapter.checkedPosition = i;
                    stickerAdapter.moOnClickListner.onClick((StickerData) moStickerList.get(i), i);
                }
            }
        });
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_effect, viewGroup, false));
    }
}
