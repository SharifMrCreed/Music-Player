package com.alle.san.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.AlbumRvAdapter;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.StorageUtil;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment {
    RecyclerView rvAlbumList;

    private ArrayList<MusicFile> allAlbums;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allAlbums = StorageUtil.getAlbums(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        rvAlbumList = view.findViewById(R.id.rv_album_fragment);
        LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
        if (allAlbums == null) allAlbums = new ArrayList<>();
        if (allAlbums.isEmpty()) nothingLayout.setVisibility(View.VISIBLE);

        initAlbumsRecyclerView();
        return view;
    }

    private void initAlbumsRecyclerView() {
        AlbumRvAdapter albumRvAdapter = new AlbumRvAdapter(getContext());
        rvAlbumList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvAlbumList.setAdapter(albumRvAdapter);
        albumRvAdapter.setAlbums(allAlbums);
    }




}