package com.alle.san.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alle.san.musicplayer.adapters.AlbumRvAdapter;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.ReadExternalStorage;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;


public class AlbumsFragment extends Fragment {
    RecyclerView rvAlbumList;
    int i = 0;
    private final ArrayList<ArrayList<MusicFile>> allAlbums = new ArrayList<>();
    HashSet<String> albumNames = new HashSet<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        rvAlbumList = view.findViewById(R.id.rv_album_fragment);
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        ArrayList<MusicFile> allSongs = ReadExternalStorage.getSongsFromStorage(getContext());
        sortAlbums(allSongs);
        rvAlbumList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvAlbumList.setAdapter(new AlbumRvAdapter(getContext(), allAlbums));
    }

    private void sortAlbums(ArrayList<MusicFile> allSongs) {
        ArrayList<MusicFile> albumSongs = new ArrayList<>();
        if (i < allSongs.size()) {
            String albumName = allSongs.get(i).getAlbum();
            if (!(albumNames.contains(albumName))) {
                for (MusicFile musicFile : allSongs) {
                    if ((musicFile.getAlbum()).equals(albumName)) {
                        albumSongs.add(musicFile);
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        try {
                            retriever.setDataSource(getContext(), Uri.parse(musicFile.getData()));
                            byte[] imageArt = retriever.getEmbeddedPicture();
                            Bitmap albumImage;
                            if (imageArt != null)
                                albumImage = BitmapFactory.decodeByteArray(imageArt, 0, imageArt.length);
                            else albumImage = null;
                            musicFile.setAlbumImage(albumImage);
                        } catch (IllegalArgumentException | SecurityException iE) {
                            Toast.makeText(getContext(), "Song Error", Toast.LENGTH_SHORT).show();
                            Log.d("Retriever", "sortAlbums: "+ iE.toString());

                        }
                    }
                }
                albumNames.add(albumName);
                allAlbums.add(albumSongs);
            }

            i++;
            sortAlbums(allSongs);
        }

        Log.d("sortAlbums: ", String.valueOf(allAlbums.size()));
    }
}