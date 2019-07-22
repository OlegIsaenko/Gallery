package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.model.Event;
import com.example.gallery.model.EventLab;

import java.util.UUID;

public class EditEventFragment extends Fragment {
    public static final String EXTRA_ID = "event_id";
    private Event mEvent;
    private EditText mEditTitle;
    private EditText mEditDescription;
    private Button mConfirmButton;

    public static EditEventFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ID, uuid);
        EditEventFragment fragment = new EditEventFragment();
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
        View view = inflater.inflate(R.layout.event_edit, container, false);

        mEditTitle = view.findViewById(R.id.edit_title);
        mEditTitle.setText(mEvent.getTitle());

        mEditDescription = view.findViewById(R.id.edit_description);
        mEditDescription.setText(mEvent.getDescription());

        mConfirmButton = view.findViewById(R.id.apply_changes_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newTitle = mEditTitle.getText().toString();
                mEvent.setTitle(newTitle);
                String newDescription = mEditDescription.getText().toString();
                mEvent.setDescription(newDescription);
                EventLab.get(getActivity()).updateEvent(mEvent);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.event_container, ViewEventFragment.newInstance(mEvent.getUUID()));
                transaction.commit();
            }
        });
        return view;
    }
}
