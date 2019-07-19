package com.example.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gallery.model.Event;
import com.example.gallery.model.EventLab;

import java.util.UUID;

public class ViewEventFragment extends Fragment {

    public static final String EXTRA_ID = "evend_id";

    private Event mEvent;
    private TextView mTitle;
    private TextView mDescription;

    public static ViewEventFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ID, uuid);
        ViewEventFragment fragment = new ViewEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID uuid = (UUID) getArguments().getSerializable(EXTRA_ID);
        mEvent = EventLab.get(getActivity()).getEvent(uuid);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_view, container, false);

        mTitle = view.findViewById(R.id.event_title);
        mTitle.setText(mEvent.getTitle());

        mDescription = view.findViewById(R.id.event_description);
        mDescription.setText(mEvent.getDescription());

        return view;
    }
}
