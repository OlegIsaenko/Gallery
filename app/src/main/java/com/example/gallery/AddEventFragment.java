package com.example.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.gallery.model.EventLab;
import com.example.gallery.model.EventPhoto;
import com.example.gallery.utils.GeocoderTest;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


public class AddEventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "crime_id";
    private static final String TAG = "tag";
    private static final int REQUEST_PHOTO = 3;
    public static final String FILE_PROVIDER = "com.example.fileprovider";

    private ImageView mPhotoView;
    private TextView mImageLocation;
    private EditText mTitle;
    private EditText mDescription;
    private File mPhotoFile;
    private Button mAddLocationButton;
    private String eventTitle;
    private String eventDescription;
    private String eventLocation;

    private LocationManager mLocationManager;
    private Location mLocation;
    private LocationListener mLocationListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                createLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if (getActivity().checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PackageManager.PERMISSION_GRANTED);
                    return;
                }
                createLocation(mLocationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        capturePhoto();
    }

    private void createLocation(Location location) {
        if (location == null) {
            Toast.makeText(getActivity(), R.string.gps_not_enabled, Toast.LENGTH_SHORT).show();
            return;
        }
        GeocoderTest geocoderTest = new GeocoderTest();
        eventLocation = geocoderTest.coordsToAddres(getActivity(), location);
        mImageLocation.setText(eventLocation);
        mLocation = location;
    }

    private void capturePhoto() {
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mPhotoFile = createPhotoFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mPhotoFile != null) {
            Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER,
                    mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(captureImage, REQUEST_PHOTO);
        }
    }

    private File createPhotoFile() throws IOException {
        String photoFileName = "temp_file_";
        Log.d(TAG, "createPhotoFile: string " + photoFileName);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(
                photoFileName,
                ".jpg",
                storageDir
        );
        Log.d(TAG, "createPhotoFile: " + photo.getName());
        return photo;
    }

    public static AddEventFragment newInstance() {
        return new AddEventFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mPhotoView = (ImageView) view.findViewById(R.id.add_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullPhotoActivity.class);
                intent.putExtra(FullPhotoActivity.EXTRA_FILE_NAME, mPhotoFile);
                startActivity(intent);
            }
        });
        updatePhotoView();
        mImageLocation = (TextView) view.findViewById(R.id.image_location_text);
        mImageLocation.setText(eventLocation);
        mImageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });

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
                if (mLocation != null) {
                    GeocoderTest geocoderTest = new GeocoderTest();
                    eventLocation = geocoderTest.coordsToAddres(getActivity(), mLocation);
                    mImageLocation.setText(eventLocation);

                } else {
                    Toast.makeText(getActivity(), R.string.gps_not_enabled, Toast.LENGTH_SHORT).show();
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
                eventPhoto.setLatLng(mLocation);
                eventPhoto.setLocationText(eventLocation);
                EventLab.get(getActivity()).addEvent(eventPhoto);
                Log.d(TAG, "onOptionsItemSelected: " + EventLab.get(getActivity()).getPhotoFile(eventPhoto).getName());
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
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Picasso.get().load(mPhotoFile).into(mPhotoView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PackageManager.PERMISSION_GRANTED:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mAddLocationButton.setEnabled(false);
                }
        }
    }
}
