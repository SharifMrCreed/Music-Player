package com.alle.san.musicplayer.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;

import static com.alle.san.musicplayer.MainActivity.allMusic;
import static com.alle.san.musicplayer.util.Globals.ADAPTER_POSITION;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "MusicService";
    UtilInterfaces.Buttons buttonControls;
    ArrayList<MusicFile> songs = new ArrayList<>();
    MediaPlayer mediaPlayer;
    private int resumePosition;
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private AudioManager audioManager;
    IBinder binder = new MusicBinder();
    boolean shuffle, repeat = false;
    int position = -1;
    MusicFile previousSong;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service has been created");
        //incoming call broadcast receiver
        callStateListener();
        //change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songs = allMusic;
        position = intent.getIntExtra(ADAPTER_POSITION, -1);
        //Could not gain focus
        if (!requestAudioFocus()) stopSelf();
        else checkAndPlay();
        if (buttonControls != null) buttonControls.handleIncomingActions(intent);
        return START_STICKY;
    }

    void checkAndPlay() {
        if (previousSong == null) {
            if (!songs.isEmpty() && position != -1) initMediaPlayer(position);
        } else {
            if (!songs.isEmpty() && position != -1 && !previousSong.equals(songs.get(position))) {
                initMediaPlayer(position);
            }else if (!songs.isEmpty() && position != -1 && previousSong.equals(songs.get(position))){
                resumeMediaPlayer(position);
            }
        }
    }

    void initMediaPlayer(int pos) {
        previousSong = songs.get(pos);
        createMediaPlayer(pos);
        start();
        mediaPlayer.setOnCompletionListener(this);
    }
    void resumeMediaPlayer(int pos) {
        previousSong = songs.get(pos);
        createMediaPlayer(pos);
        if (resumePosition > 0) seekTo(resumePosition);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
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

    public boolean isPlaying (){
        if (mediaPlayer == null) return false;
        else return mediaPlayer.isPlaying();
    }

    public void start() {
        requestAudioFocus();
        if (mediaPlayer == null) checkAndPlay();
        else if (resumePosition > 0) {
            seekTo(resumePosition);
            mediaPlayer.start();
        }else{
            mediaPlayer.start();
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        else return 0;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void stop() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void initButtonControls(Context context) {
        buttonControls = (UtilInterfaces.Buttons) context;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (repeat) buttonControls.playSong(songs.get(position));
        else buttonControls.playNextSong();
    }

    public void createMediaPlayer(int songPosition) {
        Uri uri = Uri.parse(songs.get(songPosition).getData());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
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
            buttonControls.buildNotification(PlaybackStatus.PAUSED);
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
                buttonControls.songPlayPause();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                buttonControls.songPlayPause();
                if (mediaPlayer!= null ){
                    if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) buttonControls.songPlayPause();
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
                            buttonControls.songPlayPause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                buttonControls.songPlayPause();
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

        buttonControls.removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
    }
}
