package com.androworld.musicvideo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androworld.musicvideo.R;

public class WebActivity extends AppCompatActivity {


    private static final String TAG = "Main";
    private ProgressDialog progressBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_web);
        getWindow().setFlags(1024, 1024);
        WebView webview = (WebView)findViewById(R.id.webView);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog alertDialog = new AlertDialog.Builder(WebActivity.this).create();

        progressBar = ProgressDialog.show(WebActivity.this, "Privacy Policy", "Loading...");

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
                Toast.makeText(WebActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });

        //Change privecy
        webview.loadUrl("https://www.google.com/");

    }

    public void onBackPressed() {
        finish();
    }
}
