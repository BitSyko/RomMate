package com.lovejoy777.rommate.bootanimation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import com.lovejoy777.rommate.R;

public class FullScreenBootAnim extends Activity {

    //#BlameAndrew
    private static BootAnimPrevHolder animation;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen);

        imageView = (ImageView) findViewById(R.id.image);

        imageView.setBackground(animation.getAnimationDrawable());

        animation.getAnimationDrawable().start();
        imageView.invalidate();

    }

    public static void launch(Activity activity, BootAnimPrevHolder animationDrawable) {
        animation = animationDrawable;
        Intent intent = new Intent(activity, FullScreenBootAnim.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    @Override
    public void onBackPressed() {
        animation.getAnimationDrawable().stop();
        imageView.setBackground(null);
        super.onBackPressed();
    }

    public void click(View view) {
       onBackPressed();
    }
}