package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.gallery.model.EventLab;
import com.example.gallery.model.EventPhoto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

public class EventPhotoFragment extends Fragment {

    private static final String ARG_EVENT_ID = "crime_id";
    private static final String TAG = "tag";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_PHOTO = 3;

    private EventPhoto mEventPhoto;
    private TextView mPhotoLocation;
    private ImageView mPhotoView;
    private TextView mTitle;
    private TextView mDescription;
    private File mPhotoFile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID eventId = (UUID) getArguments().getSerializable(ARG_EVENT_ID);
        mEventPhoto = EventLab.get(getActivity()).getEvent(eventId);
        mPhotoFile = EventLab.get(getActivity()).getPhotoFile(mEventPhoto);
    }


    public static EventPhotoFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, uuid);
        EventPhotoFragment fragment = new EventPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_event:
                EventLab.get(getActivity()).deleteEvent(mEventPhoto);
                getActivity().finish();
                return true;

            case R.id.menu_item_send_event:
                Uri photo = FileProvider.getUriForFile(getActivity(), AddEventFragment.FILE_PROVIDER, mPhotoFile);
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(mEventPhoto.getDescription() +
                                "\n----------------------------\n" +
                                mEventPhoto.getLocation())
                        .setSubject(mEventPhoto.getTitle())
                        .setStream(photo)
                        .getIntent();
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_event, container, false);
        mPhotoView = (ImageView) view.findViewById(R.id.event_photo);

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullPhotoActivity.class);
                intent.putExtra("photoFile", mPhotoFile);
                startActivity(intent);
            }
        });
        mPhotoLocation = (TextView) view.findViewById(R.id.image_location_text);
        mPhotoLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra(MapsActivity.EXTRA_UUID, mEventPhoto.getUUID());
                startActivity(intent);
            }
        });
        updatePhotoView();

        mTitle = (TextView) view.findViewById(R.id.event_title);
        mTitle.setText(mEventPhoto.getTitle());

        mDescription = (TextView) view.findViewById(R.id.event_description);
        mDescription.setText(mEventPhoto.getDescription());

        return view;
    }


    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Picasso.get().load(mPhotoFile).into(mPhotoView);
        }
        mPhotoLocation.setText(mEventPhoto.getLocation());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventLab.get(getActivity()).updateEvent(mEventPhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            mTitle.setText("new title");
        }
    }
}
