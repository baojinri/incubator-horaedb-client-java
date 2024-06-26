/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.models;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ResultTest {

    @Test
    public void mapTest() throws Exception {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final Result<Integer, Err> r2 = r1.map(WriteOk::getSuccess);
        Assert.assertEquals(2, r2.getOk().intValue());

        final Result<SqlQueryOk, Err> r3 = Result.ok(SqlQueryOk.ok(null, 0, mockRows(5)));
        final Result<Integer, Err> r4 = r3.map(SqlQueryOk::getRowCount);
        Assert.assertEquals(5, r4.getOk().intValue());

        final Result<SqlQueryOk, Err> r5 = Result.ok(SqlQueryOk.ok(null, 1, null));
        final Result<Integer, Err> r6 = r5.map(SqlQueryOk::getAffectedRows);
        Assert.assertEquals(1, r6.getOk().intValue());

        final Result<WriteOk, Err> r7 = Result.err(Err.writeErr(400, null, null, null));
        final Result<Integer, Err> r8 = r7.map(WriteOk::getSuccess);
        Assert.assertFalse(r8.isOk());
    }

    @Test
    public void mapOrTest() {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final Integer r2 = r1.mapOr(-1, WriteOk::getSuccess);
        Assert.assertEquals(2, r2.intValue());

        final Result<WriteOk, Err> r3 = Result.err(Err.writeErr(400, null, null, null));
        final Integer r4 = r3.mapOr(-1, WriteOk::getSuccess);
        Assert.assertEquals(-1, r4.intValue());
    }

    @Test
    public void mapOrElseTest() {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final Integer r2 = r1.mapOrElse(err -> -1, WriteOk::getSuccess);
        Assert.assertEquals(2, r2.intValue());

        final Result<WriteOk, Err> r3 = Result.err(Err.writeErr(400, null, null, null));
        final Integer r4 = r3.mapOrElse(err -> -1, WriteOk::getSuccess);
        Assert.assertEquals(-1, r4.intValue());
    }

    @Test
    public void mapErrTest() {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final Result<WriteOk, String> r2 = r1.mapErr(Err::getError);
        Assert.assertEquals(2, r2.getOk().getSuccess());

        final Result<WriteOk, Err> r3 = Result.err(Err.writeErr(400, "error test", null, null));
        final Result<WriteOk, String> r4 = r3.mapErr(Err::getError);
        Assert.assertEquals("error test", r4.getErr());
    }

    @Test
    public void andThenTest() {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final Result<WriteOk, Err> r2 = r1.andThen(writeOk -> {
            writeOk.setSuccess(writeOk.getSuccess() + 1);
            return writeOk.mapToResult();
        });
        Assert.assertEquals(3, r2.getOk().getSuccess());

        final Result<WriteOk, Err> r3 = Result.err(Err.writeErr(400, null, null, null));
        final Result<WriteOk, Err> r4 = r3.andThen(writeOk -> {
            writeOk.setSuccess(writeOk.getSuccess() + 1);
            return writeOk.mapToResult();
        });
        Assert.assertFalse(r4.isOk());
    }

    @Test
    public void orElseTest() {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final Result<WriteOk, String> r2 = r1.orElse(err -> Result.ok(WriteOk.ok(0, 0, null)));
        Assert.assertEquals(2, r2.getOk().getSuccess());

        final Result<WriteOk, Err> r3 = Result.err(Err.writeErr(400, null, null, null));
        final Result<WriteOk, String> r4 = r3.orElse(err -> Result.ok(WriteOk.ok(0, 0, null)));
        Assert.assertEquals(0, r4.getOk().getSuccess());
    }

    @Test
    public void unwrapOrTest() {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final WriteOk r2 = r1.unwrapOr(WriteOk.emptyOk());
        Assert.assertEquals(2, r2.getSuccess());

        final Result<WriteOk, Err> r3 = Result.err(Err.writeErr(400, null, null, null));
        final WriteOk r4 = r3.unwrapOr(WriteOk.emptyOk());
        Assert.assertEquals(0, r4.getSuccess());
    }

    @Test
    public void unwrapOrElseTest() {
        final Result<WriteOk, Err> r1 = Result.ok(WriteOk.ok(2, 0, null));
        final WriteOk r2 = r1.unwrapOrElse(err -> WriteOk.emptyOk());
        Assert.assertEquals(2, r2.getSuccess());

        final Result<WriteOk, Err> r3 = Result.err(Err.writeErr(400, null, null, null));
        final WriteOk r4 = r3.unwrapOrElse(err -> WriteOk.emptyOk());
        Assert.assertEquals(0, r4.getSuccess());
    }

    private List<Row> mockRows(final int rowCount) {
        List rows = new ArrayList(rowCount);
        for (int i = 0; i < rowCount; i++) {
            rows.add(new Row());
        }
        return rows;
    }
}
