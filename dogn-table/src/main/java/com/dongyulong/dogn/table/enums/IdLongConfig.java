package com.dongyulong.dogn.table.enums;

import com.dongyulong.dogn.table.common.IConfig;
import com.dongyulong.dogn.table.common.IEnvNo;
import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.dongyulong.dogn.table.common.ITableNo;
import org.apache.commons.lang3.StringUtils;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/89:08 上午
 * @since v1.0
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum IdLongConfig implements IConfig<Long> {

    /**
     * 初始时间戳
     */
    START_TIMESTAMP("start_timestamp", 1640966400L),
    /**
     * 机器节点长度为4位
     */
    MACHINE_NODE_ID_BITS("machine_node_id_bits", 4L),
    /**
     * 序列号id长度
     */
    SEQUENCE_BITS("sequence_bits", 10L),
    /**
     * 表后缀长度,如果按数字分表需要配置
     * 分表情况例如 exp、exp_1
     * 如果按时间、或地域等属性分表当前配置修改为0
     */
    TABLE_SUFFIX_BITS("table_suffix_bits", 8L),
    /**
     * 表编号长度
     *
     * @see ITableNo
     */
    TABLE_NO_BITS("table_no_bits", 6L),
    /**
     * 部署环境标识长度
     *
     * @see IEnvNo
     */
    DEPLOY_ENV_ID_BITS(2L);

    private String key;

    private final Long defValue;

    public long getProperty() {
        if (StringUtils.isBlank(this.getKey())) {
            return this.getDefValue();
        }
        return SystemPropertyUtils.getLongProperty(this.getKey(), this.getDefValue());
    }
}
