package com.alle.san.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.MultiAutoCompleteTextView;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.MusicFile;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ADAPTER_POSITION;
import static com.alle.san.musicplayer.util.Globals.ALBUMS;


public class AlbumSongListFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    Toolbar albumToolBar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    ArrayList<MusicFile> albumSongs = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            albumSongs = bundle.getParcelableArrayList(ALBUMS);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_album_song_list, container, false);
        recyclerView = view.findViewById(R.id.rv_album_song_list);
        albumToolBar = view.findViewById(R.id.albumToolbar);
        albumPhoto = view.findViewById(R.id.album_photo);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_tool_bar);

        initView();

        return view;
    }

    private void initView() {
        imageRetriever(albumSongs.get(0));
        albumToolBar.setTitle(albumSongs.get(0).getAlbum());
        recyclerView.setAdapter(new SongRecyclerAdapter(albumSongs, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    }

    private void imageRetriever(MusicFile musicFile){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getContext(), Uri.parse(musicFile.getData()));
        byte [] imageArt = retriever.getEmbeddedPicture();
        if (imageArt != null){
            Glide.with(getContext()).asBitmap().load(imageArt).centerCrop().into(albumPhoto);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageArt, 0, imageArt.length);

//            Palette.from(bitmap).generate(palette -> {
//                Palette.Swatch swatchDominant = palette.getDominantSwatch();
//                Palette.Swatch swatchMuted = palette.getMutedSwatch();
//                if (swatchDominant == null){
//                    GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchDominant.getRgb(), Color.BLACK});
//                    recyclerView.setBackground(gradientDraw);
//                }
//                if (swatchMuted == null){
//                    GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchMuted.getRgb(), swatchMuted.getRgb()});
//                    collapsingToolbarLayout.setContentScrim(gradientDraw);
//                    albumToolBar.setTitleTextColor(swatchMuted.getTitleTextColor());
//                }else if (swatchDominant == null){
//                    GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatchMuted.getRgb(), swatchMuted.getRgb()});
//                    collapsingToolbarLayout.setContentScrim(gradientDraw);
//                    albumToolBar.setTitleTextColor(swatchDominant.getTitleTextColor());
//                }
//            });
        }else{
            Glide.with(getContext()).asBitmap().load(R.drawable.allecon).centerCrop().into(albumPhoto);
        }

    }
}