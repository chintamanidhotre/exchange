package com.rbc.exchange.service;

import com.rbc.exchange.BaseOrderTest;
import com.rbc.exchange.dao.OrderDao;
import com.rbc.exchange.model.Order;
import com.rbc.exchange.model.Side;
import com.rbc.exchange.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.rbc.exchange.model.Side.BUY;
import static com.rbc.exchange.model.Side.SELL;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Unit test for ExchangeService.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExchangeServiceTest extends BaseOrderTest {
    @Mock
    private OrderDao orderDao;
    private ExchangeService exchangeService;


    @Before
    public void setUp(){
        exchangeService = new ExchangeServiceImpl(orderDao);
    }

    @Test
    public void testAddOrder(){
        when(orderDao.getOrderBookByRicAndSide(any(String.class), any(Side.class))).thenReturn(Optional.empty());

        Order order = exchangeService.addOrder(createOrder(RIC, BUY));

        assertNotNull(order);
        assertFalse(order.isExecuted());
    }

    @Test
    public void testAddOrderWhenMatchNotFound(){
        when(orderDao.getOrderBookByRicAndSide(any(String.class), any(Side.class))).thenReturn(Optional.empty());

        Order order1 = exchangeService.addOrder(createOrder(RIC, BUY));
        Order order2 = exchangeService.addOrder(createOrder(RIC, BUY));

        assertNotNull(order1);
        assertNotNull(order2);
        assertFalse(order1.isExecuted());
        assertFalse(order2.isExecuted());
    }

    @Test
    public void testOrderStatusAsExecutedWhenWhenMatch(){
        when(orderDao.getOrderBookByRicAndSide(eq(RIC), eq(SELL))).thenReturn(Optional.empty());

        Order mockOrder1 = createOrder(RIC, BUY);
        Order mockOrder2 = createOrder(RIC, SELL);
        Queue<Order> orderQueue = new ConcurrentLinkedQueue<>();
        orderQueue.add(mockOrder1);

        Optional<Queue<Order>> mockOptionalQueue = Optional.of(orderQueue);

        when(orderDao.getOrderBookByRicAndSide(eq(RIC), eq(BUY))).thenReturn(mockOptionalQueue);

        Order order1 = exchangeService.addOrder(mockOrder1);
        Order order2 = exchangeService.addOrder(mockOrder2);

        assertNotNull(order1);
        assertNotNull(order2);
        assertTrue(order1.isExecuted());
        assertTrue(order2.isExecuted());

    }

    @Test
    public void testOpenInterest(){

        Order mockOrder1 = createOrder(RIC, BUY, 100, new BigDecimal(100.12), 1001);
        Order mockOrder2 = createOrder(RIC, BUY, 100, new BigDecimal(100.12), 1001);
        Order mockOrder3 = createOrder(RIC, BUY, 100, new BigDecimal(104.15), 1001);
        Order mockOrder4 = createOrder(RIC, SELL, 100, new BigDecimal(104.15), 1001);
        Order mockOrder5 = createOrder(RIC, SELL, 100, new BigDecimal(105.15), 1001);
        Queue<Order> orderQueue = new ConcurrentLinkedQueue<>();
        orderQueue.add(mockOrder1);
        orderQueue.add(mockOrder2);
        orderQueue.add(mockOrder3);
        orderQueue.add(mockOrder4);
        orderQueue.add(mockOrder5);

        Optional<Queue<Order>> mockOptionalQueue = Optional.of(orderQueue);

        when(orderDao.getOrderBookByRicAndSide(eq(RIC), eq(BUY))).thenReturn(mockOptionalQueue);

        Map<BigDecimal,Long> openInterestMap = exchangeService.getOpenInterest(RIC, BUY);
        assertNotNull(openInterestMap);

        assertEquals(3, openInterestMap.size());
        assertEquals(new Long(200), openInterestMap.get(new BigDecimal(100.12)));
        assertEquals(new Long(200), openInterestMap.get(new BigDecimal(104.15)));
        assertEquals(new Long(100), openInterestMap.get(new BigDecimal(105.15)));

    }

    @Test
    public void testAverageExecutionPriceWhenNoExecutedOrdersAvailabe(){
        when(orderDao.getOrderBookByRic(eq(RIC))).thenReturn(Optional.empty());
        BigDecimal averagePrice = exchangeService.getAverageExecutionPrice(RIC);

        assertEquals(BigDecimal.ZERO, averagePrice);
    }

    @Test
    public void testAverageExecutionPriceWhenExecutedOrdersAvailabe(){


        BigDecimal executedPrice = new BigDecimal(100.14);
        Order mockOrder1 = createOrder(RIC, BUY, 100, executedPrice, 1001);
        mockOrder1.setExecuted(true);
        mockOrder1.setExecutedPrice(executedPrice);

        Order mockOrder2 = createOrder(RIC, SELL, 100, executedPrice, 1002);
        mockOrder2.setExecuted(true);
        mockOrder2.setExecutedPrice(executedPrice);

        Queue<Order> buyOrderQueue = new ConcurrentLinkedQueue<>();
        buyOrderQueue.add(mockOrder1);

        Queue<Order> sellOrderQueue = new ConcurrentLinkedQueue<>();
        sellOrderQueue.add(mockOrder2);

        ConcurrentHashMap<Side, Queue<Order>> sideOrderMap = new ConcurrentHashMap<>();
        sideOrderMap.put(BUY, buyOrderQueue);
        sideOrderMap.put(SELL, sellOrderQueue);

        Optional<Map<Side, Queue<Order>>> sideQueueMap = Optional.of(sideOrderMap);
        when(orderDao.getOrderBookByRic(eq(RIC))).thenReturn(sideQueueMap);
        BigDecimal averagePrice = exchangeService.getAverageExecutionPrice(RIC);

        assertEquals(executedPrice.setScale(4, BigDecimal.ROUND_DOWN), averagePrice);
    }

    @Test
    public void testExecutionQuantityWhenNoExecutedOrdersAvailabe(){

        User user = new User(1);
        when(orderDao.getOrderBookByRicAndUser(RIC, user)).thenReturn(new ArrayList<Order>());

        long executedQuantity = exchangeService.getExecutedQuantity(RIC, user);

        assertEquals(0, executedQuantity);

    }

    @Test
    public void testExecutionQuantityWhenExecutedOrdersAvailabe(){
        BigDecimal executedPrice = new BigDecimal(100.14);
        Order mockOrder1 = createOrder(RIC, BUY, 100, executedPrice, 1001);
        mockOrder1.setExecuted(true);
        mockOrder1.setExecutedPrice(executedPrice);

        Order mockOrder2 = createOrder(RIC, BUY, 300, executedPrice, 1001);
        mockOrder2.setExecuted(true);
        mockOrder2.setExecutedPrice(executedPrice);


        List<Order> orderList = new ArrayList<>();
        orderList.add(mockOrder1);
        orderList.add(mockOrder2);

        User user = new User(1);
        when(orderDao.getOrderBookByRicAndUser(RIC, user)).thenReturn(orderList);

        long executedQuantity = exchangeService.getExecutedQuantity(RIC, user);

        assertEquals(400, executedQuantity);

    }

    @Test
    public void testExecutionQuantityWhenExecutedOrdersAvailabeWithNegatedQuantity(){
        BigDecimal executedPrice = new BigDecimal(100.14);
        Order mockOrder1 = createOrder(RIC, BUY, 100, executedPrice, 1001);
        mockOrder1.setExecuted(true);
        mockOrder1.setExecutedPrice(executedPrice);

        Order mockOrder2 = createOrder(RIC, BUY, 300, executedPrice, 1001);
        mockOrder2.setExecuted(true);
        mockOrder2.setExecutedPrice(executedPrice);

        Order mockOrder3 = createOrder(RIC, SELL, 200, executedPrice, 1001);
        mockOrder3.setExecuted(true);
        mockOrder3.setExecutedPrice(executedPrice);

        Order mockOrder4 = createOrder(RIC, SELL, 200, executedPrice, 1001);

        List<Order> orderList = new ArrayList<>();
        orderList.add(mockOrder1);
        orderList.add(mockOrder2);
        orderList.add(mockOrder3);
        orderList.add(mockOrder4);

        User user = new User(1);
        when(orderDao.getOrderBookByRicAndUser(RIC, user)).thenReturn(orderList);

        long executedQuantity = exchangeService.getExecutedQuantity(RIC, user);

        assertEquals(200, executedQuantity);

    }


}
