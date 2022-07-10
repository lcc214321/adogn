package com.dongyulong.dogn.datasource.autoconfigure;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.dongyulong.dogn.datasource.autoconfigure.sqlsessionfactory.MybatisPlusSqlSessionFactoryProvider;
import com.dongyulong.dogn.datasource.plugin.AutofillCreationHandler;
import com.dongyulong.dogn.datasource.plugin.IdParserContextHolder;
import com.dongyulong.dogn.datasource.plugin.TableNameParserHandler;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.IntFunction;

/**
 * agaue
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/5/168:13 上午
 * @since v1.0
 */
@Configuration
@ConditionalOnClass({MybatisPlusInterceptor.class, DynamicTableNameInnerInterceptor.class, MetaObjectHandler.class})
@ConditionalOnBean(MybatisPlusSqlSessionFactoryProvider.class)
@ImportAutoConfiguration(DynamicDataSourceProperties.class)
public class MybatisPlusInterceptorAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = {"dynamicTableName"}, havingValue = "true")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        TableNameParserHandler tableNameParserHandler = new TableNameParserHandler();
        tableNameParserHandler.setSupplier(new IdParserContextHolder(new IntFunction<Long>() {
            @Override
            public Long apply(int value) {
                //TODO
                return null;
            }
        }));
        dynamicTableNameInnerInterceptor.setTableNameHandler(tableNameParserHandler);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = {"autofillDate"}, havingValue = "true")
    public AutofillCreationHandler autofillCreationDateHandler(DynamicDataSourceProperties dynamicDataSourceProperties) {
        return new AutofillCreationHandler(dynamicDataSourceProperties.getCreateTimeFieldName(), dynamicDataSourceProperties.getUpdateTimeFieldName());
    }

}
