package com.alle.san.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.R;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.UtilInterfaces;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.PLAY_SONG_ACTIVITY_TAG;

public class SongRecyclerAdapter extends RecyclerView.Adapter<SongRecyclerAdapter.SongViewHolder> {

    ArrayList<MusicFile> songs;
    Context context;
    UtilInterfaces.ViewChanger utilInterfaces;

    public SongRecyclerAdapter(ArrayList<MusicFile> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    public void setSongs(ArrayList<MusicFile> songs) {
        this.songs = new ArrayList<>();
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.Bind(position);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    public class SongViewHolder extends RecyclerView.ViewHolder {

        TextView songName, artistName;
        RelativeLayout songItem;
        ImageView moreIcon;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            songItem = itemView.findViewById(R.id.song_item);
            moreIcon = itemView.findViewById(R.id.more_menu);
        }

        public void Bind(final int position) {
            MusicFile song = songs.get(position);
            songName.setText(song.getTitle());
            artistName.setText(song.getArtist());
            songItem.setOnClickListener(view -> {
//                view.setBackgroundResource(R.drawable.stroked_rectangle);
                utilInterfaces.changeFragment(PLAY_SONG_ACTIVITY_TAG, songs, position);
            });
            moreIcon.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.song_popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.pop_up_delete) {
                        delete();
                    } else if (itemId == R.id.pop_up_share) {
                        share();
                    } else if (itemId == R.id.pop_up_play) {
                        utilInterfaces.changeFragment(PLAY_SONG_ACTIVITY_TAG, songs, position);
                    } else if (itemId == R.id.pop_up_to_playlist) {
                        addToPlayList();
                    } else if (itemId == R.id.pop_up_details) {
                        showDetails();
                    }
                    return true;
                });
            });
        }

        private void showDetails() {
        }

        private void addToPlayList() {

        }

        private void share() {
        }

        private void delete() {

        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        utilInterfaces = (UtilInterfaces.ViewChanger) context;
    }
}
