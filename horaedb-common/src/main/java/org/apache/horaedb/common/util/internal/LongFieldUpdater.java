/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.common.util.internal;

public interface LongFieldUpdater<U> {

    void set(final U obj, final long newValue);

    long get(final U obj);
}
