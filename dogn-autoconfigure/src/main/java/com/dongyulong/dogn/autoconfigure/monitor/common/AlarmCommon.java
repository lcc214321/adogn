package com.dongyulong.dogn.autoconfigure.monitor.common;

import com.dongyulong.dogn.core.executor.DognExecutor;
import com.dongyulong.dogn.core.executor.RejectedHandlerEnum;
import com.dongyulong.dogn.autoconfigure.tools.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 报警信息
 *
 * @author zhangshaolong
 * @create 2022/1/24
 **/
@Slf4j
public class AlarmCommon {

    /**
     * 发送报警的线程数据,不需要开过多的线程处理报警问题
     */
    private final static DognExecutor executor = DognExecutor.newBuilder("")
            .corePoolSize(5)
            .keepAliveTime(30, TimeUnit.SECONDS)
            .maximumPoolSize(10)
            .rejectedExecutionHandler(RejectedHandlerEnum.CALLER_RUNS)
            .workQueue(new LinkedBlockingQueue<>(10))
            .build();

    /**
     * 报警模版信息
     */
    private final static String TEXT_ALARM_TEMPLATE =
            "应用:%s" + "\n"
                    + "机器:%s" + "\n"
                    + "方法:%s" + "\n"
                    + "报警内容:" + "\n" + "%s";


    /**
     * 发送报警信息
     */
    public static void sendAlarm(String message) {
        if (!AppCommon.monitor()) {
            //不发送报警
            return;
        }
        executor.execute(() -> {
            String alermText = String.format(TEXT_ALARM_TEMPLATE, AppCommon.getAppName(),
                    AppCommon.getHostName(), "", message);
            sendText(AppCommon.alramUrl(), alermText);
        });
    }

    /**
     * 发送报警信息
     */
    public static void sendAlarm(String method, long qps, long error) {
        if (!AppCommon.monitor()) {
            //不发送报警
            return;
        }
        log.warn("method:{} sendAlram...", method);
        executor.execute(() -> {
            String alermText = String.format(TEXT_ALARM_TEMPLATE, AppCommon.getAppName(),
                    AppCommon.getHostName(), method,
                    "最近" + AppCommon.getMonitor() + "秒内请求:" + qps + "次,报错:" + error + "次,请注意检查");
            sendText(AppCommon.alramUrl(), alermText);
        });
    }


    /**
     * 文本信息
     *
     * @param content 消息内容
     * @param url     钉钉机器人url
     * @throws Exception
     */
    private static void sendText(String url, String content) {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        try {
            HttpClient httpclient = HttpClients.createDefault();

            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");
            // 拼接环境
            String env = SpringUtils.getContext().getEnvironment().getActiveProfiles()[0];
            content = "[" + env + "环境] " + content;
            String textMsg = "{ \"msgtype\": \"text\", \"text\": {\"content\": \"" + content + "\"}}";
            StringEntity se = new StringEntity(textMsg, "utf-8");
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            log.error("钉钉消息发送失败 url:{},content:{}", url, content, e);
        }
    }
}
