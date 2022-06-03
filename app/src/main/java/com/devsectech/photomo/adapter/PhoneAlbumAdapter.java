package com.devsectech.photomo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.Adapter;
//import com.appcenter.utils.OnSingleClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devsectech.photomo.R;
import com.devsectech.photomo.activity.AlbumListActivity;
import com.devsectech.photomo.utils.Share;
import com.devsectech.photomo.utils.Share.KEYNAME;
import com.devsectech.photomo.model.PhoneAlbum;
import com.devsectech.photomo.model.PhonePhoto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class PhoneAlbumAdapter extends Adapter<PhoneAlbumAdapter.ViewHolder> {
    public List<PhoneAlbum> al_album = new ArrayList();
    Context context;

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView iv_album;
        ProgressBar progressBar_phone;
        TextView tv_album_name;

        public ViewHolder(View view) {
            super(view);
            this.tv_album_name =  view.findViewById(R.id.tv_album_name);
            this.iv_album =  view.findViewById(R.id.iv_album);
            this.progressBar_phone =  view.findViewById(R.id.progressBar);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Share.lst_album_image.clear();
                    for (int i = 0; i < ((PhoneAlbum) al_album.get(getAdapterPosition())).getAlbumPhotos().size(); i++) {
                        Share.lst_album_image.add(((PhonePhoto) ((PhoneAlbum) al_album.get(getAdapterPosition())).getAlbumPhotos().get(i)).getPhotoUri());
                    }
                    Collections.reverse(Share.lst_album_image);
                    Intent intent = new Intent(context, AlbumListActivity.class);
                    intent.putExtra(KEYNAME.ALBUM_NAME, ((PhoneAlbum) al_album.get(getAdapterPosition())).getName());
                    context.startActivity(intent);
                }

            });
        }
    }

    public PhoneAlbumAdapter(Context context2, Vector<PhoneAlbum> vector) {
        this.context = context2;
        this.al_album = vector;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_phone_photo, viewGroup, false));
    }

    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.setIsRecyclable(false);
        viewHolder.progressBar_phone.setVisibility(View.VISIBLE);
        Glide.with(this.context).load(((PhoneAlbum) this.al_album.get(i)).getCoverUri()).listener(new RequestListener<Drawable>() {
            public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                viewHolder.progressBar_phone.setVisibility(View.GONE);
                return false;
            }

            public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                viewHolder.progressBar_phone.setVisibility(View.GONE);
                return false;
            }
        }).into(viewHolder.iv_album);
        viewHolder.tv_album_name.setText(((PhoneAlbum) this.al_album.get(i)).getName());

    }

    public int getItemCount() {
        return this.al_album.size();
    }
}
