package com.example.gallery;

import androidx.fragment.app.Fragment;

public class EventListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return EventListFragment.newInstance();
    }
}
