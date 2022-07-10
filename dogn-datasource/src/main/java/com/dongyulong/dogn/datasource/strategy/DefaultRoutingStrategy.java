package com.dongyulong.dogn.datasource.strategy;

import com.dongyulong.dogn.datasource.entities.TargetDataSource;
import org.springframework.stereotype.Service;

/**
 * @author dongy
 * @version v2.0.1
 * @date 11:12 2022/1/5
 **/
@Service
public final class DefaultRoutingStrategy implements IRoutingStrategy {

    @Override
    public String determineKey(TargetDataSource targetDataSource) {
        return targetDataSource.toString();
    }

}
