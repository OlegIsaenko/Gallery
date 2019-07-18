package com.example.gallery.activities;

import androidx.fragment.app.Fragment;

import com.example.gallery.EventListFragment;

public class EventListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return EventListFragment.newInstance();
    }
}
