package com.dongyulong.dogn.datasource.entities;

import com.dongyulong.dogn.datasource.enums.DatabaseEnum;
import com.dongyulong.dogn.datasource.enums.DatabaseTypeEnum;
import com.dongyulong.dogn.datasource.enums.IDatabase;
import com.dongyulong.dogn.datasource.routing.Routing;
import lombok.Builder;
import lombok.Data;

import java.util.Locale;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/81:48 下午
 * @since v1.0
 */
@Data
@Builder
public class TargetDataSource {

    private DatabaseTypeEnum databaseType;

    private IDatabase database;

    /**
     * 分库编号
     */
    private Routing routing;

    @Override
    public String toString() {
        if (databaseType == null) {
            databaseType = DatabaseTypeEnum.master;
        }
        if (database == null) {
            database = DatabaseEnum.payment;
        }
        return String.format("%s_%s", this.database.name(), this.databaseType.name().toLowerCase(Locale.ROOT));
    }

    public String toString(String routingKey) {
        if (databaseType == null) {
            databaseType = DatabaseTypeEnum.master;
        }
        if (database == null) {
            database = DatabaseEnum.payment;
        }
        return String.format("%s_%s_%s", this.database.name(), this.databaseType.name().toLowerCase(Locale.ROOT), routingKey);
    }

    public static TargetDataSource buildDefault() {
        return TargetDataSource.builder()
                .databaseType(DatabaseTypeEnum.master)
                .database(DatabaseEnum.payment)
                .build();
    }

}
