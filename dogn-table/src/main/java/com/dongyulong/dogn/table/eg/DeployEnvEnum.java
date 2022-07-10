package com.dongyulong.dogn.table.eg;

import com.dongyulong.dogn.table.common.IEnvNo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/810:34 上午
 * @since v1.0
 */
@Getter
@RequiredArgsConstructor
public enum DeployEnvEnum implements IEnvNo {

    DEV(0),
    ALIYUN(1),
    PRODUCTION(2);

    private final int no;


    @Override
    public String getEnvKey() {
        return this.name();
    }

}
