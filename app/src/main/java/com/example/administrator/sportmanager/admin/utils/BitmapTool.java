package com.example.administrator.sportmanager.admin.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.administrator.sportmanager.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapTool {


    public static byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


    public static Bitmap byteToBitmap(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }

}
