package com.lovejoy777.rommate.fonts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lovejoy777.rommate.MainActivity;
import com.lovejoy777.rommate.R;
import com.lovejoy777.rommate.commands.RootCommands;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lovejoy777 on 29/06/15.
 */
public class InstallFonts extends AppCompatActivity {

    static final String TAG = "InstallFontsUnZip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new InstallFont().execute();

    } // ends onCreate

    private class InstallFont extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressInstallfont;

        protected void onPreExecute() {

            progressInstallfont = ProgressDialog.show(InstallFonts.this, "install Fonts",
                    "installing...", true);

        }

        @Override
        protected Void doInBackground(Void... params) {

            // mk dir rommate
            File dir = new File(Environment.getExternalStorageDirectory() + "/rommate");
            if (!dir.exists()) {
                dir.mkdir();
            }

            // mk dir rommate/temp
            File dir4 = new File(Environment.getExternalStorageDirectory() + "/rommate/temp");
            if (!dir4.exists()) {
                dir4.mkdir();
            }

            // mk dir rommate/backups
            File dir1 = new File(Environment.getExternalStorageDirectory() + "/rommate/backups");
            if (!dir1.exists()) {
                dir1.mkdir();
            }

            // mk dir rommate/backups/fonts
            File dir2 = new File(Environment.getExternalStorageDirectory() + "/rommate/backups/fonts");
            if (!dir2.exists()) {
                dir2.mkdir();
            }

            // backup fonts
            File dir3 = new File(Environment.getExternalStorageDirectory() + "/rommate/backups/fonts/fonts");
            if (!dir3.exists()) {
                RootTools.remount("/system/media", "RW");

                // RootTools.copyFile("/system/media/bootanimation.zip", Environment.getExternalStorageDirectory() + "/rommate/backups/boots/", true, true);

                RootCommands.moveCopyRoot("/system/fonts", Environment.getExternalStorageDirectory() + "/rommate/backups/fonts/");
            }

            // GET STRING SZP
            final Intent extras = getIntent();
            String SZP = null;
            if (extras != null) {
                SZP = extras.getStringExtra("key1");

                // unzip source zip to rommate/temp
                try {
                    unzip (SZP, Environment.getExternalStorageDirectory() + "/rommate/temp");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // copy bootanimation.zip from rommate/temp to system
                RootCommands.moveCopyRoot(Environment.getExternalStorageDirectory() + "/rommate/temp/fonts", "/system");
            }

            RootTools.remount("/system", "RW");

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

            return null;
        }

        protected void onPostExecute(Void result) {

            progressInstallfont.dismiss();

            // LAUNCH MainActivity
            overridePendingTransition(R.anim.back2, R.anim.back1);
            Intent iIntent = new Intent(InstallFonts.this, MainActivity.class);
            iIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iIntent.putExtra("ShowSnackbar", true);
            iIntent.putExtra("SnackbarText", "Installed selected Fonts");
            startActivity(iIntent);
        }
    }

    /**
     * **********************************************************************************************************
     * UNZIP UTIL
     * ************
     * Unzip a zip file.  Will overwrite existing files.
     *
     * @param zipFile  Full path of the zip file you'd like to unzip.
     * @param location Full path of the directory you'd like to unzip to (will be created if it doesn't exist).
     * @throws IOException *************************************************************************************************************
     */
    public void unzip(String zipFile, String location) throws IOException {

        int size;
        byte[] buffer = new byte[1024];

        try {

            if (!location.endsWith("/")) {
                location += "/";
            }
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), 1024));
            try {
                ZipEntry ze;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {

                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs();
                            }
                        }
                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, 1024);
                        try {
                            while ((size = zin.read(buffer, 0, 1024)) != -1) {
                                fout.write(buffer, 0, size);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                            out.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
