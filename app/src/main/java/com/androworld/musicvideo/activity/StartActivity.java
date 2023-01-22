package com.androworld.musicvideo.activity;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androworld.musicvideo.BuildConfig;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.androworld.musicvideo.MyApplication;
import com.androworld.musicvideo.R;
import com.androworld.musicvideo.service.CreateVideoService;
import com.androworld.musicvideo.service.ImageCreatorService;
import com.androworld.musicvideo.util.Ads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity implements OnClickListener {

    private boolean blnMyCreation = false;
    private boolean blnStart = false;
    public ImageView myalbum;
    public ImageView start;

    class C08281 implements DialogInterface.OnClickListener {
        C08281() {
        }

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case -1:
                    StartActivity.this.checkAndRequestPermissions();
                    return;
                default:
                    return;
            }
        }
    }



    private ImageView iv_share;
    private ImageView iv_reta;
    private ImageView iv_privecy;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_start);
        getWindow().setFlags(1024, 1024);

        this.start = (ImageView) findViewById(R.id.start);
        this.start.setOnClickListener(this);
        this.myalbum = (ImageView) findViewById(R.id.myalbum);
        this.myalbum.setOnClickListener(this);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_reta = (ImageView) findViewById(R.id.iv_reta);
        iv_privecy = (ImageView) findViewById(R.id.iv_privecy);


        iv_share.setOnClickListener(this);
        iv_reta.setOnClickListener(this);
        iv_privecy.setOnClickListener(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        refreshAd();

        if(Ads.mInterstitialAd == null){
            Ads.LoadAd(this);
        }

    }



    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myalbum:
                this.blnStart = false;
                this.blnMyCreation = true;
                if (VERSION.SDK_INT < 23) {
                    callmycreation();
                    return;
                } else if (checkAndRequestPermissions()) {
                    callmycreation();
                    return;
                } else {
                    return;
                }
            case R.id.start:
                this.blnStart = true;
                this.blnMyCreation = false;
                if (VERSION.SDK_INT < 23) {
                    callnext();
                    return;
                } else if (checkAndRequestPermissions()) {
                    callnext();
                    return;
                } else {
                    return;
                }
            case R.id.iv_share:

                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                }
                break;

            case R.id.iv_reta:

                Intent i3 = new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://details?id=" + getPackageName()));
                startActivity(i3);
                break;

            case R.id.iv_privecy:
                startActivity(new Intent(StartActivity.this,WebActivity.class));

                break;

            default:
                return;
        }
    }

    private void callmycreation() {
        Ads.Loadd(new Ads.Ad_lisoner() {
            @Override
            public void onSucssec(InterstitialAd mInterstitialAd) {
                blnMyCreation = false;
                startActivity(new Intent(StartActivity.this, MyCreationActivity.class));
            }

            @Override
            public void onun() {
                blnMyCreation = false;
                startActivity(new Intent(StartActivity.this, MyCreationActivity.class));
            }
        });
    }



    private boolean checkAndRequestPermissions() {
        int writeStorage = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        int readStorage = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        List<String> listPermissionsNeeded = new ArrayList();
        if (writeStorage != 0) {
            listPermissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (readStorage != 0) {
            listPermissionsNeeded.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (listPermissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 2000);
        return false;
    }

    private void callnext() {
        Ads.Loadd(new Ads.Ad_lisoner() {
            @Override
            public void onSucssec(InterstitialAd mInterstitialAd) {
                if (isVideoInprocess()) {
                    startActivity(new Intent(StartActivity.this, ProgressActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return;
                }
                startActivity(new Intent(StartActivity.this, ImageSelectionActivity.class));
                MyApplication.getInstance().getFolderList();
            }

            @Override
            public void onun() {
                if (isVideoInprocess()) {
                    startActivity(new Intent(StartActivity.this, ProgressActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return;
                }
                startActivity(new Intent(StartActivity.this, ImageSelectionActivity.class));
                MyApplication.getInstance().getFolderList();
            }
        });
    }

    private boolean isVideoInprocess() {
        return MyApplication.isMyServiceRunning(this, CreateVideoService.class) || MyApplication.isMyServiceRunning(this, ImageCreatorService.class);
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2000:
                Map<String, Integer> perms = new HashMap();
                perms.put("android.permission.WRITE_EXTERNAL_STORAGE", Integer.valueOf(0));
                perms.put("android.permission.READ_EXTERNAL_STORAGE", Integer.valueOf(0));
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], Integer.valueOf(grantResults[i]));
                    }
                    if (((Integer) perms.get("android.permission.WRITE_EXTERNAL_STORAGE")).intValue() == 0 && ((Integer) perms.get("android.permission.READ_EXTERNAL_STORAGE")).intValue() == 0) {
                        if (this.blnStart) {
                            callnext();
                            return;
                        } else if (this.blnMyCreation) {
                            callmycreation();
                            return;
                        } else {
                            return;
                        }
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE")) {
                        showDialogOK("Permission required for this app", new C08281());
                        return;
                    } else {
                        Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                return;
            default:
                return;
        }
    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new Builder(this).setMessage((CharSequence) message).setPositiveButton((CharSequence) "OK", okListener).setNegativeButton((CharSequence) "Cancel", okListener).create().show();
    }

    public void onBackPressed() {
        showAlertDialogButtonClicked();
    }

    public void showAlertDialogButtonClicked() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure You Want Exit?");


        builder.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchMarket();
            }
        });
        builder.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private UnifiedNativeAd nativeAd;

    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.nativead));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {

            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {


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
                Toast.makeText(StartActivity.this, "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {


        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);


        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());



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




        adView.setNativeAd(nativeAd);



        VideoController vc = nativeAd.getVideoController();


    }

}
