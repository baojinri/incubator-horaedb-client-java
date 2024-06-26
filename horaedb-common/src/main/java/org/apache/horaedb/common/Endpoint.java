/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.common;

import java.io.Serializable;

import org.apache.horaedb.common.util.Strings;

/**
 * A IP address with port.
 *
 */
public class Endpoint implements Serializable {

    private static final long serialVersionUID = -7329681263115546100L;

    @SuppressWarnings("PMD")
    private String ip = "0.0.0.0";
    private int    port;

    public static Endpoint of(final String ip, final int port) {
        return new Endpoint(ip, port);
    }

    public static Endpoint parse(final String s) {
        if (Strings.isNullOrEmpty(s)) {
            return null;
        }

        final String[] arr = Strings.split(s, ':');

        if (arr == null || arr.length < 2) {
            return null;
        }

        try {
            final int port = Integer.parseInt(arr[1]);
            return Endpoint.of(arr[0], port);
        } catch (final Exception ignored) {
            return null;
        }
    }

    public Endpoint() {
        super();
    }

    public Endpoint(String address, int port) {
        super();
        this.ip = address;
        this.port = port;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return this.ip + ":" + this.port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.ip == null ? 0 : this.ip.hashCode());
        result = prime * result + this.port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Endpoint other = (Endpoint) obj;
        if (this.ip == null) {
            if (other.ip != null) {
                return false;
            }
        } else if (!this.ip.equals(other.ip)) {
            return false;
        }
        return this.port == other.port;
    }
}
