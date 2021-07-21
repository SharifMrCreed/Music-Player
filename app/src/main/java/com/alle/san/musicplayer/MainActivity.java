package com.alle.san.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.alle.san.musicplayer.util.StorageUtil;
import com.alle.san.musicplayer.util.UtilInterfaces;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ALBUMS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ALBUM_SONG_LIST_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ARTISTS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.PLAYLIST_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.SONG_LIST_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.STRING_EXTRA;
import static com.alle.san.musicplayer.util.Globals.albumBitmap;

public class MainActivity extends AppCompatActivity implements UtilInterfaces.ViewChanger {

    private static final String TAG = "MainActivity";
    private static final int STORAGE_REQUEST = 2;
    public static ArrayList<MusicFile> allMusic = new ArrayList<>();
    public static ArrayList<MusicFile> allAlbums = new ArrayList<>();
    public static ArrayList<ArtistModel> allArtists = new ArrayList<>();
    int fragmentContainer;
    ActionBar actionBar;
    FragmentManager fm;

    SongListFragment songListFragment = null;
    AlbumsFragment albumsFragment = null;
    AlbumSongListFragment albumSongListFragment = null;

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView nNavigationView;

    private UtilInterfaces.Filter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainer = R.id.fragment_container;
        drawer = findViewById(R.id.drawer_layout);
        nNavigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.appToolBar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        fm = getSupportFragmentManager();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        initNavigationView();

        if (checkPermissions()) initViewPaging();
        if (albumsFragment == null) albumsFragment = new AlbumsFragment();
    }


    private void initViewPaging() {
        if (songListFragment == null) songListFragment = new SongListFragment();
        initFragment(songListFragment, SONG_LIST_FRAGMENT_TAG);
        filter = (UtilInterfaces.Filter) songListFragment;
        getAlbums(StorageUtil.getSongsFromStorage(this));
        getArtists(StorageUtil.getSongsFromStorage(this));
    }

    private void getAlbums(ArrayList<MusicFile> allSongs) {
        ArrayList<MusicFile> albums = new ArrayList<>();
        ArrayList<String> albumNames = new ArrayList<>();
        new Thread(() -> {
            for (MusicFile musicFile : allSongs) {
                if (!albumNames.contains(musicFile.getAlbum())) {
                    albumNames.add(musicFile.getAlbum());
                    musicFile.setAlbumImage(albumBitmap(this, musicFile.getData()));
                    albums.add(musicFile);
                }
            }
            allAlbums.addAll(albums);
        }).start();

    }

    private void getArtists(ArrayList<MusicFile> allSongs) {
        ArrayList<ArtistModel> artists = new ArrayList<>();
        ArrayList<String> artistsNames = new ArrayList<>();
        new Thread(() -> {
            for (MusicFile musicFile : allSongs) {
                if (!artistsNames.contains(musicFile.getArtist())) {
                    ArtistModel artistModel = new ArtistModel();
                    ArrayList<Bitmap> one = new ArrayList<>();
                    for (MusicFile musicFile1 : allSongs) {
                        if (musicFile1.getArtist().equals(musicFile.getArtist())) {
                            one.add(albumBitmap(this, musicFile1.getData()));
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
                    artists.add(artistModel);
                }
            }
            allArtists.addAll(artists);
        }).start();

    }

    private void initFragment(Fragment fragment, String tag) {
        Log.d(TAG, "\n\ninitFragment: " + tag + "\n\n");
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(fragmentContainer, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
        actionBar.setTitle(tag);
    }


    private boolean checkPermissions() {
        boolean readStorage = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            initViewPaging();
    }

    @Override
    public void changeFragment(String tag, ArrayList<MusicFile> songs, int position) {
        switch (tag) {
            case Globals.PLAY_SONG_ACTIVITY_TAG:
                Intent intent = new Intent(this, PlaySongActivity.class);
                intent.putExtra(Globals.POSITION_KEY, position);
                intent.putExtra(Globals.SONGS_KEY, songs);
                startActivity(intent);
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

    private void initNavigationView() {
        nNavigationView.setNavigationItemSelectedListener(item -> {
            if (!actionBar.isShowing()) actionBar.show();
            switch (item.getItemId()) {
                case (R.id.nav_all_songs):
                    initFragment(songListFragment, SONG_LIST_FRAGMENT_TAG);
                    break;
                case (R.id.nav_favourites):
//                    initFragment(new FavoritesFragment(), FAVOURITES_FRAGMENT_TAG);
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
                    //TODO: TBI..
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fm.getBackStackEntryCount()>0 && !fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName().equals(ALBUM_SONG_LIST_FRAGMENT_TAG)){
                if (!actionBar.isShowing()) actionBar.show();
            }
            this.getWindow().setStatusBarColor(getColor(R.color.grey_700));
            super.onBackPressed();
        }
    }


}