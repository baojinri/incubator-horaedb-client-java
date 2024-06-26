/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.rpc.interceptors;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

import org.apache.horaedb.rpc.Context;

/**
 * Add RPC context to Grpc headers.
 *
 */
public class ContextToHeadersInterceptor implements ClientInterceptor {

    private static final ThreadLocal<Context> CURRENT_CTX = new ThreadLocal<>();

    public static void setCurrentCtx(final Context ctx) {
        CURRENT_CTX.set(ctx);
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method, //
                                                               final CallOptions callOpts, //
                                                               final Channel next) {
        return new HeaderAttachingClientCall<>(next.newCall(method, callOpts));
    }

    private static final class HeaderAttachingClientCall<ReqT, RespT>
            extends ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT> {

        // Non private to avoid synthetic class
        HeaderAttachingClientCall(ClientCall<ReqT, RespT> delegate) {
            super(delegate);
        }

        @Override
        public void start(final Listener<RespT> respListener, final Metadata headers) {
            final Context ctx = CURRENT_CTX.get();
            if (ctx != null) {
                ctx.entrySet().forEach(e -> headers.put( //
                        Metadata.Key.of(e.getKey(), Metadata.ASCII_STRING_MARSHALLER), //
                        String.valueOf(e.getValue())) //
                );
            }
            CURRENT_CTX.remove();
            super.start(respListener, headers);
        }
    }
}
