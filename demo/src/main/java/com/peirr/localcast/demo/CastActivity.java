package com.peirr.localcast.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.peirra.cast.local.LocalContract;
import com.peirra.cast.local.LocalPresenter;

public class CastActivity extends AppCompatActivity implements LocalContract.View {
    private static final String TAG = "CastActivity";
    private LocalPresenter castmanager;
    private TextView castMsg, httpMsg;

    private String MESSAGE = "{  \n" +
            "   \"typ\":\"VIDEO\",\n" +
            "   \"state\":\"PLAY\",\n" +
            "   \"version\":1,\n" +
            "   \"data\":{  \n" +
            "      \"url\":\"http://www.peirr.com/black.mp4\"\n" +
            "   }\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_demo);
        castmanager = new LocalPresenter(Injection.provideHttpRequest(this),Injection.provideCastDevice(this,CastOptionsProvider.CUSTOM_NAMESPACE));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        castMsg = (TextView) findViewById(R.id.cast_message);
        httpMsg = (TextView) findViewById(R.id.http_message);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castmanager.post(MESSAGE);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        castmanager.attachView(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        castmanager.detachView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        castmanager.addMenu(menu, R.id.action_cast);
        return true;
    }

    @Override
    public void showCastConnected(final boolean connected) {
        String message = connected ? "CONNECTED" : "DISCONNECTED";
        castMsg.setText(getString(R.string.cast_connection, message));
    }

    @Override
    public void showServerConnected(final boolean connected, final String endpoint) {
        String message = connected ? "CONNECTED" : "DISCONNECTED";
        httpMsg.setText(getString(R.string.http_connection, message + ": " + endpoint));

    }

    @Override
    public void showMessage(final String message) {
        Log.d(TAG, "showMessage() : " + "message = [" + message + "]");
    }
}
