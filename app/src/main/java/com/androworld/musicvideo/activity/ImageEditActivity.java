package com.androworld.musicvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.android.gms.ads.InterstitialAd;
import com.androworld.musicvideo.MyApplication;
import com.androworld.musicvideo.R;
import com.androworld.musicvideo.adapters.ImageEditAdapter;
import com.androworld.musicvideo.util.Ads;
import com.androworld.musicvideo.view.EmptyRecyclerView;

public class ImageEditActivity extends AppCompatActivity {
    Callback _ithCallback = new C10141();
    private MyApplication application;
    private ImageEditAdapter imageEditAdapter;
    public boolean isFromPreview = false;
    private EmptyRecyclerView rvSelectedImages;
    private Toolbar toolbar;


    class C10141 extends Callback {
        C10141() {
        }

        public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
            if (actionState == 0) {
                ImageEditActivity.this.imageEditAdapter.notifyDataSetChanged();
            }
        }

        public void onMoved(RecyclerView recyclerView, ViewHolder viewHolder, int fromPos, ViewHolder target, int toPos, int x, int y) {
            ImageEditActivity.this.imageEditAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            ImageEditActivity.this.application.min_pos = Math.min(ImageEditActivity.this.application.min_pos, Math.min(fromPos, toPos));
            MyApplication.isBreak = true;
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
            return true;
        }

        public void onSwiped(ViewHolder viewHolder, int direction) {
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return Callback.makeFlag(2, 51);
        }
    }


    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_movie_album);
        getWindow().setFlags(1024, 1024);
        this.isFromPreview = getIntent().hasExtra(ImageSelectionActivity.EXTRA_FROM_PREVIEW);
        this.application = MyApplication.getInstance();
        this.application.isEditModeEnable = true;
        bindView();
        init();
        addListener();


    }




    private void bindView() {
        this.rvSelectedImages = (EmptyRecyclerView) findViewById(R.id.rvVideoAlbum);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager((Context) this, 2, RecyclerView.VERTICAL, false);
        this.imageEditAdapter = new ImageEditAdapter(this);
        this.rvSelectedImages.setLayoutManager(gridLayoutManager);
        this.rvSelectedImages.setItemAnimator(new DefaultItemAnimator());
        this.rvSelectedImages.setEmptyView(findViewById(R.id.list_empty));
        this.rvSelectedImages.setAdapter(this.imageEditAdapter);
        new ItemTouchHelper(this._ithCallback).attachToRecyclerView(this.rvSelectedImages);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        ((TextView) this.toolbar.findViewById(R.id.toolbar_title)).setText(getString(R.string.arrange_images));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void addListener() {


    }

    protected void onResume() {
        super.onResume();
        if (this.imageEditAdapter != null) {
            this.imageEditAdapter.notifyDataSetChanged();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_selection, menu);
        menu.removeItem(R.id.menu_clear);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                if (!this.isFromPreview) {
                    super.onBackPressed();
                    break;
                }
                done();
                break;
            case R.id.menu_done:
                done();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void done() {
        this.application.isEditModeEnable = false;
        if (this.isFromPreview) {
            setResult(-1);
            finish();
            return;
        }
        loadPreview();
    }

    private void loadPreview() {
        Ads.Loadd(new Ads.Ad_lisoner() {
            @Override
            public void onSucssec(InterstitialAd mInterstitialAd) {
                startActivity(new Intent(ImageEditActivity.this, PreviewActivity.class));
            }

            @Override
            public void onun() {
                startActivity(new Intent(ImageEditActivity.this, PreviewActivity.class));
            }
        });

    }

    public void onBackPressed() {
        if (this.isFromPreview) {
            done();
        } else {
            super.onBackPressed();
        }
    }


}
