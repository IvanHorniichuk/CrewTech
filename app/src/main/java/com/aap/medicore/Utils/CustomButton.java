package com.aap.medicore.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class CustomButton extends AppCompatButton {

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface btf = Typeface.DEFAULT;

            switch (getTypeface().getStyle()) {
                case Typeface.BOLD:
                    btf = Typeface.createFromAsset(getContext().getAssets(), "ProductSans-Bold.ttf");
                    break;

                case Typeface.NORMAL:
                    btf = Typeface.createFromAsset(getContext().getAssets(), "ProductSans-Medium.ttf");
                    break;

                case Typeface.ITALIC:
                    btf = Typeface.createFromAsset(getContext().getAssets(), "ProductSans-Regular.ttf");
                    break;
            }
            setTypeface(btf);
        }
    }

}