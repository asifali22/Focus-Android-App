package com.hybrid.freeopensourceusers.Utility;

import android.graphics.Color;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by monster on 21/7/16.
 */
public class MyTextDrawable {

    TextDrawable textDrawable;
    ColorGenerator generator = ColorGenerator.MATERIAL;

    public MyTextDrawable(){

    }


    public TextDrawable setTextDrawable(String name) {
        int color = generator.getColor(name);
        String  firstLetter = name.substring(0, 1);

        textDrawable = TextDrawable.builder()
                .beginConfig()
                .width(48)  // width in px
                .height(48) // height in px
                .endConfig()
                .buildRound(firstLetter, color);
        return textDrawable;
    }

    public TextDrawable setTextDrawableForPost(String key, String name) {
        int color = generator.getColor(key);

        textDrawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(75)
                .bold()
                .endConfig()
                .buildRect(name, color);
        return textDrawable;
    }

    public TextDrawable setTextDrawableForError(String error) {

        textDrawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(75)
                .bold()
                .endConfig()
                .buildRect(error, Color.RED);
        return textDrawable;
    }
}
