package com.dongyulong.dogn.tools.device;

import com.dongyulong.dogn.tools.entities.DdcinfoEntity;
import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/811:37 下午
 * @since v1.0
 */
public class DeviceUtils {
    private static Logger logger = LoggerFactory.getLogger(DeviceUtils.class);

    //判断设备是否合法
    public static boolean isEnableDevice(DdcinfoEntity ddc) {
        if (ddc == null) {
            return false;
        }
        boolean flag = _isEnableDeviceNew(ddc.getMac(), ddc.getImei(), ddc.getMobiletype(), ddc.getUdid());
        if (!flag) {
            String carpool_enabledevice_check = SystemPropertyUtils.getProperty("carpool_device_check");
            if (StringUtils.isBlank(carpool_enabledevice_check)) {
                return flag;
            }
            if (StringUtils.isNotBlank(ddc.getOs()) && carpool_enabledevice_check.contains(ddc.getOs())) {
                return true;
            }
        }
        return flag;
    }

    //判断设备是否合法
    public static boolean _isEnableDeviceNew(String mac, String imei, String mobiletype, String udid) {
        boolean flag = (!StringUtils.isEmpty(mac) && !"00:00:00:00:00:00".equals(mac))
                || (!StringUtils.isEmpty(imei) && !"000000000000000".equals(imei))
                || (!"2".equals(mobiletype) && !"android".equals(mobiletype))
                || (!StringUtils.isEmpty(udid) && !"000000000000000".equals(udid));
        return flag;
    }

    //判断设备是否合法
    public static boolean _isEnableDevice(String mac, String imei, String mobiletype) {
        if ((StringUtils.isEmpty(mac) || "00:00:00:00:00:00".equals(mac))
                && (StringUtils.isEmpty(imei) || "000000000000000".equals(imei))
                && ("2".equals(mobiletype) || "android".equals(mobiletype))
        ) {
            return false;
        }
        return true;
    }

    //判断设备是否合法
    public static boolean isValidDevice(String mac, String imei, String idfa) {
        if (StringUtils.isNotEmpty(mac) && mac.length() > 20) {
            logger.error("mac too long:" + mac);
            return false;
        }

        if (StringUtils.isNotEmpty(imei) && imei.length() > 64) {
            logger.error("imei too long:" + imei);
            return false;
        }

        if (StringUtils.isNotEmpty(idfa) && idfa.length() > 64) {
            logger.error("idfa too long:" + idfa);
            return false;
        }
        return true;
    }

    public static boolean checkVersionNew(String current_version, String target_version) {
        String[] cv = current_version.split("\\.");
        String[] tv = target_version.split("\\.");
        boolean result = true;
        int loop = Math.min(cv.length, tv.length);
        for (int i = 0; i < loop; i++) {
            int c = Integer.parseInt(cv[i]);
            int t = Integer.parseInt(tv[i]);
            if (c > t) {
                break;
            }
            if (c < t) {
                result = false;
                break;
            }
            //未分出大小 比长度
            if (i == loop - 1 && cv.length < tv.length) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static int caleCloseTime(Date rideOnTime) {
        int reviewClosed = 0;
        if (rideOnTime != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            Date closedDate = DateUtils.addDays(rideOnTime, 2);
            String d = dateFormat.format(closedDate);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                closedDate = dateFormat.parse(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //##########
            Date now = Calendar.getInstance().getTime();
            if (closedDate.before(now)) {
                reviewClosed = 1;
            }
        }
        return reviewClosed;
    }

    /**
     * 取得bigdecimal对象的float值。
     *
     * @param totalRidePrice
     * @return
     */
    public static float getFloat(BigDecimal totalRidePrice) {
        return totalRidePrice == null ? 0 : totalRidePrice.floatValue();
    }


    public static int getInt(BigDecimal totalRidePrice) {
        return totalRidePrice == null ? 0 : totalRidePrice.intValue();
    }

    public static double getDouble(BigDecimal totalRidePrice) {
        return totalRidePrice == null ? 0 : totalRidePrice.doubleValue();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }


    public static void main(String[] args) {

    }
}
