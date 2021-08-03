package com.alle.san.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.ArtistRvAdapter;
import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.util.StorageUtil;

import java.util.ArrayList;

public class ArtistsFragment extends Fragment {

    private ArrayList<ArtistModel> allArtists;
    RecyclerView rvArtistList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        rvArtistList = view.findViewById(R.id.rv_album_fragment);
        LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
        allArtists = StorageUtil.getArtists(getContext());
        if (allArtists == null) allArtists = new ArrayList<>();
        if (allArtists.isEmpty()) nothingLayout.setVisibility(View.VISIBLE);
        initRecyclerView();
        return view;
    }
    private void initRecyclerView() {
        ArtistRvAdapter artistRvAdapter = new ArtistRvAdapter(getContext(), null);
        rvArtistList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvArtistList.setAdapter(artistRvAdapter);
        artistRvAdapter.setArtists(allArtists);
    }
}