package com.alle.san.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.StorageUtil;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ALBUMS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.STRING_EXTRA;


public class AlbumSongListFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    RelativeLayout parentLayout;
    Toolbar albumToolBar;
    Context context;
    CollapsingToolbarLayout collapsingToolbarLayout;

    MusicFile song;
    ArtistModel artist;
    String extra;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            extra = bundle.getString(STRING_EXTRA);
            if (extra.equals(ALBUMS_FRAGMENT_TAG)) song = bundle.getParcelable(ALBUMS_FRAGMENT_TAG);
            else artist = bundle.getParcelable(ALBUMS_FRAGMENT_TAG);
            bundle.clear();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_song_list, container, false);
        recyclerView = view.findViewById(R.id.rv_album_song_list);
        albumToolBar = view.findViewById(R.id.albumToolbar);
        albumPhoto = view.findViewById(R.id.album_photo);
        parentLayout = view.findViewById(R.id.parent_layout);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_tool_bar);

        initView();

        return view;
    }

    private void initView() {
        if (extra.equals(ALBUMS_FRAGMENT_TAG)){
            imageRetriever(song);
            albumToolBar.setTitle(song.getAlbum());
        }
        else {
            imageArtistRetriever(artist);
            albumToolBar.setTitle(artist.getName());
        }
        if (extra.equals(ALBUMS_FRAGMENT_TAG))
            recyclerView.setAdapter(new SongRecyclerAdapter(getAlbumSongs(), getContext()));
        else recyclerView.setAdapter(new SongRecyclerAdapter(getArtistSongs(), getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    }

    private ArrayList<MusicFile> getAlbumSongs() {
        ArrayList<MusicFile> allSongs = StorageUtil.getSongsFromStorage(context);
        ArrayList<MusicFile> album = new ArrayList<>();
        for (MusicFile musicFile : allSongs)
            if (musicFile.getAlbum().equals(song.getAlbum())) album.add(musicFile);
        return album;
    }

    private ArrayList<MusicFile> getArtistSongs() {
        ArrayList<MusicFile> allSongs = StorageUtil.getSongsFromStorage(context);
        ArrayList<MusicFile> artists = new ArrayList<>();
        for (MusicFile musicFile : allSongs)
            if (musicFile.getArtist().equals(artist.getName())) artists.add(musicFile);
        return artists;
    }

    private void imageRetriever(MusicFile musicFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] imageArt;
        try {
            retriever.setDataSource(getContext(), Uri.parse(musicFile.getData()));
            imageArt = retriever.getEmbeddedPicture();
        } catch (IllegalArgumentException | SecurityException iE) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            imageArt = null;
        }
        Bitmap bit;
        Bitmap bitmap;
        if (imageArt != null) {
            Glide.with(context).asBitmap().load(imageArt).centerCrop().into(albumPhoto);
            bitmap = BitmapFactory.decodeByteArray(imageArt, 0, imageArt.length);
            bit = BlurImage.with(context.getApplicationContext()).load(bitmap).intensity(20).Async(true).getImageBlur();
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.allecon);
            Glide.with(context).asBitmap().load(R.drawable.allecon).centerCrop().into(albumPhoto);
            bit = BlurImage.with(context.getApplicationContext()).load(R.drawable.allecon).intensity(20).Async(true).getImageBlur();
        }

        Drawable bitmapDrawable = new BitmapDrawable(getResources(), bit);
        parentLayout.setBackground(bitmapDrawable);
        Palette.from(bitmap).generate(palette -> {
            Palette.Swatch swatchDominant = palette.getDominantSwatch();
            Palette.Swatch swatchMuted = palette.getMutedSwatch();
            if (swatchDominant != null) {
                GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchDominant.getRgb(), Color.BLACK});
                collapsingToolbarLayout.setContentScrim(gradientDraw);
                collapsingToolbarLayout.setTitleEnabled(true);
                collapsingToolbarLayout.setCollapsedTitleTextColor(swatchDominant.getTitleTextColor());
                collapsingToolbarLayout.setExpandedTitleColor(swatchDominant.getTitleTextColor());
                getActivity().getWindow().setStatusBarColor(swatchDominant.getRgb());
            } else if (swatchMuted != null) {
                GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchMuted.getRgb(), swatchMuted.getRgb()});
                collapsingToolbarLayout.setContentScrim(gradientDraw);
                collapsingToolbarLayout.setTitleEnabled(true);
                collapsingToolbarLayout.setCollapsedTitleTextColor(swatchDominant.getTitleTextColor());
                collapsingToolbarLayout.setExpandedTitleColor(swatchDominant.getTitleTextColor());
                getActivity().getWindow().setStatusBarColor(swatchMuted.getRgb());
            }
        });
    }

    private void imageArtistRetriever(ArtistModel musicFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bit;
        Bitmap bitmap = musicFile.getPic1();
        if (bitmap != null) {
            Glide.with(context).asBitmap().load(bitmap).centerCrop().into(albumPhoto);
            bit = BlurImage.with(context.getApplicationContext()).load(bitmap).intensity(20).Async(true).getImageBlur();
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.allecon);
            Glide.with(context).asBitmap().load(R.drawable.allecon).centerCrop().into(albumPhoto);
            bit = BlurImage.with(context.getApplicationContext()).load(R.drawable.allecon).intensity(20).Async(true).getImageBlur();
        }

        Drawable bitmapDrawable = new BitmapDrawable(getResources(), bit);
        parentLayout.setBackground(bitmapDrawable);
        Palette.from(bitmap).generate(palette -> {
            Palette.Swatch swatchDominant = palette.getDominantSwatch();
            Palette.Swatch swatchMuted = palette.getMutedSwatch();
            if (swatchDominant != null) {
                GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchDominant.getRgb(), Color.BLACK});
                collapsingToolbarLayout.setContentScrim(gradientDraw);
                collapsingToolbarLayout.setTitleEnabled(true);
                collapsingToolbarLayout.setCollapsedTitleTextColor(swatchDominant.getTitleTextColor());
                collapsingToolbarLayout.setExpandedTitleColor(swatchDominant.getTitleTextColor());
                getActivity().getWindow().setStatusBarColor(swatchDominant.getRgb());
            } else if (swatchMuted != null) {
                GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchMuted.getRgb(), swatchMuted.getRgb()});
                collapsingToolbarLayout.setContentScrim(gradientDraw);
                collapsingToolbarLayout.setTitleEnabled(true);
                collapsingToolbarLayout.setCollapsedTitleTextColor(swatchDominant.getTitleTextColor());
                collapsingToolbarLayout.setExpandedTitleColor(swatchDominant.getTitleTextColor());
                getActivity().getWindow().setStatusBarColor(swatchMuted.getRgb());
            }
        });
    }


}