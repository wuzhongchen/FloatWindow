package com.example.wuzhongcheng.floatwindow;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private ToggleButton mToggle_btn;
    public static boolean KEY_IS_TOGGLE_BUTTON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToggle_btn = (ToggleButton) findViewById(R.id.toggle_button);
        mToggle_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    KEY_IS_TOGGLE_BUTTON = true;
                    mToggle_btn.setChecked(true);
                    startFloatingService();
                } else {
                    KEY_IS_TOGGLE_BUTTON = false;
                    mToggle_btn.setChecked(false);
                }
            }
        });
    }

    private void startFloatingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    KEY_IS_TOGGLE_BUTTON = false;
                    mToggle_btn.setChecked(false);
                } else {
                    KEY_IS_TOGGLE_BUTTON = true;
                    mToggle_btn.setChecked(true);
                }
            }
        }
    }
}
