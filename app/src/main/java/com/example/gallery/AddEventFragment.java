package com.example.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.gallery.model.EventLab;
import com.example.gallery.model.EventPhoto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class AddEventFragment extends Fragment{

    private static final String ARG_EVENT_ID = "crime_id";
    private static final String TAG = "tag";
    private static final int REQUEST_PHOTO = 3;

    private ImageView mPhotoView;
    private EditText mTitle;
    private EditText mDescription;
    private File mPhotoFile;
    private Button mAddLocationButton;
    private String eventTitle;
    private String eventDescription;

    private LocationManager mLocationManager;
    private Location mLocation;
    private LocationListener mLocationListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
        goCamera();
    }

    private void goCamera() {
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalFilesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mPhotoFile = new File(externalFilesDir, "photo_file.jpg");
        Uri uri = Uri.fromFile(mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(captureImage, REQUEST_PHOTO);
    }

    public static AddEventFragment newInstance() {
        return new AddEventFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_event_creator, container, false);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mPhotoView = (ImageView) view.findViewById(R.id.add_photo);
        mTitle = (EditText) view.findViewById(R.id.add_title);
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventTitle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDescription = (EditText) view.findViewById(R.id.add_description);
        mDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventDescription = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAddLocationButton = (Button) view.findViewById(R.id.get_location_button);
        mAddLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation = getLocation();
                if (mLocation != null) {
                    GeocoderTest geocoder = new GeocoderTest();
                    String location = geocoder.coordsToAddres(getActivity(), mLocation);

                    mDescription.setText(location);
                } else {
                    mDescription.setText("location is null");
                }

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_event_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_event:
                EventPhoto eventPhoto = new EventPhoto();
                eventPhoto.setTitle(eventTitle);
                eventPhoto.setDescription(eventDescription);
                eventPhoto.setupLocation(mLocation);
                EventLab.get(getActivity()).addEvent(eventPhoto);
                mPhotoFile.renameTo(EventLab.get(getActivity()).getPhotoFile(eventPhoto));
                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            getActivity().finish();
        }
        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            mTitle.setText("new title");
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Picasso.get().load(mPhotoFile)
                    .resize(1024, 768).into(mPhotoView);
//            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
//            mPhotoView.setImageBitmap(bitmap);
        }
    }

    public Location getLocation() {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            }
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            List<String> providers = mLocationManager.getProviders(criteria, true);
            if (providers == null || providers.size() == 0) {
                Log.i(TAG, "onActivityResult: NO LOCATION PROVIDERS");
                return null;
            }
            for (String prov : providers) {
                Log.i(TAG, "getLocation: provider: " + prov + " size " + providers.size());
            }
            String preffered = providers.get(0);
            mLocationManager.requestLocationUpdates(preffered, 0, 0, mLocationListener);
            mLocation = mLocationManager.getLastKnownLocation(preffered);
            if (mLocation == null) {
                Log.i(TAG, "getLocation: " + preffered + ": NULL" );
            } else {
                Log.i(TAG, "getLocation: " + preffered + ": " + mLocation.toString());
            }
        }
        return mLocation;
    }
}
