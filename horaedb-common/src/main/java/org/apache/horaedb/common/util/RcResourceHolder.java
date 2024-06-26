/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.common.util;

import java.util.IdentityHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RcResourceHolder<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RcResourceHolder.class);

    private final Map<ObjectPool.Resource<T>, Instance<T>> instances = new IdentityHashMap<>();

    public synchronized T get(final ObjectPool.Resource<T> resource) {
        Instance<T> ins = this.instances.get(resource);
        if (ins == null) {
            ins = new Instance<>(resource.create());
            this.instances.put(resource, ins);
            LOG.info("[RcResourceHolder] create instance: {}.", ins);
        }
        ins.inc();
        return ins.payload();
    }

    public synchronized void release(final ObjectPool.Resource<T> resource, final T returned) {
        final Instance<T> cached = this.instances.get(resource);
        Requires.requireNonNull(cached, "No cached instance found for " + resource);
        Requires.requireTrue(returned == cached.payload(), "Releasing the wrong instance, expected=%s, actual=%s",
                cached.payload(), returned);
        Requires.requireTrue(cached.rc() > 0, "RefCount has already reached zero");
        if (cached.decAndGet() == 0) {
            LOG.info("[RcResourceHolder] close instance: {}.", cached);
            resource.close(cached.payload());
            this.instances.remove(resource);
        }
    }

    private static class Instance<T> {
        final T payload;
        int     refCount;
        int     maxRefCount;

        Instance(T payload) {
            this.payload = payload;
        }

        void inc() {
            this.refCount++;
            this.maxRefCount = Math.max(this.maxRefCount, this.refCount);
        }

        int decAndGet() {
            return --this.refCount;
        }

        int rc() {
            return this.refCount;
        }

        T payload() {
            return this.payload;
        }

        @Override
        public String toString() {
            return "Instance{" + //
                   "payload=" + payload + //
                   ", refCount=" + refCount + //
                   ", maxRefCount=" + maxRefCount + //
                   '}';
        }
    }
}
