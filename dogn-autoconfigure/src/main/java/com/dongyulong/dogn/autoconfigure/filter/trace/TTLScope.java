package com.dongyulong.dogn.autoconfigure.filter.trace;

import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * Created by leiyang on 19-5-22.
 */
public class TTLScope implements Scope {

    private final TTLScopeManager scopeManager;
    private final Span wrapped;
    private final boolean finishOnClose;
    private final TTLScope toRestore;

    TTLScope(TTLScopeManager scopeManager, Span wrapped, boolean finishOnClose) {
        this.scopeManager = scopeManager;
        this.wrapped = wrapped;
        this.finishOnClose = finishOnClose;
        this.toRestore = (TTLScope)scopeManager.ttlScope.get();
        scopeManager.ttlScope.set(this);
    }

    @Override
    public void close() {
        if(this.scopeManager.ttlScope.get() == this) {
            if(this.finishOnClose) {
                this.wrapped.finish();
            }

            this.scopeManager.ttlScope.set(this.toRestore);
        }
    }

    @Override
    public Span span() {
        return this.wrapped;
    }
}
