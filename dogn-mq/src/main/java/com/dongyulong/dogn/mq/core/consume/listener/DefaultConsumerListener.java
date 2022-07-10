package com.dongyulong.dogn.mq.core.consume.listener;

import com.dongyulong.dogn.mq.core.consume.MqConfig;
import com.dongyulong.dogn.mq.entities.RocketMQConsumerConfig;
import com.dongyulong.dogn.mq.utils.TimeHolder;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangshaolong
 * @create 2022/1/18
 **/
@Slf4j
public class DefaultConsumerListener implements ConsumerListener {

    private final static Logger slowLogger = LoggerFactory.getLogger("SlowLogger");

    private final static Logger rejectLogger = LoggerFactory.getLogger("MQRejectLogger");

    private MqConfig mqConfig;

    public DefaultConsumerListener(MqConfig mqConfig) {
        this.mqConfig = mqConfig;
    }

    @Override
    public boolean onErrorRetry(MessageExt messageExt, Throwable throwable) {
        long time = TimeHolder.stop();
        boolean result = false;
        try {
            if (null == throwable) {
                result = normalRetry(messageExt, time);
            } else {
                result = exceptionRetry(messageExt, throwable, time);
            }
        } catch (Exception e) {
            log.error("onError error", e);
        } finally {
            addSlowLog(time, messageExt, mqConfig);
        }
        return result;
    }

    /**
     * 正常业务是否需要重试
     *
     * @return
     */
    private boolean normalRetry(MessageExt messageExt, long time) {
        int retryCount = messageExt.getReconsumeTimes();
        if (retryCount > Math.min(RocketMQConsumerConfig.retryThreshold, mqConfig.getMaxReconsumeTimes())) {
            rejectLogger.error("{} consume messageid {} failed after retry {} times! rejected! msgBody:{}", messageExt.getTopic(), messageExt.getMsgId(), retryCount, new String(messageExt.getBody(), Charsets.UTF_8));
            Label.LabelEnum.DROP.incLabel(mqConfig, time);
            return false;
        } else {
            Label.LabelEnum.RETRY.incLabel(mqConfig, time);
            return true;
        }
    }

    /**
     * 异常是否需要重试
     *
     * @return
     */
    private boolean exceptionRetry(MessageExt messageExt, Throwable throwable, long time) {
        if (throwable instanceof IllegalArgumentException) {
            Label.LabelEnum.SUCCESS.incLabel(mqConfig, time);
            return false;
        }
        int retryCount = messageExt.getReconsumeTimes();
        if (retryCount > Math.min(RocketMQConsumerConfig.retryThreshold, mqConfig.getMaxReconsumeTimes())) {
            rejectLogger.error("{} consume messageid {} failed after retry {} times! rejected! msgBody:{}", messageExt.getTopic(), messageExt.getMsgId(), retryCount, new String(messageExt.getBody(), Charsets.UTF_8));
            Label.LabelEnum.DROP.incLabel(mqConfig, time);
            return false;
        }
        Label.LabelEnum.FAIL.incLabel(mqConfig, time);
        log.error("Msg:{},retryCount:{} will retry!", messageExt.getMsgId(), retryCount);
        if (retryCount == 0) {
            //首次失败的数据添加到监控列表
//            MqQueueMonitor.getInstance().increment();
        }
        return true;
    }

    /**
     * 处理成功后的结果信息
     *
     * @param messageExt
     */
    @Override
    public void onSuccess(MessageExt messageExt) {
        long time = TimeHolder.stop();
        try {
            if (messageExt.getReconsumeTimes() > 0) {
                rejectLogger.info("{} consumed after retry {} times! msgBody:{}", messageExt.getTopic(), messageExt.getReconsumeTimes(), new String(messageExt.getBody(), Charsets.UTF_8));
            }
            Label.LabelEnum.SUCCESS.incLabel(mqConfig, time);
        } catch (Exception e) {
            log.error("onSuccess error", e);
        } finally {
            addSlowLog(time, messageExt, mqConfig);
        }
    }


    private void addSlowLog(long time, MessageExt messageExt, MqConfig mqConfig) {
        if (time > 500) {
            slowLogger.info("group:{},topic:{},tag:{},queueId:{},messageId:{} (cost {} ms)", mqConfig.getGroup(), mqConfig.getTopic(), mqConfig.getTags(), messageExt.getQueueId(), messageExt.getMsgId(), time);
        }
    }

}
