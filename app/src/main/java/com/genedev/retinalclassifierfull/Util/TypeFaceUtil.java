package com.genedev.retinalclassifierfull.Util;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by Gene on 6/30/2018.
 */

public class TypeFaceUtil {

    public static void overrideFont(Context context, String Roboto, String ProductSans){
        try{
            final Typeface productSans = Typeface.createFromAsset(context.getAssets(),ProductSans);

            final Field defaultFont = Typeface.class.getDeclaredField(Roboto);
            defaultFont.setAccessible(true);
            defaultFont.set(null, productSans);
        } catch(Exception e){
            //Log.e("Cannot set custom font " + ProductSans + " instead of " + Roboto);
        }
    }
}
