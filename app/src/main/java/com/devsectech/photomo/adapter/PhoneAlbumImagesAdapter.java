package com.devsectech.photomo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devsectech.photomo.R;
import com.devsectech.photomo.activity.CropActivity;
import com.devsectech.photomo.utils.Share;
import com.devsectech.photomo.utils.Share.KEYNAME;
import java.util.ArrayList;
import java.util.List;

public class PhoneAlbumImagesAdapter extends Adapter<PhoneAlbumImagesAdapter.ViewHolder> {
    public Activity activity;
    public List<String> al_album = new ArrayList();

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView iv_album;
        ProgressBar progressBar_phone;
        TextView tv_album_name;

        public ViewHolder(View view) {
            super(view);
            this.tv_album_name =  view.findViewById(R.id.tv_album_name);
            this.iv_album =  view.findViewById(R.id.iv_album);
            this.progressBar_phone = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public PhoneAlbumImagesAdapter(Activity activity2, ArrayList<String> arrayList) {
        this.activity = activity2;
        this.al_album = arrayList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_phone_photo, viewGroup, false));
    }

    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.setIsRecyclable(false);
        viewHolder.tv_album_name.setVisibility(View.GONE);
        viewHolder.progressBar_phone.setVisibility(View.VISIBLE);
        Glide.with(this.activity).load((String) this.al_album.get(i)).listener(new RequestListener<Drawable>() {
            public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                viewHolder.progressBar_phone.setVisibility(View.GONE);
                return false;
            }

            public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                viewHolder.progressBar_phone.setVisibility(View.GONE);
                return false;
            }
        }).into(viewHolder.iv_album);
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                sb.append("acrtivity from:==>");
                sb.append(activity.getIntent().hasExtra("activity"));
                String str = "TAG";
                Log.e(str, sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("file:///");
                sb2.append((String) al_album.get(i));
                Share.SAVED_BITMAP = Uri.parse(sb2.toString());
                Share.imageUrl = (String) al_album.get(i);
                StringBuilder sb3 = new StringBuilder();
                sb3.append(" :::: ");
                sb3.append(Share.imageUrl);
                Log.e(str, sb3.toString());
                Intent intent = new Intent(activity, CropActivity.class);
                intent.putExtra(KEYNAME.SELECTED_PHONE_IMAGE, (String) al_album.get(i));
                intent.setData(Share.SAVED_BITMAP);
                activity.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return this.al_album.size();
    }
}
