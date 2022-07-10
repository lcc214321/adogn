package com.dongyulong.dogn.table.id;

import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.table.common.EndStart;
import com.dongyulong.dogn.table.common.IEnvNo;
import com.dongyulong.dogn.table.common.ITableNo;
import com.dongyulong.dogn.table.enums.IdLongConfig;
import com.dongyulong.dogn.table.enums.NoArrayConfig;
import com.dongyulong.dogn.table.enums.NoIntConfig;
import com.dongyulong.dogn.table.enums.SubTableIntConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoUtils {

    private static final Map<String, IdWorker> ID_WORKER_MAP = new HashMap<>();
    private static final String DEFAULT_TRADE_NO_PREFIX_FORMAT = "yyMMdd";
    private static final String TAXIQR_TRADE_NO_PREFIX_FORMAT = "yyyyMMdd";


    /**
     * 根据id取mod获取入库后缀，如果后缀为0，则为不带_0的初始表名
     *
     * @param id 计算分表的id
     * @return -
     */
    public static long getSubTableSuffix(Long id) {
        return (id % IdLongConfig.TABLE_SUFFIX_BITS.getProperty()) >> SubTableIntConfig.SLOT_SUB_TABLE.getProperty();
    }

    /**
     * 获取最大表后缀
     *
     * @return maxSuffix
     */
    public static long getMaxTableSuffix() {
        return 255 >> SubTableIntConfig.SLOT_SUB_TABLE.getProperty();
    }

    public static String getTradeNo(long id, ITableNo tableIdEnum, Integer sourceId, Date createTime) {
        String sourceIdNo = new DecimalFormat("000").format(sourceId);
        return doGetTradeNo(id, tableIdEnum, sourceIdNo, createTime, DEFAULT_TRADE_NO_PREFIX_FORMAT);
    }

    /**
     * 生成订单号方式
     * 三化订单生成
     *
     * @param id          -
     * @param tableIdEnum -
     * @param sourceId    -
     * @param createTime  -
     * @return -
     */
    public static String getTradeNoTaxiqr(long id, ITableNo tableIdEnum, Integer sourceId, Date createTime) {
        String sourceIdNo = new DecimalFormat("000").format(sourceId);
        return doGetTradeNo(id, tableIdEnum, sourceIdNo, createTime, TAXIQR_TRADE_NO_PREFIX_FORMAT);
    }

    public static String genTradeNoByOrderNo(long id, ITableNo tableIdEnum, String orderNo, Date createTime) {
        String sourceIdNo = NoUtils.getSourceId(orderNo);
        if (isStartFourDigitYear(orderNo)) {
            return doGetTradeNo(id, tableIdEnum, sourceIdNo, createTime, TAXIQR_TRADE_NO_PREFIX_FORMAT);
        }
        return doGetTradeNo(id, tableIdEnum, sourceIdNo, createTime, DEFAULT_TRADE_NO_PREFIX_FORMAT);
    }

    /**
     * eg
     *
     * @param id
     * @param tableIdEnum
     * @param sourceIdNo
     * @param createTime
     * @return
     */
    private static String doGetTradeNo(long id, ITableNo tableIdEnum, String sourceIdNo, Date createTime, String tradeNoPrefixFormat) {
        return new SimpleDateFormat(tradeNoPrefixFormat).format(createTime) + new DecimalFormat("00").format(tableIdEnum.getNo()) + sourceIdNo + id;
    }

    /**
     * 订单号8~10位为业务标识
     * 如果为银盛订单10~12位为业务标识
     * 银盛订单与其他订单不同，以“2022”开头，其他订单都以22开头
     *
     * @param orderNo -
     * @return -
     */
    public static String getSourceId(String orderNo) {
        //三化订单是2022开头
        if (isStartFourDigitYear(orderNo)) {
            EndStart endStart = NoIntConfig.SOURCE_ID_IDX_YYYY.getProperty();
            return orderNo.substring(endStart.getStartIndx(), endStart.getEndIndx());
        }
        EndStart endStart = NoIntConfig.SOURCE_ID_IDX_YY.getProperty();
        return orderNo.substring(endStart.getStartIndx(), endStart.getEndIndx());
    }

    /**
     * 订单号6~7位为TableId
     * 如果为银盛订单8~9位为TableId
     * 银盛订单与其他订单不同，以“2022”开头，其他订单都以22开头
     *
     * @param orderNo -
     * @return -
     */
    public static int getTableId(String orderNo) {
        //三化订单是2022开头
        if (isStartFourDigitYear(orderNo)) {
            return Integer.parseInt(orderNo.substring(8, 10));
        }
        return Integer.parseInt(orderNo.substring(6, 8));
    }

    /**
     * 三化订单是202开头
     * 是否以yyyy四位年开头的订单
     * fourDigitYear
     *
     * @param tradeNo -
     * @return -
     */
    public static boolean isStartFourDigitYear(String tradeNo) {
        if (!StringUtils.isNumeric(tradeNo)) {
            return false;
        }
        if (tradeNo.length() < 28) {
            return false;
        }
        return tradeNo.substring(0, 8).matches("(20[2-9]\\d)((0[1-9])|10|11|12)((0[1-9])|[12]\\d|30|31)");
    }

    /**
     * 根据分布式id获取表名id
     *
     * @param id
     * @return
     */
    public static long getTableId(long id) {
        return IdWorker.getTableNo(id);
    }

    /**
     * 根据分布式id获取分表后缀
     *
     * @param id
     * @return
     */
    public static long getTableSuffix(long id) {
        return IdWorker.getTableSuffix(id);
    }

    /**
     * 根据tradeNo/refundNo获取分表后缀
     *
     * @param tradeNo
     * @return
     */
    public static long getTableSuffix(String tradeNo) {
        //三化订单是2022开头
        if (isStartFourDigitYear(tradeNo)) {
            return IdWorker.getTableSuffix(Long.parseLong(tradeNo.substring(13)));
        }
        return IdWorker.getTableSuffix(Long.parseLong(tradeNo.substring(11)));
    }

    /**
     * 根据表名id和表后缀生成分布式id
     *
     * @param tableId       -
     * @param tableSuffix   -
     * @param deployEnvEnum -
     * @return id
     */
    protected static long getUniqueId(long tableId, long tableSuffix, IEnvNo deployEnvEnum) {
        if (deployEnvEnum == null) {
            throw new DException(ErrorCode.ENV_ERROR);
        }
        String idWorkerMapKey = tableId + "_" + tableSuffix;
        if (ID_WORKER_MAP.get(idWorkerMapKey) != null) {
            return ID_WORKER_MAP.get(idWorkerMapKey).nextId();
        } else {
            synchronized (NoUtils.class) {
                if (ID_WORKER_MAP.get(idWorkerMapKey) != null) {
                    return ID_WORKER_MAP.get(idWorkerMapKey).nextId();
                } else {
                    IdWorker idWorker = generateIdWorker(tableId, tableSuffix, deployEnvEnum);
                    ID_WORKER_MAP.put(idWorkerMapKey, idWorker);
                    return idWorker.nextId();
                }
            }
        }
    }

    private static IdWorker generateIdWorker(long tableId, long tableSuffix, IEnvNo deployEnvEnum) {
        String ip = getHostIp();
        String[] ipAddressArray = NoArrayConfig.IP_ADDRESS_ARRAY.getProperty();
        int ipIndex = 0;
        for (String configIp : ipAddressArray) {
            if (configIp.equals(ip)) {
                return new IdWorker(ipIndex, tableId, tableSuffix, deployEnvEnum);
            }
            ipIndex++;
        }
        throw new DException();
    }

    private static String getHostIp() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                Enumeration<InetAddress> enumIpAddr = en.nextElement().getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException var5) {
            log.error("Fail to get IP address.", var5);
        }
        return ip;
    }

    /**
     * 根据表后缀模拟userId
     *
     * @param tableSuffix 表后缀
     * @return -
     */
    public static long simulateUserIdBasedOnTableSuffix(long tableSuffix) {
        return (tableSuffix << SubTableIntConfig.SLOT_SUB_TABLE.getProperty()) + 256;
    }


}
