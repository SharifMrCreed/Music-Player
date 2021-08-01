package com.alle.san.musicplayer.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.alle.san.musicplayer.R;
import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.alle.san.musicplayer.util.Globals.ACTION_NEXT;
import static com.alle.san.musicplayer.util.Globals.ACTION_PAUSE;
import static com.alle.san.musicplayer.util.Globals.ACTION_PLAY;
import static com.alle.san.musicplayer.util.Globals.ACTION_PREVIOUS;
import static com.alle.san.musicplayer.util.Globals.ACTION_STOP;
import static com.alle.san.musicplayer.util.Globals.MUSIC_CHANNEL;
import static com.alle.san.musicplayer.util.Globals.NOTIFICATION_ID;
import static com.alle.san.musicplayer.util.Globals.POSITION_KEY;

public class MusicService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "MusicService";
    UtilInterfaces.Buttons buttonControls;
    ArrayList<MusicFile> songs = new ArrayList<>();
    MediaPlayer mediaPlayer;
    private int resumePosition = 0;
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    IBinder binder = new MusicBinder();
    int position;
    MusicFile previousSong;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaSession();
        position = StorageUtil.getPosition(getApplicationContext());
        //incoming call broadcast receiver
        callStateListener();
        //change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songs = StorageUtil.getPlayingSongs(getApplicationContext());
        position = intent.getIntExtra(POSITION_KEY, -1);
        //Could not gain focus
        if (!requestAudioFocus()) stopSelf();
        else checkAndPlay();
        if (buttonControls != null) handleIncomingActions(intent);
        return START_STICKY;
    }

    void checkAndPlay() {
        if (previousSong == null) {
            if (!songs.isEmpty() && position != -1) initMediaPlayer(position);
        } else {
            if (!songs.isEmpty() && position != -1 && !previousSong.equals(songs.get(position))) {
                initMediaPlayer(position);
            } else if (!songs.isEmpty() && position != -1 && resumePosition != 0)
                resumeMediaPlayer(position);
        }
    }

    void initMediaPlayer(int pos) {
        buildNotification(PlaybackStatus.PLAYING);
        updateMetaData();
        previousSong = songs.get(pos);
        createMediaPlayer(pos);
        start();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(this);
    }

    void resumeMediaPlayer(int pos) {
        previousSong = songs.get(pos);
        createMediaPlayer(pos);
        if (resumePosition > 0) seekTo(resumePosition);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
        resumePosition = 0;
    }

    public void seekTo(int duration) {
        mediaPlayer.seekTo(duration);
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        else return mediaPlayer.isPlaying();
    }

    public void start() {
        requestAudioFocus();
        if (mediaPlayer == null) checkAndPlay();
        else if (resumePosition > 0) {
            seekTo(resumePosition);
            mediaPlayer.start();
        } else {
            mediaPlayer.start();
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        else return 0;
    }

    public void stop() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void playNextSong() {
        if (StorageUtil.isShuffle(getApplicationContext())) {
            startShuffle();
        }else if (position == (songs.size() - 1)) {
            position = 0;
        } else {
            position++;
        }
        StorageUtil.setPosition(position, getApplicationContext());
        buttonControls.playSong(songs.get(position), position);
    }
    void repeatSong(){
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    public void playPreviousSong() {
        if (StorageUtil.isShuffle(getApplicationContext())) {
            startShuffle();
        }
        if (position == 0) {
            position = (songs.size() - 1);
        } else {
            position--;
        }
        StorageUtil.setPosition(position, getApplicationContext());
        buttonControls.playSong(songs.get(position), position);
    }

    private void startShuffle() {
        Random random = new Random();
        int size = songs.size();
        if (size > 1) position = random.nextInt((size - 1));
        else position = 0;
        StorageUtil.setPosition(position, getApplicationContext());

    }

    public void initButtonControls(Context context) {
        buttonControls = (UtilInterfaces.Buttons) context;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (StorageUtil.isRepeat(getApplicationContext())){
            repeatSong();
        }
        else playNextSong();
    }

    public void createMediaPlayer(int songPosition) {
        Uri uri = Uri.parse(songs.get(songPosition).getData());
        StorageUtil.setCurrentSong(songs.get(songPosition), getApplication());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(getApplicationContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);

        // TODO: 21/07/2021 Correct bugs here 
        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                resumePlayback();
            }

            @Override
            public void onPause() {
                pausePlayback();
            }

            @Override
            public void onSkipToNext() {
                playNextSong();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                playPreviousSong();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                removeNotification();
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    public void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buildNotification(PlaybackStatus.PAUSED);
        }
        buttonControls.songPause();
    }

    public void resumePlayback(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            buildNotification(PlaybackStatus.PLAYING);
        }else checkAndPlay();
        buttonControls.songPlay();
    }

    private void updateMetaData() {
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, Globals.albumBitmap(getApplicationContext(), songs.get(position).getData()))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songs.get(position).getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songs.get(position).getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songs.get(position).getTitle())
                .build());
    }

    public void buildNotification(PlaybackStatus playbackStatus) {
        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;
        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.play_icon;
            play_pauseAction = playbackAction(0);
        }

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MUSIC_CHANNEL)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setColor(getResources().getColor(R.color.colorPrimary, getTheme()))
                .setLargeIcon(Globals.albumBitmap(getApplicationContext(), songs.get(StorageUtil.getPosition(getApplicationContext())).getData()))
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setSilent(true)
                .setContentIntent(mediaSession.getController().getSessionActivity())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(songs.get(StorageUtil.getPosition(getApplicationContext())).getArtist())
                .setContentTitle(songs.get(StorageUtil.getPosition(getApplicationContext())).getAlbum())
                .setContentInfo(songs.get(StorageUtil.getPosition(getApplicationContext())).getTitle())
                .addAction(R.drawable.rewind_icon, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(R.drawable.next_icon, "next", playbackAction(2));

        startForeground(NOTIFICATION_ID, notificationBuilder.build());
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

    public class MusicBinder extends Binder {
        public MusicService getService() {
            Log.d(TAG, "getService: The service has been bound");
            return MusicService.this;
        }
    }

    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pause();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                resumePlayback();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                pausePlayback();
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        pause();
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                }
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) {
                    pause();
                    pausePlayback();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        //Focus gained
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void removeAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pause();
                            pausePlayback();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumePlayback();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stop();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();
        StorageUtil.clearCachedAudioPlaylist(getApplicationContext());
        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
    }
}
