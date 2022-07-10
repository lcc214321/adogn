package com.dongyulong.dogn.table.enums;

import com.dongyulong.dogn.table.common.EndStart;
import com.dongyulong.dogn.table.common.IConfig;
import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/811:09 上午
 * @since v1.0
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum NoArrayConfig implements IConfig<String[]> {

    IP_ADDRESS_ARRAY("ip_address_array");


    private final String key;

    private String[] defValue;

    public String[] getProperty() {
        if (StringUtils.isBlank(this.getKey())) {
            return this.getDefValue();
        }
        return SystemPropertyUtils.getProperty(this.getKey(), String[].class, this.getDefValue());
    }
}
