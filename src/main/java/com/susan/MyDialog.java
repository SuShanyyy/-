package com.susan;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

public class MyDialog extends Dialog {
    public MyDialog(@NonNull Context context, int themeResId, BitmapDrawable img) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        View show = findViewById(R.id.show);
        show.setBackground(img);
    }
}
