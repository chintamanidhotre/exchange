package com.rbc.exchange.dao;

import com.rbc.exchange.BaseOrderTest;
import com.rbc.exchange.model.Side;
import com.rbc.exchange.model.Order;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static com.rbc.exchange.model.Side.BUY;
import static com.rbc.exchange.model.Side.SELL;
import static org.junit.Assert.*;

/**
 * Unit test for Order DAO.
 */
public class OrderDaoTest extends BaseOrderTest {

    private OrderDao orderDao = new OrderDao();


    @Before
    public void setUp(){

        orderDao.addOrder(createOrder("VOD.L", SELL, 100, BigDecimal.TEN, 1001));
        orderDao.addOrder(createOrder("VOD.L", BUY, 103, BigDecimal.TEN, 1002));
        orderDao.addOrder(createOrder("VOD.L", SELL, 101, BigDecimal.TEN, 1001));
        orderDao.addOrder(createOrder("VOD.L", BUY, 105, BigDecimal.TEN, 1004));
        orderDao.addOrder(createOrder("VOD.L", BUY, 102, BigDecimal.TEN, 1001));

        orderDao.addOrder(createOrder("MSFT.OQ", BUY, 200, BigDecimal.TEN, 1002));

        orderDao.addOrder(createOrder("INFY.NS", SELL, 300, BigDecimal.TEN, 1004));

        orderDao.addOrder(createOrder("APPL.O", SELL, 300, BigDecimal.TEN, 1003));
        orderDao.addOrder(createOrder("APPL.O", SELL, 400, BigDecimal.TEN, 1002));

        orderDao.addOrder(createOrder("TATA.NS", BUY, 500, BigDecimal.TEN, 1005));
        orderDao.addOrder(createOrder("TATA.NS", BUY, 600, BigDecimal.TEN, 1006));

    }

    @Test
    public void testTotalRicsInOrderBook() {
        Map<String, Map<Side, Queue<Order>>> orderBook = orderDao.getOrderBook();
        assertEquals(5,orderBook.size());
    }

    @Test
    public void testTotalOrdersByRicAndSideWhenBothBuyAndSellAvailable() {
        Optional<Queue<Order>> orderBookBuy = orderDao.getOrderBookByRicAndSide("VOD.L", BUY);
        assertTrue(orderBookBuy.isPresent());
        Queue<Order> orderQueueBuy = orderBookBuy.get();
        assertEquals(3,orderQueueBuy.size());

        Optional<Queue<Order>> orderBookSell = orderDao.getOrderBookByRicAndSide("VOD.L", SELL);
        assertTrue(orderBookSell.isPresent());
        Queue<Order> orderQueueSell = orderBookSell.get();
        assertEquals(2,orderQueueSell.size());

    }

    @Test
    public void testTotalOrdersByRicAndSideWhenMulitpleBuyAvailable() {
        Optional<Queue<Order>> orderBook = orderDao.getOrderBookByRicAndSide("TATA.NS", BUY);
        assertTrue(orderBook.isPresent());
        Queue<Order> orderQueue = orderBook.get();
        assertEquals(2,orderQueue.size());
    }

    @Test
    public void testTotalOrdersByRicAndSideWhenSingleBuyAvailable() {
        Optional<Queue<Order>> orderBook = orderDao.getOrderBookByRicAndSide("MSFT.OQ", BUY);
        assertTrue(orderBook.isPresent());
        Queue<Order> orderQueue = orderBook.get();
        assertEquals(1,orderQueue.size());
    }

    @Test
    public void testTotalOrdersByRicAndSideWhenMulitpleSellAvailable() {
        Optional<Queue<Order>> orderBook = orderDao.getOrderBookByRicAndSide("APPL.O", SELL);
        assertTrue(orderBook.isPresent());
        Queue<Order> orderQueue = orderBook.get();
        assertEquals(2,orderQueue.size());
    }

    @Test
    public void testTotalOrdersByRicAndSideWhenSingleSellAvailable() {
        Optional<Queue<Order>> orderBook = orderDao.getOrderBookByRicAndSide("INFY.NS", SELL);
        assertTrue(orderBook.isPresent());
        Queue<Order> orderQueue = orderBook.get();
        assertEquals(1,orderQueue.size());
    }

    @Test
    public void testOrdersByRic() {
        Optional<Map<Side, Queue<Order>>> orderBookByRic = orderDao.getOrderBookByRic("VOD.L");
        assertTrue(orderBookByRic.isPresent());

        Queue<Order> buyOrderQueue = orderBookByRic.get().get(Side.BUY);
        assertEquals(3,buyOrderQueue.size());
        Queue<Order> sellOrderQueue = orderBookByRic.get().get(Side.SELL);
        assertEquals(2,sellOrderQueue.size());

    }


}
