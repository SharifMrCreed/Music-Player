package com.alle.san.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.ViewChanger;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static com.alle.san.musicplayer.util.Globals.LISTENING;
import static com.alle.san.musicplayer.util.Globals.OFF;


public class PlaySongFragment extends Fragment implements MediaPlayer.OnCompletionListener {
    TextView songName,
             artistName,
             currentTime,
             totalTime,
             voiceControlMode;
    LinearLayout voiceControl,
                 parentLayout;
    SeekBar seekBar;
    ConstraintLayout upperLayout;
    RelativeLayout parentCard;
    ImageView nextButton,
              previousButton, shuffleButton,
              backButton, repeatButton,
              albumImage,pauseButton,
              listButton;

    ArrayList<MusicFile> songs;
    MediaPlayer mediaPlayer;
    ViewChanger viewChanger;
    SpeechRecognizer speechRecognizer;
    Intent speechRecognizerIntent;
    String speechResult = "";
    MusicFile song;
    Handler handler = new Handler();

    private static final String TAG = "PlaySongFragment";
    int adapterPosition;
    int position;
    int vcClicks = 0;
    int play = 1;
    boolean shuffle, repeat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            songs = bundle.getParcelableArrayList(Globals.SONGS_LIST);
            adapterPosition = bundle.getInt(Globals.ADAPTER_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_song, container, false);

        //initialize views
        songName = view.findViewById(R.id.song_name);
        seekBar = view.findViewById(R.id.seek_bar);
        voiceControl = view.findViewById(R.id.voice_control);
        currentTime = view.findViewById(R.id.current_time);
        totalTime = view.findViewById(R.id.total_time);
        pauseButton =view.findViewById(R.id.pause_button);
        nextButton =view.findViewById(R.id.next_button);
        previousButton =view.findViewById(R.id.previous_button);
        backButton =view.findViewById(R.id.back_Arrow);
        listButton =view.findViewById(R.id.play_list);
        artistName = view.findViewById(R.id.artist_name);
        voiceControlMode = view.findViewById(R.id.voice_control_mode);
        albumImage = view.findViewById(R.id.album_photo);
        parentCard = view.findViewById(R.id.parent_card);
        parentLayout = view.findViewById(R.id.parent_layout);
        upperLayout = view.findViewById(R.id.upper_layout);
        repeatButton = view.findViewById(R.id.repeat_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);


        //initialize Variables
        position = adapterPosition;
        song = songs.get(position);
        shuffle = false;
        repeat = false;


        //Initialize Methods
        initSpeechRecognizer();
        initButtons();
        initVoiceControl();
        playSong(song);

