package com.dongyulong.dogn.datasource.routing;

import cn.hutool.core.util.NumberUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author dongy
 * @version v2.0.1
 * @date 10:27 2022/1/5
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routing {
    /**
     * 分库id,不需要分库禁止设置当前值
     */
    private Long id;
    /**
     * 按用户id分表,不需要分表禁止设置当前值
     */
    private Integer userId;

    public static final Routing DEFAULT = null;

    public static Routing build(Long id) {
        return new Routing().setId(id);
    }

    public static Routing build(Integer userId) {
        return new Routing().setUserId(userId);
    }

    public static Routing build(Long id, Integer userId) {
        return new Routing().setId(id).setUserId(userId);
    }

    public Routing setStrId(String id) {
        if (NumberUtil.isLong(id)) {
            this.id = Long.parseLong(id);
        }
        return this;
    }
}
