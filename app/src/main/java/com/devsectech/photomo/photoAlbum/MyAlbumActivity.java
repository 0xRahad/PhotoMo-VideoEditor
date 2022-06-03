package com.devsectech.photomo.photoAlbum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsectech.photomo.R;
import com.devsectech.photomo.activity.BaseActivity;
import com.devsectech.photomo.activity.MainActivity;
import com.devsectech.photomo.photoAlbum.AlbumAdapter.ClickListener;
import com.devsectech.photomo.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Collections;

public class MyAlbumActivity extends BaseActivity implements OnClickListener {
    public AlbumAdapter adapter;
    public ArrayList<String> allFiles = new ArrayList<>();
    public ArrayList<String> favFiles = new ArrayList<>();
    public FavouriteHelper favHelper;
    public ImageView ivAllDelete;
    public ImageView ivNoPhoto;
    public ProgressBar progressBar;
    public RecyclerView rvThumbs;
    public int selectedTab = 0;
    public int sizeOfAllFiles = -1;
    public int sizeOfFavFiles = -1;
    private ImageView ivBackMyPhotos;
    private long mLastClickTime = 0;
    private TextView tvAll;
    private TextView tvFav;

    public Context getContext() {
        return this;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_my_album);
    }

    public void initViews() {
        ivBackMyPhotos =  findViewById(R.id.iv_back_my_photos);
        ivAllDelete =  findViewById(R.id.iv_all_delete);
        tvAll =  findViewById(R.id.tvAll);
        tvFav =  findViewById(R.id.tvFav);
        rvThumbs =  findViewById(R.id.rv_thumbs);
        ivNoPhoto =  findViewById(R.id.lin_no_photo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rvThumbs.setLayoutManager(new GridLayoutManager(this, 3));
        rvThumbs.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(5), true));
        rvThumbs.setItemAnimator(new DefaultItemAnimator());
        tvAll.setSelected(true);
        tvFav.setSelected(false);
