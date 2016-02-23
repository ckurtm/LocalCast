package com.peirr.localcast.demo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.common.images.WebImage;
import com.google.android.libraries.cast.companionlibrary.utils.Utils;
import com.peirr.localcast.io.LocalCastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CastActivity extends AppCompatActivity {
    private static final String TAG = "CastActivity";
    private LocalCastManager castManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_demo);
        castManager = new LocalCastManager(this,getString(R.string.cast_app_id),5678,false);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/4k/Bees.mp4");
                MediaPlayer mp = MediaPlayer.create(CastActivity.this,Uri.parse(file.getAbsolutePath()));
                int duration = mp.getDuration();
                String url = castManager.getEndpoint()  + file.getAbsolutePath();
                Log.d(TAG,"[duration:"+duration+"] [url:"+url+"]");
                MediaInfo info = buildMediaInfo("test","Studio","subtitle",duration,url,URLConnection.guessContentTypeFromName(file.getAbsolutePath()),"","",new ArrayList<MediaTrack>());
                castManager.play(info);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        castManager.connect();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        castManager.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        castManager.addMediaRouterButton(menu, R.id.action_cast);
        return true;
    }


    private static MediaInfo buildMediaInfo(String title, String studio, String subTitle,int duration, String url, String mimeType, String imgUrl, String bigImageUrl,List<MediaTrack> tracks) {
        String KEY_DESCRIPTION = "description";
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, studio);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        movieMetadata.addImage(new WebImage(Uri.parse(imgUrl)));
        movieMetadata.addImage(new WebImage(Uri.parse(bigImageUrl)));
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject();
            jsonObj.put(KEY_DESCRIPTION, subTitle);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to add description to the json object", e);
        }
        return new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(mimeType)
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .setStreamDuration(duration)
                .setCustomData(jsonObj)
                .build();
    }
}
