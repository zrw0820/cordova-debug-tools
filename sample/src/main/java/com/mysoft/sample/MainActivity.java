package com.mysoft.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mysoft.debug_tools.ui.view.FloatMenuLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppPrefs appPrefs = PrefsHelper.getPrefs(AppPrefs.class);
        appPrefs.setDebugUrl("http://www.baidu.com");
        appPrefs.setLocalWebVersion("1.0.0");
        appPrefs.setUseX5Engine(true);
        appPrefs.setX5Install(true);

        ((FloatMenuLayout) findViewById(R.id.menu_layout)).setOnMenuItemClickListener(new FloatMenuLayout.OnMenuItemClickListener() {
            @Override
            public void onMenuClick(View menuItem, int menuIndex) {
                Toast.makeText(MainActivity.this, "menuIndex:" + menuIndex, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
