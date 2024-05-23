package com.example.twittersearch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    public ItemDivider(Context context) {
        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.listDivider});
        divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + (divider != null ? divider.getIntrinsicHeight() : 0);

            if (divider != null) {
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }
}

