package com.dongyulong.dogn.table.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/811:12 上午
 * @since v1.0
 */
@Data
@Accessors(chain = true)
public class EndStart {

    private int endIndx;

    private int startIndx;
}
