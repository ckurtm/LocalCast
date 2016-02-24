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
import android.widget.TextView;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.common.images.WebImage;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;
import com.peirr.localcast.io.LocalCastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CastActivity extends AppCompatActivity implements LocalCastManager.HttpConnectionListener, LocalCastManager.CastConnectionListener {
    private static final String TAG = "CastActivity";
    private LocalCastManager castManager;
    private TextView castMsg, httpMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_demo);
        castManager = new LocalCastManager(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        castMsg = (TextView) findViewById(R.id.cast_message);
        httpMsg = (TextView) findViewById(R.id.http_message);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DevBytes.mp4");
                MediaPlayer mp = MediaPlayer.create(CastActivity.this, Uri.parse(file.getAbsolutePath()));
                int duration = mp.getDuration();
                String url = file.getAbsolutePath();
                MediaInfo info = castManager.buildMediaInfo("test", "subtitle", duration, url, URLConnection.guessContentTypeFromName(file.getAbsolutePath()), "", "", new ArrayList<MediaTrack>());
                Log.d(TAG, "[duration:" + duration + "] [url:" + castManager.getEndpoint() + "/" + url + "]");
                castManager.play(info);
            }
        });

        castManager.registerHttpConnectionListener(this);
        castManager.registerCastConnectionListener(this);
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

    @Override
    public void onHttpConnectionChanged(int status, SimpleHttpInfo info) {
        String message = "";
        switch (status) {
            case SimpleHttpService.STATE_RUNNING:
                message = "RUNNING [" + info.ip + ":" + info.port + "]";
                break;
            case SimpleHttpService.STATE_STOPPED:
                message = "SHUTDOWN [" + info.ip + ":" + info.port + "]";
                break;
            case SimpleHttpService.STATE_ERROR:
                message = "ERROR: " + (info != null ? info.message : "");
                break;
        }
        httpMsg.setText(getString(R.string.http_connection, message));
    }

    @Override
    public void onCastConnectionChanged(boolean connected, Exception exception) {
        String message = connected ? "CONNECTED" : "DISCONNECTED";
        castMsg.setText(getString(R.string.cast_connection, message));

    }
}
