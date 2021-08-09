package com.alle.san.musicplayer.util;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
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
    public static final String ABOUT_DEVELOPER_FRAGMENT_TAG = "About developer";
    public static final String MINIMIZED_FRAGMENT_TAG = "minimized fragment";
    public static final String ALBUMS_FRAGMENT_TAG = "Albums";
    public static final String FOLDERS_FRAGMENT_TAG = "Folders";
    public static final String FAVORITES = "Favorites";

    //Sorts
    public static final String TITLE = MediaStore.Audio.Media.TITLE;
    public static final String DATE_ADDED = MediaStore.Audio.Media.DATE_ADDED;
    public static final String ARTIST = MediaStore.Audio.Media.ARTIST;
    public static final String ALBUM = MediaStore.Audio.Media.ALBUM;
    public static final String ASCENDING_ORDER = "ASC";
    public static final String DESCENDING_ORDER = "DESC";

    //keys
    public static final String AUDIO_PLAYER_STORAGE = " com.alle.san.audioplayer.STORAGE";
    public static final String AUDIO_PLAYER_PLAYLISTS = " com.alle.san.audioplayer.PLAYLISTS";
    public static final String POSITION_KEY = "Position";
    public static final String PLAYLIST_KEY = "playlist";
    public static final String RESUME_KEY = "resume";
    public static final String SORT_ORDER = "SortOrder";
    public static final String IS_PLAYING = "isPlaying";
    public static final String WIDGET_ID = "widgetId";
    public static final String ORDER = "Order";
    public static final String SONGS_KEY = "Songs";
    public static final String CURRENT_SONG = "current song";
    public static final String SHUFFLE_KEY = "shuffle";
    public static final String REPEAT_KEY = "repeat";
    public static final String STRING_EXTRA = "String extra";

    //AudioPlayer notification
    public static final int NOTIFICATION_ID = 101;
    public static final int PENDING_REQUEST_CODE = 1998;
    public static final String MUSIC_CHANNEL = "Music channel";
    public static final String NOTIFICATION_CHANNEL = "Notification channel";
    public static final String PLAYER_NOTIFICATION = "Player notifications";
    public static final String MUSIC_PLAYER_NOTIFICATION_CONTROL = "Player notification controls";

    public static final String ACTION_PLAY = "com.alle.san.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.alle.san.musicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.alle.san.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.alle.san.musicplayer.ACTION_NEXT";
    public static final String ACTION_SHUFFLE = "com.alle.san.musicplayer.ACTION_SHUFFLE";
    public static final String ACTION_REPEAT = "com.alle.san.musicplayer.ACTION_REPEAT";
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

    public static String getOrder(Context context) {
        if (StorageUtil.isWhichOrder(context)) return Globals.DESCENDING_ORDER;
        else return Globals.ASCENDING_ORDER;
    }

    public static Uri getSongUri(int songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    public static final String WHATSAPP_URL = "https://wa.me/256705944594/?I%20just%20saw%20your%20Music" +
            "%20player%20and%20I%20think%20its%20awesome.%20I%20would%20like%20to%20work%20with%20you%20on" +
            "%20a%20project.";

    public static final String LINKED_IN_URL = "https://www.linkedin.com/in/semujju-sharif-abdukarim-735a03158/";

    public static final String INSTAGRAM_URL = "https://www.instagram.com/shareefcreed?r=nametag";

    public static final String FACEBOOK_URL = "https://www.facebook.com/semujju.sharif";

    public static final String ABOUT_DEVELOPER = "I am passionate about" +
            "building great apps. For me app development" +
            "is not just about coding, it's about creating" +
            "awesome user experiences." +
            "\n\n" +
            "I have worked on a variety of mobile apps and would like" +
            "to work on more. I am seeking out opportunities" +
            "to collaborate with companies / agencies /individuals," +
            " not just work for them but work with them to solve" +
            " real business-problems in a way that optimizes all of our" +
            "respective experiences and collective knowledge." +
            "\n\n" +
            "Note: If you want to know the cost of" +
            "building a mobile app, and how we can work together, my contact info is" +
            "listed at the bottom. lets for sure chat about how we can work together.";

    public static void showPlaylistDialog(Context context, MusicFile song) {
        Dialog dialog = new Dialog(context);
        PlayListRvAdapter adapter = new PlayListRvAdapter(context, PlayListRvAdapter.LINEAR_VIEW);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.playlist_dialog);
        dialog.findViewById(R.id.btn_create_playlist).setOnClickListener(v -> createNewPlaylistDialog(context, adapter));
        String count;
        if (StorageUtil.getPlaylists(context) != null)
            count = StorageUtil.getPlaylists(context).size() + " " + PLAYLIST_FRAGMENT_TAG;
        else {
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
