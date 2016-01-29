package com.peirr.localcast.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.peirr.localcast.io.LocalCastManager;

public class CastActivity extends AppCompatActivity {

    private LocalCastManager castManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_demo);
        castManager = new LocalCastManager(this,getString(R.string.cast_app_id),5678);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        castManager.connect();
    }


    @Override
    protected void onPause() {
        castManager.disconnect();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        DataCastManager.getInstance().addMediaRouterButton(menu, R.id.action_cast);
        return true;
    }
}
