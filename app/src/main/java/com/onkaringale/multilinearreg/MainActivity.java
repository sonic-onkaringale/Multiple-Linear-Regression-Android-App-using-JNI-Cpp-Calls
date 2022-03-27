package com.onkaringale.multilinearreg;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cpp.R;
import com.example.cpp.databinding.ActivityMainBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    ExoPlayer exoplayer;

    public static final int myrequestCode = 1014;
    private ActivityMainBinding binding;

    List<String[]> dataset = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String vpath = "android.resource://" + getPackageName() + "/" + R.raw.reintroweb;

        exoplayer = new ExoPlayer.Builder(this).build();
        PlayerView playerView = findViewById(R.id.videoview);
        playerView.setPlayer(exoplayer);
        playerView.setUseController(false);
        MediaItem mediaItem= MediaItem.fromUri(Uri.parse(vpath));
        exoplayer.addMediaItem(mediaItem);
        exoplayer.setRepeatMode(exoplayer.REPEAT_MODE_ONE);
        exoplayer.prepare();
        exoplayer.play();



//        videoView = (VideoView) findViewById(R.id.videoview);

//        videoView.setVideoURI(Uri.parse(vpath));
//        MediaController mediaController;
//        mediaController = new MediaController(MainActivity.this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setVolume(0f,0f);
//                mp.setLooping(true);
//            }
//        });
//        videoView.start();

        Button fcbtn = (Button) findViewById(R.id.choosefilebtn);
        fcbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choosefile = new Intent(Intent.ACTION_GET_CONTENT);
                choosefile.addCategory(Intent.CATEGORY_OPENABLE);
                choosefile.setType("text/comma-separated-values");
                choosefile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(choosefile, "Open CSV"), myrequestCode);



            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(exoplayer != null)
        {
            exoplayer.play();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case myrequestCode:
                if(resultCode == Activity.RESULT_OK)
                {
//                    TextView line1 = binding.filepath;
                    assert data != null;
                    Uri uri = data.getData();
                    try {
                        InputStream input = this.getContentResolver().openInputStream(uri);
                        CSVReader reader = new CSVReader(new InputStreamReader(input));
                        dataset = reader.readAll();
                        ArrayList<Integer> drop_indexes = new ArrayList<>();

                        //Drop Implementation
                        for (int i = 0; i < dataset.get(1).length; i++)
                        {
                            try {
                                Double.parseDouble(dataset.get(1)[i]);
                            }
                            catch (NumberFormatException e )
                            {
                                drop_indexes.add(i);
                            }
                        }

                        if (0<drop_indexes.size())
                        {
                            for (int i = 0; i < dataset.size(); i++)
                            {
                                dataset.set(i,drop_index(dataset.get(i),drop_indexes));
                            }
                        }
                        //Drop Implementation End

                        Intent intent_csvdashb = new Intent(MainActivity.this,csv_dashboard.class);
                        intent_csvdashb.putExtra("size",dataset.size());
                        for (int i = 0; i < dataset.size(); i++) {
                            intent_csvdashb.putExtra( ("row_"+i),dataset.get(i));
                        }
//                        exoplayer.release();
                        startActivity(intent_csvdashb);
                    } catch (IOException | CsvException e) {
                        e.printStackTrace();
                    }

                }

        }
    }

    public String[] drop_index(String[] a,ArrayList<Integer> index)
    {
        ArrayList<String> a_al = new ArrayList<String>(Arrays.asList(a));
        for (int i = 0; i < index.size(); i++)
        {
            int temp = index.get(i);
            a_al.remove(temp);
        }
        String[] b = new String[a_al.size()];
        for (int i = 0; i < b.length; i++)
        {
            b[i] = a_al.get(i);
        }
        return b;
    }


}