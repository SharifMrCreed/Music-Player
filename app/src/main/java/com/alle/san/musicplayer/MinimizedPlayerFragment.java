package com.alle.san.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import static com.alle.san.musicplayer.util.Globals.PLAY_SONG_ACTIVITY_TAG;


public class MinimizedPlayerFragment extends Fragment {

    TextView songName, artistName;
    RelativeLayout relativeLayout;
    ImageView playButton, albumImage;
    ConstraintLayout parent;
    UtilInterfaces.ViewChanger utilInterfaces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minimized_player, container, false);
        songName = view.findViewById(R.id.song_name);
        artistName = view.findViewById(R.id.artist_name);
        relativeLayout = view.findViewById(R.id.mini_fade_layout);
        parent = view.findViewById(R.id.minimized_parent);
        albumImage = view.findViewById(R.id.album_art);
        playButton = view.findViewById(R.id.pause_button);

        parent.setOnClickListener(view1 -> utilInterfaces.openPlaySongActivity(StorageUtil.getCurrentSong(getContext())));
        if (StorageUtil.getCurrentSong(getContext()) != null) {
            songName.setText(StorageUtil.getCurrentSong(getContext()).getTitle());
            artistName.setText(StorageUtil.getCurrentSong(getContext()).getArtist());
            Bitmap bitmap = Globals.albumBitmap(getContext(), StorageUtil.getCurrentSong(getContext()).getData());
            Glide.with(getContext()).load(bitmap).into(albumImage);
            makePaletteFrom(bitmap);

        } else makePaletteFrom(Globals.albumBitmap(getContext(), null));
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        utilInterfaces = (UtilInterfaces.ViewChanger) context;
    }

    private void makePaletteFrom(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    Bitmap bit = BlurImage.with(getContext()).load(bitmap).intensity(25).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(getResources(), bit);

                    GradientDrawable gradientDraw = new GradientDrawable(
                            GradientDrawable.Orientation.RIGHT_LEFT,
                            new int[]{
                                    getResources().getColor(R.color.transparent, getActivity().getTheme()),
                                    swatch.getRgb(),
                                    getResources().getColor(R.color.transparent, getActivity().getTheme())
                            });

                    relativeLayout.setBackground(gradientDraw);
                    parent.setBackgroundColor(swatch.getRgb());
                    artistName.setTextColor(swatch.getTitleTextColor());
                    songName.setTextColor(swatch.getTitleTextColor());
                }
            }
        });
    }
}