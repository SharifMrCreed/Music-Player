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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.MusicFile;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ADAPTER_POSITION;
import static com.alle.san.musicplayer.util.Globals.ALBUMS;


public class AlbumSongListFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    RelativeLayout parentLayout;
    Toolbar albumToolBar;
    Context context;
    CollapsingToolbarLayout collapsingToolbarLayout;

    ArrayList<MusicFile> albumSongs = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            albumSongs = bundle.getParcelableArrayList(ALBUMS);
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
        imageRetriever(albumSongs.get(0));
        albumToolBar.setTitle(albumSongs.get(0).getAlbum());
        recyclerView.setAdapter(new SongRecyclerAdapter(albumSongs, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

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
}