package com.alle.san.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.MusicService;
import com.alle.san.musicplayer.util.ReadExternalStorage;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;
import java.util.Random;

import static com.alle.san.musicplayer.MainActivity.allMusic;
import static com.alle.san.musicplayer.util.Globals.ADAPTER_POSITION;

public class PlaySongActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, UtilInterfaces.Buttons,
        ServiceConnection {

    TextView songName, artistName, currentTime, albumName, totalTime;
    RelativeLayout parentLayout, parentLayout2, linearLayout;
    SeekBar seekBar;
    RelativeLayout parentCard;
    ImageView nextButton, previousButton, shuffleButton, backButton, repeatButton, albumImage, pauseButton, listButton;
    ArrayList<MusicFile> songs;
    MusicService musicService;
    MusicFile song;
    Handler handler = new Handler();
    boolean shuffle, repeat;

    int position;
    int play = 1;

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
        songs = allMusic;
        position = getIntent().getIntExtra(ADAPTER_POSITION, 0);
        song = songs.get(position);
        shuffle = false;
        repeat = false;

        //Initialize Methods
        initButtons();
        playSong(song);
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

        previousButton.setOnClickListener(view -> playPreviousSong());

        nextButton.setOnClickListener(view -> playNextSong());

        backButton.setOnClickListener(view -> onBackPressed());

        shuffleButton.setOnClickListener(view -> {
            if (!shuffle) {
                startShuffle();
                shuffle = true;
                if (musicService != null) musicService.setShuffle(true);
                Glide.with(this).load(R.drawable.shuffle_icon_on).into(shuffleButton);
            } else {
                shuffle = false;
                if (musicService != null) musicService.setShuffle(false);
                Glide.with(this).load(R.drawable.shuffle_icon).into(shuffleButton);
            }
        });

        repeatButton.setOnClickListener(view -> {
            if (!repeat) {
                repeat = true;
                if (musicService != null) musicService.setRepeat(true);
                Glide.with(this).load(R.drawable.repeat_icon_on).into(repeatButton);
            } else {
                repeat = false;
                if (musicService != null) musicService.setRepeat(false);
                Glide.with(this).load(R.drawable.repeat_icon).into(repeatButton);
            }
        });

    }

    private void startShuffle() {
        Glide.with(this).load(R.drawable.shuffle_icon_on).into(shuffleButton);
        Random random = new Random();
        int size = songs.size();
        if (size > 1) position = random.nextInt((size - 1));
        else position = 0;

    }

    public void songPlayPause() {
        if (musicService != null && play == 1) {
            play = 0;
            musicService.pause();

            Glide.with(this).load(R.drawable.play_icon).into(pauseButton);
        } else if (musicService != null && play == 0) {
            play = 1;
            musicService.start();
            Glide.with(this).load(R.drawable.pause_icon).into(pauseButton);
        } else {
            playSong(songs.get(position));
        }
    }

    private byte[] getAlbumArt(String dataPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] image;
        try {
            retriever.setDataSource(dataPath);
            image = retriever.getEmbeddedPicture();
        } catch (IllegalArgumentException | SecurityException iE) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            image = null;
        }
        retriever.release();
        return image;
    }

    public void playNextSong() {
        if (shuffle) {
            startShuffle();
            shuffle = false;
            if (musicService != null) musicService.setShuffle(false);
        }
        if (position == (songs.size() - 1)) {
            position = 0;
        } else {
            position++;
        }
        playSong(songs.get(position));
    }

    public void playPreviousSong() {
        if (shuffle) {
            startShuffle();
            shuffle = true;
            if (musicService != null) musicService.setShuffle(true);
        }
        if (position == 0) {
            position = (songs.size() - 1);
        } else {
            position--;
        }
        playSong(songs.get(position));
    }

    public void playSong(MusicFile currentSong) {
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
                handler.postDelayed(this, 1000);
            }
        });

        byte[] image = getAlbumArt(currentSong.getData());

        if (image != null) {
            Glide.with(this).asBitmap().load(image).centerCrop().into(albumImage);
            Bitmap art = BitmapFactory.decodeByteArray(image, 0, image.length);
            makePaletteFrom(art);

        } else {
            Glide.with(this).load(R.drawable.allecon).centerCrop().into(albumImage);
            Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.allecon)).getBitmap();
            makePaletteFrom(bitmap);
        }
        Glide.with(this).load(R.drawable.pause_icon).into(pauseButton);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra(ADAPTER_POSITION, position);
        startService(serviceIntent);

    }

    private String timeFormat(int playtime) {
        String hoursT = playtime / 60 > 60 ? String.valueOf(playtime / 3600) : null;
        String minutesT = playtime/60 > 60? String.valueOf((playtime /60) % 60) : String.valueOf(playtime / 60);
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
        Intent serviceIntent= new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (repeat) {
            playSong(songs.get(position));
        } else {
            playNextSong();
        }
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