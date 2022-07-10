package com.dongyulong.dogn.autoconfigure.monitor.handle;

import com.dongyulong.dogn.tools.entities.DdcinfoEntity;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/86:28 下午
 * @since v1.0
 */
public class DdcHelper {
    private static ThreadLocal<DdcinfoEntity> local = new ThreadLocal<DdcinfoEntity>();

    public static void setCurrentDdcInfo(DdcinfoEntity entity) {
        local.set(entity);
    }

    public static DdcinfoEntity getCurrentDdcInfo(){
        DdcinfoEntity ddcinfoEntity = local.get();
        if(ddcinfoEntity!=null){
            if(ddcinfoEntity.getLatitude()==null){
                ddcinfoEntity.setLatitude("0");
            }
            if(ddcinfoEntity.getLongitude()==null){
                ddcinfoEntity.setLatitude("0");
            }
        }
        return ddcinfoEntity;
    }
}
