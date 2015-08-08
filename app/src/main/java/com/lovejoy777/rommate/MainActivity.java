package com.lovejoy777.rommate;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lovejoy777.rommate.bootanimation.Screen1BootAnim;
import com.lovejoy777.rommate.commands.RootCommands;
import com.lovejoy777.rommate.fonts.Screen1Fonts;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

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
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);


        card1 = (CardView) findViewById(R.id.CardView_bootanim);
        card2 = (CardView) findViewById(R.id.CardView_fonts);
        card3 = (CardView) findViewById(R.id.CardView_manager);

        // CARD 1
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bootanimactivity = new Intent(MainActivity.this, Screen1BootAnim.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);

            }
        }); // end card1

        // CARD 2
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent fontsactivity = new Intent(MainActivity.this, Screen1Fonts.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(fontsactivity, bndlanimation);


            }
        }); // end card3

        // CARD 3
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean installed = appInstalledOrNot("com.lovejoy777.rroandlayersmanager");
                if(installed) {
                    //This intent will help you to launch if the package is already installed
                    Intent layersmanager = new Intent();
                    layersmanager.setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager", "com.lovejoy777.rroandlayersmanager.menu"));
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                    startActivity(layersmanager, bndlanimation);

                } else {
                    Toast.makeText(MainActivity.this, "Please install the layers manager app", Toast.LENGTH_LONG).show();
                    Intent layersmanagerPS = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lovejoy777.rroandlayersmanager&hl=en"));
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                    startActivity(layersmanagerPS, bndlanimation);

                }
              //  new Restore().execute();
                // RestoreCommand();


            }
        }); // end card3

    } // ends onCreate

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }



    private class Restore extends AsyncTask<Void,Void,Void> {

        ProgressDialog progressRestore;

        protected void onPreExecute() {

            progressRestore = ProgressDialog.show(MainActivity.this, "Restoring Originals",
                    "restoring...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            //ProgressDialog progressRestore;
            String startpath = getApplicationInfo().dataDir + "/orig/bootanim/bootanimation.zip";
            String checkpath = getApplicationInfo().dataDir + "/orig/bootanim";
            String startpathfonts = getApplicationInfo().dataDir + "/orig/fonts/fonts";
            String checkpathfonts = getApplicationInfo().dataDir + "/orig/fonts";
            String media = "/system/media";
            String fonts = "/system/fonts";



            File dir1 = new File(checkpath);
            if (!dir1.exists()) {

                Toast.makeText(MainActivity.this, "no original boot animation found", Toast.LENGTH_LONG).show();

            } else {

                try {
                    RootTools.remount("/system", "RW");

                    // CHANGE PERMISSIONS TO COPY FINAL /VENDOR/OVERLAY FOLDER & FILES TO 777
                    CommandCapture command8 = new CommandCapture(0, "chmod -R 777 /system/fonts");
                    RootTools.getShell(true).add(command8);
                    while (!command8.isFinished()) {
                        Thread.sleep(1);
                    }


                    RootTools.copyFile(startpath, media + "/", true, true);
                    //  RootTools.copyFile(startpathfonts, fonts + "/", true, true);
                    //  RootCommands.moveCopyRoot(startpath, media + "/");

                    RootCommands.moveCopyRoot(startpathfonts, "/system");

                    // CHANGE PERMISSIONS OF FINAL /VENDOR/OVERLAY FOLDER & FILES TO 666 RECURING
                    CommandCapture command9 = new CommandCapture(0, "chmod -R 644 /system/fonts");
                    RootTools.getShell(true).add(command9);
                    while (!command9.isFinished()) {
                        Thread.sleep(1);
                    }

                    // CHANGE PERMISSIONS OF FINAL /VENDOR/OVERLAY FOLDER BACK TO 777
                    CommandCapture command10 = new CommandCapture(0, "chmod 755 /system/fonts");
                    RootTools.getShell(true).add(command10);
                    while (!command10.isFinished()) {
                        Thread.sleep(1);
                        RootTools.remount("/system", "RO");
                    }


                    RootTools.remount("/system", "RO");
                    // CLOSE ALL SHELLS
                    RootTools.closeAllShells();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RootDeniedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            //  finish();
            return null;
        }


        protected void onPostExecute(Void result) {

            progressRestore.dismiss();

            //finish();

            // LAUNCH LAYERS.CLASS
            overridePendingTransition(R.anim.back2, R.anim.back1);
            Intent iIntent = new Intent(MainActivity.this, MainActivity.class);
            iIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iIntent.putExtra("ShowSnackbar", true);
            iIntent.putExtra("SnackbarText","Installed selected Overlays");
            startActivity(iIntent);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.back2, R.anim.back1);
            return true;
        }
        return false;
    }
}