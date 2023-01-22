package com.androworld.musicvideo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.androworld.musicvideo.Album;
import com.androworld.musicvideo.R;
import com.androworld.musicvideo.TokanData.Glob;
import com.androworld.musicvideo.libffmpeg.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class MyCreationActivity extends AppCompatActivity implements OnClickListener {
    public static final String TAG = "MyCreationActivity";
    static int id;
    public static int screenWidth;
    boolean fbshow = true;
    Handler handler = new Handler();

    private ImageView ivBack;

    private MyAlbum_Adapter myAlbum_adapter;
    Runnable runnable = new C07941();
    private RecyclerView rv_my_album;
    private ArrayList<Album> videoList;
    class C07941 implements Runnable {
        C07941() {
        }

        public void run() {
        }
    }

    class C07953 implements Runnable {
        C07953() {
        }

        public void run() {
            if (!MyCreationActivity.this.isFinishing()) {
                MyCreationActivity.this.showDialogforshow();
            }
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private ProgressDialog progress;

        private AsyncTaskRunner() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progress = new ProgressDialog(MyCreationActivity.this);
            this.progress.setMessage("Loading...");
            this.progress.setProgressStyle(0);
            MyCreationActivity.this.videoList.clear();
            Glob.fileList.clear();
            this.progress.show();
        }

        protected String doInBackground(String... params) {
            MyCreationActivity.this.videoList = Glob.getfile(FileUtils.APP_DIRECTORY, "video", MyCreationActivity.this);
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.progress.dismiss();
            MyCreationActivity.this.myAlbum_adapter = new MyAlbum_Adapter();
            MyCreationActivity.this.rv_my_album.setAdapter(MyCreationActivity.this.myAlbum_adapter);
        }
    }



    private class MenuItemClickListener implements OnMenuItemClickListener {
        Album videoData;

        public MenuItemClickListener(Album videoData) {
            this.videoData = videoData;
        }

        public boolean onMenuItemClick(MenuItem menu) {
            final int pos = MyCreationActivity.this.videoList.indexOf(this.videoData);
            switch (menu.getItemId()) {
                case R.id.action_delete:
                    Builder builder = new Builder(MyCreationActivity.this, R.style.Theme_MovieMaker_AlertDialog);
                    builder.setTitle((CharSequence) "Delete Video !");
                    builder.setMessage("Are you sure to delete " + new File(((Album) MyCreationActivity.this.videoList.get(pos)).strImagePath).getName() + " ?");
                    builder.setPositiveButton((CharSequence) "Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FileUtils.deleteFile(((Album) MyCreationActivity.this.videoList.remove(pos)).strImagePath);
                            MyCreationActivity.this.myAlbum_adapter.notifyItemRemoved(pos);
                        }
                    });
                    builder.setNegativeButton((CharSequence) "Cancel", null);
                    builder.show();

                    new AsyncTaskRunner().execute(new String[0]);






                    break;
                case R.id.action_share_native:
                    File file = new File(((Album) MyCreationActivity.this.videoList.get(pos)).strImagePath);
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    String imagePath =  getIntent().getExtras().get("FinalURI").toString();
                    ArrayList<Uri> streamUris = new ArrayList<Uri>();
                    for (int i = 0; i < 10; i++) {
                        File tmpFile = new File(imagePath);
                        String authority = getPackageName() + ".fileprovider";
                        Uri tmp = FileProvider.getUriForFile(MyCreationActivity.this,authority, tmpFile);
                        streamUris.add(tmp);
                    }
                    Intent shareIntent = new Intent("android.intent.action.SEND");
                    shareIntent.setType("video/*");
                    shareIntent.putExtra("android.intent.extra.SUBJECT", new File(((Album) MyCreationActivity.this.videoList.get(pos)).strImagePath).getName());
                    shareIntent.putExtra("android.intent.extra.TITLE", new File(((Album) MyCreationActivity.this.videoList.get(pos)).strImagePath).getName());
                    shareIntent.putExtra("android.intent.extra.STREAM", streamUris.get(0));
                    MyCreationActivity.this.startActivity(Intent.createChooser(shareIntent, "Share Video"));
                    break;
            }
            return false;
        }
    }

    private class MyAlbum_Adapter extends Adapter<MyAlbum_Adapter.MyViewHolder> {

        public class MyViewHolder extends ViewHolder {
            private ImageView list_item_video_thumb;
            private TextView list_item_video_title;
            private Toolbar toolbar;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.list_item_video_thumb = (ImageView) itemView.findViewById(R.id.list_item_video_thumb);
                this.list_item_video_title = (TextView) itemView.findViewById(R.id.list_item_video_title);
                this.toolbar = (Toolbar) itemView.findViewById(R.id.list_item_video_toolbar);
            }
        }

        private MyAlbum_Adapter() {
        }

        @NonNull
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyCreationActivity.this).inflate(R.layout.my_album_item, null));
        }

        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            Glide.with(MyCreationActivity.this).load(((Album) MyCreationActivity.this.videoList.get(position)).strImagePath).into(holder.list_item_video_thumb);
            holder.list_item_video_thumb.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MyCreationActivity.this.startActivity(new Intent(MyCreationActivity.this, PlayVideoFromMyCreationActivity.class).putExtra("video_path", ((Album) MyCreationActivity.this.videoList.get(position)).strImagePath));
                }
            });
            holder.list_item_video_title.setText(new File(((Album) MyCreationActivity.this.videoList.get(position)).strImagePath).getName());
            MyCreationActivity.menu(holder.toolbar, R.menu.home_item_exported_video_local_menu, new MenuItemClickListener((Album) MyCreationActivity.this.videoList.get(position)));
        }

        public int getItemCount() {
            return MyCreationActivity.this.videoList.size();
        }
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_my_creation);
        getWindow().setFlags(1024, 1024);
        this.videoList = new ArrayList();

        rateUs();
        bindView();
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        refreshAd();
        new AsyncTaskRunner().execute(new String[0]);
    }

    private void bindView() {
        this.ivBack = (ImageView) findViewById(R.id.ivBack);
        this.ivBack.setOnClickListener(this);
        this.rv_my_album = (RecyclerView) findViewById(R.id.rv_my_album);
        this.rv_my_album.setLayoutManager(new GridLayoutManager((Context) this, 1, RecyclerView.VERTICAL, false));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, StartActivity.class).addFlags(335544320));
        finish();
    }

    public static void menu(Toolbar paramToolbar, int paramInt, OnMenuItemClickListener paramOnMenuItemClickListener) {
        paramToolbar.getMenu().clear();
        paramToolbar.inflateMenu(paramInt);
        paramToolbar.setOnMenuItemClickListener(paramOnMenuItemClickListener);
    }
    protected void onResume() {
        super.onResume();
    }

    private void rateUs() {
        id = Glob.getPref(this, "dialog_count");
        Log.d(TAG, "onCreate: pref" + id);
        if (id == 1 && !isFinishing()) {
            Log.d(TAG, "onCreate: pref");
            new Handler().postDelayed(new C07953(), 3000);
        }
        if (Glob.getBoolPref(this, "isRated")) {
            id++;
            if (id == 6) {
                id = 1;
            }
            Glob.setPref(this, "dialog_count", id);
        }
    }

    public void showDialogforshow() {
        final Dialog dialog = new Dialog(this, 16973839);
        dialog.requestWindowFeature(1);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = (int) (((double) displayMetrics.heightPixels) * 1.0d);
        int i2 = (int) (((double) displayMetrics.widthPixels) * 1.0d);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimarytrannew);
        dialog.getWindow().setLayout(i2, i);
        dialog.setContentView(R.layout.rate_dialog);
        TextView textView = (TextView) dialog.findViewById(R.id.rate);
        TextView textView2 = (TextView) dialog.findViewById(R.id.remindlater);
        TextView textView3 = (TextView) dialog.findViewById(R.id.nothanks);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });
        AdView mAdView = dialog.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ((ImageView) dialog.findViewById(R.id.img)).setAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MyCreationActivity.this.gotoStore();
                MyCreationActivity.id++;
                Glob.setPref(MyCreationActivity.this, "dialog_count", MyCreationActivity.id);
                Glob.setBoolPref(MyCreationActivity.this, "isRated", true);
                dialog.dismiss();
            }
        });
        textView2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Glob.dialog = false;
                Glob.setBoolPref(MyCreationActivity.this, "isRated", false);
                Glob.setPref(MyCreationActivity.this, "dialog_count", 1);
                dialog.cancel();
            }
        });
        textView3.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Glob.setBoolPref(MyCreationActivity.this, "isRated", false);
                Glob.setPref(MyCreationActivity.this, "dialog_count", 1);
                dialog.cancel();
            }
        });
        if (Glob.dialog) {
            dialog.show();
        }
    }

    public void gotoStore() {
        Intent myAppLinkToMarket = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
        myAppLinkToMarket.setFlags(268468224);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "You don't have Google Play installed", Toast.LENGTH_LONG).show();
        }
    }


    private UnifiedNativeAd nativeAd;

    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.nativead));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout =
                        findViewById(R.id.fl_adplaceholder);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.adunity, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(MyCreationActivity.this, "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
    }

}