        return view;
    }



    private void initButtons() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int duration, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo((duration*1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pauseButton.setOnClickListener(view -> pauseSong());

        previousButton.setOnClickListener(view -> playPreviousSong());

        nextButton.setOnClickListener(view -> playNextSong());

        backButton.setOnClickListener(view -> viewChanger.onBackPressed());

        shuffleButton.setOnClickListener(view -> {
            if(!shuffle) {
                startShuffle();
                shuffle = true;
                shuffleButton.setImageDrawable(getContext().getDrawable(R.drawable.shuffle_icon_on));
            }else{
                shuffle = false;
                shuffleButton.setImageDrawable(getContext().getDrawable(R.drawable.shuffle_icon));
            }
        });
        repeatButton.setOnClickListener(view -> {
            if (!repeat){
                repeat = true;
                repeatButton.setImageDrawable(getContext().getDrawable(R.drawable.repeat_icon_on));
            }else{
                repeat = false;
                repeatButton.setImageDrawable(getContext().getDrawable(R.drawable.repeat_icon));
            }
        } );

    }

    private void startShuffle() {

        shuffleButton.setImageDrawable(getContext().getDrawable(R.drawable.shuffle_icon_on));
        Random random = new Random();
        position = random.nextInt((songs.size()-1));

    }

    private void pauseSong() {
        if (mediaPlayer != null && play == 1){
            play = 0;
            mediaPlayer.pause();
            pauseButton.setImageDrawable(getContext().getDrawable(R.drawable.play_icon));
        }else if (mediaPlayer != null && play == 0){
            play = 1;
            mediaPlayer.start();
            pauseButton.setImageDrawable(getContext().getDrawable(R.drawable.pause_icon));
        }else{
            playSong(songs.get(position));
        }
    }

    private byte[] getAlbumArt(String dataPath){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(dataPath);
        byte[] image = retriever.getEmbeddedPicture();
        retriever.release();
        return image;
    }

    private void playNextSong() {
        if(shuffle) {
            startShuffle();
            shuffle = false;
        }
        if (position == (songs.size() - 1)){
            position = 0;
        }else{
            position++;
        }
        playSong(songs.get(position));
    }

    private void playPreviousSong() {
        if(shuffle) {
            startShuffle();
            shuffle = true;
        }
        if (position == 0){
            position = (songs.size() - 1);
        }else{
            position--;
        }
        playSong(songs.get(position));
    }

    private void playSong(MusicFile currentSong) {
        songName.setText(currentSong.getTitle());
        artistName.setText(currentSong.getArtist());
        seekBar.setMax(currentSong.getDuration()/1000);
        totalTime.setText(timeFormat(currentSong.getDuration()/1000));

        if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int playtime = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(playtime);
                        currentTime.setText(timeFormat(playtime));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }

        byte [] image = getAlbumArt(currentSong.getData());

        if (getContext()!= null){

            if(image != null){
                Glide.with(getContext()).asBitmap()
                        .load(image)
                        .centerCrop()
                        .into(albumImage);
                Bitmap art = BitmapFactory.decodeByteArray(image, 0, image.length);
                makePaletteFrom(art);

            }else{
                Glide.with(getContext())
                        .load(R.drawable.allecon)
                        .centerCrop()
                        .into(albumImage);
                Bitmap bitmap = ((BitmapDrawable)getContext().getDrawable(R.drawable.allecon)).getBitmap();
                makePaletteFrom(bitmap);
            }
            if (mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            pauseButton.setImageDrawable(getContext().getDrawable(R.drawable.pause_icon));
            Uri songUri = Uri.parse(currentSong.getData());
            mediaPlayer = MediaPlayer.create(getContext(), songUri);

        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
    }

    private String timeFormat(int playtime) {
        String minutesT = String.valueOf(playtime /60);
        String secondsT = String.valueOf(playtime % 60);
        String timeT = "";
        if (secondsT.length() == 1){
            timeT = minutesT + ":0" + secondsT;
        }else{
            timeT = minutesT + ":" + secondsT;
        }
        return timeT;
    }

    private void makePaletteFrom(Bitmap bitmap){
        Palette.from(bitmap).generate(palette -> {

            Palette.Swatch swatch = palette.getDominantSwatch();

            if (swatch != null){
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                GradientDrawable gradientDraw = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), Color.BLACK});
                parentLayout.setBackground(gradientDraw);
                upperLayout.setBackgroundDrawable(gradientDrawable);
                songName.setTextColor(swatch.getTitleTextColor());
                artistName.setTextColor(swatch.getBodyTextColor());
                currentTime.setTextColor(swatch.getBodyTextColor());
                totalTime.setTextColor(swatch.getBodyTextColor());
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVoiceControl() {
        voiceControl.setOnClickListener(view -> {
            if(vcClicks == 0){
                vcClicks =1;
                voiceControlMode.setText(LISTENING);
                speechRecognizer.startListening(speechRecognizerIntent);
                speechResult = "";
            }else if (vcClicks == 1){
                vcClicks =0;
                speechRecognizer.stopListening();
                voiceControlMode.setText(OFF);
            }
        });

    }

    private void initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> stringArrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (stringArrayList != null){
                    speechResult = stringArrayList.get(0);
                    Toast.makeText(getContext(), speechResult, Toast.LENGTH_LONG).show();
                    if(speechResult.contains("next song")) {
                        playNextSong();
                    }else if(speechResult.contains("previous song")) {
                        playPreviousSong();
                    }else if(speechResult.contains("pause now")) {
                        pauseSong();
                    }else if(speechResult.contains("song list")||speechResult.contains("playlist")) {
                        viewChanger.onBackPressed();
                    }else if(speechResult.contains("shut down")) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        Objects.requireNonNull(getActivity()).finish();
                    }else if(speechResult.contains("stop listening")) {
                        speechRecognizer.stopListening();
                    }
                 }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewChanger = (ViewChanger) context;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (repeat){
            playSong(songs.get(position));
        }else{
            playNextSong();
        }
    }
}