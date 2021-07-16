package com.alle.san.musicplayer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.R;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ALBUMS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ALBUM_SONG_LIST_FRAGMENT_TAG;

public class AlbumRvAdapter extends RecyclerView.Adapter<AlbumRvAdapter.AlbumViewHolder> {

    Context context;
    ArrayList<MusicFile> albums = new ArrayList<>();
    UtilInterfaces.ViewChanger utilInterfaces;

    public AlbumRvAdapter(Context context) {
        this.context = context;
    }

    public void setAlbums(ArrayList<MusicFile> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_album_item, parent, false);
        return new AlbumViewHolder(view, albums, utilInterfaces, context);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.bindAlbum(position);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        ImageView albumImage;
        CardView cardView;
        LinearLayoutCompat linearLayout;
        ArrayList<MusicFile> albums;
        Context context;
        UtilInterfaces.ViewChanger utilInterfaces;

        public AlbumViewHolder(@NonNull View itemView, ArrayList<MusicFile> albums, UtilInterfaces.ViewChanger utilInterfaces, Context context) {
            super(itemView);
            this.albums = albums;
            this.utilInterfaces = utilInterfaces;
            this.context = context;
            albumImage = itemView.findViewById(R.id.album_art);
            albumName = itemView.findViewById(R.id.album_name);
            linearLayout = itemView.findViewById(R.id.album_item_parent);
            cardView = itemView.findViewById(R.id.parent_card);
        }

        public void bindAlbum(int position) {
            MusicFile musicFile = albums.get(position);
            imageRetriever(musicFile);
            albumName.setText(musicFile.getAlbum());
            linearLayout.setOnClickListener(view -> utilInterfaces.changeFragment(musicFile, ALBUMS_FRAGMENT_TAG));
        }


        private void imageRetriever(MusicFile musicFile) {
            Glide.with(context).asBitmap().load(musicFile.getAlbumImage()).centerCrop().into(albumImage);
            Bitmap bit = BlurImage.with(context.getApplicationContext()).load(musicFile.getAlbumImage()).intensity(20).Async(true).getImageBlur();
            Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), bit);
            cardView.setBackground(bitmapDrawable);

        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        utilInterfaces = (UtilInterfaces.ViewChanger) context;
    }
}
