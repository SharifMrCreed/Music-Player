package com.alle.san.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.alle.san.musicplayer.util.Globals.ABOUT_DEVELOPER;


public class AboutDeveloperFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about_developer, container, false);
        TextView textView = view.findViewById(R.id.dev);
        textView.setText(ABOUT_DEVELOPER);
        return view;
    }
}