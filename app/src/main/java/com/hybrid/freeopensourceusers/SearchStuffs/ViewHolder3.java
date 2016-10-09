package com.hybrid.freeopensourceusers.SearchStuffs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hybrid.freeopensourceusers.R;

/**
 * Created by monster on 9/10/16.
 */

public class ViewHolder3 extends RecyclerView.ViewHolder {

    public TextView headerTVforHeader;

    public ViewHolder3(View itemView) {
        super(itemView);
        headerTVforHeader = (TextView) itemView.findViewById(R.id.headerTVforRecyclerView);
    }


}
