package com.example.goforlunch.views.recyclerViews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;

public class Divider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mOrientation;

    /**
     * Method to add a divider between two articles in recyclerView
     * @param context current
     * @param orientation must be vertical
     */
    public Divider(Context context, int orientation) {
        this.mDivider = ContextCompat.getDrawable(context, R.drawable.divider);
        if (orientation != LinearLayoutManager.VERTICAL) {
            throw new IllegalArgumentException("This item can be used only with a RecyclerView that uses a LinearLayoutManager with a vertical orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawHorizontalDivider(c, parent);
        }
    }

    private void drawHorizontalDivider(Canvas c, RecyclerView parent) {
        int left, right, top, bottom;
        left = convertDpToPx();
        right = parent.getWidth();
        int count = parent.getChildCount() - parent.getPaddingRight();
        for (int i = 0; i < count; i++) {
            View current = parent.getChildAt(i);
            top = current.getTop();
            bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);

        }

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation== LinearLayoutManager.VERTICAL){
            outRect.set(0,0,0,mDivider.getIntrinsicHeight());}
    }

    private static int convertDpToPx(){
        return (int) (110 * Resources.getSystem().getDisplayMetrics().density);
    }
}
