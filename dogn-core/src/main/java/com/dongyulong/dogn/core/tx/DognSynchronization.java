package com.dongyulong.dogn.core.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存支持事务级别
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/96:28 上午
 * @since v1.0
 */
public class DognSynchronization extends TransactionSynchronizationAdapter {
    private final static Logger logger = LoggerFactory.getLogger(DognSynchronization.class);
    private final List<ITxCallback> list = new ArrayList<ITxCallback>();
    private final String transatctionName;
    private Map<String, Object> localTxCache = new LinkedHashMap<String, Object>();

    public DognSynchronization(String transatctionName) {
        this.transatctionName = transatctionName;
    }

    public String getTransatctionName() {
        return transatctionName;
    }

    public void reset() {
        list.clear();
        localTxCache.clear();
    }

    public void addCallback(ITxCallback callback) {
        if (!list.contains(callback)) {
            list.add(callback);
        }
    }

    public Map<String, Object> getLocalTxCache() {
        return this.localTxCache;
    }

    @Override
    public void afterCompletion(int status) {
        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        if (!this.transatctionName.equals(currentTransactionName)) {
            return;
        }
        try {
            for (ITxCallback callback : list) {
                handleCallback(callback, status);
            }
        } finally {
            //重置线程变量，防止线程复用时出问题
            for (ITxCallback callback : list) {
                try {
                    if (callback instanceof ITxCompletedCallback) {
                        ((ITxCompletedCallback) callback).afterCompletion();
                    }
                } catch (Exception e) {
                    logger.error("handle call back after tx completion failed.", e);
                }
            }
            TransactionExtHelper.resetSynchronization();
        }
    }

    private void handleCallback(ITxCallback callback, int status) {
        switch (status) {
            case TransactionSynchronization.STATUS_COMMITTED://提交
                //事务成功提交后才更新缓存
                logger.debug("after commit in tx synchronization.");
                try {
                    if (callback instanceof ITxCommittedCallback) {
                        ((ITxCommittedCallback) callback).afterCommitted();
                    }
                } catch (Exception e) {
                    logger.error("handle call back after tx committed failed.", e);
                }
                break;
            case TransactionSynchronization.STATUS_ROLLED_BACK:
                //事务回滚
                logger.debug("after roll back in tx synchronization.");
                try {
                    if (callback instanceof ITxRolledbackCallback) {
                        ((ITxRolledbackCallback) callback).afterRolledback();
                    }
                } catch (Exception e) {
                    logger.error("handle call back after tx rollbacked failed.", e);
                }
                break;
            //case TransactionSynchronization.STATUS_UNKNOWN:
            //	break;
            default:
                logger.debug(String.format(
                        "after completion[%d] in tx synchronization.",
                        status));
                break;
        }
    }

}
