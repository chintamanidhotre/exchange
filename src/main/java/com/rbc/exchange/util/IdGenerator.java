package com.rbc.exchange.util;

/**
 * ID generator interface
 */
public interface IdGenerator<R> {

    R getNextId();
}
