package com.example.gallery;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullPhotoActivity extends Activity {
    private File mPhotoFile;
    private ZoomageView mZoomageView;
    public static final String EXTRA_FILE_NAME = "photoFileName";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_photo_activity);
        mPhotoFile = (File) getIntent().getSerializableExtra(EXTRA_FILE_NAME);
        mZoomageView = findViewById(R.id.zoom_view_image);
        Picasso.get().load(mPhotoFile).into(mZoomageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
