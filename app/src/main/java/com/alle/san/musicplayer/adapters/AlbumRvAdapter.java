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
import com.alle.san.musicplayer.util.ViewChanger;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

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
        CardView cardView;
        LinearLayoutCompat linearLayout;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage= itemView.findViewById(R.id.album_art);
            albumName= itemView.findViewById(R.id.album_name);
            linearLayout = itemView.findViewById(R.id.album_item_parent);
            cardView = itemView.findViewById(R.id.parent_card);
        }

        public void bindAlbum(int position){
            ArrayList<MusicFile> albumSongs = albums.get(position);
            MusicFile musicFile = albumSongs.get(0);
            imageRetriever(musicFile);
            albumName.setText(musicFile.getAlbum());
            linearLayout.setOnClickListener(view-> viewChanger.changeFragment(ALBUM_SONG_LIST_FRAGMENT_TAG, albumSongs, position));
        }


        private void imageRetriever(MusicFile musicFile){
            if (musicFile.getAlbumImage() != null) {
                Glide.with(context).asBitmap().load(musicFile.getAlbumImage()).centerCrop().into(albumImage);
                Bitmap bit = BlurImage.with(context.getApplicationContext()).load(musicFile.getAlbumImage()).intensity(20).Async(true).getImageBlur();
                Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), bit);
                cardView.setBackground(bitmapDrawable);
            }
            else {
                Glide.with(context).asBitmap().load(R.drawable.allecon).centerCrop().into(albumImage);
                Bitmap bit = BlurImage.with(context.getApplicationContext()).load(R.drawable.allecon).intensity(20).Async(true).getImageBlur();
                Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), bit);
                cardView.setBackground(bitmapDrawable);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        viewChanger = (ViewChanger)context;
    }
}
