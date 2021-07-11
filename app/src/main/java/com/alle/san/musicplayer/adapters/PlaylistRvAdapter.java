package com.alle.san.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.R;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.UtilInterfaces;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ALBUM_SONG_LIST_FRAGMENT_TAG;

public class PlaylistRvAdapter extends RecyclerView.Adapter<PlaylistRvAdapter.PlaylistViewHolder> {

    Context context;
    ArrayList<ArrayList<MusicFile>> albums;
    UtilInterfaces.ViewChanger utilInterfaces;

    public PlaylistRvAdapter(Context context, ArrayList<ArrayList<MusicFile>> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_album_item, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.bindPlaylist(position);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        ImageView albumImage;
        LinearLayoutCompat parentLayout;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.album_art);
            albumName = itemView.findViewById(R.id.album_name);
            parentLayout = itemView.findViewById(R.id.album_item_parent);
        }

        public void bindPlaylist(int position) {
            ArrayList<MusicFile> albumSongs = albums.get(position);
            MusicFile musicFile = albumSongs.get(0);
            albumName.setText(musicFile.getPlaylist()[0]);
            parentLayout.setOnClickListener(view -> utilInterfaces.changeFragment(ALBUM_SONG_LIST_FRAGMENT_TAG, albumSongs, position));
        }


    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        utilInterfaces = (UtilInterfaces.ViewChanger) context;
    }
}
