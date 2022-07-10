package com.dongyulong.dogn.tools.system;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/912:44 上午
 * @since v1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    public static final String DATE_JFP_STR = "yyyyMM";
    public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_SMALL_STR = "yyyy-MM-dd";
    public static final String DATE_KEY_STR = "yyMMddHHmmss";

    public static Date parse(String strDate) {
        return parse(strDate, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);

        try {
            return df.parse(strDate);
        } catch (ParseException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String getNowTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    public static String getNowTime(String type) {
        SimpleDateFormat df = new SimpleDateFormat(type);
        return df.format(new Date());
    }

    public static String getJFPTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
        return df.format(new Date());
    }

    public static long dateToUnixTimestamp(String date) {
        long timestamp = 0L;

        try {
            timestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(date).getTime();
        } catch (ParseException var4) {
            var4.printStackTrace();
        }

        return timestamp;
    }

    public static long dateToUnixTimestamp(String date, String dateFormat) {
        long timestamp = 0L;

        try {
            timestamp = (new SimpleDateFormat(dateFormat)).parse(date).getTime();
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return timestamp;
    }

    public static long dateToUnixTimestamp() {
        long timestamp = (new Date()).getTime();
        return timestamp;
    }

    public static String unixTimestampToDate(long timestamp) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sd.format(new Date(timestamp));
    }
}
