package com.dongyulong.dogn.mq.core.consume.listener;

import com.dongyulong.dogn.autoconfigure.monitor.common.AlarmCommon;
import com.dongyulong.dogn.common.config.CommonUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * 监控队列的数据信息
 *
 * @author zhangshaolong
 * @create 2022/1/18
 **/
public class MqQueueMonitor {

    /**
     * group 对应的监控信息,主题topic
     */
    private ConcurrentHashMap<String, MqMonitor> countMap = new ConcurrentHashMap<>();


    private ScheduledExecutorService scheduledExecutor;

    private static final Joiner DEFAULT_JOINER = Joiner.on("_").skipNulls();

    private final static int TIME_RANGE = 60;

    private final LongAdder count = new LongAdder();

    /**
     * 报警模版信息
     */
    private final static String TEXT_ALARM_TEMPLATE = "topic:group:%s" + "\n"
            + "host:%s" + "\n"
            + "ququeId:" + "\n" + "%s"
            + "offset:" + "\n" + "%s";

    private static class MqMonitorHelper {
        private final static MqQueueMonitor mqQueueMonitor = new MqQueueMonitor();
    }

    public static MqQueueMonitor getInstance() {
        return MqMonitorHelper.mqQueueMonitor;
    }


    public MqQueueMonitor() {
        //开启定时任务
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(this::monitor,
                TIME_RANGE + 1, TIME_RANGE, TimeUnit.SECONDS);
    }

    /**
     * 统计队列的报警数据信息
     */
    private void monitor() {
        Map<String, List<MqMonitor>> data = Maps.newHashMap();
        //定时采集信息,取平均值,按照topic 采集信息,如果最大和最下的差值很大,应该就有问题
        if (MapUtils.isEmpty(countMap)) {
            return;
        }
        countMap.forEach((key, value) -> {
            //按照升序进行排列,获取最大和最小的信息group_topic_queueId
            String mapkey = key.substring(0, key.lastIndexOf("_"));
            List<MqMonitor> list = data.get(mapkey);
            if (list == null) {
                list = new ArrayList<>();
                data.put(key, list);
            }
            list.add(value);
        });
        if (MapUtils.isNotEmpty(data)) {
            data.forEach((key, value) -> {
                List<MqMonitor> mqList = value.stream().sorted(Comparator.comparing(MqMonitor::getQueueOffset))
                        .collect(Collectors.toList());
                MqMonitor start = mqList.get(0);
                MqMonitor end = mqList.get(mqList.size() - 1);
                //队列之间消费不均匀
                if (end.queueOffset - start.queueOffset > 100) {
                    //TODO group topic queueOffset 信息
                    AlarmCommon.sendAlarm(String.format(TEXT_ALARM_TEMPLATE, start.topic + ":" + start.group, CommonUtils.getHostName(), start.queueId, start.queueOffset + "｜" + end.queueOffset));
                }
            });
        }
        //重试的数据比较多,说明服务可能会有问题
        if (count.longValue() > 200) {
            //TODO
        }
    }

    /**
     * 更新节点的数据信息
     *
     * @param group
     * @param messageExt
     */
    public void updateQueue(String group, MessageExt messageExt) {
        String topic = messageExt.getTopic();
        int queue = messageExt.getQueueId();
        MqMonitor mqMonitor = getMqMonitor(group, topic, queue);
        mqMonitor.setQueueOffset(messageExt.getQueueOffset());
    }

    /**
     * 获取数据信息
     *
     * @param group
     * @param topic
     * @param queue
     * @return
     */
    private MqMonitor getMqMonitor(String group, String topic, int queue) {
        String key = getKey(group, topic, String.valueOf(queue));
        MqMonitor mqMonitor = countMap.get(key);
        if (mqMonitor == null) {
            MqMonitor temp = new MqMonitor(topic, group, queue);
            mqMonitor = countMap.putIfAbsent(key, temp);
            if (mqMonitor == null) {
                mqMonitor = temp;
            }
        }
        return mqMonitor;
    }

    /**
     * 添加重试的数据信息
     */
    public void increment() {
        count.increment();
    }

    /**
     * 减少重试的数据信息
     */
    public void decrement() {
        count.decrement();
    }

    private class MqMonitor implements Comparable<MqMonitor> {

        private String topic;

        private String group;

        private int queueId;

        private Long queueOffset;

        public MqMonitor(String topic, String group, int queueId) {
            this.topic = topic;
            this.group = group;
            this.queueId = queueId;
            this.queueOffset = 0L;
        }

        public void setQueueOffset(Long queueOffSet) {
            if (queueOffSet > queueOffset) {
                queueOffset = queueOffSet;
            }
        }

        public String getGroup() {
            return group;
        }

        public String getTopic() {
            return topic;
        }

        public int getQueueId() {
            return queueId;
        }

        public long getQueueOffset() {
            return queueOffset;
        }

        @Override
        public int compareTo(MqMonitor o) {
            return queueOffset.compareTo(o.queueOffset);
        }
    }

    private String getKey(String... params) {
        return DEFAULT_JOINER.join(Arrays.asList(params));
    }

}
