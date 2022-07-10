package com.dongyulong.dogn.tools.amount;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额换算工具
 *
 * @author dongy
 * @date 17:18 2022/2/9
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmountConvertUtil {

    public static final String CURRENCY_CNY = "CNY";
    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);


    /**
     * 元转分
     *
     * @param amount -
     * @return -
     */
    public static int yuan2penny(BigDecimal amount) {
        if (amount == null) {
            return 0;
        }
        return toDecimalPlaces(amount).multiply(ONE_HUNDRED).intValue();
    }

    /**
     * 元转分
     *
     * @param amount -
     * @return -
     */
    public static String yuan2penny2(BigDecimal amount) {
        if (amount == null) {
            return String.valueOf(ZERO.intValue());
        }
        return String.valueOf(yuan2penny(amount));
    }

    /**
     * 元转分
     *
     * @param amount -
     * @return -
     */
    public static int yuan2penny(String amount) {
        if (StringUtils.isBlank(amount)) {
            return ZERO.intValue();
        }
        return yuan2penny(new BigDecimal(amount));
    }


    /**
     * 分转元
     *
     * @param money -
     * @return -
     */
    public static String fen2yuan2(String money) {
        return penny2yuan(money).toString();
    }

    /**
     * 分转元
     *
     * @param money -
     * @return -
     */
    public static String fen2yuan2(Long money) {
        return penny2yuan(money).toString();
    }

    /**
     * 分转元
     *
     * @param money -
     * @return -
     */
    public static String fen2yuan2(Integer money) {
        return penny2yuan(money).toString();
    }

    /**
     * 分转元
     *
     * @param money -
     * @return -
     */
    public static BigDecimal penny2yuan(Integer money) {
        if (money == null) {
            return ZERO;
        }
        return BigDecimal.valueOf(money).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    /**
     * 分转元
     *
     * @param money -
     * @return -
     */
    public static BigDecimal penny2yuan(Long money) {
        if (money == null) {
            return ZERO;
        }
        return BigDecimal.valueOf(money).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    /**
     * 分转元
     *
     * @param money -
     * @return -
     */
    public static BigDecimal penny2yuan(String money) {
        if (StringUtils.isBlank(money)) {
            return ZERO;
        }
        return new BigDecimal(money).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }


    /**
     * 保留两位小数
     *
     * @param money -
     * @return -
     */
    public static BigDecimal toDecimalPlaces(BigDecimal money) {
        if (money == null) {
            return ZERO;
        }
        return money.setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * {@link String} 转 {@link BigDecimal}
     *
     * @param money -
     * @return -
     */
    public static BigDecimal yuan2BigDecimal(String money) {
        if (StringUtils.isBlank(money)) {
            return ZERO;
        }
        return toDecimalPlaces(new BigDecimal(money));
    }

    /**
     * {@link BigDecimal} 转 {@link String}
     *
     * @param money -
     * @return -
     */
    public static String yuan2String(BigDecimal money) {
        if (money == null) {
            return ZERO.toString();
        }
        return toDecimalPlaces(money).toString();
    }

    /**
     * null to zero
     *
     * @param money -
     * @return -
     */
    public static BigDecimal null2Zero(BigDecimal money) {
        if (money == null) {
            return ZERO;
        }
        return money;
    }

    /**
     * 是否大于{@link BigDecimal#ZERO}，是否为正数金额
     *
     * @return -
     */
    public static boolean isPositiveNumberAmount(String yuanAmount) {
        BigDecimal yuan = yuan2BigDecimal(yuanAmount);
        return yuan.compareTo(ZERO) > 0;
    }

    /**
     * 是否大于{@link BigDecimal#ZERO}，是否为正数金额
     *
     * @return -
     */
    public static boolean isPositiveNumberAmount(BigDecimal yuan) {
        if (yuan == null) {
            return false;
        }
        return yuan.compareTo(ZERO) > 0;
    }


    /**
     * 是否大于{@link BigDecimal#ZERO}，是否为正数金额
     *
     * @return -
     */
    public static boolean isNegativeNumberAmount(String yuanAmount) {
        BigDecimal yuan = yuan2BigDecimal(yuanAmount);
        return yuan.compareTo(ZERO) < 0;
    }

    /**
     * 是否大于{@link BigDecimal#ZERO}，是否为正数金额
     *
     * @return -
     */
    public static boolean isNegativeNumberAmount(BigDecimal yuan) {
        if (yuan == null) {
            return false;
        }
        return yuan.compareTo(ZERO) < 0;
    }

    /**
     * 比较金额大小
     *
     * @param currentYuanAmount 当前金额，单位元
     * @param compareYuanAmount 待比较值，单位元
     * @return currentYuanAmount >= compareYuanAmount 返回true
     */
    public static boolean compareAmountSize(String currentYuanAmount, String compareYuanAmount) {
        BigDecimal currentYuan = yuan2BigDecimal(currentYuanAmount);
        BigDecimal compareYuan = yuan2BigDecimal(compareYuanAmount);
        return currentYuan.compareTo(compareYuan) >= 0;
    }

    /**
     * 比较金额大小
     *
     * @param currentYuan 当前金额，单位元
     * @param compareYuan 待比较值，单位元
     * @return {@param compareYuan} >= {@param compareYuan} 返回true
     */
    public static boolean compareAmountSize(BigDecimal currentYuan, BigDecimal compareYuan) {
        if (currentYuan == null || compareYuan == null) {
            return false;
        }
        return currentYuan.compareTo(compareYuan) >= 0;
    }

    /**
     * 批量加金额
     *
     * @param yuanAmount       金额，单位元
     * @param othersYuanAmount 金额，单位元
     * @return -
     */
    public static BigDecimal batchPlusAmount(String yuanAmount, String... othersYuanAmount) {
        BigDecimal baseAmount = yuan2BigDecimal(yuanAmount);
        return batchPlusAmount(baseAmount, othersYuanAmount);
    }

    /**
     * 批量加金额
     *
     * @param yuan             金额，单位元
     * @param othersYuanAmount 金额，单位元
     * @return -
     */
    public static BigDecimal batchPlusAmount(BigDecimal yuan, String... othersYuanAmount) {
        BigDecimal baseAmount = null2Zero(yuan);
        if (ArrayUtils.isNotEmpty(othersYuanAmount)) {
            for (String otherYuanAmount : othersYuanAmount) {
                baseAmount = baseAmount.add(yuan2BigDecimal(otherYuanAmount));
            }
        }
        return baseAmount;
    }

    /**
     * 批量加金额
     *
     * @param yuan       金额，单位元
     * @param othersYuan 金额，单位元
     * @return -
     */
    public static BigDecimal batchPlusAmount(BigDecimal yuan, BigDecimal... othersYuan) {
        BigDecimal baseAmount = null2Zero(yuan);
        if (ArrayUtils.isNotEmpty(othersYuan)) {
            for (BigDecimal otherYuan : othersYuan) {
                baseAmount = baseAmount.add(null2Zero(otherYuan));
            }
        }
        return baseAmount;
    }

}
