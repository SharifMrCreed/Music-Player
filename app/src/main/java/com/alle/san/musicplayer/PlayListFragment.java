package com.alle.san.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.PlaylistRvAdapter;
import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;

public class PlayListFragment extends Fragment {

    RecyclerView rvPlaylist;

    ArrayList<ArrayList<MusicFile>> musicFiles = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        rvPlaylist = view.findViewById(R.id.rv_playlist_fragment);

        if (musicFiles != null) initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        rvPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvPlaylist.setAdapter(new PlaylistRvAdapter(getContext(), musicFiles));
    }
}