//        tvAll.setTextColor(-1);
//        tvFav.setTextColor(getResources().getColor(R.color.colorPrimary));
        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAdsApp(mAdView);
    }

    public void initData() {
        favHelper = new FavouriteHelper(mContext);
        adapter = new AlbumAdapter(mContext, new ClickListener() {
            public void onClick(int i) {
                String path = adapter.getPath(i);
                String str = "isfav";
                Intent intent2 = new Intent(mContext, VideoPreviewActivity.class);
                intent2.putExtra("video_path", path);
                intent2.putExtra(str, selectedTab);
                startActivityForResult(intent2, 101);
            }
        });
        new LoadFileTask().execute(new Void[0]);
        adapter.setFiles(allFiles);
        rvThumbs.setAdapter(adapter);
    }

    public void setSelection() {
        if (selectedTab == 0) {
            tvAll.setSelected(true);
            tvFav.setSelected(false);
//            tvAll.setTextColor(-1);
//            tvFav.setTextColor(getResources().getColor(R.color.colorPrimary));
            if (allFiles.isEmpty()) {
                ivNoPhoto.setVisibility(View.VISIBLE);
                rvThumbs.setVisibility(View.GONE);
                ivAllDelete.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPlanoDeFundoTransparente), Mode.SRC_IN);
                return;
            }
            adapter.setFiles(allFiles);
            adapter.notifyDataSetChanged();
            ivNoPhoto.setVisibility(View.GONE);
            rvThumbs.setVisibility(View.VISIBLE);
            ivAllDelete.setColorFilter(ContextCompat.getColor(mContext, R.color.white), Mode.SRC_IN);
        } else if (selectedTab == 1) {
            tvAll.setSelected(false);
            tvFav.setSelected(true);
//            tvAll.setTextColor(getResources().getColor(R.color.colorPrimary));
//            tvFav.setTextColor(-1);
            if (favFiles.isEmpty()) {
                rvThumbs.setVisibility(View.GONE);
                ivNoPhoto.setVisibility(View.VISIBLE);
                ivAllDelete.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPlanoDeFundoTransparente), Mode.SRC_IN);
                return;
            }
            rvThumbs.setVisibility(View.VISIBLE);
            adapter.setFiles(favFiles);
            adapter.notifyDataSetChanged();
            ivNoPhoto.setVisibility(View.GONE);
            ivAllDelete.setColorFilter(ContextCompat.getColor(mContext, R.color.white), Mode.SRC_IN);
        }
    }

    public void initActions() {
        ivBackMyPhotos.setOnClickListener(this);
        tvAll.setOnClickListener(this);
        tvFav.setOnClickListener(this);
       // ivAllDelete.setOnClickListener(this);
    }

    public void ConfirmationDialog() {
        Builder builder = new Builder(mContext);
        builder.setMessage((CharSequence) "Are you sure want to delete all gif & videos?");
        builder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                new DeleteFileTask().execute(new Void[0]);
            }
        });
        builder.setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private int dpToPx(int i) {
        return Math.round(TypedValue.applyDimension(1, (float) i, getResources().getDisplayMetrics()));
    }

    public void onPause() {

        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 101) {
            new LoadFileTask().execute(new Void[0]);
        }
    }

    public void onClick(View view) {
        int i;
        if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
            mLastClickTime = SystemClock.elapsedRealtime();
            String str = " : ";
            String str2 = "onClick: ";
            switch (view.getId()) {
                case R.id.iv_all_delete:
                    if (selectedTab == 0) {
                        i = sizeOfAllFiles;
                    } else {
                        i = sizeOfFavFiles;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(sizeOfAllFiles);
                    sb.append(str);
                    sb.append(sizeOfFavFiles);
                    Log.e("SIZE", sb.toString());
                    if (i > 0) {
                        ConfirmationDialog();
                        break;
                    }
                    break;
                case R.id.iv_back_my_photos:
                    onBackPressed();
                    break;
                case R.id.tvAll:
                    selectedTab = 0;
                    setSelection();
                    break;
                case R.id.tvFav:
                    selectedTab = 1;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(allFiles.size());
                    sb2.append(str);
                    sb2.append(favFiles.size());
                    Log.e("tvFavPhoto", sb2.toString());
                    if (allFiles.size() == 0) {
                        favFiles.clear();
                        favFiles = new ArrayList<>();
                        favHelper.deleteAllFav();
                    }
                    setSelection();
                    break;
            }
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(mContext, MainActivity.class));
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }

    public class DeleteFileTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        public Void doInBackground(Void... voidArr) {
            if (selectedTab == 0) {
                for (int i = 0; i < allFiles.size(); i++) {
                    AlbumHelper.delete((String) allFiles.get(i));
                }
                allFiles.clear();
                allFiles = new ArrayList();
                sizeOfAllFiles = -1;
            } else {
                favFiles.clear();
                favFiles = new ArrayList();
                favHelper.deleteAllFav();
                sizeOfFavFiles = -1;
            }
            return null;
        }

        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            progressBar.setVisibility(View.GONE);
            rvThumbs.setVisibility(View.GONE);
            ivNoPhoto.setVisibility(View.VISIBLE);
            ivAllDelete.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPlanoDeFundoTransparente), Mode.SRC_IN);
            StringBuilder sb = new StringBuilder();
            sb.append("onPostExecute: All:");
            sb.append(allFiles.size());
            String str = "AAAAA";
            Log.i(str, sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("onPostExecute: Fav:");
            sb2.append(allFiles.size());
            Log.i(str, sb2.toString());
        }
    }

    public class LoadFileTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        public Void doInBackground(Void... voidArr) {
            MyAlbumActivity myAlbumActivity = MyAlbumActivity.this;
            myAlbumActivity.allFiles = AlbumHelper.loadFiles(myAlbumActivity.mContext);
            MyAlbumActivity myAlbumActivity2 = MyAlbumActivity.this;
            myAlbumActivity2.favFiles = myAlbumActivity2.favHelper.getAllFav();
            Collections.reverse(favFiles);
            return null;
        }

        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            progressBar.setVisibility(View.GONE);
            if (!(sizeOfAllFiles == allFiles.size() && sizeOfFavFiles == favFiles.size())) {
                MyAlbumActivity myAlbumActivity = MyAlbumActivity.this;
                myAlbumActivity.sizeOfAllFiles = myAlbumActivity.allFiles.size();
                MyAlbumActivity myAlbumActivity2 = MyAlbumActivity.this;
                myAlbumActivity2.sizeOfFavFiles = myAlbumActivity2.favFiles.size();
                setSelection();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("onPostExecute: All:");
            sb.append(allFiles.size());
            String str = "AAAAA";
            Log.i(str, sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("onPostExecute: Fav:");
            sb2.append(allFiles.size());
            Log.i(str, sb2.toString());
        }
    }
}
