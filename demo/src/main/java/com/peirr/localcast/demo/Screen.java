package com.peirr.localcast.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.peirra.cast.local.LocalContract;
import com.peirra.cast.local.LocalPresenter;

public class Screen extends AppCompatActivity implements LocalContract.View {
    private static final String TAG = "Screen";
    private LocalPresenter presenter;
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
        presenter = new LocalPresenter(Injection.provideHttpRequest(this),
                Injection.provideCastDevice(this, CastOptionsProvider.CUSTOM_NAMESPACE),false);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        castMsg = (TextView) findViewById(R.id.cast_message);
        httpMsg = (TextView) findViewById(R.id.http_message);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(Screen.this, Screen.class));
            }
        });
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.post(MESSAGE);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        presenter.attachView(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        presenter.detachView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        presenter.addMenu(menu, R.id.action_cast);
        return true;
    }

    @Override
    public void showCastConnected(final boolean connected) {
        Log.d(TAG, "showCastConnected() : " + "connected = [" + connected + "]");
        String message = connected ? "CONNECTED" : "DISCONNECTED";
        castMsg.setText(getString(R.string.cast_connection, message));
    }

    @Override
    public void showServerConnected(final boolean connected, final String endpoint) {
        Log.d(TAG, "showServerConnected() : " + "connected = [" + connected + "], endpoint = [" + endpoint + "]");
        String message = connected ? "CONNECTED" : "DISCONNECTED";
        httpMsg.setText(getString(R.string.http_connection, message + ": " + endpoint));

    }

    @Override
    public void showMessage(final String message) {
        Log.d(TAG, "showMessage() : " + "message = [" + message + "]");
    }
}
