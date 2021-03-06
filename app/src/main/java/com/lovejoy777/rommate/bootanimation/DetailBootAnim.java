package com.lovejoy777.rommate.bootanimation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lovejoy777.rommate.R;
import com.lovejoy777.rommate.commands.RootCommands;
import com.squareup.picasso.Picasso;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class DetailBootAnim extends AppCompatActivity {

    Button downloadbutton, generateButton, installButton, showButton;
    String title, link, md5;
    BootAnimPrevHolder previewHolder;
    ImageView promoimg;
    CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // GET STRING SZP
        final Intent extras = getIntent();

        // GET STRINGS
        title = extras.getStringExtra("keytitle");
        link = extras.getStringExtra("keylink");
        md5 = extras.getStringExtra("keymd5");
        final String promo = extras.getStringExtra("keypromo");
        String description = extras.getStringExtra("keydescription");
        String developer = extras.getStringExtra("keydeveloper");


        // ASIGN VIEWS

        promoimg = (ImageView) findViewById(R.id.promo);
        TextView txt2 = (TextView) findViewById(R.id.tvdescription);
        TextView developertv = (TextView) findViewById(R.id.tvDeveloper);
        downloadbutton = (Button) findViewById(R.id.downloadButton);
        generateButton = (Button) findViewById(R.id.generateButton);
        installButton = (Button) findViewById(R.id.installButton);
        showButton = (Button) findViewById(R.id.showButton);

        // SET TEXT/IMAGE VIEWS
        collapsingToolbar.setTitle(title);

        Picasso.with(this).load(promo).placeholder(R.drawable.heroimage).into(promoimg);

        txt2.setText(description);
        developertv.setText(developer);

        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadBoot().execute();
            }
        });

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BootVideo().execute();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenBootAnim.launch(DetailBootAnim.this, previewHolder);
            }
        });


        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InstallBoot().execute();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }


    private class InstallBoot extends AsyncTask<Void, String, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DetailBootAnim.this, "Installing",
                    "Installing bootanimation" + "...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Create backup of current bootanimation

            File currentBootani = new File("/system/media/bootanimation.zip");

            DateTime currentDateTime = new DateTime();

            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd_MM_yyyy_HH:mm:ss");


            //Creating backup folder
            new File(Environment.getExternalStorageDirectory() + "/rommate/backups/boots").mkdirs();


            File backup = new File(Environment.getExternalStorageDirectory() +
                    "/rommate/backups/boots/bootanimation_" + fmt.print(currentDateTime) + ".zip");

            try {
                FileUtils.copyFile(currentBootani, backup);
                publishProgress("Backup created in " + backup.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress("Creating backup failed");
                return null;
            }


            File bootanizipLocation = new File(DetailBootAnim.this.getCacheDir() + "/" + title + "_bootani" + ".zip");

            RootTools.remount("/system/media", "RW");

            RootCommands.moveCopyRoot(bootanizipLocation.getAbsolutePath(), "/system/media/bootanimation.zip");

            try {

                CommandCapture command5 = new CommandCapture(0, "chmod 644 /system/media/bootanimation.zip");
                RootTools.getShell(true).add(command5);
                while (!command5.isFinished()) {
                    Thread.sleep(1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            RootTools.remount("/system/media", "RO");


            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(DetailBootAnim.this, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }
    }


    private class DownloadBoot extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressBackup;

        @Override
        protected void onPreExecute() {
            progressBackup = ProgressDialog.show(DetailBootAnim.this, "Downloading",
                    "Downloading bootanimation" + "...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            File zipFile = new File(DetailBootAnim.this.getCacheDir() + "/" + title + ".zip");


            try {

                while (!zipFile.exists() || !(fileToMd5(zipFile).equalsIgnoreCase(md5))) {
                    zipFile.delete();
                    FileUtils.copyURLToFile(new URL(link), zipFile);
                    Log.d("MD5", fileToMd5(zipFile) + " vs " + md5);
                }

                File bootanizipLocation = new File(DetailBootAnim.this.getCacheDir() + "/" + title + "_bootani" + ".zip");


                ZipFile zip = new ZipFile(zipFile);
                InputStream manifestInputStream = zip.getInputStream(zip.getEntry("bootanimation.zip"));


                FileUtils.copyInputStreamToFile(manifestInputStream, bootanizipLocation);
                zip.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBackup.dismiss();
            downloadbutton.setVisibility(View.GONE);
            generateButton.setVisibility(View.VISIBLE);
            installButton.setVisibility(View.VISIBLE);
        }

        private String fileToMd5(File file) throws IOException {
            return new String(Hex.encodeHex(DigestUtils.md5(new FileInputStream(file))));
        }

    }


    private class BootVideo extends AsyncTask<Void, String, AnimationDrawable> {

        Context context;
        String name;
        ProgressDialog progressBackup;

        public BootVideo() {
            this.context = DetailBootAnim.this;
            name = DetailBootAnim.this.title;
        }

        @Override
        protected void onPreExecute() {
            progressBackup = ProgressDialog.show(DetailBootAnim.this, "Generating",
                    "Generating preview" + "...", true);
        }

        @Override
        protected AnimationDrawable doInBackground(Void... params) {
            try {

                File bootanizipLocation = new File(context.getCacheDir() + "/" + name + "_bootani" + ".zip");

                Log.d("Animation", "unzipping started");
                publishProgress("unzipping started");

                ZipFile zip = new ZipFile(bootanizipLocation);

                ZipEntry ze;
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(bootanizipLocation)));

                File unpackedFolder = new File(context.getCacheDir() + "/" + name + "_cache");
                unpackedFolder.mkdir();


                while ((ze = zis.getNextEntry()) != null) {

                    String fileName = ze.getName();


                    if (ze.isDirectory()) {
                        new File(unpackedFolder.getAbsolutePath() + "/" + fileName).mkdirs();
                        continue;
                    }


                    FileUtils.copyInputStreamToFile(zip.getInputStream(ze), new File(unpackedFolder.getAbsolutePath() + "/" + fileName));

                }


                Log.d("Animation", "unzipping finished");
                publishProgress("unzipping finished");

                AnimationDrawable animationDrawable = null;

                int skip = 5;

                //Firstly we're trying to create animation from every image
                //If it fails, we're removing every 4h file
                //If it fails every 3rd
                //2nd
                //...

                while (skip > 1) {


                    try {
                        animationDrawable = getAnimation(skip);
                        break;
                    } catch (OutOfMemoryError e) {
                        System.gc();
                        System.gc();
                        System.gc();
                        e.printStackTrace();
                        Log.d("Increase skip", String.valueOf(skip));
                    }

                    skip--;

                }

                return animationDrawable;

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(AnimationDrawable animation) {
            super.onPostExecute(animation);

            Log.d("Animation", "started");
            Toast.makeText(context, "Animation started", Toast.LENGTH_LONG).show();
            progressBackup.dismiss();
            generateButton.setVisibility(View.GONE);


            if (animation != null) {
                //  previewHolder = new BootAnimPrevHolder();
                //   previewHolder.setAnimationDrawable(animation);
                //  showButton.setVisibility(View.VISIBLE);
                promoimg.setBackgroundColor(0xFF000000);
                collapsingToolbar.setTitle("");
                promoimg.setImageDrawable(animation);
                animation.invalidateSelf();
                promoimg.invalidate();
                animation.start();
            }

        }


        private ArrayList<String> loadFiles(String directory) {

            File f = new File(directory);
            ArrayList<String> files = new ArrayList<>();

            if (!f.exists() || !f.isDirectory()) {
                return files;
            }

            for (File file : f.listFiles()) {
                if (!file.isDirectory()) {
                    files.add(file.getName());
                }
            }
            Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
            return files;
        }

        private Bitmap RotateBitmap(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }


        private AnimationDrawable getAnimation(int skip) throws IOException {

            if (skip == 5) {
                skip = Integer.MAX_VALUE;
            }

            File bootaniCacheFolder = new File(context.getCacheDir() + "/" + name + "_cache");

            List<String> confFile = FileUtils.readLines(new File(bootaniCacheFolder.getAbsoluteFile() + "/desc.txt"));

            Pattern firstLineRegex = Pattern.compile("(\\d*) (\\d*) (\\d*)");
            Pattern otherLineRegex = Pattern.compile("(\\D*) (\\d*) (\\d*) (.*)");


            Matcher matcher = firstLineRegex.matcher(confFile.get(0));

            matcher.find();

            int fps = Integer.parseInt(matcher.group(3));

            int duration = (int) (1000.0 / fps);

            AnimationDrawable animationDrawable = new AnimationDrawable();
            animationDrawable.setOneShot(false);

            int fileNumber = 1;

            for (int i = 1; i < confFile.size(); i++) {

                String line = confFile.get(i);

                Matcher lineMatcher = otherLineRegex.matcher(line);

                if (!lineMatcher.find()) {
                    continue;
                }

                String folder = lineMatcher.group(4);


                ArrayList<String> files = loadFiles(context.getCacheDir() + "/" + name + "_cache/" + folder);


                for (String fileName : files) {

                    fileNumber++;

                    if (fileNumber % skip == 0) {
                        continue;
                    }

                    File file = new File(context.getCacheDir() + "/" + name + "_cache/" + folder + "/" + fileName);

                    BitmapFactory.Options op = new BitmapFactory.Options();
                    op.inPreferredConfig = Bitmap.Config.RGB_565;

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), op);

                    Drawable d = new BitmapDrawable(getResources(), bitmap);

                    if (fileNumber % skip == skip - 1) {
                        animationDrawable.addFrame(d, duration * 2);
                    } else {
                        animationDrawable.addFrame(d, duration);
                    }

                    Log.d("Animation adding", file.getAbsolutePath());

                }

            }

            return animationDrawable;


        }


    }


}

