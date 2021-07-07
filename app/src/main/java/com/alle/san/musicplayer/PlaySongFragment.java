package com.alle.san.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.ViewChanger;
import com.bumptech.glide.Glide;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class PlaySongFragment extends Fragment implements MediaPlayer.OnCompletionListener {
    TextView songName,
            artistName,
            currentTime,
            totalTime;
    RelativeLayout parentLayout, parentLayout2, linearLayout;
    SeekBar seekBar;
    RelativeLayout parentCard;
    ImageView nextButton,
            previousButton, shuffleButton,
            backButton, repeatButton,
            albumImage, pauseButton,
            listButton;
    Context context;

    ArrayList<MusicFile> songs;
    MediaPlayer mediaPlayer;
    ViewChanger viewChanger;
    MusicFile song;
    Handler handler = new Handler();

    int adapterPosition;
    int position;
    int play = 1;
    boolean shuffle, repeat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (getContext() != null) context = getContext();
        if (bundle != null) {
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
        currentTime = view.findViewById(R.id.current_time);
        totalTime = view.findViewById(R.id.total_time);
        pauseButton = view.findViewById(R.id.pause_button);
        nextButton = view.findViewById(R.id.next_button);
        previousButton = view.findViewById(R.id.previous_button);
        backButton = view.findViewById(R.id.back_Arrow);
        listButton = view.findViewById(R.id.play_list);
        artistName = view.findViewById(R.id.artist_name);
        albumImage = view.findViewById(R.id.album_photo);
        parentCard = view.findViewById(R.id.parent_card);
        parentLayout = view.findViewById(R.id.parent_layout);
        linearLayout = view.findViewById(R.id.ll);
        parentLayout2 = view.findViewById(R.id.parent_layout2);
        repeatButton = view.findViewById(R.id.repeat_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);


        //initialize Variables
        position = adapterPosition;
        song = songs.get(position);
        shuffle = false;
        repeat = false;


        //Initialize Methods
        initButtons();
        playSong(song);

        return view;
    }


    private void initButtons() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int duration, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo((duration * 1000));
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
            if (!shuffle) {
                startShuffle();
                shuffle = true;
                shuffleButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shuffle_icon_on));
            } else {
                shuffle = false;
                shuffleButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shuffle_icon));
            }
        });
        repeatButton.setOnClickListener(view -> {
            if (!repeat) {
                repeat = true;
                repeatButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.repeat_icon_on));
            } else {
                repeat = false;
                repeatButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.repeat_icon));
            }
        });

    }

    private void startShuffle() {

        shuffleButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shuffle_icon_on));
        Random random = new Random();
        int size = songs.size();
        if (size > 1) position = random.nextInt((size - 1));
        else position = 0;

    }

    private void pauseSong() {
        if (mediaPlayer != null && play == 1) {
            play = 0;
            mediaPlayer.pause();
            pauseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_icon));
        } else if (mediaPlayer != null && play == 0) {
            play = 1;
            mediaPlayer.start();
            pauseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_icon));
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
        }catch (IllegalArgumentException | SecurityException iE){
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            image = null;
        }
        retriever.release();
        return image;
    }

    private void playNextSong() {
        if (shuffle) {
            startShuffle();
            shuffle = false;
        }
        if (position == (songs.size() - 1)) {
            position = 0;
        } else {
            position++;
        }
        playSong(songs.get(position));
    }

    private void playPreviousSong() {
        if (shuffle) {
            startShuffle();
            shuffle = true;
        }
        if (position == 0) {
            position = (songs.size() - 1);
        } else {
            position--;
        }
        playSong(songs.get(position));
    }

    private void playSong(MusicFile currentSong) {
        songName.setText(currentSong.getTitle());
        artistName.setText(currentSong.getArtist());
        seekBar.setMax(currentSong.getDuration() / 1000);
        totalTime.setText(timeFormat(currentSong.getDuration() / 1000));

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int playtime = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(playtime);
                        currentTime.setText(timeFormat(playtime));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }

        byte[] image = getAlbumArt(currentSong.getData());

        if (getContext() != null) {

            if (image != null) {
                Glide.with(getContext()).asBitmap()
                        .load(image)
                        .centerCrop()
                        .into(albumImage);
                Bitmap art = BitmapFactory.decodeByteArray(image, 0, image.length);
                makePaletteFrom(art);

            } else {
                Glide.with(getContext())
                        .load(R.drawable.allecon)
                        .centerCrop()
                        .into(albumImage);
                Bitmap bitmap = ((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.allecon))).getBitmap();
                makePaletteFrom(bitmap);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            pauseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_icon));
            Uri songUri = Uri.parse(currentSong.getData());
            mediaPlayer = MediaPlayer.create(getContext(), songUri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    private String timeFormat(int playtime) {
        String minutesT = String.valueOf(playtime / 60);
        String secondsT = String.valueOf(playtime % 60);
        String timeT;
        if (secondsT.length() == 1) {
            timeT = minutesT + ":0" + secondsT;
        } else {
            timeT = minutesT + ":" + secondsT;
        }
        return timeT;
    }

    private void makePaletteFrom(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {

            if (palette != null) {

                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    Bitmap bit = BlurImage.with(context.getApplicationContext()).load(bitmap).intensity(20).Async(true).getImageBlur();
                    Drawable bitmapDrawable = new BitmapDrawable(getResources(), bit);
                    GradientDrawable gradientDraw = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{getResources().getColor(R.color.transparent, context.getTheme()), getResources().getColor(R.color.grey_900, context.getTheme())});
                   GradientDrawable gradientDraw2 = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{getResources().getColor(R.color.grey_900, context.getTheme()), getResources().getColor(R.color.transparent, context.getTheme())});
                    parentLayout.setBackground(gradientDraw2);
                    parentLayout2.setBackground(gradientDraw);
                    linearLayout.setBackground(bitmapDrawable);
                    artistName.setTextColor(swatch.getTitleTextColor());
                    currentTime.setTextColor(swatch.getTitleTextColor());
                    totalTime.setTextColor(swatch.getTitleTextColor());
                }
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
        if (repeat) {
            playSong(songs.get(position));
        } else {
            playNextSong();
        }
    }
}