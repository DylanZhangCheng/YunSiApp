package com.lehua.tablet0306.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lehua.tablet0306.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class MethodUtils {

    private static Context context;
    private static ProgressDialog dialog;
    private static Toast toast;


    public MethodUtils(Context mContext) {
        context = mContext;
    }


    public static void showToast(final Context context, final String msg) {
        if ("main".equalsIgnoreCase(Thread.currentThread().getName())) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();
        } else {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toast == null) {
                        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                    } else {
                        toast.setText(msg);
                    }
                    toast.show();
                }
            });
        }
    }

    //显示一个Dialog
    public static void showLoadingDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage("数据加载中，请稍候..");
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMax(100);
        dialog.show();
    }


    /**
     * 判断当前网络是否是wifi网络
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否是3G网络
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        // 有存储的SDCard
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static Properties loadConfig(String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void saveConfig(String filePath, Properties properties) {
        try {
            File file = new File(filePath);
            file.createNewFile();
            FileOutputStream s = new FileOutputStream(file, false);
            properties.store(s, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String deleteChar(String source, char ch) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) != ch) {
                result.append(source.charAt(i));
            }
        }
        return result.toString();
    }

    // 加载图片的函数
    public static void loadImage(Context context, ImageView imageView, String imgUrl) {
        RequestQueue queue = Volley.newRequestQueue(context);
        ImageLoader loader = new ImageLoader(queue, new ImageLoader.ImageCache() {

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,
                R.drawable.loading, R.drawable.failed);

        loader.get(imgUrl, listener);
    }


}
