package com.dongyulong.dogn.datasource.plugin;

import com.dongyulong.dogn.datasource.routing.Routing;
import com.dongyulong.dogn.datasource.entities.TargetDataSource;
import com.dongyulong.dogn.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * agaue
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/5/157:31 下午
 * @since v1.0
 */
@Slf4j
@RequiredArgsConstructor
public class IdParserContextHolder implements Supplier<String> {

    private final static String DEFAULT_SPACER = "_";

    private final IntFunction<Long> function;

    @Override
    public String get() {
        TargetDataSource dataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
        if (dataSourceType == null) {
            return StringUtils.EMPTY;
        }
        Routing routing = dataSourceType.getRouting();
        if (routing == null) {
            return StringUtils.EMPTY;
        }
        Integer idValue = routing.getUserId();
        if (idValue == null || idValue == 0) {
            return StringUtils.EMPTY;
        }
        long suffix = function.apply(idValue);
        if (suffix == 0) {
            return StringUtils.EMPTY;
        }
        log.debug("解析表后缀为:{} idValue:{}", DEFAULT_SPACER + suffix, idValue);
        return DEFAULT_SPACER + suffix;
    }

}
