package com.rbc.exchange.integration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to test asserts.
 */
public class ExpectedResult {

    private Map<BigDecimal, Long> openIntBuy;
    private Map<BigDecimal, Long> openIntSell;
    private BigDecimal avgExecPrice;
    private long u1ExeQty;
    private long u2ExeQty;

    private static Map<Long, ExpectedResult> expectedResultMap;

    public ExpectedResult(Map<BigDecimal, Long> openIntBuy, Map<BigDecimal, Long> openIntSell, BigDecimal avgExecPrice, long u1ExeQty, long u2ExeQty) {
        this.openIntBuy = openIntBuy;
        this.openIntSell = openIntSell;
        this.avgExecPrice = avgExecPrice;
        this.u1ExeQty = u1ExeQty;
        this.u2ExeQty = u2ExeQty;
    }

    public static void initialiseExpectedResults() {
        expectedResultMap = new HashMap<>();
        Map<BigDecimal, Long> openIntSell = new HashMap<>();
        openIntSell.put(new BigDecimal("200.1"), 3000L);
        openIntSell.put(new BigDecimal("202"), 1500L);
        openIntSell.put(new BigDecimal("198"), 1000L);
        expectedResultMap.put(1L, new ExpectedResult(Collections.EMPTY_MAP, openIntSell, BigDecimal.ZERO, 0, 0));

        Map<BigDecimal, Long> openIntBuy = new HashMap<>();
        openIntBuy.put(new BigDecimal("203"), 1500L);
        openIntBuy.put(new BigDecimal("201"), 2000L);
        openIntBuy.put(new BigDecimal("199"), 3000L);
        openIntBuy.put(new BigDecimal("202.2"), 4000L);
        expectedResultMap.put(2L, new ExpectedResult(openIntBuy, Collections.EMPTY_MAP, new BigDecimal("202.0000"), -1000, 1000));

        expectedResultMap.put(3L, new ExpectedResult(openIntBuy, Collections.EMPTY_MAP, new BigDecimal("202.0000"), -1000, 1000));

        openIntSell = new HashMap<>(); openIntSell.put(new BigDecimal("102"), 1500L);
        expectedResultMap.put(4L, new ExpectedResult(openIntBuy, openIntSell, new BigDecimal("100.2000"), 1500, 1000));

        expectedResultMap.put(5L, new ExpectedResult(openIntBuy, Collections.EMPTY_MAP, new BigDecimal("101.1333"), 1500, -500));

        openIntBuy = new HashMap<>(); openIntBuy.put(new BigDecimal("99"), 1000L);
        expectedResultMap.put(6L, new ExpectedResult(openIntBuy, Collections.EMPTY_MAP, new BigDecimal("99.8800"), 500, -1500));
    }

    public static Map<Long, ExpectedResult> getExpectedResultMap() {
        return expectedResultMap;
    }

    public Map<BigDecimal, Long> getOpenIntBuy() {
        return openIntBuy;
    }

    public Map<BigDecimal, Long> getOpenIntSell() {
        return openIntSell;
    }

    public BigDecimal getAvgExecPrice() {
        return avgExecPrice;
    }

    public long getU1ExeQty() {
        return u1ExeQty;
    }

    public long getU2ExeQty() {
        return u2ExeQty;
    }

    @Override
    public String toString() {
        return "ExpectedResult{" +
                "openIntBuy=" + openIntBuy +
                ", openIntSell=" + openIntSell +
                ", avgExecPrice=" + avgExecPrice +
                ", u1ExeQty=" + u1ExeQty +
                ", u2ExeQty=" + u2ExeQty +
                '}';
    }
}
