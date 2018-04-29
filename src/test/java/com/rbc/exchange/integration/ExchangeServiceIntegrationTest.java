package com.rbc.exchange.integration;

import com.rbc.exchange.dao.OrderDao;
import com.rbc.exchange.model.Order;
import com.rbc.exchange.model.Side;
import com.rbc.exchange.model.User;
import com.rbc.exchange.service.ExchangeService;
import com.rbc.exchange.service.ExchangeServiceImpl;
import com.rbc.exchange.util.OrderIdGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import static com.rbc.exchange.BaseOrderTest.RIC;
import static org.junit.Assert.assertEquals;

/**
 * Integration test for ExchangeService.
 */
public class ExchangeServiceIntegrationTest {

    private ExchangeService exchangeService;
    private OrderIdGenerator orderIdGenerator;

    private static final int ARRAY_LENGTH = 5;


    @Before
    public void setUp() throws URISyntaxException, IOException {
        OrderDao orderDao = new OrderDao();
        exchangeService = new ExchangeServiceImpl(orderDao);
        orderIdGenerator = new OrderIdGenerator();
        Stream<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource("orders.txt").toURI()));
        lines.forEach(line -> {
            String[] split = line.split("\\|");
            assertEquals(ARRAY_LENGTH, split.length);
            User user = new User(Long.parseLong(split[4]));
            Order order = new Order(orderIdGenerator, Side.valueOf(split[0]), split[2], Long.parseLong(split[1]), new BigDecimal(split[3]), user);
            exchangeService.addOrder(order);
        });
    }

    @Test
    public void testExchangeServiceImplementation() {
        ExpectedResult.initialiseExpectedResults();
        Map<Long, ExpectedResult> expectedResultMap = ExpectedResult.getExpectedResultMap();

        assertOpenInt(expectedResultMap.get(1L).getOpenIntSell(), exchangeService.getOpenInterest(RIC, Side.SELL));
        assertOpenInt(expectedResultMap.get(2L).getOpenIntBuy(), exchangeService.getOpenInterest(RIC, Side.BUY));
        assertAvgExePrice(expectedResultMap.get(3L).getAvgExecPrice(), exchangeService.getAverageExecutionPrice(RIC));
        assertExeQuantity(expectedResultMap.get(4L).getU1ExeQty(), exchangeService.getExecutedQuantity(RIC, new User(1)));
        assertExeQuantity(expectedResultMap.get(5L).getU1ExeQty(), exchangeService.getExecutedQuantity(RIC, new User(1)));
        assertExeQuantity(expectedResultMap.get(6L).getU2ExeQty(), exchangeService.getExecutedQuantity(RIC, new User(2)));

    }
    private void assertOpenInt(Map<BigDecimal, Long> expectedValue , Map<BigDecimal, Long> openIntResult){
        assertEquals(expectedValue, openIntResult);
    }

    private void assertAvgExePrice(BigDecimal expectedValue, BigDecimal actualValue) {
        assertEquals(expectedValue, actualValue);
    }

    private void assertExeQuantity(long expectedValue, long actualValue) {
        assertEquals(expectedValue, actualValue);
    }
}
