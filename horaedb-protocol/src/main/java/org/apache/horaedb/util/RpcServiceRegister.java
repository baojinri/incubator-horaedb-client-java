/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.util;

import org.apache.horaedb.proto.internal.Storage;
import org.apache.horaedb.rpc.MethodDescriptor;
import org.apache.horaedb.rpc.RpcFactoryProvider;
import org.apache.horaedb.common.OptKeys;
import org.apache.horaedb.common.util.SystemPropertyUtil;

public class RpcServiceRegister {

    private static final double WRITE_LIMIT_PERCENT = writeLimitPercent();

    private static final String STORAGE_METHOD_TEMPLATE = "storage.StorageService/%s";

    public static void registerStorageService() {
        // register protobuf serializer
        RpcFactoryProvider.getRpcFactory().register(
                MethodDescriptor.of(String.format(STORAGE_METHOD_TEMPLATE, "Route"), MethodDescriptor.MethodType.UNARY),
                //
                Storage.RouteRequest.class, //
                Storage.RouteRequest.getDefaultInstance(), //
                Storage.RouteResponse.getDefaultInstance());
        RpcFactoryProvider.getRpcFactory().register(
                MethodDescriptor.of(String.format(STORAGE_METHOD_TEMPLATE, "Write"), MethodDescriptor.MethodType.UNARY,
                        WRITE_LIMIT_PERCENT), //
                Storage.WriteRequest.class, //
                Storage.WriteRequest.getDefaultInstance(), //
                Storage.WriteResponse.getDefaultInstance());
        RpcFactoryProvider.getRpcFactory().register(
                MethodDescriptor.of(String.format(STORAGE_METHOD_TEMPLATE, "StreamWrite"),
                        MethodDescriptor.MethodType.CLIENT_STREAMING), //
                Storage.WriteRequest.class, //
                Storage.WriteRequest.getDefaultInstance(), //
                Storage.WriteResponse.getDefaultInstance());
        RpcFactoryProvider.getRpcFactory().register(
                MethodDescriptor.of(String.format(STORAGE_METHOD_TEMPLATE, "SqlQuery"),
                        MethodDescriptor.MethodType.UNARY, 1 - WRITE_LIMIT_PERCENT), //
                Storage.SqlQueryRequest.class, //
                Storage.SqlQueryRequest.getDefaultInstance(), //
                Storage.SqlQueryResponse.getDefaultInstance());
        RpcFactoryProvider.getRpcFactory().register(
                MethodDescriptor.of(String.format(STORAGE_METHOD_TEMPLATE, "StreamSqlQuery"),
                        MethodDescriptor.MethodType.SERVER_STREAMING), //
                Storage.SqlQueryRequest.class, //
                Storage.SqlQueryRequest.getDefaultInstance(), //
                Storage.SqlQueryResponse.getDefaultInstance());
    }

    private static double writeLimitPercent() {
        try {
            return Math.min(1.0, Double.parseDouble(SystemPropertyUtil.get(OptKeys.WRITE_LIMIT_PERCENT, "0.7")));
        } catch (final Throwable ignored) {
            return 0.7;
        }
    }
}
