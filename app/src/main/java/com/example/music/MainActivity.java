package com.example.music;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static MediaPlayer mp;
    BottomNavigationView bottomNavigationView;
    ImageButton imageButton;
    ;
    ImageButton openPlaylist;
    SongAdapter songAdapter;
    int count = 0;
    RecyclerView recyclerViewPlaylist;
    List<Integer> buyProducts = new ArrayList<>();
    List<String> webSite = new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;
    int position = 0;
    SeekBar sb;
    Thread updateSeekBar;
    Button pause, next, previous;
    TextView songNameText;
    String sname;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ArrayList<SongInfo> _songs = new ArrayList<SongInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songAdapter = new SongAdapter(this, _songs);

        buyProducts.add(R.drawable.phone);
        buyProducts.add(R.drawable.watch);
        buyProducts.add(R.drawable.watch1);
        buyProducts.add(R.drawable.jeans1);
        buyProducts.add(R.drawable.shirt);
        buyProducts.add(R.drawable.shirt1);
        buyProducts.add(R.drawable.shoes);
        buyProducts.add(R.drawable.shoes2);
        webSite.add("https://www.amazon.in/iPhone-Plus-Mobile-Cover-SQUARE/dp/B07JLKX12Z");
        webSite.add("https://www.amazon.in/Watches-Square/s?rh=n%3A1350387031%2Cp_n_style_browse-bin%3A1663601031");
        webSite.add("https://www.amazon.in/Digital-Military-Square-Watches-Electronic/dp/B0786J49HK");
        webSite.add("https://www.amazon.in/");
        webSite.add("https://www.amazon.in/Sunward-T-Shirt-Ethnic-Printing-Hawaiian/dp/B084NVQCC2/ref=asc_df_B084NVQCC2/?tag=googleshopdes-21&linkCode=df0&hvadid=397079320316&hvpos=&hvnetw=g&hvrand=1146682170955984920&hvpone=&hvptwo=&hvqmt=&hvdev=c&hvdvcmdl=&hvlocint=&hvlocphy=9061645&hvtargid=pla-893881803498&psc=1&ext_vrnc=hi");
        webSite.add("https://www.amazon.in/Sunward-T-Shirt-Ethnic-Printing-Hawaiian/dp/B084NVQCC2/ref=asc_df_B084NVQCC2/?tag=googleshopdes-21&linkCode=df0&hvadid=397079320316&hvpos=&hvnetw=g&hvrand=1146682170955984920&hvpone=&hvptwo=&hvqmt=&hvdev=c&hvdvcmdl=&hvlocint=&hvlocphy=9061645&hvtargid=pla-893881803498&psc=1&ext_vrnc=hi");
        webSite.add("https://www.amazon.in/Square-Trendy-Canvas-Sneaker-Color/dp/B07L3ZLCC3");
        webSite.add("https://www.amazon.in/Square-Smart-Casual-Shoes-Color/dp/B07L3ZRW4R");
        RecyclerView recyclerView = findViewById(R.id.ShoppingLayout);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(this, buyProducts, webSite);
        recyclerView.setAdapter(recyclerViewAdapter);
        openPlaylist = findViewById(R.id.openPlaylist);
        checkUserPermission();

        openPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });

        imageButton = findViewById(R.id.appMenuButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Side Navigation Drawer will be added", Toast.LENGTH_LONG).show();
            }
        });
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);


        songNameText = (TextView) findViewById(R.id.txtSongLabel);

        pause = (Button) findViewById(R.id.pause);

        previous = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);

        sb = (SeekBar) findViewById(R.id.seekBar);

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int runtime = mp.getDuration();
                int currentPosition = 0;
                int adv = 0;
                while ((adv = ((adv = runtime - currentPosition) < 500) ? adv : 500) > 2) {
                    try {
                        if (mp != null & mp.isPlaying())
                            currentPosition = mp.getCurrentPosition();
                        else
                            currentPosition = 0;
                        if (sb != null) {
                            sb.setProgress(currentPosition);
                        }
                        sleep(adv);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        sb.setProgress(0);
                        break;
                    }

                }
            }
        };

        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
        }
        sname = _songs.get(position).getSongname();
        songNameText.setText(sname);
        songNameText.setSelected(true);
        Uri u = Uri.parse(_songs.get(position).getSongUrl());
        mp = MediaPlayer.create(this, u);
        sb.setMax(mp.getDuration());
        updateSeekBar.start();
        sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sb.setMax(mp.getDuration());
                if (mp.isPlaying()) {
                    pause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    mp.pause();
                } else {
                    pause.setBackgroundResource(R.drawable.pause);
                    mp.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.reset();
                mp.release();
                previous.setVisibility(View.VISIBLE);
                position = ((position + 1) % _songs.size());
                Uri u = Uri.parse(_songs.get(position).getSongUrl());
                mp = MediaPlayer.create(MainActivity.this, u);
                sname = _songs.get(position).getSongname();
                songNameText.setText(sname);
                pause.setBackgroundResource(R.drawable.pause);
                mp.start();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                    position = ((position - 1) < 0) ? (_songs.size() - 1) : (position - 1);
                    Uri u = Uri.parse(_songs.get(position).getSongUrl());
                    mp = MediaPlayer.create(MainActivity.this, u);
                    sname = _songs.get(position).getSongname();
                    songNameText.setText(sname);
                    pause.setBackgroundResource(R.drawable.pause);
                    mp.start();
                } else {
                    Toast.makeText(MainActivity.this, "This is the first song", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createPopupDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);

        recyclerViewPlaylist = view.findViewById(R.id.recyclerViewPlaylist);

        recyclerViewPlaylist.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPlaylist.getContext(),
                linearLayoutManager.getOrientation());
        recyclerViewPlaylist.setLayoutManager(linearLayoutManager);
        recyclerViewPlaylist.addItemDecoration(dividerItemDecoration);

        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CardView cardView, View view, SongInfo obj, int i) {
                position = i;
                mp.stop();
                mp.reset();
                mp.release();
                sname = _songs.get(position).getSongname();
                songNameText.setText(sname);
                songNameText.setSelected(true);
                Uri u = Uri.parse(_songs.get(position).getSongUrl());
                mp = MediaPlayer.create(MainActivity.this, u);
                pause.setBackgroundResource(R.drawable.pause);
                mp.start();
                sb.setMax(mp.getDuration());
                sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }
        loadSongs();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadSongs();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void loadSongs() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    SongInfo s = new SongInfo(name, artist, url);
                    _songs.add(s);

                } while (cursor.moveToNext());
            }

            cursor.close();
            songAdapter = new SongAdapter(MainActivity.this, _songs);
        }
    }
}