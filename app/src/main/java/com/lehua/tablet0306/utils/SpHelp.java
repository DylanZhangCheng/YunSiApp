package com.lehua.tablet0306.utils;


import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.lehua.tablet0306.application.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SpHelp {
    /**
     * 系统中保存到的用户Id
     */
    public static final String USER_ID = "user_id";

    public static void saveUserId(String id) {
        MyApplication.sp.edit().putString(USER_ID, id).commit();
    }

    public static String getUserId() {
        return MyApplication.sp.getString(USER_ID, "");
    }

    /**
     * IS_FIRST_OPEN_APPLICATION 代表是否是第一次打开软件，如果是，则初始进入Activity1,
     * 否则进入Actovity2
     */
    public static final String IS_FIRST_OPEN_APPLICATION = "is_first_open_application";

    public static void saveIsFirstOpenApplication(boolean isFirstOpenApplication) {
        MyApplication.sp.edit().putBoolean(IS_FIRST_OPEN_APPLICATION, isFirstOpenApplication).commit();
    }

    public static boolean getIsFirstOpenApplication() {
        return MyApplication.sp.getBoolean(IS_FIRST_OPEN_APPLICATION, true);
    }


    public static final String USER_ENTITY = "USER_ENTITY";

    /**
     * 将自定义的数据类型保存到SharePreference中
     */

    public static void saveObject(String key, Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] originalBytes = baos.toByteArray();
            byte[] dealedBytes = Base64.encode(originalBytes, 0);
            String content = new String(dealedBytes);
            MyApplication.sp.edit().putString(key, content).commit();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Object getObject(String key) {
        Object object = null;
        String content = MyApplication.sp.getString(key, null);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        try {
            byte[] dealedBytes = content.getBytes();
            byte[] originalBytes = Base64.decode(dealedBytes, 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(originalBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            object = ois.readObject();
        } catch (IOException e) {
            System.out.println(Log.getStackTraceString(e));
        } catch (ClassNotFoundException e) {
            System.out.println(Log.getStackTraceString(e));
        }
        return object;
    }


}
