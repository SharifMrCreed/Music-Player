package com.alle.san.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.ViewChanger;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.ReadExternalStorage.getSongsFromStorage;

public class SongListFragment extends Fragment implements ViewChanger.Filter {

    RecyclerView recyclerView;
    SongRecyclerAdapter songRecyclerAdapter;
    private ArrayList<MusicFile> songs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = view.findViewById(R.id.rv_song_list);
        songs = getSongsFromStorage(getContext());
        songRecyclerAdapter = new SongRecyclerAdapter(songs, getContext());

        displaySongNames();
        return view;
    }


    private void displaySongNames() {
//        TODO:
//        for (MusicFile musicFile: songs){
//            musicFile.setAlbumImage(getBitmap(musicFile.getData()));
//        }
        recyclerView.setAdapter(songRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    }

    @Override
    public void filter(String s) {
        ArrayList<MusicFile> newList = new ArrayList<>();
        for (MusicFile musicFile : songs) if (musicFile.getTitle().contains(s)) newList.add(musicFile);
        songRecyclerAdapter.setSongs(newList);
    }
}