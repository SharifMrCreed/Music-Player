package com.alle.san.musicplayer.adapters;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.R;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.ViewChanger;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ALBUM_SONG_LIST_FRAGMENT_TAG;

public class AlbumRvAdapter extends RecyclerView.Adapter<AlbumRvAdapter.AlbumViewHolder> {

    Context context;
    ArrayList <ArrayList<MusicFile>> albums;
    ViewChanger viewChanger;

    public AlbumRvAdapter(Context context, ArrayList<ArrayList<MusicFile>> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.bindAlbum(position);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{
        TextView albumName;
        ImageView albumImage;
        LinearLayoutCompat parentLayout;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage= itemView.findViewById(R.id.album_art);
            albumName= itemView.findViewById(R.id.album_name);
            parentLayout= itemView.findViewById(R.id.album_item_parent);
        }

        public void bindAlbum(int position){
            ArrayList<MusicFile> albumSongs = albums.get(position);
            MusicFile musicFile = albumSongs.get(0);
            imageRetriever(musicFile);
            albumName.setText(musicFile.getAlbum());
            parentLayout.setOnClickListener(view-> viewChanger.changeFragment(ALBUM_SONG_LIST_FRAGMENT_TAG, albumSongs, position));
        }


        private void imageRetriever(MusicFile musicFile){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(musicFile.getData()));
            byte [] imageArt = retriever.getEmbeddedPicture();
            if (imageArt != null){
                Glide.with(context).asBitmap().load(imageArt).centerCrop().into(albumImage);
            }else{
                Glide.with(context).asBitmap().load(R.drawable.allecon).centerCrop().into(albumImage);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        viewChanger = (ViewChanger)context;
    }
}
