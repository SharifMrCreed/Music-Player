package com.alle.san.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alle.san.musicplayer.adapters.AlbumRvAdapter;
import com.alle.san.musicplayer.adapters.ArtistRvAdapter;

import static com.alle.san.musicplayer.MainActivity.allAlbums;
import static com.alle.san.musicplayer.MainActivity.allArtists;

public class ArtistsFragment extends Fragment {

    RecyclerView rvAlbumList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        rvAlbumList = view.findViewById(R.id.rv_album_fragment);
        initRecyclerView();

        return view;
    }
    private void initRecyclerView() {
        ArtistRvAdapter artistRvAdapter = new ArtistRvAdapter(getContext());
        rvAlbumList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvAlbumList.setAdapter(artistRvAdapter);
        artistRvAdapter.setArtists(allArtists);
    }
}