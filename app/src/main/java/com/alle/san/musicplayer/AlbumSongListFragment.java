package com.alle.san.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.StorageUtil;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ALBUMS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.FOLDERS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.PLAYLIST_FRAGMENT_TAG;
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
    String playlistName;
    String extra;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            extra = bundle.getString(STRING_EXTRA);
            if (extra.equals(ALBUMS_FRAGMENT_TAG)) song = bundle.getParcelable(ALBUMS_FRAGMENT_TAG);
            else if (extra.equals(PLAYLIST_FRAGMENT_TAG)) playlistName = bundle.getString(ALBUMS_FRAGMENT_TAG);
            else artist = bundle.getParcelable(ALBUMS_FRAGMENT_TAG);
            bundle.clear();

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (extra.equals(ALBUMS_FRAGMENT_TAG)) {
            view = inflater.inflate(R.layout.fragment_album_song_list, container, false);
            albumPhoto = view.findViewById(R.id.album_photo);
        } else {
            view = inflater.inflate(R.layout.fragment_artist_song_list, container, false);
        }
        recyclerView = view.findViewById(R.id.rv_album_song_list);
        albumToolBar = view.findViewById(R.id.albumToolbar);
        parentLayout = view.findViewById(R.id.parent_layout);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_tool_bar);

        initView(view);

        return view;
    }

    private void initView(View view) {
        if (extra.equals(ALBUMS_FRAGMENT_TAG)){
            imageRetriever(song);
            albumToolBar.setTitle(song.getAlbum());
            recyclerView.setAdapter(new SongRecyclerAdapter(getAlbumSongs()));
        }
        else if (extra.equals(PLAYLIST_FRAGMENT_TAG)) {
            LinearLayout nothingLayout = view.findViewById(R.id.nothing_layout);
            if (StorageUtil.getPlaylistSongs(context, playlistName) == null){
                nothingLayout.setVisibility(View.VISIBLE);
            }
            ArrayList<MusicFile> playlistSongs = StorageUtil.getPlaylistSongs(context, playlistName);
            if (playlistSongs == null)playlistSongs = new ArrayList<>();
            ImageView imageView2, imageView3, imageView4;
            imageView2 = view.findViewById(R.id.album_photo2);
            albumPhoto = view.findViewById(R.id.album_photo1);
            imageView3 = view.findViewById(R.id.album_photo3);
            imageView4 = view.findViewById(R.id.album_photo4);
            LinearLayout ll = view.findViewById(R.id.ll);
            if (playlistSongs.isEmpty()) imageRetriever((String) null);
            else imageRetriever(playlistSongs.get(0).getData());
            if (playlistSongs.size()>=4){
                Bitmap bitmap1 = Globals.albumBitmap(context, playlistSongs.get(1).getData());
                Bitmap bitmap2 = Globals.albumBitmap(context, playlistSongs.get(2).getData());
                Bitmap bitmap3 = Globals.albumBitmap(context, playlistSongs.get(3).getData());
                imageView2.post(() -> {
                    Glide.with(context).load(bitmap1).centerCrop().into(imageView2);
                    Glide.with(context).load(bitmap2).centerCrop().into(imageView3);
                    Glide.with(context).load(bitmap3).centerCrop().into(imageView4);
                });
            }else if (playlistSongs.size()==3){
                Bitmap bitmap1 = Globals.albumBitmap(context, playlistSongs.get(1).getData());
                Bitmap bitmap2 = Globals.albumBitmap(context, playlistSongs.get(2).getData());
                imageView2.post(() -> {
                    Glide.with(context).load(bitmap1).centerCrop().into(imageView2);
                    Glide.with(context).load(bitmap2).centerCrop().into(imageView3);
                    imageView4.setVisibility(View.GONE);
                    ll.setWeightSum(3);
                });
            }else if (playlistSongs.size() == 2){
                Bitmap bitmap1 = Globals.albumBitmap(context, playlistSongs.get(1).getData());
                imageView2.post(() -> {
                    Glide.with(context).load(bitmap1).centerCrop().into(imageView2);
                    imageView3.setVisibility(View.GONE);
                    imageView4.setVisibility(View.GONE);
                    ll.setWeightSum(2);
                });
            }else {
                imageView2.post(() -> {
                    imageView2.setVisibility(View.GONE);
                    imageView3.setVisibility(View.GONE);
                    imageView4.setVisibility(View.GONE);
                    ll.setWeightSum(1);
                });
            }
            albumToolBar.setTitle(playlistName);
            recyclerView.setAdapter(new SongRecyclerAdapter(playlistSongs));
        }
        else {
            ImageView imageView2, imageView3, imageView4;
            imageView2 = view.findViewById(R.id.album_photo2);
            albumPhoto = view.findViewById(R.id.album_photo1);
            imageView3 = view.findViewById(R.id.album_photo3);
            imageView4 = view.findViewById(R.id.album_photo4);
            LinearLayout ll = view.findViewById(R.id.ll);
            imageRetriever(artist.getPic1());
            albumToolBar.setTitle(artist.getName());
            if (extra.equals(FOLDERS_FRAGMENT_TAG)) {
                recyclerView.setAdapter(new SongRecyclerAdapter(StorageUtil.getSongsFromFolder(context, artist.getName())));
            } else recyclerView.setAdapter(new SongRecyclerAdapter(getArtistSongs()));
            if (artist.getPic2() == null) {
                imageView2.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                imageView4.setVisibility(View.GONE);
                ll.setWeightSum(1);
            } else if (artist.getPic3() == null) {
                imageView3.setVisibility(View.GONE);
                imageView4.setVisibility(View.GONE);
                ll.setWeightSum(2);
            } else if (artist.getPic4() == null) {
                imageView4.setVisibility(View.GONE);
                ll.setWeightSum(3);
            }
            new Thread(()->{
                Bitmap bitmap2, bitmap3, bitmap4;
                bitmap2 = Globals.albumBitmap(context, artist.getPic2());
                bitmap3 = Globals.albumBitmap(context, artist.getPic3());
                bitmap4 = Globals.albumBitmap(context, artist.getPic4());
                imageView2.post(() -> {
                    Glide.with(this).load(bitmap2).into(imageView2);
                    Glide.with(this).load(bitmap3).into(imageView3);
                    Glide.with(this).load(bitmap4).into(imageView4);
                });
            }).start();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    }

    private ArrayList<MusicFile> getAlbumSongs() {
        ArrayList<MusicFile> allSongs = StorageUtil.getSongsFromStorage(context, StorageUtil.getSortOrder(context), Globals.getOrder(context));
        ArrayList<MusicFile> album = new ArrayList<>();
        for (MusicFile musicFile : allSongs)
            if (musicFile.getAlbum().equals(song.getAlbum())) album.add(musicFile);
        return album;
    }

    private ArrayList<MusicFile> getArtistSongs() {
        ArrayList<MusicFile> allSongs = StorageUtil.getSongsFromStorage(context, StorageUtil.getSortOrder(context), Globals.getOrder(context));
        ArrayList<MusicFile> artists = new ArrayList<>();
        for (MusicFile musicFile : allSongs)
            if (musicFile.getArtist().equals(artist.getName())) artists.add(musicFile);
        return artists;
    }

    private void imageRetriever(MusicFile musicFile) {
        Bitmap bitmap = Globals.albumBitmap(getContext(), musicFile.getData());
        Glide.with(context).load(bitmap).centerCrop().into(albumPhoto);
        Bitmap bit = BlurImage.with(context.getApplicationContext()).load(bitmap).intensity(25).Async(true).getImageBlur();

        Drawable bitmapDrawable = new BitmapDrawable(getResources(), bit);
        parentLayout.setBackground(bitmapDrawable);
        Palette.from(bitmap).generate(palette -> {
            Palette.Swatch swatchDominant = palette.getDominantSwatch();
            Palette.Swatch swatchMuted = palette.getMutedSwatch();
            Palette.Swatch swatchLightVibrant = palette.getLightVibrantSwatch();
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
                getActivity().getWindow().setStatusBarColor(swatchMuted.getRgb());
            }
            if (swatchLightVibrant != null) {
                collapsingToolbarLayout.setCollapsedTitleTextColor(swatchLightVibrant.getRgb());
                collapsingToolbarLayout.setExpandedTitleColor(swatchLightVibrant.getRgb());
            }
        });
    }

    private void imageRetriever(String data) {
        new Thread(()->{
            Bitmap bitmap = Globals.albumBitmap(context, data);
            Bitmap bit;
            bit = BlurImage.with(context.getApplicationContext()).load(bitmap).intensity(25).Async(true).getImageBlur();
            Drawable bitmapDrawable = new BitmapDrawable(getResources(), bit);

            albumPhoto.post(() -> {
                Glide.with(context).asBitmap().load(bitmap).centerCrop().into(albumPhoto);
                parentLayout.setBackground(bitmapDrawable);
                Palette.from(bitmap).generate(palette -> {
                    Palette.Swatch swatchDominant = palette.getDominantSwatch();
                    Palette.Swatch swatchMuted = palette.getMutedSwatch();
                    Palette.Swatch swatchLightVibrant = palette.getLightVibrantSwatch();
                    if (swatchDominant != null) {
                        GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchDominant.getRgb(), Color.BLACK});
                        collapsingToolbarLayout.setContentScrim(gradientDraw);
                        collapsingToolbarLayout.setTitleEnabled(true);
                        getActivity().getWindow().setStatusBarColor(swatchDominant.getRgb());
                    } else if (swatchMuted != null) {
                        GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchMuted.getRgb(), swatchMuted.getRgb()});
                        collapsingToolbarLayout.setContentScrim(gradientDraw);
                        collapsingToolbarLayout.setTitleEnabled(true);
                        getActivity().getWindow().setStatusBarColor(swatchMuted.getRgb());
                    }
                    if (swatchLightVibrant != null) {
                        collapsingToolbarLayout.setCollapsedTitleTextColor(swatchLightVibrant.getRgb());
                        collapsingToolbarLayout.setExpandedTitleColor(swatchLightVibrant.getRgb());
                    }

                });

            });
        }).start();
    }


}