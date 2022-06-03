package com.devsectech.photomo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.devsectech.photomo.R;
import com.devsectech.photomo.adapter.PhoneAlbumImagesAdapter;
import com.devsectech.photomo.utils.Share;
import com.devsectech.photomo.utils.Share.KEYNAME;
//
import com.devsectech.photomo.utils.GridSpacingItemDecoration;

public class AlbumListActivity extends Activity implements OnClickListener {
    public static AlbumListActivity activity;
    private PhoneAlbumImagesAdapter albumAdapter;
    private GridLayoutManager gridLayoutManager;
    private ImageView iv_back_album_image;
    public Activity mContext;
    private long mLastClickTime = 0;
    private RecyclerView rcv_album_images;
    private TextView tv_title_album_image;
    public AlbumListActivity() {
        String str = "";
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_album);
        this.mContext = this;
        activity = this;

            initView();
            initViewAction();
    }



    private void initView() {
        this.rcv_album_images =  findViewById(R.id.rcv_album_images);
        this.tv_title_album_image =  findViewById(R.id.tv_title_album_image);
        this.iv_back_album_image =  findViewById(R.id.iv_back_album_image);
        this.gridLayoutManager = new GridLayoutManager(this, 3);
        this.rcv_album_images.setLayoutManager(this.gridLayoutManager);
        this.rcv_album_images.addItemDecoration(new GridSpacingItemDecoration(3, 10, true));
        try {
            this.albumAdapter = new PhoneAlbumImagesAdapter(this, Share.lst_album_image);
            this.rcv_album_images.setAdapter(this.albumAdapter);
            this.tv_title_album_image.setText(getIntent().getExtras().getString(KEYNAME.ALBUM_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViewAction() {
        this.iv_back_album_image.setOnClickListener(this);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void onPause() {

        super.onPause();
    }

    public void onResume() {
        super.onResume();

            findViewById(R.id.fl_adplaceholder).setVisibility(View.GONE);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - this.mLastClickTime >= 1000) {
            this.mLastClickTime = SystemClock.elapsedRealtime();
            int id = view.getId();
            if (id == R.id.iv_back_album_image) {
                onBackPressed();
            }
        }
    }

}
