package com.lovejoy777.rommate.bootanimation;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.squareup.picasso.Picasso;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        Picasso.with(this).load(promo).into(promoimg);
        Picasso.with(this).load(promo).into(videoView1);

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

                for (int i = 1; i < confFile.size(); i++) {

                    String line = confFile.get(i);

                    Matcher lineMatcher = otherLineRegex.matcher(line);

                    if (!lineMatcher.find()) {
                        continue;
                    }

                    String folder = lineMatcher.group(4);


                    ArrayList<String> files = loadFiles(context.getCacheDir() + "/" + name + "_cache/" + folder);


                    for (String fileName : files) {

                        File file = new File(context.getCacheDir() + "/" + name + "_cache/" + folder + "/" + fileName);

                        //   Bitmap bitmap = Picasso.with(context).load(file).get();

                        BitmapFactory.Options op = new BitmapFactory.Options();
                        op.inPreferredConfig = Bitmap.Config.RGB_565;
                        op.outHeight = 200;
                        op.outWidth = 200;

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), op);


                        //publishProgress(bitmap);

                        //Thread.sleep(duration);

                        Drawable d = new BitmapDrawable(getResources(), bitmap);

                        // bitmap.recycle();

                        animationDrawable.addFrame(d, duration);

                        Log.d("Animation adding", file.getAbsolutePath());

                    }

                }

                return animationDrawable;

            } catch (IOException | OutOfMemoryError e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //   super.onProgressUpdate(values);
            // imageView.setImageBitmap(values[0]);
            Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(AnimationDrawable aVoid) {
            super.onPostExecute(aVoid);

            Log.d("Animation", "started");
            Toast.makeText(context, "Animation started", Toast.LENGTH_LONG).show();


            if (aVoid != null) {
                imageView.setImageBitmap(null);
                imageView.setBackground(aVoid);
                ((AnimationDrawable) imageView.getBackground()).start();
                imageView.invalidate();
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

    }


}

