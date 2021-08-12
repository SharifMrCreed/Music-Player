package com.alle.san.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.adapters.SongRecyclerAdapter;
import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.models.MyModels;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.MusicService;
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.POSITION_KEY;
import static com.alle.san.musicplayer.util.Globals.SONGS_KEY;
import static com.alle.san.musicplayer.util.Globals.WIDGET_ID;

public class PlaySongActivity extends AppCompatActivity implements UtilInterfaces.Buttons, ServiceConnection, UtilInterfaces.ViewChanger,
        UtilInterfaces.songPopUpMenu{

    TextView songName, artistName, currentTime, albumName, totalTime;
    RelativeLayout parentLayout, parentLayout2, linearLayout;
    SeekBar seekBar;
    RelativeLayout parentCard;
    ImageView nextButton, previousButton, listButton, backButton, shuffleButton, repeatButton, albumImage, pauseButton;

    ArrayList<MusicFile> songs;
    MusicService musicService;
    MusicFile song;

    private Bitmap albumArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        songName = findViewById(R.id.song_name);
        seekBar = findViewById(R.id.seek_bar);
        currentTime = findViewById(R.id.current_time);
        albumName = findViewById(R.id.album_name);
        totalTime = findViewById(R.id.total_time);
        pauseButton = findViewById(R.id.pause_button);
        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        backButton = findViewById(R.id.back_Arrow);
        listButton = findViewById(R.id.play_list);
        artistName = findViewById(R.id.artist_name);
        albumImage = findViewById(R.id.album_photo);
        parentCard = findViewById(R.id.parent_card);
        parentLayout = findViewById(R.id.parent_layout);
        linearLayout = findViewById(R.id.ll);
        parentLayout2 = findViewById(R.id.parent_layout2);
        repeatButton = findViewById(R.id.repeat_button);
        shuffleButton = findViewById(R.id.shuffle_button);

        //initialize Variables
        bindMusicService(this);
        songs = getIntent().getParcelableArrayListExtra(SONGS_KEY);
        StorageUtil.setPlayingSongs(songs, getApplicationContext());
        StorageUtil.setPosition(getIntent().getIntExtra(POSITION_KEY, 0), this);
        song = songs.get(getIntent().getIntExtra(POSITION_KEY, 0));
        albumArt = Globals.albumBitmap(this, song.getData());
        initButtons();
        playSong(song, getIntent().getIntExtra(POSITION_KEY, 0));
    }

    private void initButtons() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int duration, boolean fromUser) {
                if (musicService != null && fromUser) musicService.seekTo((duration * 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        listButton.setOnClickListener(v -> openPlayingSongs());

        pauseButton.setOnClickListener(view -> songPlayPause());

        previousButton.setOnClickListener(view -> musicService.playPreviousSong());

        nextButton.setOnClickListener(view -> musicService.playNextSong());

        backButton.setOnClickListener(view -> onBackPressed());



        shuffleButton.setOnClickListener(view -> {
            StorageUtil.setShuffle(this);
            if (musicService != null) musicService.notifyRemoteViews();

            if (StorageUtil.isShuffle(this)){ Glide.with(this).load(
                    R.drawable.shuffle_icon_on)
                    .into(shuffleButton);
            songs = StorageUtil.shufflePlayingSongs(this);
            }
            else {
                Glide.with(this).load(R.drawable.shuffle_icon).into(shuffleButton);
                songs = StorageUtil.getPlayingSongs(this);
            }

        });

        repeatButton.setOnClickListener(view -> {
            StorageUtil.setRepeat(this);
            if (musicService != null) musicService.notifyRemoteViews();
            if (StorageUtil.isRepeat(this)) Glide.with(this).load(
                    R.drawable.repeat_icon_on)
                    .into(repeatButton);
            else Glide.with(this).load(R.drawable.repeat_icon).into(repeatButton);
        });

    }

    private void openPlayingSongs() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.current_songs_bottom_sheet,
                findViewById(R.id.parent), false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_song_list);
        SongRecyclerAdapter songRecyclerAdapter = new SongRecyclerAdapter(songs);
        recyclerView.setAdapter(songRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public void songPlayPause() {
        if (!this.isDestroyed()){
            if (musicService != null && musicService.isPlaying()) {
                musicService.pausePlayback();
            } else if (musicService != null && !musicService.isPlaying()) {
                musicService.resumePlayback();
            } else {
                playSong(song, StorageUtil.getPosition(this));
            }
        }
    }


    void loadViews(MusicFile currentSong) {
        if (!this.isDestroyed()) {
            albumArt = Globals.albumBitmap(this, currentSong.getData());
            songName.setText(currentSong.getTitle());
            artistName.setText(currentSong.getArtist());
            albumName.setText(currentSong.getAlbum());
            seekBar.setMax(currentSong.getDuration() / 1000);
            totalTime.setText(Globals.timeFormat(currentSong.getDuration() / 1000));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int playtime = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(playtime);
                        currentTime.setText(Globals.timeFormat(playtime));
                    }
                    new Handler(getMainLooper()).postDelayed(this, 1000);
                }
            });

            Glide.with(this).asBitmap().load(albumArt).centerCrop().into(albumImage);
            makePaletteFrom(albumArt);

            Glide.with(this).load(android.R.drawable.ic_media_pause).into(pauseButton);
        }
    }

    public void playSong(MusicFile currentSong, int position) {
        song = currentSong;
        loadViews(currentSong);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra(POSITION_KEY, position);
        startService(serviceIntent);
    }

    @Override
    public void songPause() {
        if (!this.isDestroyed()) Glide.with(this).load(R.drawable.play_icon).into(pauseButton);
    }

    @Override
    public void songPlay() {
        if (!this.isDestroyed()) Glide.with(this).load(R.drawable.pause_icon).into(pauseButton);
    }


    private void makePaletteFrom(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    Bitmap bit = BlurImage.with(getApplicationContext()).load(bitmap).intensity(10).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(getResources(), bit);
                    Window window = getWindow();
                    window.setStatusBarColor(swatch.getRgb());

                    GradientDrawable gradientDraw = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{getResources().getColor(R.color.transparent, getTheme()), swatch.getRgb()});
                    GradientDrawable gradientDraw2 = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{swatch.getRgb(), getResources().getColor(R.color.transparent, getTheme())});

                    parentLayout.setBackground(gradientDraw2);
                    parentLayout2.setBackground(gradientDraw);
                    linearLayout.setBackground(bitmapDrawable);
                    artistName.setTextColor(swatch.getTitleTextColor());
                    currentTime.setTextColor(swatch.getTitleTextColor());
                    totalTime.setTextColor(swatch.getTitleTextColor());
                    albumName.setTextColor(swatch.getBodyTextColor());
                }
            }
        });
    }


    void bindMusicService(ServiceConnection serviceConnection) {
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        musicService = ((MusicService.MusicBinder) service).getService();
        musicService.initButtonControls(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra(WIDGET_ID) != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindMusicService(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void changeFragment(String tag, ArrayList<MusicFile> songs, int position) {
        this.songs = songs;
        StorageUtil.setPlayingSongs(songs, getApplicationContext());
        StorageUtil.setPosition(position, this);
        song = songs.get(position);
        albumArt = Globals.albumBitmap(this, song.getData());
        initButtons();
        playSong(song, getIntent().getIntExtra(POSITION_KEY, 0));
    }

    @Override
    public void changeFragment(MusicFile musicFile, String tag) {

    }

    @Override
    public void changeFragment(MyModels artistModel, String tag) {

    }

    @Override
    public void changeFragment(String playlistName, String tag) {

    }

    @Override
    public void openPlaySongActivity(MusicFile song) {

    }

    @Override
    public void shareMusicFile(String id) {
        Uri uri = Globals.getSongUri(Integer.parseInt(id));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Sound File"));
    }

    @Override
    public void deleteMusicFile(String id) {

    }
}