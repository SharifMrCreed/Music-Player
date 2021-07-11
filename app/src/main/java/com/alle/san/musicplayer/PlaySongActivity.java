package com.alle.san.musicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.MusicService;
import com.alle.san.musicplayer.util.PlaybackStatus;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static com.alle.san.musicplayer.MainActivity.allMusic;
import static com.alle.san.musicplayer.util.Globals.ACTION_NEXT;
import static com.alle.san.musicplayer.util.Globals.ACTION_PAUSE;
import static com.alle.san.musicplayer.util.Globals.ACTION_PLAY;
import static com.alle.san.musicplayer.util.Globals.ACTION_PREVIOUS;
import static com.alle.san.musicplayer.util.Globals.ACTION_STOP;
import static com.alle.san.musicplayer.util.Globals.ADAPTER_POSITION;
import static com.alle.san.musicplayer.util.Globals.MUSIC_CHANNEL;
import static com.alle.san.musicplayer.util.Globals.NOTIFICATION_ID;

public class PlaySongActivity extends AppCompatActivity implements UtilInterfaces.Buttons, ServiceConnection {

    TextView songName, artistName, currentTime, albumName, totalTime;
    RelativeLayout parentLayout, parentLayout2, linearLayout;
    SeekBar seekBar;
    RelativeLayout parentCard;
    ImageView nextButton, previousButton, shuffleButton, backButton, repeatButton, albumImage, pauseButton, listButton;

    ArrayList<MusicFile> songs;
    MusicService musicService;
    MusicFile song;
    Handler handler = new Handler();
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    boolean shuffle, repeat;

    int position;
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
        songs = allMusic;
        position = getIntent().getIntExtra(ADAPTER_POSITION, 0);
        song = songs.get(position);
        albumArt = albumBitmap(song.getData());
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
        if (musicService != null && musicService.isPlaying()) {
            musicService.pause();
            Glide.with(this).load(R.drawable.play_icon).into(pauseButton);
        } else if (musicService != null && !musicService.isPlaying()) {
            musicService.start();
            Glide.with(this).load(android.R.drawable.ic_media_pause).into(pauseButton);
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
        song = currentSong;
        albumArt = albumBitmap(currentSong.getData());
        albumArt = albumBitmap(currentSong.getData());
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
        Glide.with(this).asBitmap().load(albumArt).centerCrop().into(albumImage);
        makePaletteFrom(albumArt);

        Glide.with(this).load(android.R.drawable.ic_media_pause).into(pauseButton);

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                Intent serviceIntent = new Intent(this, MusicService.class);
                serviceIntent.putExtra(ADAPTER_POSITION, position);
                startService(serviceIntent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }
    }

    private Bitmap albumBitmap(String dataPath) {
        byte[] image = getAlbumArt(dataPath);
        if (image != null) return BitmapFactory.decodeByteArray(image, 0, image.length);
        else
            return ((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.allecon))).getBitmap();
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

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists
        mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                songPlayPause();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                songPlayPause();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                playNextSong();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                playPreviousSong();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                musicService.stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                .build());
    }

    public void buildNotification(PlaybackStatus playbackStatus) {
        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;
        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.play_icon;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, MUSIC_CHANNEL)
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorPrimary, getTheme()))
                // Set the large and small icons
                .setLargeIcon(albumArt)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(song.getArtist())
                .setContentTitle(song.getAlbum())
                .setContentInfo(song.getTitle())
                // Add playback actions
                .addAction(R.drawable.rewind_icon, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(R.drawable.next_icon, "next", playbackAction(2));

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MusicService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    public void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;
        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) transportControls.play();
        else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) transportControls.pause();
        else if (actionString.equalsIgnoreCase(ACTION_NEXT)) transportControls.skipToNext();
        else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) transportControls.skipToPrevious();
        else if (actionString.equalsIgnoreCase(ACTION_STOP)) transportControls.stop();
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