/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.horaedb.models.Err;
import org.apache.horaedb.models.SqlQueryOk;
import org.apache.horaedb.models.Row;
import org.apache.horaedb.models.SqlQueryRequest;
import org.apache.horaedb.models.Result;
import org.apache.horaedb.rpc.Context;
import org.apache.horaedb.rpc.Observer;

/**
 * The query API for CeresDB client.
 *
 */
public interface Query {

    /**
     * @see #sqlQuery(SqlQueryRequest, Context)
     */
    default CompletableFuture<Result<SqlQueryOk, Err>> sqlQuery(final SqlQueryRequest req) {
        return sqlQuery(req, Context.newDefault());
    }

    /**
     * According to the conditions, query data from the database.
     *
     * @param req the query request
     * @param ctx the invoke context
     * @return query result
     */
    CompletableFuture<Result<SqlQueryOk, Err>> sqlQuery(final SqlQueryRequest req, final Context ctx);

    /**
     * @see #streamSqlQuery(SqlQueryRequest, Context, Observer)
     */
    default void streamSqlQuery(final SqlQueryRequest req, final Observer<SqlQueryOk> observer) {
        streamSqlQuery(req, Context.newDefault(), observer);
    }

    /**
     * Executes a stream-query-call with a streaming response.
     *
     * @param req      the query request
     * @param observer receives data from an observable stream
     * @param ctx      the invoke context
     */
    void streamSqlQuery(final SqlQueryRequest req, final Context ctx, final Observer<SqlQueryOk> observer);

    /**
     * @see #blockingStreamSqlQuery(SqlQueryRequest, long, TimeUnit, Context)
     */
    default Iterator<Row> blockingStreamSqlQuery(final SqlQueryRequest req, //
                                                 final long timeout, //
                                                 final TimeUnit unit) {
        return blockingStreamSqlQuery(req, timeout, unit, Context.newDefault());
    }

    /**
     * Executes a stream-query-call with a streaming response.
     *
     * @param req     the query request
     * @param timeout how long to wait {@link Iterator#hasNext()} before giving up, in units of unit
     * @param unit    a TimeUnit determining how to interpret the timeout parameter
     * @param ctx     the invoke context
     * @return the iterator of record
     */
    default Iterator<Row> blockingStreamSqlQuery(final SqlQueryRequest req, //
                                                 final long timeout, //
                                                 final TimeUnit unit, //
                                                 final Context ctx) {
        final BlockingStreamIterator streams = new BlockingStreamIterator(timeout, unit);
        streamSqlQuery(req, ctx, streams.getObserver());
        return new RowIterator(streams);
    }
}
