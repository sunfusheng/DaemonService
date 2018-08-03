package com.sunfusheng.daemon.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sunfusheng.daemon.DaemonHolder;

public class MainActivity extends AppCompatActivity {

    TextView vText;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vText = findViewById(R.id.text);

        vText.setText(sb);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startService1:
                DaemonHolder.startService(WorkService1.class);
                break;
            case R.id.stopService1:

                break;
            case R.id.startService2:
                DaemonHolder.startService(WorkService2.class);
                break;
            case R.id.stopService2:

                break;
        }
    }
}
