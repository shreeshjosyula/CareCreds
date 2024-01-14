package com.example.carecreds.RecyclerViewDecoration;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Paint dividerPaint;

    public SimpleDividerItemDecoration(@ColorInt int dividerColor, float dividerHeight, Context context) {
        dividerPaint = new Paint();
        dividerPaint.setColor(dividerColor);
        dividerPaint.setStrokeWidth(dividerHeight);
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + (int) dividerPaint.getStrokeWidth();

            canvas.drawRect(left, top, right, bottom, dividerPaint);
        }
    }
}

