package com.lehua.tablet0306.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.lehua.tablet0306.okhttp.HttpRequest;
import com.lehua.tablet0306.okhttp.URLConstant;
import com.vise.log.ViseLog;


import org.json.JSONObject;

public class UserImg {
    public void loadUserImg(String account, final CircleImageView center_img) {
        try {
            JSONObject json = new JSONObject();
            json.put("account", account);

            HttpRequest.post(URLConstant.URL_USER_IMG,json.toString(),new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response){
                    if(response.optString("error").equals("ok")) {
                        String user_img = response.optString("user_img");
                        ViseLog.d(user_img);
                        if (!user_img.equals("__null__")) {
                            Bitmap bitmap = getBitmap(user_img);
                            center_img.setImageBitmap(bitmap);
                        }
                    }
                }
                @Override
                public void onFailure() {}
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //将字符串转换成Bitmap类型
    public Bitmap getBitmap(String string){
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray= Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
