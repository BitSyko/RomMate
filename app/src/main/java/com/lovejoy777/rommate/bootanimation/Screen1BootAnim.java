package com.lovejoy777.rommate.bootanimation;

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
 * Created by lovejoy777 on 24/06/15.
 */
public class Screen1BootAnim extends AppCompatActivity {

    final String startDirInstall = Environment.getExternalStorageDirectory() +  "/Download";
    private static final int CODE_SD = 0;
    private static final int CODE_DB = 1;
    CardView card1, card3, card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen1bootanim);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        card1 = (CardView) findViewById(R.id.CardView_bootcard1);
        card3 = (CardView) findViewById(R.id.CardView_bootcard3);
        card4 = (CardView) findViewById(R.id.CardView_bootcard4);


        // CARD 1
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent freeactivity = new Intent(Screen1BootAnim.this, FreeBootAnim.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(freeactivity, bndlanimation);

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
                i.putExtra("FilePickerMode", "Install Boot Animation");

                // start filePicker forResult
                startActivityForResult(i, CODE_SD);


            }
        });

        // CARD 4
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RestoreBootanim().execute();

            }
        });

    } // ends onCreate


    private class RestoreBootanim extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressRestorebootanim;

        protected void onPreExecute() {

            progressRestorebootanim = ProgressDialog.show(Screen1BootAnim.this, "restore BootAnimation",
                    "restoring...", true);

        }

        @Override
        protected Void doInBackground(Void... params) {

            // check if backedup boot anim exits
            File dir = new File(Environment.getExternalStorageDirectory() + "/rommate/backups/boots/bootanimation.zip");
            if (!dir.exists()) {

                Toast.makeText(Screen1BootAnim.this, "no boot animation found", Toast.LENGTH_LONG).show();

            } else {

                // if /system/media/bootanimation exists delete it
                File dir1 = new File("/system/media/bootanimation.zip");
                if (dir1.exists()) {

                    RootCommands.DeleteFileRoot("/system/media/bootanimation.zip");
                }

                try {

                    RootTools.remount("/system/media", "RW");

                    RootCommands.moveCopyRoot(Environment.getExternalStorageDirectory() + "/rommate/backups/boots/bootanimation.zip", "/system/media/");
                    //  RootTools.copyFile(Environment.getExternalStorageDirectory() + "/rommate/backups/boots/bootanimation.zip", "/system/media/", true, true);

                    // RootTools.remount("/system/media", "RW");
                    CommandCapture command5 = new CommandCapture(0, "chmod 644 /system/media/bootanimation.zip");
                    RootTools.getShell(true).add(command5);
                    while (!command5.isFinished()) {
                        Thread.sleep(1);
                    }

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

            progressRestorebootanim.dismiss();

            // LAUNCH MainActivity
            overridePendingTransition(R.anim.back2, R.anim.back1);
            Intent iIntent = new Intent(Screen1BootAnim.this, MainActivity.class);
            iIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iIntent.putExtra("ShowSnackbar", true);
            iIntent.putExtra("SnackbarText", "Restored BootAnimation");
            startActivity(iIntent);
        }
    }

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
                    Intent iIntent = new Intent(this, InstallBootAnim.class);
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
                    Intent iIntent = new Intent(this, InstallBootAnim.class);
                    iIntent.putExtra("key1", SZP);
                    startActivity(iIntent);
                    finish();
                }
            }
        }
    } // ends onActivityForResult


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

