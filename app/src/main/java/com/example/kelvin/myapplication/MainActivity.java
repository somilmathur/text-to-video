package com.example.kelvin.myapplication;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Button search;
    public Button play;

    public EditText videoNameInput;
    public TextView videoUrlText;

    public VideoView video;
    public ArrayList<String> videoUrls;
    public Map<String,String> urlMappings;
    public int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = (Button) findViewById(R.id.button1);
        search.setOnClickListener(this);

        play = (Button) findViewById(R.id.button2);
        play.setOnClickListener(this);

        videoNameInput = (EditText) findViewById(R.id.editText);
        videoUrlText = (TextView) findViewById(R.id.textView);

        video = (VideoView)findViewById(R.id.videoView);
        videoUrls = new ArrayList<String>();
        urlMappings = new HashMap<>();
        index = 0;

        final Handler handler = new Handler();

        Thread th = new Thread(new Runnable() {
            public void run() {

                try {

                    final Map<String,String> mappings = UrlManager.ListVideos();

                    handler.post(new Runnable() {

                        public void run() {
                            MainActivity.this.urlMappings = mappings;
                        }
                    });
                }
                catch(Exception ex) {
                    final String exceptionMessage = ex.getMessage();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }});
        th.start();



    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case (R.id.button2):
                try {
                    videoUrls.clear();
                    video.stopPlayback();
                    index = 0;

                    String videoName = videoNameInput.getText().toString();
                    ArrayList<String> words = new ArrayList<>(Arrays.asList(videoName.split("\\s+")));
                    ArrayList<String> vids = getCombinations(words);

                    for(String vid : vids)
                    {
                        vid = vid.toLowerCase();
                        String url = urlMappings.get(vid);
                        if(url!=null)
                        {
                            videoUrls.add(url);
                            //Toast.makeText(MainActivity.this, "Video Url : " + url, Toast.LENGTH_LONG);
                        }
                    }

                    playvideo();
                } catch (Exception e){
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }

        }

    }

    public void playvideo(){
        video.setVideoURI(Uri.parse(videoUrls.get(index)));
        video.setMediaController(new MediaController(this));
        video.requestFocus();
        video.start();
        //Toast.makeText(getApplicationContext(), "Playing video " + (index+1) + " out of "+ videoUrls.size(), Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "Video URI :  "  + videoUrls.get(index), Toast.LENGTH_LONG).show();


        video.setClickable(true);
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion (MediaPlayer mp)
            {
                mp.stop();
                video.stopPlayback();
                video.suspend();
                index++;

                if (index < videoUrls.size())
                {
                    playvideo();
                }
            }
        });
    }

    public ArrayList<String> getCombinations(ArrayList<String> words)
    {
        int number = words.size();
        ArrayList<String> test = new ArrayList<>();

        for(int index = 0; index < number; index++)
        {
            for(int n = 1; n <= number; n++)
                if(n + index <= number)
                    test.add(getCombination(words,index,n+index));
        }

        return test;
    }

    public String getCombination(ArrayList<String> words, int start, int end)
    {
        String combination = words.get(start);

        for(int index = start+1; index < end; index++)
        {
            combination += " " + words.get(index);
        }
        System.out.println(combination);
        return combination;
    }
}
