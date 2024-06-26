/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.common.util;

import org.apache.horaedb.common.Endpoint;
import org.junit.Assert;
import org.junit.Test;

public class EndpointTest {

    @Test
    public void parseTest() {
        final Endpoint ep = Endpoint.parse("192.168.1.1:18091");
        Assert.assertEquals(Endpoint.of("192.168.1.1", 18091), ep);
    }
}
