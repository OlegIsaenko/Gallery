package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class EventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "crime_id";
    private static final String TAG = "tag";

    private static final int REQUEST_PHOTO = 3;

    private EventPhoto mEvent;
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
        mEvent = EventLab.get(getActivity()).getEvent(eventId);
        mPhotoFile = EventLab.get(getActivity()).getPhotoFile(mEvent);
    }


    public static EventFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, uuid);
        EventFragment fragment = new EventFragment();
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
                EventLab.get(getActivity()).deleteEvent(mEvent);
                getActivity().finish();
                return true;

            case R.id.menu_item_send_event:
                Uri photo = FileProvider.getUriForFile(getActivity(), AddEventFragment.FILE_PROVIDER, mPhotoFile);
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
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


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mPhotoView = view.findViewById(R.id.event_photo);

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullPhotoActivity.class);
                intent.putExtra(FullPhotoActivity.EXTRA_FILE_NAME, mPhotoFile);
                startActivity(intent);
            }
        });
        mPhotoLocation = view.findViewById(R.id.image_location_text);
        mPhotoLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra(MapsActivity.EXTRA_UUID, mEvent.getUUID());
                startActivity(intent);
            }
        });
        updatePhotoView();

        mTitle = view.findViewById(R.id.event_title);
        mTitle.setText(mEvent.getTitle());

        mDescription = view.findViewById(R.id.event_description);
        mDescription.setText(mEvent.getDescription());

        return view;
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
    public void onPause() {
        super.onPause();
        EventLab.get(getActivity()).updateEvent(mEvent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            mTitle.setText(R.string.enter_new_title);
        }
    }
}
