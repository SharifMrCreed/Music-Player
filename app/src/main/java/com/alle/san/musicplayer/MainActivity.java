package com.alle.san.musicplayer;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.MusicService;
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.alle.san.musicplayer.util.Globals.ABOUT_DEVELOPER_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ALBUMS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ALBUM_SONG_LIST_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ARTISTS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.FACEBOOK_URL;
import static com.alle.san.musicplayer.util.Globals.FAVORITES;
import static com.alle.san.musicplayer.util.Globals.FOLDERS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.INSTAGRAM_URL;
import static com.alle.san.musicplayer.util.Globals.LINKED_IN_URL;
import static com.alle.san.musicplayer.util.Globals.MINIMIZED_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.PLAYLIST_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.POSITION_KEY;
import static com.alle.san.musicplayer.util.Globals.SONG_LIST_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.STRING_EXTRA;
import static com.alle.san.musicplayer.util.Globals.WHATSAPP_URL;
import static com.alle.san.musicplayer.util.Globals.getSongUri;

public class MainActivity extends AppCompatActivity implements UtilInterfaces.ViewChanger, UtilInterfaces.MusicServiceCallbacks,
UtilInterfaces.ContactThrough, UtilInterfaces.songPopUpMenu {

    private static final int STORAGE_REQUEST = 2;
    private static final int WRITE_REQUEST = 3;
    private static final int REQUEST_PERM_DELETE = 1;
    int fragmentContainer;
    ActionBar actionBar;
    FragmentManager fm;

    SongListFragment songListFragment;
    AlbumsFragment albumsFragment;
    FoldersFragment foldersFragment;
    AlbumSongListFragment albumSongListFragment;

    CardView miniPlayerCard;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView nNavigationView;

    private UtilInterfaces.Filter filter;
    private Intent playSongIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainer = R.id.fragment_container;
        drawer = findViewById(R.id.drawer_layout);
        nNavigationView = findViewById(R.id.nav_view);
        miniPlayerCard = findViewById(R.id.mini_container);
        toolbar = findViewById(R.id.appToolBar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        fm = getSupportFragmentManager();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        initNavigationView();

        if (checkReadPermissions()) initViewPaging();
        if (albumsFragment == null) albumsFragment = new AlbumsFragment();
    }
    
    private void initViewPaging() {
        if (songListFragment == null) songListFragment = new SongListFragment();
        initFragment(songListFragment, SONG_LIST_FRAGMENT_TAG);
        filter = songListFragment;
        getAlbums(StorageUtil.getSongsFromStorage(this, StorageUtil.getSortOrder(this), Globals.getOrder(this) ));
        getArtists(StorageUtil.getSongsFromStorage(this, StorageUtil.getSortOrder(this), Globals.getOrder(this)));
        getFolders();
    }

    private void getAlbums(ArrayList<MusicFile> allSongs) {
        ArrayList<MusicFile> albums = new ArrayList<>();
        ArrayList<String> albumNames = new ArrayList<>();
        new Thread(() -> {
            for (MusicFile musicFile : allSongs) {
                if (!albumNames.contains(musicFile.getAlbum())) {
                    albumNames.add(musicFile.getAlbum());
                    albums.add(musicFile);
                }
            }
            StorageUtil.setAlbums(albums, this);
        }).start();

    }

    private void getArtists(ArrayList<MusicFile> allSongs) {
        ArrayList<ArtistModel> artists = new ArrayList<>();
        ArrayList<String> artistsNames = new ArrayList<>();
        new Thread(() -> {
            for (MusicFile musicFile : allSongs) {
                if (!artistsNames.contains(musicFile.getArtist())) {
                    ArtistModel artistModel = new ArtistModel();
                    ArrayList<String> one = new ArrayList<>();
                    for (MusicFile musicFile1 : allSongs) {
                        if (musicFile1.getArtist().equals(musicFile.getArtist())) {
                            one.add( musicFile1.getData());
                        }
                    }
                    if (one.size() == 1)
                        artistModel = new ArtistModel(musicFile.getArtist(), one.get(0), null, null, null);
                    else if (one.size() == 2)
                        artistModel = new ArtistModel(musicFile.getArtist(), one.get(0), one.get(1), null, null);
                    else if (one.size() == 3)
                        artistModel = new ArtistModel(musicFile.getArtist(), one.get(0), one.get(1), one.get(2), null);
                    else if (one.size() == 4)
                        artistModel = new ArtistModel(musicFile.getArtist(), one.get(0), one.get(1), one.get(2), one.get(3));
                    artistsNames.add(musicFile.getArtist());

                    if (!TextUtils.isEmpty(artistModel.getName())) artists.add(artistModel);
                }
            }
            StorageUtil.setArtists(artists, this);
        }).start();

    }

    private void getFolders() {
        ArrayList<ArtistModel> artists = new ArrayList<>();
        new Thread(() -> {
            HashSet<String> folders = StorageUtil.getSongFolders(Environment.getExternalStorageDirectory());
            for (String folderName: folders){
                ArrayList<MusicFile> one = StorageUtil.getSongsFromFolder(this, folderName);
                ArtistModel artistModel = new ArtistModel();
                if (one.size() == 1)
                    artistModel = new ArtistModel(folderName, one.get(0).getData(), null, null, null);
                else if (one.size() == 2)
                    artistModel = new ArtistModel(folderName, one.get(0).getData(), one.get(1).getData(), null, null);
                else if (one.size() == 3)
                    artistModel = new ArtistModel(folderName, one.get(0).getData(), one.get(1).getData(), one.get(2).getData(), null);
                else if (one.size() > 3)
                    artistModel = new ArtistModel(folderName, one.get(0).getData(), one.get(1).getData(), one.get(2).getData(), one.get(3).getData());
                artists.add(artistModel);
            }
            StorageUtil.setFolders(artists, this);
        }).start();

    }

    private void sortSongs(){
        RadioButton rbTitle, rbRecently, rbArtists, rbAlbum;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.sort_bottom_sheet,
                findViewById(R.id.parent), false);
        RadioGroup radioGroup = view.findViewById(R.id.sort_radio_group);
        rbTitle = view.findViewById(R.id.rb_title);
        rbAlbum = view.findViewById(R.id.rb_album);
        rbArtists = view.findViewById(R.id.rb_artists);
        rbRecently = view.findViewById(R.id.rb_recently_added);
        if (StorageUtil.getSortOrder(this).equals(Globals.TITLE)) rbTitle.setChecked(true);
        if (StorageUtil.getSortOrder(this).equals(Globals.ARTIST)) rbArtists.setChecked(true);
        if (StorageUtil.getSortOrder(this).equals(Globals.DATE_ADDED)) rbRecently.setChecked(true);
        if (StorageUtil.getSortOrder(this).equals(Globals.ALBUM)) rbAlbum.setChecked(true);
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = view.findViewById(checkedId);
            if (radioButton.getText().equals(getString(R.string.title))) StorageUtil.setSortOrder(Globals.TITLE, MainActivity.this);
            else if (radioButton.getText().equals(getString(R.string.recently_added))) StorageUtil.setSortOrder(Globals.DATE_ADDED, MainActivity.this);
            else if (radioButton.getText().equals(getString(R.string.artist))) StorageUtil.setSortOrder(Globals.ARTIST, MainActivity.this);
            else if (radioButton.getText().equals(getString(R.string.albums))) StorageUtil.setSortOrder(Globals.ALBUM, MainActivity.this);
            initFragment(new SongListFragment(), SONG_LIST_FRAGMENT_TAG);
            bottomSheetDialog.dismiss();
        });
        checkBox.setChecked(StorageUtil.isWhichOrder(this));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> StorageUtil.setWhichOrder(this));
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void initFragment(Fragment fragment, String tag) {
        Bundle args = new Bundle();
        args.putString(ALBUMS_FRAGMENT_TAG, tag);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(fragmentContainer, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
        actionBar.setTitle(tag);
    }

    private boolean checkReadPermissions() {
        boolean readStorage = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST);
            } else readStorage = true;


        }
        return readStorage;
    }

    private boolean checkWritePermissions() {
        boolean readStorage = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST);
            } else readStorage = true;

        }
        return readStorage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint("Type song or artist name..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                filter.filter(search.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter.filter(s.toLowerCase());
                return true;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort) sortSongs();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            StorageUtil.createPlaylist(FAVORITES, this);
            initViewPaging();
    }

    @Override
    public void changeFragment(String tag, ArrayList<MusicFile> songs, int position) {
        switch (tag) {
            case Globals.PLAY_SONG_ACTIVITY_TAG:
                playSongIntent = new Intent(this, PlaySongActivity.class);
                playSongIntent.putExtra(Globals.POSITION_KEY, position);
                playSongIntent.putExtra(Globals.SONGS_KEY, songs);
                startActivity(playSongIntent);
                break;
            case SONG_LIST_FRAGMENT_TAG:
                if (songListFragment == null) {
                    songListFragment = new SongListFragment();
                }
                initFragment(songListFragment, tag);
                break;
        }
    }

    @Override
    public void changeFragment(MusicFile musicFile, String tag) {
        if (albumSongListFragment == null) {
            albumSongListFragment = new AlbumSongListFragment();
        }
        actionBar.hide();
        Bundle args = new Bundle();
        args.putParcelable(ALBUMS_FRAGMENT_TAG, musicFile);
        args.putString(STRING_EXTRA, tag);
        albumSongListFragment.setArguments(args);
        initFragment(albumSongListFragment, ALBUM_SONG_LIST_FRAGMENT_TAG);

    }

    @Override
    public void changeFragment(ArtistModel artistModel, String tag) {
        if (albumSongListFragment == null) {
            albumSongListFragment = new AlbumSongListFragment();
        }
        actionBar.hide();
        Bundle args = new Bundle();
        args.putParcelable(ALBUMS_FRAGMENT_TAG, artistModel);
        args.putString(STRING_EXTRA, tag);
        albumSongListFragment.setArguments(args);
        initFragment(albumSongListFragment, ALBUM_SONG_LIST_FRAGMENT_TAG);
    }

    @Override
    public void changeFragment(String playlistName, String tag) {

        if (albumSongListFragment == null) {
            albumSongListFragment = new AlbumSongListFragment();
        }
        actionBar.hide();
        Bundle args = new Bundle();
        args.putString(ALBUMS_FRAGMENT_TAG, playlistName);
        args.putString(STRING_EXTRA, tag);
        albumSongListFragment.setArguments(args);
        initFragment(albumSongListFragment, ALBUM_SONG_LIST_FRAGMENT_TAG);
    }

    @Override
    public void openPlaySongActivity(MusicFile song) {
        if (playSongIntent == null) {
            playSongIntent = new Intent(this, PlaySongActivity.class);
        }
        playSongIntent.putExtra(Globals.POSITION_KEY, StorageUtil.getPosition(this));
        playSongIntent.putExtra(Globals.SONGS_KEY, StorageUtil.getPlayingSongs(this));
        startActivity(playSongIntent);
    }

    private void initNavigationView() {
        nNavigationView.setNavigationItemSelectedListener(item -> {
            if (!actionBar.isShowing()) actionBar.show();
            switch (item.getItemId()) {
                case (R.id.nav_all_songs):
                    initFragment(songListFragment, SONG_LIST_FRAGMENT_TAG);
                    break;
                case (R.id.nav_albums):
                    initFragment(albumsFragment, ALBUMS_FRAGMENT_TAG);
                    break;
                case (R.id.nav_artists):
                    initFragment(new ArtistsFragment(), ARTISTS_FRAGMENT_TAG);
                    break;
                case (R.id.nav_playlists):
                    initFragment(new PlayListFragment(), PLAYLIST_FRAGMENT_TAG);
                    break;
                case (R.id.nav_folders):
                    if (foldersFragment == null) foldersFragment = new FoldersFragment();
                    initFragment(foldersFragment, FOLDERS_FRAGMENT_TAG);
                    break;
                case (R.id.nav_about):
                    initFragment(new AboutDeveloperFragment(), ABOUT_DEVELOPER_FRAGMENT_TAG);
                    break;
                case (R.id.nav_settings):
                    //TODO: TBI...
                    break;
                default:
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (StorageUtil.getCurrentSong(this) == null) miniPlayerCard.setVisibility(View.GONE);
        else{
            miniPlayerCard.setVisibility(View.VISIBLE);
            fm.beginTransaction().replace(R.id.mini_player_view, new MinimizedPlayerFragment(), MINIMIZED_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fm.getBackStackEntryCount()>0 && (fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName().equals(ALBUM_SONG_LIST_FRAGMENT_TAG ) || fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName().equals(ABOUT_DEVELOPER_FRAGMENT_TAG ) )){
                if (!actionBar.isShowing()) actionBar.show();
                this.getWindow().setStatusBarColor(getColor(R.color.grey_700));
                super.onBackPressed();
                if (fm.getBackStackEntryCount()>0) actionBar.setTitle(fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName());

            }else{
                moveTaskToBack(false);
            }

        }
    }

    @Override
    public void bindMusicService(ServiceConnection serviceConnection) {
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void startMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra(POSITION_KEY, StorageUtil.getPosition(this));
        startService(serviceIntent);
    }

    public void shareMusicFile(String id){
        Uri uri = Globals.getSongUri(Integer.parseInt(id));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Sound File"));
    }

    @Override
    public void deleteMusicFile(String id) {
        try {
            if (checkWritePermissions()) getContentResolver().delete(getSongUri(Integer.parseInt(id)), null, null);
        } catch (SecurityException e){
            requestDeletePermission(List.of(getSongUri(Integer.parseInt(id))));
        }
    }

    private void requestDeletePermission(List<Uri> uriList){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            PendingIntent pi = MediaStore.createDeleteRequest(getContentResolver(), uriList);

            try {
                startIntentSenderForResult(pi.getIntentSender(), REQUEST_PERM_DELETE, null, 0, 0,
                        0);
            } catch (IntentSender.SendIntentException ignored) { }
        }
    }

    @Override
    public void whatsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(WHATSAPP_URL));
        startActivity(intent);
    }

    @Override
    public void facebook() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(FACEBOOK_URL));
        startActivity(intent);
    }

    @Override
    public void instagram() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(INSTAGRAM_URL));
        startActivity(intent);
    }

    @Override
    public void gmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, "shinshray@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Request to collaborate");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey Sharif, I been using your music player app and i think its amazing, i would like to collaborate" +
                "with you on a project if you dont mind.");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @Override
    public void linkedIn() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(LINKED_IN_URL));
        startActivity(intent);
    }
}