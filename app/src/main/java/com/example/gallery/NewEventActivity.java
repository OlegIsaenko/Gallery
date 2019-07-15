package com.example.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class NewEventActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AddEventFragment();
    }
}
