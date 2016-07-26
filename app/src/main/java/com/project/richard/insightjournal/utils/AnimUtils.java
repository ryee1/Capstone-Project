package com.project.richard.insightjournal.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.project.richard.insightjournal.R;

/**
 * Created by richard on 7/25/16.
 */
public class AnimUtils {
    public static void slideDown(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if (animation != null) {
            animation.reset();
            if (view != null) {
                view.clearAnimation();
                view.startAnimation(animation);
            }
        }
    }

    public static void slideUp(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if (animation != null) {
            animation.reset();
            if (view != null) {
                view.clearAnimation();
                view.startAnimation(animation);
            }
        }
    }
}
