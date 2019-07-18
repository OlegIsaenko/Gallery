package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.model.EventLab;
import com.example.gallery.model.EventPhoto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class EventListFragment extends Fragment {

    public static final String TAG = "hyeg";

    private RecyclerView mEventRecyclerView;
    private EventAdapter mAdapter;


    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        updatePermission();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);
        mEventRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_gallery_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mEventRecyclerView.setLayoutManager(linearLayoutManager);
        updateUI();

        return view;
    }

    private void updatePermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_event:
                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_item_map:
                Intent mapIntent = new Intent(getActivity(), MapsActivity.class);
//                mapIntent.putExtra("mylocation", true);
                startActivity(mapIntent);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    private void updateUI() {
        EventLab eventLab = EventLab.get(getActivity());
        List<EventPhoto> events = eventLab.getEvents();

        if (mAdapter == null) {
            mAdapter = new EventAdapter(events);
            mEventRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setEvents(events);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private TextView mTextView;
        private TextView mDescription;
        private EventPhoto mPhoto;

        public EventHolder(View eventView) {
            super(eventView);
            eventView.setOnClickListener(this);
            mImageView = (ImageView) eventView.findViewById(R.id.photo_list_image_view);
            mTextView = (TextView) eventView.findViewById(R.id.photo_list_text_view);
            mDescription = (TextView) eventView.findViewById(R.id.photo_list_description_view);

        }

        public void bindEvent(EventPhoto eventPhoto) {
            mPhoto = eventPhoto;
            File bitmap = EventLab.get(getActivity()).getPhotoFile(eventPhoto);
            if (bitmap != null) {
                Picasso.get().load(bitmap).into(mImageView);
            } else {
                mImageView.setImageDrawable(null);
            }
            mTextView.setText(mPhoto.getTitle());
            mDescription.setText(mPhoto.getDescription());
        }

        @Override
        public void onClick(View v) {
            Intent intent = EventPagerActivity.newIntent(getActivity(), mPhoto.getUUID());
            startActivity(intent);
        }
    }


    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {
        private List<EventPhoto> mPhotos;

        public EventAdapter(List<EventPhoto> photos) {
            mPhotos = photos;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.event_holder_view, parent, false);
            return new EventHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventHolder holder, int position) {
            EventPhoto eventPhoto = mPhotos.get(position);
            holder.bindEvent(eventPhoto);
        }

        @Override
        public int getItemCount() {
            return mPhotos.size();
        }

        private void setEvents(List<EventPhoto> photos) {
            mPhotos = photos;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
