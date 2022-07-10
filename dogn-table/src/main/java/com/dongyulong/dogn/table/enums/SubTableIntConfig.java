package com.dongyulong.dogn.table.enums;

import com.dongyulong.dogn.table.common.IConfig;
import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/88:30 上午
 * @since v1.0
 */
@Getter
@RequiredArgsConstructor
public enum SubTableIntConfig implements IConfig<Integer> {
    /**
     * 分表倍数，控制分表数量
     */
    SLOT_SUB_TABLE("slot_sub_table", 4);

    private final String key;

    private final Integer defValue;

    public int getProperty() {
        return SystemPropertyUtils.getIntProperty(this.getKey(), this.getDefValue());
    }


}
