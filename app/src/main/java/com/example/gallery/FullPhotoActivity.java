package com.example.gallery;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;

public class FullPhotoActivity extends AppCompatActivity {
    private File mPhotoFile;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_photo_activity);
        getSupportActionBar().hide();
//        mEventPhoto = EventLab.get(this).getEvent((UUID) getIntent().getSerializableExtra("UUID"));
        mPhotoFile = (File) getIntent().getSerializableExtra("photoFile");
        mImageView = (ImageView) findViewById(R.id.full_photo_view);
        Picasso.get().load(mPhotoFile).into(mImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
