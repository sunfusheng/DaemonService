package com.sunfusheng.daemon.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sunfusheng.daemon.DaemonHolder;

public class MainActivity extends AppCompatActivity {
    private TextView vText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vText = findViewById(R.id.text);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startService:
                DaemonHolder.startService();
                break;
            case R.id.stopService:
                DaemonHolder.stopService();
                break;
        }
    }
}
