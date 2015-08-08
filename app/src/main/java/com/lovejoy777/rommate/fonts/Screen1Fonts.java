package com.lovejoy777.rommate.fonts;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lovejoy777.rommate.MainActivity;
import com.lovejoy777.rommate.R;
import com.lovejoy777.rommate.commands.RootCommands;
import com.lovejoy777.rommate.filepicker.FilePickerActivity;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by lovejoy777 on 29/06/15.
 */
public class Screen1Fonts extends AppCompatActivity {

    final String startDirInstall = Environment.getExternalStorageDirectory() +  "/Download";
    private static final int CODE_SD = 0;
    private static final int CODE_DB = 1;
    CardView card1, card2, card3, card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen1fonts);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);


        card1 = (CardView) findViewById(R.id.CardView_fontscard1);
        card2 = (CardView) findViewById(R.id.CardView_fontscard2);
        card3 = (CardView) findViewById(R.id.CardView_fontscard3);
        card4 = (CardView) findViewById(R.id.CardView_fontscard4);

        // CARD 1
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent freeactivity = new Intent(Screen1Fonts.this, FreeFonts.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(freeactivity, bndlanimation);

            }
        });

        // CARD 2
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent donateactivity = new Intent(Screen1Fonts.this, DonateFonts.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(donateactivity, bndlanimation);


            }
        });

        // CARD 3
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, startDirInstall);
                i.putExtra("FilePickerMode", "Install Fonts");

                // start filePicker forResult
                startActivityForResult(i, CODE_SD);

            }
        });

        // CARD 4
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RestoreFonts().execute();

            }
        });

    } // ends onCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if ((CODE_SD == requestCode || CODE_DB == requestCode) &&
                resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                    false)) {
                ArrayList<String> paths = data.getStringArrayListExtra(
                        FilePickerActivity.EXTRA_PATHS);

                StringBuilder sb = new StringBuilder();

                if (paths != null) {
                    for (String path : paths) {
                        if (path.startsWith("file://")) {
                            path = path.substring(7);
                            sb.append(path);

                        }
                    }

                    String SZP = (sb.toString());
                    Intent iIntent = new Intent(this, InstallFonts.class);
                    iIntent.putExtra("key1", SZP);
                    iIntent.putStringArrayListExtra("key2", paths);
                    startActivity(iIntent);
                    finish();

                }

            } else {
                // Get the File path from the Uri
                String SZP = (data.getData().toString());

                if (SZP.startsWith("file://")) {
                    SZP = SZP.substring(7);
                    Intent iIntent = new Intent(this, InstallFonts.class);
                    iIntent.putExtra("key1", SZP);
                    startActivity(iIntent);
                    finish();
                }
            }
        }
    } // ends onActivityForResult

    private class RestoreFonts extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressRestorefonts;

        protected void onPreExecute() {

            progressRestorefonts = ProgressDialog.show(Screen1Fonts.this, "restore Fonts",
                    "restoring...", true);

        }

        @Override
        protected Void doInBackground(Void... params) {

            // check if backedup fonts exits
            File dir = new File(Environment.getExternalStorageDirectory() + "/rommate/backups/fonts/fonts");
            if (!dir.exists()) {

                Toast.makeText(Screen1Fonts.this, "no fonts found", Toast.LENGTH_LONG).show();

            } else {

                RootTools.remount("/system", "RW");

                RootCommands.moveCopyRoot(Environment.getExternalStorageDirectory() + "/rommate/backups/fonts/fonts", "/system");


                try {

                    // change perms of fonts folder and files 644
                    CommandCapture command9 = new CommandCapture(0, "chmod -R 644 /system/fonts");
                    RootTools.getShell(true).add(command9);
                    while (!command9.isFinished()) {
                        Thread.sleep(1);
                    }

                    // change perms of fonts folder 755
                    CommandCapture command10 = new CommandCapture(0, "chmod 755 /system/fonts");
                    RootTools.getShell(true).add(command10);
                    while (!command10.isFinished()) {
                        Thread.sleep(1);

                    }

                    //  RootCommands.DeleteFileRoot(Environment.getExternalStorageDirectory() + "/rommate/temp");

                    RootTools.remount("/system/media", "RO");
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

            return null;
        }

        protected void onPostExecute(Void result) {

            progressRestorefonts.dismiss();

            // LAUNCH MainActivity
            overridePendingTransition(R.anim.back2, R.anim.back1);
            Intent iIntent = new Intent(Screen1Fonts.this, MainActivity.class);
            iIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iIntent.putExtra("ShowSnackbar", true);
            iIntent.putExtra("SnackbarText", "Restored Fonts");
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