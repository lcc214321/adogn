package com.dongyulong.dogn.mq.core.consume.listener;

import com.alibaba.fastjson.JSON;
import com.dongyulong.dogn.mq.common.AbstractRocketMQSubscriber;
import com.dongyulong.dogn.mq.core.consume.MqConfig;
import com.dongyulong.dogn.mq.entities.RocketMQConsumerConfig;
import com.dongyulong.dogn.mq.utils.TimeHolder;
import com.dongyulong.dogn.tools.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author zhangshaolong
 * @create 2022/1/17
 **/
@Slf4j
public abstract class AbstractMqListener<T> extends AbstractRocketMQSubscriber implements IMqListener<T> {

    private final static Logger MQMessageLogger = LoggerFactory.getLogger("MQMessageLogger");

    protected Type clazz;

    private ConsumerListener consumerListener;

    private MqConfig mqConfig;

    @Override
    public void init() {
        //TODO
    }

    @Override
    public void onConfig(MqConfig mqConfig) {
        this.mqConfig = mqConfig;
        consumerListener = new DefaultConsumerListener(mqConfig);
        this.setTags(mqConfig.getTags());
        this.setGroup(mqConfig.getGroup());
        this.clazz = ((ParameterizedType) AbstractMqListener.this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 监听消费数据
     *
     * @param msgs
     * @param context
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs,
                                                    final ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus status = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        //batch 为1的时候这个为1个数据
        for (final MessageExt messageExt : msgs) {
            if (null == messageExt || null == messageExt.getBody()) {
                //直接返回
                continue;
            }
            TimeHolder.start();
            try {
                if (messageExt.getProperties().get("MAX_OFFSET") != null) {
                    long offset = Long.parseLong(messageExt.getProperty(MessageConst.PROPERTY_MAX_OFFSET)) - messageExt.getQueueOffset();
                    if (offset > RocketMQConsumerConfig.maxOffsetThreshold) {
                        //TODO 需要报警处理
                        log.warn("{}消息积压过多,offset:{},超过阈值{},请确认是否可以重置消费点或调大阈值！", mqConfig.getGroup(), offset, RocketMQConsumerConfig.maxOffsetThreshold);
//                        shutdown();
                        System.exit(1);
                    }
                }
                final String json = new String(messageExt.getBody());
                MQMessageLogger.info("reciveMsgFromMQ message topic:{}, tags:{}, offset:{} body:{}", messageExt.getTopic(), messageExt.getTags(), messageExt.getQueueOffset(), json);
                T jsonObject = getData(json, clazz);
                if (null == jsonObject) {
                    log.warn("reciveMsgFromMQ consumeMessage null,topic:{}, tags:{}, offset:{} data:{}", messageExt.getTopic(), messageExt.getTags(), messageExt.getQueueOffset(), json);
                    continue;
                }
                boolean result = this.receive(jsonObject);
                if (result) {
                    consumerListener.onSuccess(messageExt);
                } else {
                    status = consumerListener.onErrorRetry(messageExt, null) ? ConsumeConcurrentlyStatus.RECONSUME_LATER : ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            } catch (Throwable e) {
                log.error(String.format("reciveMsgFromMQ consumeMessage faild,topic:%s, tags:%s, offset:%d messageExt:%s", messageExt.getTopic(), messageExt.getTags(), messageExt.getQueueOffset(), messageExt), e);
                status = consumerListener.onErrorRetry(messageExt, e) ? ConsumeConcurrentlyStatus.RECONSUME_LATER : ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } finally {
                MqQueueMonitor.getInstance().updateQueue(mqConfig.getGroup(), messageExt);
            }
            if (status == ConsumeConcurrentlyStatus.RECONSUME_LATER) {
                //有一个失败就直接跳出循环,因为是批量处理信息
                break;
            }
        }
        return status;
    }

    private T getData(String json, Type clazz) {
        if (String.class.equals(clazz)) {
            return (T) json;
        }
        T jsonObject;
        try {
            jsonObject = JSON.parseObject(json, clazz);
        } catch (Exception e) {
            jsonObject = JsonMapper.json2Bean(json, clazz);
        }
        return jsonObject;
    }
}
