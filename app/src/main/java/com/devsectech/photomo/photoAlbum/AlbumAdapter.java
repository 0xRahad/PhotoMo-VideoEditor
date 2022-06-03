package com.devsectech.photomo.photoAlbum;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
//import com.appcenter.utils.OnSingleClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devsectech.photomo.R;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumAdapter extends Adapter<AlbumAdapter.MyViewHolder> {
    public ClickListener clickListener;
    private ArrayList<String> creationList;
    private Context mContext;

    public interface ClickListener {
        void onClick(int i);
    }

    public class MyViewHolder extends ViewHolder {

        public ProgressBar progressBar;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail =  view.findViewById(R.id.thumbnail);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public AlbumAdapter(Context context, ClickListener clickListener2) {
        mContext = context;
        clickListener = clickListener2;
    }

    public void setFiles(ArrayList<String> arrayList) {
        Collections.reverse(arrayList);
        creationList = arrayList;
    }

    public String getPath(int i) {
        return (String) creationList.get(i);
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_album, viewGroup, false));
    }

    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        myViewHolder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(creationList.get(i)).listener(new RequestListener<Drawable>() {
            public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                myViewHolder.progressBar.setVisibility(View.GONE);
                return false;
            }

            public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                myViewHolder.progressBar.setVisibility(View.GONE);
                return false;

            }
        }).into(myViewHolder.thumbnail);
        myViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clickListener.onClick(i);
            }
        });
    }

    public int getItemCount() {
        return creationList.size();
    }
}
