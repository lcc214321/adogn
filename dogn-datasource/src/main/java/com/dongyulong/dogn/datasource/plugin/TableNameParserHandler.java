package com.dongyulong.dogn.datasource.plugin;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * 按用户的id取模分表处理器
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/5/154:40 下午
 * @since v1.0
 */
public class TableNameParserHandler implements TableNameHandler {

    /**
     * 生成分表后缀的方法
     */
    @Setter
    private Supplier<String> supplier;

    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (supplier == null) {
            return tableName;
        }
        String suffix = supplier.get();
        if (StringUtils.isBlank(suffix)) {
            return tableName;
        }
        tableName = StringUtils.replace(tableName, "`", StringUtils.EMPTY);
        return tableName + suffix;
    }


}
