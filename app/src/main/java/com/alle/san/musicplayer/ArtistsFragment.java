package com.alle.san.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alle.san.musicplayer.adapters.ArtistRvAdapter;
import com.alle.san.musicplayer.util.StorageUtil;

import static com.alle.san.musicplayer.MainActivity.allArtists;

public class ArtistsFragment extends Fragment {

    RecyclerView rvArtistList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        rvArtistList = view.findViewById(R.id.rv_album_fragment);
        initRecyclerView();
        LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
        if (allArtists.isEmpty()){
            nothingLayout.setVisibility(View.VISIBLE);
        }
        return view;
    }
    private void initRecyclerView() {
        ArtistRvAdapter artistRvAdapter = new ArtistRvAdapter(getContext());
        rvArtistList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvArtistList.setAdapter(artistRvAdapter);
        artistRvAdapter.setArtists(allArtists);
    }
}