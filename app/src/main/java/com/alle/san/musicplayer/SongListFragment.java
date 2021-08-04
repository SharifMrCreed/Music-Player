package com.alle.san.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.StorageUtil.getSongsFromStorage;

public class SongListFragment extends Fragment implements UtilInterfaces.Filter {

    RecyclerView recyclerView;
    SongRecyclerAdapter songRecyclerAdapter;
    private ArrayList<MusicFile> songs = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = view.findViewById(R.id.rv_song_list);
        songs = getSongsFromStorage(getContext(), StorageUtil.getSortOrder(getContext()), Globals.getOrder(getContext()) );
        songRecyclerAdapter = new SongRecyclerAdapter(songs);
        LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
        if (songs.isEmpty()) nothingLayout.setVisibility(View.VISIBLE);
        displaySongNames();
        return view;
    }


    private void displaySongNames() {
        recyclerView.setAdapter(songRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    }

    @Override
    public void filter(String s) {
        ArrayList<MusicFile> newList = new ArrayList<>();
        for (MusicFile musicFile : songs) {
            if (musicFile.getTitle().toLowerCase().contains(s)) newList.add(musicFile);
            else if (musicFile.getArtist().toLowerCase().contains(s)) newList.add(musicFile);
        }
        songRecyclerAdapter.setSongs(newList);
    }
}