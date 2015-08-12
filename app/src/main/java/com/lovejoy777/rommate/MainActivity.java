package com.lovejoy777.rommate;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // switch and build.prop call for no launcher Icon.
        SharedPreferences prefs = this.getSharedPreferences("switch1", Context.MODE_PRIVATE);
        boolean switch1 = prefs.getBoolean("switch1", false);

        Process p1 = null;
        String noIcon = "";
        try {
            p1 = new ProcessBuilder("/system/bin/getprop", "ro.rommate.noIcon").redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            String line = "";
            if ((line = br.readLine()) != null) {
                noIcon = line;

                if (noIcon.equals("noIcon") && switch1) {

                    PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(this, com.lovejoy777.rommate.MainActivity.class);
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                }

            } else {
                //Toast.makeText(MainActivity.this, "is not null", Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Icon null", Toast.LENGTH_LONG).show();

            }
            p1.destroy();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent intent = new Intent(this, MainActivity1.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();

    }
}