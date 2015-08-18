package com.lovejoy777.rommate;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lovejoy777.rommate.bootanimation.Screen1BootAnim;
import com.lovejoy777.rommate.fonts.Screen1Fonts;
import com.stericson.RootTools.RootTools;

import java.io.IOException;

/**
 * Created by lovejoy777 on 12/08/15.
 */
public class MainActivity1 extends AppCompatActivity {

    CardView card1, card2, card3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!RootTools.isAccessGiven()) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=eu.chainfire.supersu")));

        }

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        
        card1 = (CardView) findViewById(R.id.CardView_bootanim);
        card2 = (CardView) findViewById(R.id.CardView_fonts);
        card3 = (CardView) findViewById(R.id.CardView_manager);

        // CARD 1
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bootanimactivity = new Intent(MainActivity1.this, Screen1BootAnim.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);

            }
        }); // end card1

        // CARD 2
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity1.this, "Not ready yet (long click to enter)", Toast.LENGTH_SHORT).show();
            }
        }); // end card3

        card2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent fontsactivity = new Intent(MainActivity1.this, Screen1Fonts.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(fontsactivity, bndlanimation);

                return true;
            }
        });


        // CARD 3
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean installed = appInstalledOrNot("com.lovejoy777.rroandlayersmanager");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    Intent layersmanager = new Intent();
                    layersmanager.setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager", "com.lovejoy777.rroandlayersmanager.menu"));
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                    startActivity(layersmanager, bndlanimation);

                } else {
                    Toast.makeText(MainActivity1.this, "Please install the layers manager app", Toast.LENGTH_LONG).show();
                    Intent layersmanagerPS = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lovejoy777.rroandlayersmanager&hl=en"));
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                    startActivity(layersmanagerPS, bndlanimation);

                }
            }
        }); // end card3

    } // ends onCreate

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_reboot) {
            reboot();
            return true;
        }
        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainActivity1.this, Settings.class);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
            startActivity(settings, bndlanimation);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reboot() {
        new AlertDialog.Builder(this)
                .setTitle("Soft Reboot")
                .setMessage("Are you sure you want to reboot?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Process proc = Runtime.getRuntime()
                                    .exec(new String[]{"su", "-c", "busybox killall system_server"});
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}