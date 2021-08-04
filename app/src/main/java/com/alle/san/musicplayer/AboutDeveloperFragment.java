package com.alle.san.musicplayer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alle.san.musicplayer.util.UtilInterfaces;

import static com.alle.san.musicplayer.util.Globals.ABOUT_DEVELOPER;


public class AboutDeveloperFragment extends Fragment {
    UtilInterfaces.ContactThrough contactThrough;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about_developer, container, false);
        TextView textView = view.findViewById(R.id.dev);
        view.findViewById(R.id.facebook_button).setOnClickListener(v -> contactThrough.facebook());
        view.findViewById(R.id.gmail_button).setOnClickListener(v -> contactThrough.gmail());
        view.findViewById(R.id.linkedIn_button).setOnClickListener(v -> contactThrough.linkedIn());
        view.findViewById(R.id.whatsApp_button).setOnClickListener(v -> contactThrough.whatsApp());
        view.findViewById(R.id.instagram_button).setOnClickListener(v -> contactThrough.instagram());
        textView.setText(ABOUT_DEVELOPER);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contactThrough = (UtilInterfaces.ContactThrough) context;
    }
}