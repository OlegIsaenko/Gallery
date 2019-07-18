package com.example.gallery;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.gallery.model.EventLab;
import com.example.gallery.model.EventPhoto;
import java.util.List;
import java.util.UUID;

public class EventPagerActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "eventId";
    private ViewPager mViewPager;
    private List<EventPhoto> mPhotos;

    public static Intent newIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, EventPagerActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, uuid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pager);
        final UUID eventId = (UUID) getIntent().getSerializableExtra(EXTRA_EVENT_ID);
        mViewPager = findViewById(R.id.activity_event_view_pager);
        mPhotos = EventLab.get(this).getEvents();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                EventPhoto eventPhoto = mPhotos.get(position);
                return EventFragment.newInstance(eventPhoto.getUUID());
            }

            @Override
            public int getCount() {
                return mPhotos.size();
            }
        });

        for (int i = 0; i < mPhotos.size(); i++) {
            if (mPhotos.get(i).getUUID().equals(eventId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
