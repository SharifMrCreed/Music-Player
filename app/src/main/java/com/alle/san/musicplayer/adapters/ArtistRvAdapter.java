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
import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ARTISTS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.FOLDERS_FRAGMENT_TAG;

public class ArtistRvAdapter extends RecyclerView.Adapter<ArtistRvAdapter.ArtistViewHolder> {

    Context context;
    ArrayList<ArtistModel> artists = new ArrayList<>();
    String category;
    UtilInterfaces.ViewChanger utilInterfaces;

    public ArtistRvAdapter(Context context, String category) {
        this.context = context;
        this.category = category;
    }

    public void setArtists(ArrayList<ArtistModel> artists) {
        if (artists == null) artists = new ArrayList<>();
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_artist_item, parent, false);
        return new ArtistViewHolder(view, artists, utilInterfaces, context, category);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        holder.bindAlbum(position);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView artistName;
        ImageView iv1;
        ImageView iv2;
        ImageView iv3;
        ImageView iv4;
        CardView cardView;
        String category;
        LinearLayoutCompat linearLayout;
        ArrayList<ArtistModel> albums;
        Context context;
        UtilInterfaces.ViewChanger utilInterfaces;

        public ArtistViewHolder(@NonNull View itemView, ArrayList<ArtistModel> albums, UtilInterfaces.ViewChanger utilInterfaces, Context context, String category) {
            super(itemView);
            this.albums = albums;
            this.utilInterfaces = utilInterfaces;
            this.context = context;
            this.category = category;
            iv1 = itemView.findViewById(R.id.iv_1);
            iv2 = itemView.findViewById(R.id.iv_2);
            iv3 = itemView.findViewById(R.id.iv_3);
            iv4 = itemView.findViewById(R.id.iv_4);
            artistName = itemView.findViewById(R.id.artist_name);
            linearLayout = itemView.findViewById(R.id.album_item_parent);
            cardView = itemView.findViewById(R.id.parent_card);
        }

        public void bindAlbum(int position) {
            ArtistModel musicFile = albums.get(position);
            imageRetriever(musicFile);
            artistName.setText(musicFile.getName());
            linearLayout.setOnClickListener(view -> utilInterfaces.changeFragment(musicFile, category == null ? ARTISTS_FRAGMENT_TAG : FOLDERS_FRAGMENT_TAG));

        }


        private void imageRetriever(ArtistModel artist) {
            new Thread(()->{
                Bitmap bitmap2, bitmap3, bitmap4;
                bitmap2 = Globals.albumBitmap(context, artist.getPic1());
                bitmap3 = Globals.albumBitmap(context, artist.getPic2());
                bitmap4 = Globals.albumBitmap(context, artist.getPic3());
                Bitmap bit = BlurImage.with(context.getApplicationContext()).load(bitmap2).intensity(20).Async(true).getImageBlur();
                Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), bit);
                iv1.post(() -> {
                    Glide.with(context).load(bitmap2).centerCrop().into(iv1);
                    Glide.with(context).load(bitmap3).centerCrop().into(iv2);
                    Glide.with(context).load(bitmap4).centerCrop().into(iv3);
                    cardView.setBackground(bitmapDrawable);
                });
            }).start();
            if (artist.getPic3() == null) iv2.setVisibility(View.GONE);
            if (artist.getPic4() == null) iv4.setVisibility(View.GONE);
            if (artist.getPic2() == null) iv3.setVisibility(View.GONE);


        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        utilInterfaces = (UtilInterfaces.ViewChanger) context;
    }
}
