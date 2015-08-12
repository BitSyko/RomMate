package com.lovejoy777.rommate.bootanimation;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

public class BootAnimPrevHolder {

    private AnimationDrawable animationDrawable;

    public AnimationDrawable getAnimationDrawable() {
        return animationDrawable;
    }

    public void setAnimationDrawable(AnimationDrawable animationDrawable) {
        this.animationDrawable = animationDrawable;
    }

    public void transform() {

        int currentId = getCurrentId();

        AnimationDrawable newAnimation = new AnimationDrawable();


        for (int k = currentId; k < animationDrawable.getNumberOfFrames(); k++) {
            Drawable frame = animationDrawable.getFrame(k);
            newAnimation.addFrame(frame, animationDrawable.getDuration(k));
        }
        for (int k = 0; k < currentId; k++) {
            Drawable frame = animationDrawable.getFrame(k);
            newAnimation.addFrame(frame, animationDrawable.getDuration(k));
        }

        animationDrawable = newAnimation;

    }


    private int getCurrentId() {

        Drawable current = animationDrawable.getCurrent();

        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {

            if (animationDrawable.getFrame(i) == current) {
                return i;
            }
        }

        return -1;
    }

}
