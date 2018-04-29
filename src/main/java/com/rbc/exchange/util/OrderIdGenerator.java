package com.rbc.exchange.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Distinct Object Id generator class.
 */
public class OrderIdGenerator implements IdGenerator<Long>{

    private AtomicLong id = new AtomicLong(0);

    public Long getNextId() {
        return Long.valueOf(id.incrementAndGet());
    }

}
