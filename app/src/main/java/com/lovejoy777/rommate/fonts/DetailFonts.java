package com.lovejoy777.rommate.fonts;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovejoy777.rommate.ImageLoadTaskPromo;
import com.lovejoy777.rommate.R;

/**
 * Created by lovejoy777 on 29/06/15.
 */
public class DetailFonts extends AppCompatActivity {

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

        // GET STRINGSinter
        String title = extras.getStringExtra("keytitle");
        final String link = extras.getStringExtra("keylink");
        final String md5 = extras.getStringExtra("keymd5");
        final String promo = extras.getStringExtra("keypromo");
        String description = extras.getStringExtra("keydescription");
        String developer = extras.getStringExtra("keydeveloper");

        // ASIGN VIEWS

        ImageView promoimg= (ImageView) findViewById(R.id.promo);
        TextView txt2 = (TextView) findViewById(R.id.tvdescription);
        TextView developertv = (TextView) findViewById(R.id.tvDeveloper);

        // SET TEXT/IMAGE VIEWS
        collapsingToolbar.setTitle(title);
        new ImageLoadTaskPromo(promo, promoimg).execute();
        txt2.setText(description);
        developertv.setText(developer);

        // DOWNLOAD BUTTON
        Button installbutton;
        installbutton = (Button) findViewById(R.id.button);

        installbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent installtheme = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(installtheme, bndlanimation);

            }
        }); // end DOWNLOADBUTTON



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}

