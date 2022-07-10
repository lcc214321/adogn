package com.dongyulong.dogn.mq.core;

import com.dongyulong.dogn.mq.common.SaltConstants;
import com.dongyulong.dogn.mq.message.TradeMqMessage;
import com.dongyulong.dogn.mq.message.messagecontext.BaseContext;
import com.dongyulong.dogn.mq.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;

/**
 * 构造不通的方法
 *
 * @author zhangshaolong
 * @create 2021/12/29
 **/
public class MessageFactory {

    /**
     * 构造thea的数据信息
     *
     * @param context
     * @return
     */
    public static <T extends BaseContext> TradeMqMessage<T> buildTradeMessage(T context) {
        TradeMqMessage<T> message = new TradeMqMessage<>(context);
        message.setSign(sign(message));
        return message;
    }

    /**
     * 加密
     *
     * @return
     * @throws Exception
     */
    private static String sign(TradeMqMessage tradeMqMessage) {
        try {
            return MD5Util.md5(tradeMqMessage.getAppId() + tradeMqMessage.getMessageId() + tradeMqMessage.getTime(), SaltConstants.DEFAULT_MD5_SALT);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 验证
     *
     * @return
     * @throws Exception
     */
    public static boolean verification(TradeMqMessage tradeMqMessage) throws Exception {
        return StringUtils.equals(MD5Util.md5(tradeMqMessage.getAppId() + tradeMqMessage.getMessageId() + tradeMqMessage.getTime(), SaltConstants.DEFAULT_MD5_SALT), tradeMqMessage.getSign());
    }
}
