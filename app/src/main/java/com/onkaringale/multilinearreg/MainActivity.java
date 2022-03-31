package com.onkaringale.multilinearreg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cpp.R;
import com.example.cpp.databinding.ActivityMainBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ExoPlayer exoplayer;
    ProgressBar progressBar;

    public static final int myrequestCode = 1014;
    static boolean null_detected = false;

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
        progressBar = findViewById(R.id.progressBarmain);
        progressBar.setVisibility(View.INVISIBLE);




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
                    assert data != null;
                    Uri uri = data.getData();
                    InputStream input = null;
                    try {
                        input = this.getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());


                    InputStream finalInput = input;

                    progressBar.setVisibility(View.VISIBLE);
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {

                            //Background work here

                            try {


                                CSVReader reader = new CSVReader(new InputStreamReader(finalInput));
                                dataset = reader.readAll();
                                ArrayList<Integer> drop_indexes = new ArrayList<>();
                                if (dataset==null||dataset.size()<=0 || dataset.get(0).length<=1)
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Error: Something is wrong with your csv file.",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                    return;
                                }

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
                                Collections.sort(drop_indexes);
                                //Setting the drop indices according to future dropping
                                for (int i = 0; i < drop_indexes.size(); i++) {
                                    drop_indexes.set(i,
                                            (drop_indexes.get(i)-i)
                                    );
                                }


                                if (drop_indexes.size() == dataset.get(0).length)
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Error: Something is wrong with your csv file.",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);

                                        }
                                    });
                                    return;
                                }
                                else if (0<drop_indexes.size())
                                {
                                    for (int i = 0; i < dataset.size(); i++)
                                    {
                                        dataset.set(i,drop_index(dataset.get(i),drop_indexes,i));
                                        if (dataset.get(i) == null)
                                        {
                                            dataset.remove(i);
                                            --i;
                                        }
                                    }
                                }

                                //Null Values

                                //Drop Implementation End

                                Intent intent_csvdashb = new Intent(MainActivity.this,csv_dashboard.class);
                                //New Global Implementation
                                Globals globals = (Globals)getApplication();
                                globals.setDataset(dataset);



                                startActivity(intent_csvdashb);
                            } catch (IOException | CsvException e) {
                                e.printStackTrace();
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //UI Thread work here

                                    if (null_detected)
                                    {
                                        Toast.makeText(getApplicationContext(),"Null Values where deleted.",Toast.LENGTH_SHORT).show();

                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }


                    });
//                    TextView line1 = binding.filepath;


                }

        }
    }

    static String[] drop_index(String[] a,ArrayList<Integer> index,int pos)
    {
        ArrayList<String> a_al = new ArrayList<String>(Arrays.asList(a));
        for (int i = 0; i < index.size(); i++)
        {
            int temp = index.get(i);
            a_al.remove(temp);
        }
        if (pos != 0)
        {
            if (is_null_value(a_al))
            {
                return null;
            }
        }
        String[] b = new String[a_al.size()];
        for (int i = 0; i < b.length; i++)
        {
            b[i] = a_al.get(i);
        }
        return b;
    }

    static boolean is_null_value(ArrayList<String> row)
    {
        for (int i = 0; i < row.size(); i++) {
            try
            {
                Double.parseDouble(row.get(i));
            }
            catch (NumberFormatException e)
            {
                null_detected = true;
                return true;
            }

        }
        return false;
    }


}