package com.alle.san.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.PlayListRvAdapter;
import com.alle.san.musicplayer.util.StorageUtil;

public class PlayListFragment extends Fragment {

    RecyclerView rvPlaylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        rvPlaylist = view.findViewById(R.id.rv_play_list);
        LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
        if (StorageUtil.getPlaylists(getContext()) == null)
            nothingLayout.setVisibility(View.VISIBLE);
        initRecyclerView();

        return view;
    }
    private void initRecyclerView() {
        PlayListRvAdapter playListRvAdapter = new PlayListRvAdapter(getContext(), PlayListRvAdapter.GRID_VIEW);
        rvPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvPlaylist.setAdapter(playListRvAdapter);
    }

}