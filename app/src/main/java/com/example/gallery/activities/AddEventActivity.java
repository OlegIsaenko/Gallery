package com.example.gallery.activities;

import androidx.fragment.app.Fragment;

import com.example.gallery.AddEventFragment;

public class AddEventActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return AddEventFragment.newInstance();
    }
}
