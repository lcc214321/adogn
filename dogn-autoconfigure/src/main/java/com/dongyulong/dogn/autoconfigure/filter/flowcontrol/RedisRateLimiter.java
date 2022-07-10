package com.dongyulong.dogn.autoconfigure.filter.flowcontrol;

import com.dongyulong.dogn.redis.spring.boot.RedisClusterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 老系统迁移过来的代码
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/9 7:51 上午
 * @since v1.0
 */
@Slf4j
public class RedisRateLimiter {

    public static final String GROUP_KEY = "[rc25]flowControl:";
    private final RateLimiterProperties properties;

    public RedisRateLimiter(RateLimiterProperties properties) {
        this.properties = properties;
    }

    /**
     * 简单计数限流，防爬防刷，暂不考虑平滑问题
     *
     * @param resource 资源名称，一般是接口 URI
     * @param identify 热点参数，ip 或 cid
     * @return
     */
    public boolean tryAcquire(String resource, String identify) {
        log.debug("try acquire " + resource + identify);
        if (StringUtils.isEmpty(identify) || properties.getConfigs() == null
                || !properties.getConfigs().containsKey(resource)) {
            log.debug("{}:{} not controlled.", resource, identify);
            return true;
        }
        Long cnt = RedisClusterUtils.get(GROUP_KEY, resource + ":" + identify, Long.class);
        log.debug("flowControl cnt: {}", cnt);
        if (cnt == null) {
            log.debug("init flow control cnt for {}:{}.", resource, identify);
            RedisClusterUtils.setAndExpire(GROUP_KEY, resource + ":" + identify, 1,
                    properties.getConfigs().get(resource).getTimeWindowSeconds(), false);
            return true;
        } else if (cnt >= properties.getConfigs().get(resource).getRate()) {
            if (RedisClusterUtils.getRedisClient(GROUP_KEY, StringUtils.EMPTY).ttl(GROUP_KEY + resource + ":" + identify) == -1) {
                RedisClusterUtils.del(GROUP_KEY, resource + ":" + identify);
                log.info("reset flow control cnt for {}:{}. because it was persisted!", resource, identify);
                return true;
            }
            log.warn("Flow control for {} exceed {}", resource + identify, properties.getConfigs().get(resource).getRate());
            return false;
        } else {
            log.debug("inc flow control cnt for {}:{}.", resource, identify);
            RedisClusterUtils.incr(GROUP_KEY, resource + ":" + identify, -1);
            return true;
        }
    }
}
