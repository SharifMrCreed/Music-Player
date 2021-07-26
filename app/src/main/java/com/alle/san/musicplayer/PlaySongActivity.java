package com.alle.san.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.MusicService;
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.POSITION_KEY;
import static com.alle.san.musicplayer.util.Globals.SONGS_KEY;

public class PlaySongActivity extends AppCompatActivity implements UtilInterfaces.Buttons, ServiceConnection {

    TextView songName, artistName, currentTime, albumName, totalTime;
    RelativeLayout parentLayout, parentLayout2, linearLayout;
    SeekBar seekBar;
    RelativeLayout parentCard;
    ImageView nextButton, previousButton, shuffleButton, backButton, repeatButton, albumImage, pauseButton, listButton;

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
        songs = getIntent().getParcelableArrayListExtra(SONGS_KEY);
        StorageUtil.storeAudio(songs, getApplicationContext());
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

        pauseButton.setOnClickListener(view -> songPlayPause());

        previousButton.setOnClickListener(view -> musicService.playPreviousSong());

        nextButton.setOnClickListener(view -> musicService.playNextSong());

        backButton.setOnClickListener(view -> onBackPressed());

        shuffleButton.setOnClickListener(view -> {
            StorageUtil.setShuffle(this);
            if (StorageUtil.isShuffle(this)) Glide.with(this).load(
                    R.drawable.shuffle_icon_on)
                    .into(shuffleButton);
            else Glide.with(this).load(
                    R.drawable.shuffle_icon)
                    .into(shuffleButton);

        });

        repeatButton.setOnClickListener(view -> {
            StorageUtil.setRepeat(this);
            if (StorageUtil.isRepeat(this)) Glide.with(this).load(
                    R.drawable.repeat_icon_on)
                    .into(repeatButton);
            else Glide.with(this).load(
                    R.drawable.repeat_icon)
                    .into(repeatButton);
        });

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
            totalTime.setText(timeFormat(currentSong.getDuration() / 1000));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int playtime = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(playtime);
                        currentTime.setText(timeFormat(playtime));
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

    private String timeFormat(int playtime) {
        String hoursT = playtime / 60 > 60 ? String.valueOf(playtime / 3600) : null;
        String minutesT = playtime / 60 > 60 ? String.valueOf((playtime / 60) % 60) : String.valueOf(playtime / 60);
        String secondsT = String.valueOf(playtime % 60);
        String timeT;
        if (hoursT == null)
            timeT = secondsT.length() == 1 ? minutesT + ":0" + secondsT : minutesT + ":" + secondsT;
        else {
            if (minutesT.length() == 1)
                timeT = secondsT.length() == 1 ? hoursT + ":0" + minutesT + ":0" + secondsT : hoursT + ":0" + minutesT + ":" + secondsT;
            else
                timeT = secondsT.length() == 1 ? hoursT + ":" + minutesT + ":0" + secondsT : hoursT + ":" + minutesT + ":" + secondsT;
        }
        return timeT;
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
    public void onResume() {
        super.onResume();
        bindMusicService(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        unbindService(this);
    }
}