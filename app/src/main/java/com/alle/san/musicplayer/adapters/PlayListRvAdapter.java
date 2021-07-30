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
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.PLAYLIST_FRAGMENT_TAG;

public class PlayListRvAdapter extends RecyclerView.Adapter<PlayListRvAdapter.ArtistViewHolder> {

    ArrayList<String> playlists;
    UtilInterfaces.ViewChanger utilInterfaces;
    public static final int LINEAR_VIEW = 1;
    public static final int GRID_VIEW = 2;
    private OnItemClickListener listener;
    int layoutView;
    Context context;
    public interface OnItemClickListener{
        void onItemClicked(String playlistName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PlayListRvAdapter(Context context, int layoutView) {
        playlists = StorageUtil.getPlaylists(context);
        this.layoutView =layoutView;
        this.context = context;
        if (playlists == null) playlists = new ArrayList<>();
    }

    public void setPlaylists(ArrayList<String> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == GRID_VIEW) view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_artist_item, parent, false);
        else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_playlist_dialog_item, parent, false);
        return new ArtistViewHolder(view, playlists, utilInterfaces, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        holder.bindAlbum(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (LINEAR_VIEW == layoutView) return LINEAR_VIEW;
        else return GRID_VIEW;
    }

    @Override
    public int getItemCount() {
        if (playlists == null) playlists = new ArrayList<>();
        return playlists.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView iv1;
        ImageView iv2;
        ImageView iv3;
        ImageView iv4;
        CardView cardView;
        LinearLayoutCompat linearLayout;
        ArrayList<String> playlists;
        OnItemClickListener listener;
        UtilInterfaces.ViewChanger utilInterfaces;

        public ArtistViewHolder(@NonNull View itemView, ArrayList<String> playlists, UtilInterfaces.ViewChanger utilInterfaces, OnItemClickListener listener) {
            super(itemView);
            this.playlists = playlists;
            this.listener = listener;
            this.utilInterfaces = utilInterfaces;
            iv1 = itemView.findViewById(R.id.iv_1);
            iv2 = itemView.findViewById(R.id.iv_2);
            iv3 = itemView.findViewById(R.id.iv_3);
            iv4 = itemView.findViewById(R.id.iv_4);
            playlistName = itemView.findViewById(R.id.artist_name);
            linearLayout = itemView.findViewById(R.id.album_item_parent);
            cardView = itemView.findViewById(R.id.parent_card);
        }

        public void bindAlbum(int position) {
            String name = playlists.get(position);
            ArrayList<MusicFile> playlistSongs = StorageUtil.getPlaylistSongs(itemView.getContext(), name);
            if (playlistSongs == null) playlistSongs = new ArrayList<>();
            imageRetriever(playlistSongs);
            playlistName.setText(name);
            linearLayout.setOnClickListener(view -> {
                if (getItemViewType() == GRID_VIEW) utilInterfaces.changeFragment(name, PLAYLIST_FRAGMENT_TAG);
                else listener.onItemClicked(name);
            });

        }


        private void imageRetriever(ArrayList<MusicFile> playlistSongs) {
            Context context = itemView.getContext();
            new Thread(() -> {
                if (playlistSongs.size()>=4){
                    Bitmap bitmap1 = Globals.albumBitmap(context, playlistSongs.get(0).getData());
                    Bitmap bitmap2 = Globals.albumBitmap(context, playlistSongs.get(1).getData());
                    Bitmap bitmap3 = Globals.albumBitmap(context, playlistSongs.get(2).getData());
                    Bitmap bit = BlurImage.with(itemView.getContext()).load(bitmap1).intensity(20).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(itemView.getContext().getResources(), bit);
                    iv1.post(() -> {
                        Glide.with(context).load(bitmap1).centerCrop().into(iv1);
                        Glide.with(context).load(bitmap2).centerCrop().into(iv2);
                        Glide.with(context).load(bitmap3).centerCrop().into(iv3);
                        cardView.setBackground(bitmapDrawable);
                    });
                }else if (playlistSongs.size()==3){
                    Bitmap bitmap1 = Globals.albumBitmap(context, playlistSongs.get(0).getData());
                    Bitmap bitmap2 = Globals.albumBitmap(context, playlistSongs.get(1).getData());
                    Bitmap bitmap3 = Globals.albumBitmap(context, playlistSongs.get(2).getData());
                    Bitmap bit = BlurImage.with(itemView.getContext()).load(bitmap1).intensity(20).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(itemView.getContext().getResources(), bit);
                    iv1.post(() -> {
                        Glide.with(context).load(bitmap1).centerCrop().into(iv1);
                        Glide.with(context).load(bitmap2).centerCrop().into(iv2);
                        Glide.with(context).load(bitmap3).centerCrop().into(iv3);
                        iv4.setVisibility(View.GONE);
                        cardView.setBackground(bitmapDrawable);
                    });
                }else if (playlistSongs.size() == 2){
                    Bitmap bitmap1 = Globals.albumBitmap(context, playlistSongs.get(0).getData());
                    Bitmap bitmap2 = Globals.albumBitmap(context, playlistSongs.get(1).getData());
                    Bitmap bit = BlurImage.with(itemView.getContext()).load(bitmap1).intensity(20).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(itemView.getContext().getResources(), bit);
                    iv1.post(() -> {
                        Glide.with(context).load(bitmap1).centerCrop().into(iv1);
                        Glide.with(context).load(bitmap2).centerCrop().into(iv3);
                        cardView.setBackground(bitmapDrawable);
                        iv2.setVisibility(View.GONE);
                        iv4.setVisibility(View.GONE);
                    });
                }else if (playlistSongs.size() == 1){
                    Bitmap bitmap1 = Globals.albumBitmap(context, playlistSongs.get(0).getData());
                    Bitmap bit = BlurImage.with(itemView.getContext()).load(bitmap1).intensity(20).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(itemView.getContext().getResources(), bit);
                    iv1.post(() -> {
                        Glide.with(context).load(bitmap1).centerCrop().into(iv1);
                        cardView.setBackground(bitmapDrawable);
                        iv2.setVisibility(View.GONE);
                        iv4.setVisibility(View.GONE);
                        iv3.setVisibility(View.GONE);
                    });
                }else {
                    Bitmap bitmap1 = Globals.albumBitmap(context, null);
                    Bitmap bit = BlurImage.with(itemView.getContext()).load(bitmap1).intensity(20).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(itemView.getContext().getResources(), bit);
                    iv1.post(() -> {
                        Glide.with(context).load(bitmap1).centerCrop().into(iv1);
                        cardView.setBackground(bitmapDrawable);
                        iv2.setVisibility(View.GONE);
                        iv4.setVisibility(View.GONE);
                        iv3.setVisibility(View.GONE);
                    });
                }
            }).start();
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        utilInterfaces = (UtilInterfaces.ViewChanger) context;
    }
}
