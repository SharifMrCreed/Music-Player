 package com.alle.san.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alle.san.musicplayer.adapters.ViewPagerAdapter;
import com.alle.san.musicplayer.models.MusicFile;
import com.alle.san.musicplayer.util.Globals;
import com.alle.san.musicplayer.util.ViewChanger;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.alle.san.musicplayer.util.Globals.ADAPTER_POSITION;
import static com.alle.san.musicplayer.util.Globals.ALBUMS;
import static com.alle.san.musicplayer.util.Globals.ALBUM_SONG_LIST_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.PLAYLIST;
import static com.alle.san.musicplayer.util.Globals.SONGS;
import static com.alle.san.musicplayer.util.Globals.SONG_LIST_FRAGMENT_TAG;

public class MainActivity extends AppCompatActivity implements ViewChanger {

    private static final String TAG = "MainActivity";
    private static final int STORAGE_REQUEST =2;
    int fragmentContainer;

    SongListFragment songListFragment = null;
    PlaySongFragment playSongFragment = null;
    AlbumSongListFragment albumSongListFragment= null;
    EditText search;

    TabLayout tabLayout;
    ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Filter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainer=R.id.fragment_container;
        tabLayout = findViewById(R.id.tab_layout);
        viewPager =  findViewById(R.id.view_pager);
        search =  findViewById(R.id.search_view);
        initSearch();

        if (checkPermissions()) initViewPaging();
    }


    private void initViewPaging() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongListFragment(), SONGS);
        viewPagerAdapter.addFragments(new AlbumsFragment(), ALBUMS);
        viewPagerAdapter.addFragments(new PlayListFragment(), PLAYLIST);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        filter = (Filter) viewPagerAdapter.getItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset>0.5 && position == 0) search.setVisibility(View.GONE);
                else if (position>0) search.setVisibility(View.GONE);
                else search.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageSelected(int position) {
                if (position>0) search.setVisibility(View.GONE);
                else search.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initFragment(Fragment fragment, String tag) {
        Log.d(TAG, "\n\ninitFragment: "+ tag + "\n\n");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentContainer, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }


    private boolean checkPermissions() {
        boolean readStorage = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST);
            }else readStorage = true;


        }
        return readStorage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) initViewPaging();
    }

    @Override
    public void changeFragment(String tag, ArrayList<MusicFile> songs, int position) {
        switch (tag) {
            case Globals.PLAY_SONG_FRAGMENT_TAG: {
                if (playSongFragment == null) {
                    playSongFragment = new PlaySongFragment();
                }
                Bundle args = new Bundle();
                args.putInt(Globals.ADAPTER_POSITION, position);
                args.putParcelableArrayList(Globals.SONGS_LIST, songs);
                playSongFragment.setArguments(args);
                initFragment(playSongFragment, tag);

                break;
            }
            case SONG_LIST_FRAGMENT_TAG:
                if (songListFragment == null) {
                    songListFragment = new SongListFragment();
                }
                initFragment(songListFragment, tag);
                break;
            case ALBUM_SONG_LIST_FRAGMENT_TAG: {
                if (songListFragment == null) {
                    songListFragment = new SongListFragment();
                }
                if (albumSongListFragment == null) {
                    albumSongListFragment = new AlbumSongListFragment();
                }
                Bundle args = new Bundle();
                args.putParcelableArrayList(ALBUMS, songs);
                args.putInt(ADAPTER_POSITION, position);
                albumSongListFragment.setArguments(args);
                initFragment(albumSongListFragment, ALBUM_SONG_LIST_FRAGMENT_TAG);
                break;
            }
        }
    }


    private void initSearch() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter.filter(s.toString());

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}