package com.alle.san.musicplayer;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.ReadExternalStorage.getBitmap;
import static com.alle.san.musicplayer.util.ReadExternalStorage.getSongsFromStorage;

public class SongListFragment extends Fragment {

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = view.findViewById(R.id.rv_song_list);

        displaySongNames();
        return view;
    }


    private void displaySongNames() {
        final ArrayList<MusicFile> songs = getSongsFromStorage(getContext());
//        TODO:
//        for (MusicFile musicFile: songs){
//            musicFile.setAlbumImage(getBitmap(musicFile.getData()));
//        }

        recyclerView.setAdapter(new SongRecyclerAdapter(songs, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    }
}