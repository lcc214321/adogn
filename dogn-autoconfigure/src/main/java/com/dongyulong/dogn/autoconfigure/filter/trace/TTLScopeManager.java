package com.dongyulong.dogn.autoconfigure.filter.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;

/**
 * Created by leiyang on 19-5-22.
 */
public class TTLScopeManager implements ScopeManager {
    final TransmittableThreadLocal<TTLScope> ttlScope = new TransmittableThreadLocal<>();

    public TTLScopeManager() {
    }

    @Override
    public Scope activate(Span span, boolean finishOnClose) {
        return new TTLScope(this, span, finishOnClose);
    }

    @Override
    public Scope active() {
        return (Scope) this.ttlScope.get();
    }
}
