package com.alle.san.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.AlbumRvAdapter;

import static com.alle.san.musicplayer.MainActivity.allAlbums;

public class AlbumsFragment extends Fragment {
    RecyclerView rvAlbumList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        rvAlbumList = view.findViewById(R.id.rv_album_fragment);
        initRecyclerView();
        LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
        if (allAlbums.isEmpty()) nothingLayout.setVisibility(View.VISIBLE);
        return view;
    }

    private void initRecyclerView() {
        AlbumRvAdapter albumRvAdapter = new AlbumRvAdapter(getContext());
        rvAlbumList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvAlbumList.setAdapter(albumRvAdapter);
        albumRvAdapter.setAlbums(allAlbums);
    }



}