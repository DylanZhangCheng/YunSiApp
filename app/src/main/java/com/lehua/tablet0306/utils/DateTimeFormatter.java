package com.lehua.tablet0306.utils;

import java.text.SimpleDateFormat;

/**
 * Created by pendragon on 17-4-9.
 */

public final class DateTimeFormatter {

    /**
     * 定义了3种时间格式器
     */
    public static final SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat HMS = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
}
