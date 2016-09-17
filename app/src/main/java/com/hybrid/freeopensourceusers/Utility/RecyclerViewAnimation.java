package com.hybrid.freeopensourceusers.Utility;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by monster on 4/9/16.
 */

public class RecyclerViewAnimation {

    public static void animateRecyclerView(RecyclerView.ViewHolder holder, Boolean goesDown){
        ObjectAnimator animateTranlateY = ObjectAnimator.ofFloat(holder.itemView,"translationY",goesDown?100:-100,0);
        animateTranlateY.setDuration(1000);
        animateTranlateY.start();
    }
}
