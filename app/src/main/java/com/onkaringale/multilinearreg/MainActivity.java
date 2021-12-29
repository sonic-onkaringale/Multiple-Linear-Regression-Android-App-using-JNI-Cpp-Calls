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
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;

    public static final int myrequestCode = 1014;
    private ActivityMainBinding binding;

    List<String[]> dataset = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoView = (VideoView) findViewById(R.id.videoview);
        String vpath = "android.resource://" + getPackageName() + "/" + R.raw.intro;
        videoView.setVideoURI(Uri.parse(vpath));
//        MediaController mediaController;
//        mediaController = new MediaController(MainActivity.this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0f,0f);
                mp.setLooping(true);
            }
        });
        videoView.start();

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
        if(videoView != null)
        {
            videoView.start();
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
                        Intent intent_csvdashb = new Intent(MainActivity.this,csv_dashboard.class);
                        intent_csvdashb.putExtra("size",dataset.size());
                        for (int i = 0; i < dataset.size(); i++) {
                            intent_csvdashb.putExtra( ("row_"+i),dataset.get(i));
                        }
                        startActivity(intent_csvdashb);
                    } catch (IOException | CsvException e) {
                        e.printStackTrace();
                    }

                }

        }
    }


}