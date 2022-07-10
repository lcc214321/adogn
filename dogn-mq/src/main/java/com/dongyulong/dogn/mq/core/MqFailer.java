package com.dongyulong.dogn.mq.core;

import com.dongyulong.dogn.autoconfigure.monitor.common.AlarmCommon;
import com.dongyulong.dogn.common.config.CommonUtils;
import com.dongyulong.dogn.mq.common.SaltConstants;
import com.dongyulong.dogn.mq.core.bean.RedisMqBean;
import com.dongyulong.dogn.redis.spring.boot.RedisClusterUtils;
import com.dongyulong.dogn.tools.json.JsonMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消费的业务逻辑处理
 *
 * @author zhangshaolong
 * @create 2021/12/29
 **/
@Slf4j
public class MqFailer {

    private final AtomicBoolean RUN = new AtomicBoolean(false);

    private BlockingQueue<Message> retryQueue = new LinkedBlockingQueue<>(256);

    private DefaultMQProducer producer;

    private MqFailer() {
    }

    public MqFailer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    /**
     * 启动消费监听
     */
    public void start() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(
                "mqfail-thread-%d").build());
        executorService.execute(this::queueTask);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                RUN.set(false);
                executorService.shutdown();
                executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
                executorService.shutdownNow();
            } catch (Exception e) {
                log.error("mqfail shutdown error", e);
            }
        }));
    }

    /**
     * 内存管理的定时任务
     */
    private void queueTask() {
        if (!RUN.compareAndSet(false, true)) {
            return;
        }
        //循环使用
        while (RUN.get()) {
            try {
                //阻塞,这块防止内存过大,不能有太多的数据信息
                Message msg = retryQueue.take();
                try {
                    producer.send(msg);
                    log.warn("RocketMQProducerHelper.retry send msg:{}:{}:{}", msg.getTopic(), msg.getKeys(), new String(msg.getBody()));
                } catch (Exception e) {
                    retryQueue.add(msg);
                }
            } catch (InterruptedException e) {
            } catch (Exception e) {
                log.error("consumer error", e);
            }
        }
    }

    /**
     * 推送失败的数据信息
     *
     * @param meesage
     */
    public void addFailMessage(Message meesage, Integer mqType, boolean alarm) {
        log.warn("addFailMessage {}:{}:{}", meesage.getTopic(), meesage.getKeys(), new String(meesage.getBody()));
        //走redis
        AlarmHelper.alarm(meesage, alarm);
        if (SaltConstants.online()) {
            //走redis队列处理
            try {
                RedisMqBean redisMqBean = new RedisMqBean();
                redisMqBean.setMessage(meesage);
                redisMqBean.setMqType(mqType);
                JedisCluster jedisCluster = RedisClusterUtils.getRedisClient(SaltConstants.REDIS_GROUP, SaltConstants.REDIS_KEY);
                jedisCluster.rpush(SaltConstants.REDIS_QUEUE, JsonMapper.toJson(redisMqBean));
            } catch (Exception e) {
                log.error("addFailMessage redis:{} error", new String(meesage.getBody()));
                //redis 失败可以走本地内存处理
                addLocalCache(meesage);
            }
        } else {
            addLocalCache(meesage);
        }
    }

    /**
     * 添加本地的内存处理
     *
     * @param meesage
     */
    private void addLocalCache(Message meesage) {
        try {
            retryQueue.add(meesage);
        } catch (Exception e) {
            //打印日志,队列满了会抛出异常
            log.error("addFailMessage local:{} error", new String(meesage.getBody()));
        }
    }


    public static class AlarmHelper {
        /**
         * 发送报警的线程数据,不需要开过多的线程处理报警问题
         */
        private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, 10, 20, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadFactoryBuilder().setNameFormat(
                        "alarm-thread-%d").build(), new ThreadPoolExecutor.DiscardPolicy());


        /**
         * 限制报警的发送信息
         */
        private final static RateLimiter limiter = RateLimiter.create(1, 10, TimeUnit.SECONDS);

        /**
         * 统计失败的次数
         */
        private final static AtomicInteger atomicInteger = new AtomicInteger(0);

        /**
         * 报警模版信息
         */
        private final static String TEXT_ALARM_TEMPLATE = "消息发送失败,失败数量:%s" + "\n"
                + "机器:%s" + "\n"
                + "应用:%s" + "\n"
                + "最近一条消息:" + "\n" + "%s";


        /**
         * 报警服务信息
         *
         * @param alarm
         */
        public static void alarm(Message meesage, boolean alarm) {
            if (!alarm) {
                return;
            }
            atomicInteger.incrementAndGet();
            if (limiter.tryAcquire()) {
                executor.execute(() -> {
                    //TODO 报警信息
                    // 最近失败数量:
                    // 最新一条失败消息:
                    int alram = getAlarm();
                    AlarmCommon.sendAlarm(String.format(TEXT_ALARM_TEMPLATE, alram, CommonUtils.getHostName(), CommonUtils.getAppName(), meesage.getTopic() + ":" + meesage.getTags() + ":" + meesage.getKeys()));
                });
            }
            ;
        }


        private static int getAlarm() {
            return atomicInteger.getAndSet(0);
        }
    }


}
