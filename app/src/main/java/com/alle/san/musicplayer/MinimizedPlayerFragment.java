package com.alle.san.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.MusicService;
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;

import static com.alle.san.musicplayer.util.Globals.AUDIO_PLAYER_STORAGE;


public class MinimizedPlayerFragment extends Fragment implements ServiceConnection, SharedPreferences.OnSharedPreferenceChangeListener {

    TextView songName, artistName;
    RelativeLayout relativeLayout;
    ImageView playButton, albumImage;
    ConstraintLayout parent;
    UtilInterfaces.ViewChanger viewChanger;
    SharedPreferences preferences;
    UtilInterfaces.MusicServiceCallbacks musicServiceCallbacks;
    MusicService musicService;

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

        preferences = getContext().getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        loadViews();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        parent.setOnClickListener(view1 -> viewChanger.openPlaySongActivity(StorageUtil.getCurrentSong(getContext())));
        playButton.setOnClickListener(v -> {
            if (musicService != null) {
                if (musicService.isPlaying()) {
                    musicService.pausePlayback();
                    Glide.with(this).load(R.drawable.play_icon).into(playButton);
                } else {
                    musicService.resumePlayback();
                    Glide.with(this).load(R.drawable.pause_icon).into(playButton);
                }
            }else {
                musicServiceCallbacks.startMusicService();
            }

        });
    }

    private void playPauseButton() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                Glide.with(this).load(R.drawable.pause_icon).into(playButton);
            } else {
                Glide.with(this).load(R.drawable.play_icon).into(playButton);
            }
        }else Glide.with(this).load(R.drawable.play_icon).into(playButton);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewChanger = (UtilInterfaces.ViewChanger) context;
        musicServiceCallbacks = (UtilInterfaces.MusicServiceCallbacks) context;
    }

    private void makePaletteFrom(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
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



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        musicService = ((MusicService.MusicBinder) service).getService();
        playPauseButton();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;

    }

    @Override
    public void onResume() {
        super.onResume();
        musicServiceCallbacks.bindMusicService(this);
        playPauseButton();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        musicServiceCallbacks.unbindService(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadViews();
    }

    private void loadViews() {
        if (StorageUtil.getCurrentSong(getContext()) != null) {
            parent.setOnClickListener(view1 -> viewChanger.openPlaySongActivity(StorageUtil.getCurrentSong(getContext())));
            songName.setText(StorageUtil.getCurrentSong(getContext()).getTitle());
            artistName.setText(StorageUtil.getCurrentSong(getContext()).getArtist());
            Bitmap bitmap = Globals.albumBitmap(getContext(), StorageUtil.getCurrentSong(getContext()).getData());
            Glide.with(getContext()).load(bitmap).into(albumImage);
            makePaletteFrom(bitmap);

        }
    }
}