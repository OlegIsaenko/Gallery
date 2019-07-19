package com.example.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.model.Event;
import com.example.gallery.model.EventLab;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

public class EventActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "eventId";
    public static final String EDIT_OR_NOT = "editOrNot";
    private static final String TAG = "tag";
    private Event mEvent;
    private ImageView mPhotoView;
    private TextView mPhotoLocation;
    private File mPhotoFile;
    private FragmentTransaction mTransaction;
    private boolean isEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (savedInstanceState != null) {
            isEdit = savedInstanceState.getBoolean(EDIT_OR_NOT);
            Log.i(TAG, "onCreate: EDITED " + isEdit);
        } else {
            isEdit = false;
        }

        UUID eventId = (UUID) getIntent().getSerializableExtra(EXTRA_EVENT_ID);
        mEvent = EventLab.get(this).getEvent(eventId);
        mPhotoFile = EventLab.get(this).getPhotoFile(mEvent);

        mPhotoView = findViewById(R.id.event_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, FullPhotoActivity.class);
                intent.putExtra(FullPhotoActivity.EXTRA_FILE_NAME, mPhotoFile);
                startActivity(intent);
            }
        });

        mPhotoLocation = findViewById(R.id.image_location_text);
        mPhotoLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, MapsActivity.class);
                intent.putExtra(MapsActivity.EXTRA_UUID, mEvent.getUUID());
                startActivity(intent);
            }
        });
        updatePhotoView();

        Log.i(TAG, "onCreate: EDITED " + isEdit);

        if (!isEdit) {
            ViewEventFragment viewEventFragment = ViewEventFragment.newInstance(mEvent.getUUID());
            mTransaction = getSupportFragmentManager().beginTransaction();
            mTransaction.add(R.id.event_container, viewEventFragment);
            mTransaction.commit();
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Picasso.get().load(mPhotoFile).into(mPhotoView);
        }
        mPhotoLocation.setText(mEvent.getLocation());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_event:
                EventLab.get(this).deleteEvent(mEvent);
                EventActivity.this.finish();
                return true;

            case R.id.menu_item_send_event:
                Uri photo = FileProvider.getUriForFile(this, AddEventFragment.FILE_PROVIDER, mPhotoFile);
                Intent i = ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText(mEvent.getDescription() +
                                "\n----------------------------\n" +
                                mEvent.getLocation())
                        .setSubject(mEvent.getTitle())
                        .setStream(photo)
                        .getIntent();
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);

            case R.id.menu_item_update_event:
                isEdit = true;
                Log.i(TAG, "onCreate: EDITED onclick " + isEdit);
                mTransaction = getSupportFragmentManager().beginTransaction();
                mTransaction.replace(R.id.event_container, EditEventFragment.newInstance(mEvent.getUUID()));
                mTransaction.commit();


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EDIT_OR_NOT, isEdit);
    }
}
