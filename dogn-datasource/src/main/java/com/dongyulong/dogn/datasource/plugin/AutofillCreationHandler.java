package com.dongyulong.dogn.datasource.plugin;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * mybatis-plus 自动填充创建时间和修改时间处理器
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/5/26 6:34 下午
 * @since v1.0
 */
@RequiredArgsConstructor
public class AutofillCreationHandler implements MetaObjectHandler {

    private final String createTimeFieldName;
    private final String updateTimeFieldName;

    public String getCreateTimeFieldName() {
        return StringUtils.isBlank(createTimeFieldName) ? "createTime" : createTimeFieldName;
    }

    public String getUpdateTimeFieldName() {
        return StringUtils.isBlank(updateTimeFieldName) ? "updateTime" : updateTimeFieldName;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = metaObject.getValue(getCreateTimeFieldName());
        if (createTime == null) {
            this.setFieldValByName(getCreateTimeFieldName(), new Date(), metaObject);
        }
        Object updateTime = metaObject.getValue(getUpdateTimeFieldName());
        if (updateTime == null) {
            this.setFieldValByName(getUpdateTimeFieldName(), new Date(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updateTime = metaObject.getValue(getUpdateTimeFieldName());
        if (updateTime == null) {
            this.setFieldValByName(getUpdateTimeFieldName(), new Date(), metaObject);
        }
    }
}
