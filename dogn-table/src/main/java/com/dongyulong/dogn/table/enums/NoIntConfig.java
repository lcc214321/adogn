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
public enum NoIntConfig implements IConfig<EndStart> {

    SOURCE_ID_IDX_YYYY("yyyy", new EndStart().setStartIndx(10).setEndIndx(13)),
    SOURCE_ID_IDX_YY("yy", new EndStart().setStartIndx(8).setEndIndx(11));


    private String key;

    private final EndStart defValue;

    public EndStart getProperty() {
        if (StringUtils.isBlank(this.getKey())) {
            return this.getDefValue();
        }
        return SystemPropertyUtils.getProperty(this.getKey(), EndStart.class, this.getDefValue());
    }
}
