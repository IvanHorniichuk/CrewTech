package com.aap.medicore.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.DEFAULT;

            switch (getTypeface().getStyle()) {
                case Typeface.BOLD:
                    tf = Typeface.createFromAsset(getContext().getAssets(), "ProductSans-Bold.ttf");
                    break;

                case Typeface.NORMAL:
                    tf = Typeface.createFromAsset(getContext().getAssets(), "ProductSans-Medium.ttf");
                    break;

                case Typeface.ITALIC:
                    tf = Typeface.createFromAsset(getContext().getAssets(), "ProductSans-Regular.ttf");
                    break;
            }
            setTypeface(tf);
        }
    }
}