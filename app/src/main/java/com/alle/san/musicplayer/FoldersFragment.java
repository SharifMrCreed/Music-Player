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

import static com.alle.san.musicplayer.util.Globals.FOLDERS_FRAGMENT_TAG;

public class FoldersFragment extends Fragment {
    RecyclerView rvArtistList;
    private ArrayList<ArtistModel> allFolders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        rvArtistList = view.findViewById(R.id.rv_album_fragment);
        LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
        allFolders = StorageUtil.getFolders(getContext());
        if (allFolders == null) allFolders = new ArrayList<>();
        if (allFolders.isEmpty()) nothingLayout.setVisibility(View.VISIBLE);

        initFoldersRecyclerView();
        return view;
    }

    private void initFoldersRecyclerView() {
        ArtistRvAdapter artistRvAdapter = new ArtistRvAdapter(getContext(), FOLDERS_FRAGMENT_TAG);
        rvArtistList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvArtistList.setAdapter(artistRvAdapter);
        artistRvAdapter.setArtists(allFolders);
    }
}