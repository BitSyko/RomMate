package com.lovejoy777.rommate.bootanimation;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.lovejoy777.rommate.ImageLoadTaskPromo;
import com.lovejoy777.rommate.R;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by lovejoy777 on 27/06/15.
 */
public class DetailBootAnim extends AppCompatActivity {

    Bitmap bitmap[] = new Bitmap[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        // Handle Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // GET STRING SZP
        final Intent extras = getIntent();

        // GET STRINGS
        String title = extras.getStringExtra("keytitle");
        final String link = extras.getStringExtra("keylink");
        final String md5 = extras.getStringExtra("keymd5");
        final String promo = extras.getStringExtra("keypromo");
        String description = extras.getStringExtra("keydescription");
        String developer = extras.getStringExtra("keydeveloper");
        String video = extras.getStringExtra("keyvideo");


        // ASIGN VIEWS

        ImageView promoimg = (ImageView) findViewById(R.id.promo);
        TextView txt2 = (TextView) findViewById(R.id.tvdescription);
        TextView developertv = (TextView) findViewById(R.id.tvDeveloper);
        ImageView videoView1 = (ImageView) findViewById(R.id.videoView1);


        // SET TEXT/IMAGE VIEWS
        collapsingToolbar.setTitle(title);
        new ImageLoadTaskPromo(promo, promoimg).execute();
        txt2.setText(description);
        developertv.setText(developer);

        new BootVideo(this, videoView1, title, link).execute();

        /*
        videoView1.setVideoPath(video);
        videoView1.setMediaController(new MediaController(this));
        videoView1.requestFocus();
        videoView1.start();
*/

        // DOWNLOAD BUTTON
        Button downloadbutton;
        downloadbutton = (Button) findViewById(R.id.button);

        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent installtheme = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(installtheme, bndlanimation);

            }
        }); // end DOWNLOAD BUTTON

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }


    private class BootVideo extends AsyncTask<Void, String, AnimationDrawable> {

        Context context;
        ImageView imageView;
        String name;
        String link;

        public BootVideo(Context context, ImageView imageView, String name, String link) {
            this.context = context;
            this.imageView = imageView;
            this.name = name;
            this.link = link;
        }

        @Override
        protected AnimationDrawable doInBackground(Void... params) {
            try {

                Log.d("Animation", "download started");
                publishProgress("download started");

                // FileUtils.copyURLToFile();

                File downloadLocation = new File(context.getCacheDir() + "/" + name + ".zip");
                downloadLocation.delete();

                File bootanizipLocation = new File(context.getCacheDir() + "/" + name + "_bootani" + ".zip");
                bootanizipLocation.delete();

                FileUtils.copyURLToFile(new URL(link), downloadLocation);

                Log.d("Animation", "download finished");
                publishProgress("download finished");


                Log.d("Animation", "unzipping started");
                publishProgress("unzipping started");

                ZipFile zip = new ZipFile(downloadLocation);
                InputStream manifestInputStream = zip.getInputStream(zip.getEntry("bootanimation.zip"));


                FileUtils.copyInputStreamToFile(manifestInputStream, bootanizipLocation);
                zip.close();

                zip = new ZipFile(bootanizipLocation);

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


                //30 fps
                int duration = (int) (1000.0 / 30.0);

                ArrayList<String> files = loadFiles(context.getCacheDir() + "/" + name + "_cache/part1");

                AnimationDrawable animationDrawable = new AnimationDrawable();
                animationDrawable.setOneShot(false);


                for (String fileName : files) {

                    File file = new File(context.getCacheDir() + "/" + name + "_cache/part1/" + fileName);

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), null);

                    Drawable d = new BitmapDrawable(getResources(), bitmap);

                    animationDrawable.addFrame(d, duration);

                    Log.d("Animation adding", file.getAbsolutePath());

                }


                return animationDrawable;

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(AnimationDrawable aVoid) {
            super.onPostExecute(aVoid);

            Log.d("Animation", "started");
            Toast.makeText(context, "Animation started", Toast.LENGTH_LONG).show();


            if (aVoid != null) {
                imageView.setBackground(aVoid);
                ((AnimationDrawable) imageView.getBackground()).start();
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

    }


}

