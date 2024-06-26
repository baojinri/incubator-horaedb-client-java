/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.options;

import org.apache.horaedb.RouteMode;
import org.apache.horaedb.rpc.RpcClient;
import org.apache.horaedb.common.Copiable;
import org.apache.horaedb.common.Endpoint;

/**
 * Router options.
 *
 */
public class RouterOptions implements Copiable<RouterOptions> {

    private RpcClient rpcClient;
    private Endpoint  clusterAddress;
    private RouteMode routeMode;
    // Specifies the maximum number of routing table caches. When the number reaches the limit, the ones that
    // have not been used for a long time are cleared first
    private int maxCachedSize = 10_000;
    // The frequency at which the route tables garbage collector is triggered. The default is 60 seconds
    private long gcPeriodSeconds = 60;
    // Refresh frequency of route tables. The background refreshes all route tables periodically. By default,
    // all route tables are refreshed every 30 seconds.
    private long refreshPeriodSeconds = 30;

    public RpcClient getRpcClient() {
        return rpcClient;
    }

    public void setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public Endpoint getClusterAddress() {
        return clusterAddress;
    }

    public void setClusterAddress(Endpoint clusterAddress) {
        this.clusterAddress = clusterAddress;
    }

    public int getMaxCachedSize() {
        return maxCachedSize;
    }

    public void setMaxCachedSize(int maxCachedSize) {
        this.maxCachedSize = maxCachedSize;
    }

    public long getGcPeriodSeconds() {
        return gcPeriodSeconds;
    }

    public void setGcPeriodSeconds(long gcPeriodSeconds) {
        this.gcPeriodSeconds = gcPeriodSeconds;
    }

    public long getRefreshPeriodSeconds() {
        return refreshPeriodSeconds;
    }

    public void setRefreshPeriodSeconds(long refreshPeriodSeconds) {
        this.refreshPeriodSeconds = refreshPeriodSeconds;
    }

    public RouteMode getRouteMode() {
        return routeMode;
    }

    public void setRouteMode(RouteMode routeMode) {
        this.routeMode = routeMode;
    }

    @Override
    public RouterOptions copy() {
        final RouterOptions opts = new RouterOptions();
        opts.rpcClient = rpcClient;
        opts.clusterAddress = this.clusterAddress;
        opts.maxCachedSize = this.maxCachedSize;
        opts.gcPeriodSeconds = this.gcPeriodSeconds;
        opts.refreshPeriodSeconds = this.refreshPeriodSeconds;
        opts.routeMode = this.routeMode;
        return opts;
    }

    @Override
    public String toString() {
        return "RouterOptions{" + //
               "rpcClient=" + rpcClient + //
               ", clusterAddress=" + clusterAddress + //
               ", maxCachedSize=" + maxCachedSize + //
               ", gcPeriodSeconds=" + gcPeriodSeconds + //
               ", refreshPeriodSeconds=" + refreshPeriodSeconds + //
               ", routeMode=" + routeMode + //
               '}';
    }

}
