package com.ti.aditya.swipecloseactivity.lib;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * Created by adity on 9/19/2016.
 */
public class SwipeCloseActivity extends AppCompatActivity implements SwipeCloseLayout.SwipeCloseListener {
    private final String LOG_TAG = SwipeCloseActivity.class.getSimpleName();
    private SwipeCloseLayout swipeCloseLayout;
    private ImageView activityShadow;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        swipeCloseLayout.addView(view);
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        swipeCloseLayout = new SwipeCloseLayout(this);
        swipeCloseLayout.setOnSwipeCloseListener(this);
        activityShadow = new ImageView(this);
        activityShadow.setBackgroundColor(Color.parseColor("#7f000000"));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        container.addView(activityShadow, params);
        container.addView(swipeCloseLayout);
        Log.i(LOG_TAG,"End of get container");
        return container;
    }

    public void setDragEdge(SwipeCloseLayout.DragEdge dragEdge) {
        swipeCloseLayout.setDragEdge(dragEdge);
        Log.i(LOG_TAG,"End of setdragedge");
    }

    /*public SwipeCloseLayout getSwipeBackLayout() {
        return swipeCloseLayout;
    }*/

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        activityShadow.setAlpha(1 - fractionScreen);
    }

}

