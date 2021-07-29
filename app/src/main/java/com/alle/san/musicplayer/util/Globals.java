package com.alle.san.musicplayer.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alle.san.musicplayer.R;
import com.alle.san.musicplayer.adapters.PlayListRvAdapter;
import com.alle.san.musicplayer.models.MusicFile;

import java.util.Objects;

public class Globals {
    public static final String  SONG_LIST_FRAGMENT_TAG = "All Songs";
    public static final String  ARTISTS_FRAGMENT_TAG = "Artists";
    public static final String  PLAYLIST_FRAGMENT_TAG = "Playlists";
    public static final String  ALBUM_SONG_LIST_FRAGMENT_TAG = "Album Songs";
    public static final String PLAY_SONG_ACTIVITY_TAG = "Play Song Fragment Tag";
    public static final String ALBUMS_FRAGMENT_TAG = "Albums";

    //keys
    public static final String AUDIO_PLAYER_STORAGE = " com.alle.san.audioplayer.STORAGE";
    public static final String AUDIO_PLAYER_PLAYLISTS = " com.alle.san.audioplayer.PLAYLISTS";
    public static final String POSITION_KEY = "Position";
    public static final String PLAYLIST_KEY = "playlist";
    public static final String RESUME_KEY = "Position";
    public static final String SONGS_KEY = "Songs";
    public static final String SHUFFLE_KEY = "shuffle";
    public static final String REPEAT_KEY = "repeat";
    public static final String STRING_EXTRA = "String extra";

    //AudioPlayer notification
    public static final int NOTIFICATION_ID = 101;
    public static final String MUSIC_CHANNEL = "Music channel";
    public static final String NOTIFICATION_CHANNEL = "Notification channel";
    public static final String PLAYER_NOTIFICATION = "Player notifications";
    public static final String MUSIC_PLAYER_NOTIFICATION_CONTROL = "Player notification controls";

    public static final String ACTION_PLAY = "com.alle.san.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.alle.san.musicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.alle.san.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.alle.san.musicplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.alle.san.musicplayer.ACTION_STOP";

    //Methods
    public static Bitmap albumBitmap(Context context, String dataPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] image;
        try {
            retriever.setDataSource(dataPath);
            image = retriever.getEmbeddedPicture();
        } catch (IllegalArgumentException | SecurityException iE) {
            image = null;
        }
        retriever.release();
        if (image != null) return BitmapFactory.decodeByteArray(image, 0, image.length);
        else
            return ((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.allecon))).getBitmap();
    }

    public static void showPlaylistDialog(Context context, MusicFile song) {
        Dialog dialog = new Dialog(context);
        PlayListRvAdapter adapter = new PlayListRvAdapter(context, PlayListRvAdapter.LINEAR_VIEW);
        ;
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.playlist_dialog);
        dialog.findViewById(R.id.btn_create_playlist).setOnClickListener(v -> createNewPlaylistDialog(context, adapter));
        String count;
        if (StorageUtil.getPlaylists(context) != null)
            count = StorageUtil.getPlaylists(context).size() + " " + PLAYLIST_FRAGMENT_TAG;
        else {
            dialog.findViewById(R.id.nothing_layout).setVisibility(View.VISIBLE);
            count = "0 " + PLAYLIST_FRAGMENT_TAG;
        }
        TextView playlistCount = dialog.findViewById(R.id.playlist_num);
        playlistCount.setText(count);
        RecyclerView recyclerView = dialog.findViewById(R.id.rv_play_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        adapter.setOnItemClickListener(playlistName -> {
            StorageUtil.addToPlaylist(playlistName, song, context);
            Toast.makeText(context, song.getTitle() + " has been added to " + playlistName, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnShowListener(dialog1 -> adapter.setPlaylists(StorageUtil.getPlaylists(context)));
        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    private static void createNewPlaylistDialog(Context context, PlayListRvAdapter adapter) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.playlist_create_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        EditText playlistNameEt = dialog.findViewById(R.id.playlist_name);
        dialog.findViewById(R.id.btn_save_playlist).setOnClickListener(v -> {
            String playlistName = playlistNameEt.getText().toString();
            if (!TextUtils.isEmpty(playlistName)) {
                StorageUtil.createPlaylist(playlistName, context);
                adapter.setPlaylists(StorageUtil.getPlaylists(context));
                dialog.dismiss();
            } else playlistNameEt.setError("Required");
        });
        String count;
        if (StorageUtil.getPlaylists(context) != null)
            count = StorageUtil.getPlaylists(context).size() + " " + PLAYLIST_FRAGMENT_TAG;
        else {
            count = "0 " + PLAYLIST_FRAGMENT_TAG;
        }
        TextView playlistCount = dialog.findViewById(R.id.playlist_num);
        playlistCount.setText(count);
        dialog.show();
    }
}
